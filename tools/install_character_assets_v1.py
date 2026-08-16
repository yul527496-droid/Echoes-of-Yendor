#!/usr/bin/env python3
"""Verify the checked-in Chapter 1 production map-character assets are installed."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
REQUIRED = [
    ROOT / "core/src/main/assets/sprites/echoes_ch1_character_sprites_v1.png",
    ROOT / "core/src/main/assets/sprites/echoes_donkey_cart_v5.png",
]

missing = [p for p in REQUIRED if not p.is_file()]
if missing:
    raise SystemExit("Missing production character assets: " + ", ".join(str(p.relative_to(ROOT)) for p in missing))

print("Chapter 1 production map-character assets are installed.")
