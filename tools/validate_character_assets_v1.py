#!/usr/bin/env python3
"""Validate Chapter 1 production character runtime assets."""
from pathlib import Path
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1]
PNG = b"\x89PNG\r\n\x1a\n"
EXPECTED = {
    Path("core/src/main/assets/sprites/echoes_ch1_character_sprites_v1.png"): (128, 272),
    Path("core/src/main/assets/sprites/echoes_donkey_cart_v5.png"): (256, 16),
    Path("core/src/main/assets/interfaces/echoes/echoes_dialogue_portraits_v2.png"): (912, 48),
}

def dimensions(path):
    data = path.read_bytes()
    if not data.startswith(PNG):
        raise SystemExit(f"{path}: invalid PNG signature")
    if data[12:16] != b"IHDR":
        raise SystemExit(f"{path}: missing IHDR")
    w, h = struct.unpack(">II", data[16:24])
    # validate all chunk CRCs and the compressed image stream
    pos = 8
    idat = bytearray()
    saw_end = False
    while pos < len(data):
        length = struct.unpack(">I", data[pos:pos+4])[0]
        kind = data[pos+4:pos+8]
        payload = data[pos+8:pos+8+length]
        crc = struct.unpack(">I", data[pos+8+length:pos+12+length])[0]
        actual = zlib.crc32(kind)
        actual = zlib.crc32(payload, actual) & 0xffffffff
        if crc != actual:
            raise SystemExit(f"{path}: bad CRC in {kind!r}")
        if kind == b"IDAT": idat.extend(payload)
        if kind == b"IEND": saw_end = True; break
        pos += 12 + length
    if not saw_end or not idat:
        raise SystemExit(f"{path}: incomplete PNG")
    zlib.decompress(bytes(idat))
    return w, h

for rel, expected in EXPECTED.items():
    path = ROOT / rel
    if not path.is_file():
        raise SystemExit(f"missing production asset: {rel}")
    got = dimensions(path)
    if got != expected:
        raise SystemExit(f"{rel}: expected {expected[0]}x{expected[1]}, got {got[0]}x{got[1]}")
    print(f"Production PNG OK: {rel} ({got[0]}x{got[1]})")

print("Chapter 1 production character asset contract OK.")
