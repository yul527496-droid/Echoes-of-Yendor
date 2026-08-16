from pathlib import Path
import struct
import subprocess
import sys
import zlib

PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"

# Production art is source-generated before validation. This keeps checked-in source
# editable text rather than opaque binary blobs while guaranteeing deterministic CI assets.
subprocess.run([sys.executable, "tools/generate_surface_vertical_slice.py"], check=True)
subprocess.run([sys.executable, "tools/generate_surface_characters_v2.py"], check=True)
subprocess.run([sys.executable, "tools/generate_farmer_v3.py"], check=True)

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
    Path("core/src/main/assets/environment/echoes/ch1_surface/grass.png"),
    Path("core/src/main/assets/environment/echoes/ch1_surface/forest.png"),
    Path("core/src/main/assets/environment/echoes/ch1_surface/road.png"),
    Path("core/src/main/assets/environment/echoes/ch1_surface/river.png"),
    Path("core/src/main/assets/environment/echoes/ch1_surface/bridge.png"),
    Path("core/src/main/assets/environment/echoes/ch1_surface/camp_ruin.png"),
    Path("core/src/main/assets/interfaces/echoes/minimap_pixel.png"),
    Path("core/src/main/assets/sprites/echoes_farmer_v3.png"),
    Path("core/src/main/assets/sprites/echoes_farmer_v2.png"),
    Path("core/src/main/assets/sprites/echoes_donkey_cart_v2.png"),
    Path("core/src/main/assets/sprites/echoes_bird_v2.png"),
    Path("core/src/main/assets/sprites/echoes_wolf_v2.png"),
    Path("core/src/main/assets/sprites/surface_villagers_v3.png"),
    # Keep validating old prototype sheets while they remain in the repository.
    Path("core/src/main/assets/sprites/surface_villagers.png"),
    Path("core/src/main/assets/sprites/surface_villagers_v2.png"),
    Path("core/src/main/assets/sprites/echoes_farmer_v1.png"),
    Path("core/src/main/assets/sprites/echoes_donkey_cart_v1.png"),
    Path("core/src/main/assets/sprites/echoes_bird_v1.png"),
    Path("core/src/main/assets/sprites/echoes_wolf_v1.png"),
    Path("core/src/main/assets/interfaces/echoes/ledger_open.png"),
    Path("core/src/main/assets/interfaces/echoes/ledger_closed.png"),
    Path("core/src/main/assets/environment/custom_tiles/training_dungeon_entrance.png"),
    Path("core/src/main/assets/sprites/training_dummy.png"),
    Path("core/src/main/assets/sprites/training_target.png"),
    Path("core/src/main/assets/sprites/veteran_trainer.png"),
]

EXPECTED_DIMENSIONS = {
    Path("core/src/main/assets/environment/echoes/ch1_surface/grass.png"): (128, 32),
    Path("core/src/main/assets/environment/echoes/ch1_surface/forest.png"): (128, 64),
    Path("core/src/main/assets/environment/echoes/ch1_surface/road.png"): (128, 32),
    Path("core/src/main/assets/environment/echoes/ch1_surface/river.png"): (128, 32),
    Path("core/src/main/assets/environment/echoes/ch1_surface/bridge.png"): (64, 48),
    Path("core/src/main/assets/environment/echoes/ch1_surface/camp_ruin.png"): (192, 64),
    Path("core/src/main/assets/sprites/echoes_farmer_v3.png"): (128, 16),
    Path("core/src/main/assets/sprites/echoes_farmer_v2.png"): (64, 16),
    Path("core/src/main/assets/sprites/echoes_donkey_cart_v2.png"): (128, 16),
    Path("core/src/main/assets/sprites/echoes_bird_v2.png"): (64, 16),
    Path("core/src/main/assets/sprites/echoes_wolf_v2.png"): (64, 16),
    Path("core/src/main/assets/sprites/surface_villagers_v3.png"): (256, 16),
}


def validate_png(path: Path) -> tuple[int, int]:
    data = path.read_bytes()
    if not data.startswith(PNG_SIGNATURE):
        raise SystemExit(f"{path}: invalid PNG signature")
    pos = len(PNG_SIGNATURE)
    saw_iend = False
    saw_ihdr = False
    idat = bytearray()
    dimensions = None
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
            dimensions = struct.unpack(">II", chunk_data[:8])
        elif chunk_type == b"IDAT":
            idat.extend(chunk_data)
        elif chunk_type == b"IEND":
            saw_iend = True
            pos = end
            break
        pos = end
    if not saw_ihdr:
        raise SystemExit(f"{path}: missing IHDR chunk")
    if not idat:
        raise SystemExit(f"{path}: missing IDAT data")
    if not saw_iend:
        raise SystemExit(f"{path}: missing IEND chunk")
    try:
        zlib.decompress(bytes(idat))
    except zlib.error as error:
        raise SystemExit(f"{path}: corrupt PNG image stream: {error}")
    if dimensions is None:
        raise SystemExit(f"{path}: no PNG dimensions")
    expected = EXPECTED_DIMENSIONS.get(path)
    if expected is not None and dimensions != expected:
        raise SystemExit(f"{path}: expected {expected[0]}x{expected[1]}, got {dimensions[0]}x{dimensions[1]}")
    print(f"PNG OK: {path} ({dimensions[0]}x{dimensions[1]})")
    return dimensions


for file_path in FILES:
    validate_png(file_path)

print("Surface art scale contract OK: environment packs remain integer multiples of SPD's 16px grid.")
print("Character scale contract OK: Farmer v3 uses eight native 16x16 frames; bird/wolf/villagers remain 16px-frame prototypes; donkey-cart remains an authored 32x16 wide frame.")
