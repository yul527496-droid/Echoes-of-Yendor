#!/usr/bin/env python3
"""Generate the Chapter 1 roadside Farmer v3 directly on SPD's native 16px grid.

No antialiasing, no high-resolution source, no resampling. The 128x16 sheet contains:
idle A, idle B, walk 1, walk 2, walk 3, walk 4, confused, shaken.
"""
from pathlib import Path
import hashlib
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "core/src/main/assets/sprites/echoes_farmer_v3.png"
PNG = b"\x89PNG\r\n\x1a\n"


def chunk(kind, payload):
    crc = zlib.crc32(kind)
    crc = zlib.crc32(payload, crc) & 0xFFFFFFFF
    return struct.pack(">I", len(payload)) + kind + payload + struct.pack(">I", crc)


def write_png(path, width, height, pixels):
    raw = bytearray()
    stride = width * 4
    for y in range(height):
        raw.append(0)
        raw.extend(pixels[y * stride:(y + 1) * stride])
    data = (
        PNG
        + chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
        + chunk(b"IDAT", zlib.compress(bytes(raw), 9))
        + chunk(b"IEND", b"")
    )
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(data)
    return hashlib.sha256(data).hexdigest()


class Canvas:
    def __init__(self, width, height):
        self.w = width
        self.h = height
        self.p = bytearray((0, 0, 0, 0) * (width * height))

    def set(self, x, y, color):
        if 0 <= x < self.w and 0 <= y < self.h:
            i = (x + y * self.w) * 4
            self.p[i:i + 4] = bytes(color)

    def rect(self, x0, y0, x1, y1, color):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                self.set(x, y, color)

    def blit(self, source, dx, dy):
        for y in range(source.h):
            for x in range(source.w):
                i = (x + y * source.w) * 4
                if source.p[i + 3]:
                    self.set(dx + x, dy + y, tuple(source.p[i:i + 4]))


# Fifteen opaque working colors. Values are deliberately muted and clustered so the
# farmer carries similar visual weight to SPD's existing ordinary human NPC sprites.
HAT_DARK = (74, 50, 30, 255)
HAT_MID = (111, 76, 41, 255)
HAT_LIGHT = (151, 105, 53, 255)
SKIN_DARK = (125, 79, 52, 255)
SKIN = (194, 133, 86, 255)
SKIN_LIGHT = (229, 171, 116, 255)
BEARD_DARK = (91, 86, 77, 255)
BEARD_MID = (151, 145, 132, 255)
BEARD_LIGHT = (207, 201, 183, 255)
COAT_DARK = (69, 44, 28, 255)
COAT_MID = (108, 67, 38, 255)
SHIRT_DARK = (51, 61, 34, 255)
SHIRT = (83, 94, 49, 255)
BOOT = (45, 35, 30, 255)
EYE = (38, 29, 24, 255)
CLEAR = (0, 0, 0, 0)


def farmer_base(arm_l=0, arm_r=0, leg_l=0, leg_r=0, lean=0, hat_shift=0):
    c = Canvas(16, 16)

    # Hat: broad silhouette and asymmetric pixel clusters, not a rectangular cap.
    c.rect(6 + hat_shift + lean, 1, 9 + hat_shift + lean, 1, HAT_DARK)
    c.rect(5 + hat_shift + lean, 2, 10 + hat_shift + lean, 3, HAT_MID)
    c.set(6 + hat_shift + lean, 2, HAT_LIGHT)
    c.set(9 + hat_shift + lean, 2, HAT_DARK)
    c.rect(3 + hat_shift + lean, 4, 12 + hat_shift + lean, 4, HAT_DARK)
    c.rect(4 + hat_shift + lean, 4, 10 + hat_shift + lean, 4, HAT_LIGHT)
    c.set(2 + hat_shift + lean, 5, HAT_DARK)
    c.set(11 + hat_shift + lean, 5, HAT_MID)

    # Head and hair. The face is deliberately broken into small clusters rather than a block.
    c.rect(5 + lean, 5, 10 + lean, 7, SKIN)
    c.set(5 + lean, 5, SKIN_DARK)
    c.set(10 + lean, 5, SKIN_DARK)
    c.set(6 + lean, 5, SKIN_LIGHT)
    c.set(6 + lean, 6, EYE)
    c.set(9 + lean, 6, EYE)
    c.set(10 + lean, 7, SKIN_LIGHT)
    c.set(4 + lean, 6, HAT_DARK)
    c.set(4 + lean, 7, SKIN_DARK)
    c.set(11 + lean, 6, HAT_DARK)

    # Beard: three value groups and an irregular bottom edge.
    c.set(5 + lean, 8, BEARD_DARK)
    c.rect(6 + lean, 8, 9 + lean, 9, BEARD_MID)
    c.set(10 + lean, 8, BEARD_DARK)
    c.set(6 + lean, 8, BEARD_LIGHT)
    c.set(9 + lean, 9, BEARD_LIGHT)
    c.rect(7 + lean, 10, 8 + lean, 10, BEARD_MID)
    c.set(6 + lean, 10, BEARD_DARK)
    c.set(9 + lean, 10, BEARD_DARK)

    # Coat, shirt and shoulders. There is no continuous heavy outline around the torso.
    c.set(4 + lean, 9, COAT_DARK)
    c.rect(4 + lean, 10, 5 + lean, 12, COAT_MID)
    c.rect(10 + lean, 10, 11 + lean, 12, COAT_MID)
    c.set(11 + lean, 9, COAT_DARK)
    c.rect(6 + lean, 10, 9 + lean, 12, SHIRT)
    c.set(6 + lean, 10, SHIRT_DARK)
    c.set(9 + lean, 12, SHIRT_DARK)

    # Arms.
    left_x = 3 + lean + arm_l
    right_x = 12 + lean + arm_r
    c.set(left_x, 10, COAT_DARK)
    c.set(left_x, 11, COAT_MID)
    c.set(left_x, 12, SKIN)
    c.set(right_x, 10, COAT_DARK)
    c.set(right_x, 11, COAT_MID)
    c.set(right_x, 12, SKIN)

    # Hips and boots share SPD's tile baseline at y=15.
    c.rect(6 + lean, 13, 9 + lean, 13, COAT_DARK)
    c.set(6 + lean + leg_l, 14, BOOT)
    c.set(6 + lean + leg_l, 15, BOOT)
    c.set(9 + lean + leg_r, 14, BOOT)
    c.set(9 + lean + leg_r, 15, BOOT)
    return c


def idle_b():
    c = farmer_base()
    # Breath/weight change is intentionally tiny: shoulders shift without bobbing the whole body.
    c.set(4, 10, CLEAR)
    c.set(4, 11, COAT_MID)
    c.set(11, 10, CLEAR)
    c.set(11, 11, COAT_MID)
    c.set(7, 10, BEARD_LIGHT)
    return c


def walk_fourth():
    c = farmer_base()
    c.set(5, 2, HAT_DARK)
    c.set(10, 3, HAT_LIGHT)
    c.set(4, 12, CLEAR)
    c.set(5, 12, COAT_MID)
    return c


def confused():
    c = farmer_base()
    # Left hand rises toward the brim; the rest of the body stays grounded.
    for x, y in ((3, 10), (3, 11), (3, 12)):
        c.set(x, y, CLEAR)
    c.set(4, 9, COAT_DARK)
    c.set(4, 8, COAT_MID)
    c.set(4, 7, SKIN)
    c.set(5, 7, SKIN_LIGHT)
    return c


def shaken():
    c = farmer_base(lean=1, hat_shift=-1)
    # A single readable recoil pose for the Yendor break: lean back, arms open, hat tipped.
    for x, y in ((4, 10), (4, 11), (4, 12), (13, 10), (13, 11), (13, 12)):
        c.set(x, y, CLEAR)
    c.set(3, 10, COAT_DARK)
    c.set(2, 11, COAT_MID)
    c.set(1, 12, SKIN)
    c.set(13, 9, COAT_DARK)
    c.set(14, 10, COAT_MID)
    c.set(15, 11, SKIN)
    c.set(3, 3, HAT_DARK)
    c.set(4, 3, HAT_MID)
    return c


def build_sheet():
    frames = [
        farmer_base(),
        idle_b(),
        farmer_base(arm_l=1, arm_r=-1, leg_l=-1, leg_r=0),
        farmer_base(),
        farmer_base(arm_l=-1, arm_r=1, leg_l=0, leg_r=1),
        walk_fourth(),
        confused(),
        shaken(),
    ]
    sheet = Canvas(128, 16)
    for index, frame in enumerate(frames):
        sheet.blit(frame, index * 16, 0)
    return sheet


sheet = build_sheet()
digest = write_png(OUT, sheet.w, sheet.h, sheet.p)
print(f"echoes_farmer_v3.png {sheet.w}x{sheet.h} {digest}")
