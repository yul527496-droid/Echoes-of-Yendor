from pathlib import Path
import struct
import zlib

PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"
FILES = [
    Path("core/src/main/assets/environment/tiles_surface.png"),
    Path("core/src/main/assets/environment/water_surface.png"),
]


def validate_png(path: Path) -> None:
    data = path.read_bytes()
    if not data.startswith(PNG_SIGNATURE):
        raise SystemExit(f"{path}: invalid PNG signature")

    pos = len(PNG_SIGNATURE)
    saw_iend = False
    while pos < len(data):
        if pos + 12 > len(data):
            raise SystemExit(f"{path}: truncated PNG chunk")

        length = struct.unpack(">I", data[pos:pos + 4])[0]
        chunk_type = data[pos + 4:pos + 8]
        end = pos + 12 + length
        if end > len(data):
            raise SystemExit(f"{path}: truncated {chunk_type.decode('ascii', 'replace')} chunk")

        chunk_data = data[pos + 8:pos + 8 + length]
        expected_crc = struct.unpack(">I", data[pos + 8 + length:end])[0]
        actual_crc = zlib.crc32(chunk_type)
        actual_crc = zlib.crc32(chunk_data, actual_crc) & 0xFFFFFFFF
        if actual_crc != expected_crc:
            name = chunk_type.decode("ascii", "replace")
            raise SystemExit(f"{path}: bad CRC in {name} chunk")

        pos = end
        if chunk_type == b"IEND":
            saw_iend = True
            break

    if not saw_iend:
        raise SystemExit(f"{path}: missing IEND chunk")

    print(f"PNG OK: {path}")


for file_path in FILES:
    validate_png(file_path)
