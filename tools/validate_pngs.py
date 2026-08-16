from pathlib import Path
import struct
import zlib

PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"
FILES = [
    Path("core/src/main/assets/environment/tiles_surface.png"),
    Path("core/src/main/assets/environment/water_surface.png"),
    Path("core/src/main/assets/environment/tiles_surface_v1.png"),
    Path("core/src/main/assets/environment/tiles_farm_v1.png"),
    Path("core/src/main/assets/environment/tiles_town_v1.png"),
    Path("core/src/main/assets/environment/tiles_inn_v1.png"),
    Path("core/src/main/assets/environment/water_surface_v1.png"),
    Path("core/src/main/assets/environment/custom_tiles/echoes_surface_art_v2.png"),
    Path("core/src/main/assets/environment/custom_tiles/echoes_landmarks_v1.png"),
    Path("core/src/main/assets/interfaces/echoes/minimap_pixel.png"),
    Path("core/src/main/assets/sprites/surface_villagers.png"),
    Path("core/src/main/assets/interfaces/echoes/ledger_open.png"),
    Path("core/src/main/assets/interfaces/echoes/ledger_closed.png"),
    Path("core/src/main/assets/environment/custom_tiles/training_dungeon_entrance.png"),
    Path("core/src/main/assets/sprites/training_dummy.png"),
    Path("core/src/main/assets/sprites/training_target.png"),
    Path("core/src/main/assets/sprites/veteran_trainer.png"),
]


def validate_png(path: Path) -> None:
    data = path.read_bytes()
    if not data.startswith(PNG_SIGNATURE):
        raise SystemExit(f"{path}: invalid PNG signature")
    pos = len(PNG_SIGNATURE)
    saw_iend = False
    saw_ihdr = False
    idat = bytearray()
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
            raise SystemExit(f"{path}: bad CRC in {chunk_type.decode('ascii', 'replace')} chunk")
        if chunk_type == b"IHDR":
            if length != 13:
                raise SystemExit(f"{path}: invalid IHDR length")
            saw_ihdr = True
        elif chunk_type == b"IDAT":
            idat.extend(chunk_data)
        elif chunk_type == b"IEND":
            saw_iend = True
            pos = end
            break
        pos = end
    if not saw_ihdr: raise SystemExit(f"{path}: missing IHDR chunk")
    if not idat: raise SystemExit(f"{path}: missing IDAT data")
    if not saw_iend: raise SystemExit(f"{path}: missing IEND chunk")
    try:
        zlib.decompress(bytes(idat))
    except zlib.error as error:
        raise SystemExit(f"{path}: corrupt PNG image stream: {error}")
    print(f"PNG OK: {path}")


for file_path in FILES:
    validate_png(file_path)
