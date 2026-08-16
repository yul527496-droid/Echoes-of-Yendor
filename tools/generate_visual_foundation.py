#!/usr/bin/env python3
"""Generate Echoes Visual Foundation terrain atlases from the checked-in surface base.

The four atlases are stored as zlib-compressed XOR deltas against the decoded
RGBA pixels of environment/tiles_surface.png. This keeps source control text-only
for the larger generated atlases while preserving the exact pixel art created in
the Visual Foundation pass. The generation uses only the Python standard library.
"""
from pathlib import Path
import base64
import hashlib
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / "core/src/main/assets/environment/tiles_surface.png"
DELTA_DIR = ROOT / "tools/visual_foundation"

TARGETS = {
    "tiles_surface_v1.png": "29ae8fb2c72e2f438f3a329462215fe48b64c59c4a23fe39180a73914e14c5fa",
    "tiles_farm_v1.png": "d04480b82073c7e0726d3bd2bb9e2ac6826f997fde785d817676a305be521824",
    "tiles_town_v1.png": "ce3321d7f0082f6d57d0ae1151479ef7948778b51aac644239dae6e54b282e8c",
    "tiles_inn_v1.png": "a5518fc32fe3818d0ff390dbbb988f245ca6cef70ec6ca09f5170f9f62d7a68a",
}

PNG_SIG = b"\x89PNG\r\n\x1a\n"

def paeth(a, b, c):
    p = a + b - c
    pa = abs(p-a); pb = abs(p-b); pc = abs(p-c)
    if pa <= pb and pa <= pc: return a
    if pb <= pc: return b
    return c

def decode_indexed_png(path):
    data = path.read_bytes()
    if not data.startswith(PNG_SIG):
        raise SystemExit(f"{path}: not PNG")
    pos = 8
    palette = None
    alpha = None
    idat = bytearray()
    width = height = bit_depth = color_type = None
    while pos < len(data):
        length = struct.unpack(">I", data[pos:pos+4])[0]
        typ = data[pos+4:pos+8]
        chunk = data[pos+8:pos+8+length]
        pos += 12 + length
        if typ == b"IHDR":
            width, height, bit_depth, color_type, comp, filt, interlace = struct.unpack(">IIBBBBB", chunk)
            if bit_depth != 8 or color_type != 3 or interlace != 0:
                raise SystemExit(f"{path}: expected non-interlaced indexed PNG8, got depth={bit_depth}, type={color_type}")
        elif typ == b"PLTE":
            palette = [tuple(chunk[i:i+3]) for i in range(0, len(chunk), 3)]
        elif typ == b"tRNS":
            alpha = list(chunk)
        elif typ == b"IDAT":
            idat.extend(chunk)
        elif typ == b"IEND":
            break
    raw = zlib.decompress(bytes(idat))
    stride = width
    rows = []
    prev = bytearray(stride)
    off = 0
    for _y in range(height):
        f = raw[off]; off += 1
        src = bytearray(raw[off:off+stride]); off += stride
        dst = bytearray(stride)
        for x, val in enumerate(src):
            left = dst[x-1] if x else 0
            up = prev[x]
            ul = prev[x-1] if x else 0
            if f == 0: out = val
            elif f == 1: out = (val + left) & 255
            elif f == 2: out = (val + up) & 255
            elif f == 3: out = (val + ((left + up)//2)) & 255
            elif f == 4: out = (val + paeth(left, up, ul)) & 255
            else: raise SystemExit(f"{path}: unsupported PNG filter {f}")
            dst[x] = out
        rows.append(dst)
        prev = dst
    rgba = bytearray(width*height*4)
    p=0
    for row in rows:
        for idx in row:
            r,g,b = palette[idx]
            a = alpha[idx] if alpha is not None and idx < len(alpha) else 255
            rgba[p:p+4] = bytes((r,g,b,a))
            p += 4
    return width, height, rgba

def png_chunk(typ, payload):
    crc = zlib.crc32(typ)
    crc = zlib.crc32(payload, crc) & 0xffffffff
    return struct.pack(">I",len(payload)) + typ + payload + struct.pack(">I",crc)

def write_rgba_png(path, width, height, rgba):
    stride = width*4
    raw = bytearray()
    for y in range(height):
        raw.append(0)
        raw.extend(rgba[y*stride:(y+1)*stride])
    ihdr = struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)
    data = PNG_SIG + png_chunk(b"IHDR", ihdr)
    data += png_chunk(b"IDAT", zlib.compress(bytes(raw), 9))
    data += png_chunk(b"IEND", b"")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(data)

def main():
    width, height, base = decode_indexed_png(BASE)
    if (width, height) != (256, 256):
        raise SystemExit(f"Unexpected surface atlas dimensions: {width}x{height}")
    for filename, expected_pixels in TARGETS.items():
        delta_file = DELTA_DIR / (filename + ".xor.b64")
        delta = zlib.decompress(base64.b64decode(delta_file.read_text(encoding="ascii").strip()))
        if len(delta) != len(base):
            raise SystemExit(f"{filename}: XOR delta length mismatch")
        out = bytearray(a ^ b for a,b in zip(base, delta))
        actual = hashlib.sha256(out).hexdigest()
        if actual != expected_pixels:
            raise SystemExit(f"{filename}: pixel SHA256 mismatch: expected {expected_pixels}, got {actual}")
        target = ROOT / "core/src/main/assets/environment" / filename
        write_rgba_png(target, width, height, out)
        print(f"Generated {target.relative_to(ROOT)} pixels-sha256={actual}")

if __name__ == "__main__":
    main()
