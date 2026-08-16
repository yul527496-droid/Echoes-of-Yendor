#!/usr/bin/env python3
"""Install the checked-in Chapter 1 production character assets into runtime paths.

The production source PNGs themselves are tracked directly under core/src/main/assets in
this revision; this helper is intentionally lightweight so local/CI workflows have one
stable hook for future packed-source migrations without reintroducing the old prototype
generators.
"""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
REQUIRED = [
    ROOT / "core/src/main/assets/sprites/echoes_ch1_character_sprites_v1.png",
    ROOT / "core/src/main/assets/sprites/echoes_donkey_cart_v5.png",
    ROOT / "core/src/main/assets/interfaces/echoes/echoes_dialogue_portraits_v2.png",
]

missing = [p for p in REQUIRED if not p.is_file()]
if missing:
    raise SystemExit("Missing production character assets: " + ", ".join(str(p.relative_to(ROOT)) for p in missing))

print("Chapter 1 production character assets are installed.")
