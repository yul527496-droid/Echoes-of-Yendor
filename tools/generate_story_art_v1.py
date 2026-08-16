#!/usr/bin/env python3
"""Generate native-pixel Chapter 1 story portraits and Old Crow innkeeper art.

All art is authored directly on integer pixel grids. No embedded binary data,
anti-aliasing, interpolation, or high-resolution downsampling is used.
"""
from pathlib import Path
import hashlib
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1]
SPRITES = ROOT / "core/src/main/assets/sprites"
INTERFACES = ROOT / "core/src/main/assets/interfaces/echoes"
SPRITES.mkdir(parents=True, exist_ok=True)
INTERFACES.mkdir(parents=True, exist_ok=True)
PNG = b"\x89PNG\r\n\x1a\n"


def chunk(t, p):
    crc = zlib.crc32(t)
    crc = zlib.crc32(p, crc) & 0xFFFFFFFF
    return struct.pack(">I", len(p)) + t + p + struct.pack(">I", crc)


def write(path, w, h, pixels):
    raw = bytearray()
    stride = w * 4
    for y in range(h):
        raw.append(0)
        raw.extend(pixels[y * stride:(y + 1) * stride])
    data = PNG + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    data += chunk(b"IDAT", zlib.compress(bytes(raw), 9)) + chunk(b"IEND", b"")
    path.write_bytes(data)
    return hashlib.sha256(data).hexdigest()


class C:
    def __init__(self, w, h):
        self.w, self.h = w, h
        self.p = bytearray((0, 0, 0, 0) * (w * h))

    def s(self, x, y, color):
        if 0 <= x < self.w and 0 <= y < self.h:
            i = (x + y * self.w) * 4
            self.p[i:i + 4] = bytes(color)

    def r(self, x, y, w, h, color):
        for yy in range(y, y + h):
            for xx in range(x, x + w):
                self.s(xx, yy, color)

    def h(self, x, y, n, color):
        for i in range(n):
            self.s(x + i, y, color)

    def blit(self, src, dx, dy):
        for y in range(src.h):
            for x in range(src.w):
                i = (x + y * src.w) * 4
                if src.p[i + 3]:
                    self.s(dx + x, dy + y, tuple(src.p[i:i + 4]))


# Shared muted SPD-like portrait palette. Each portrait uses only a subset.
INK = (34, 30, 27, 255)
DEEP = (52, 43, 38, 255)
SKIN_D = (132, 82, 57, 255)
SKIN = (190, 128, 87, 255)
SKIN_H = (224, 166, 112, 255)
STEEL_D = (65, 72, 74, 255)
STEEL = (102, 112, 111, 255)
STEEL_H = (157, 163, 151, 255)
CREAM = (207, 197, 166, 255)
GOLD_D = (124, 91, 45, 255)
GOLD = (177, 133, 64, 255)
RED_D = (93, 45, 35, 255)
RED = (145, 66, 48, 255)
BLUE_D = (48, 58, 79, 255)
BLUE = (70, 87, 112, 255)
GREEN_D = (48, 67, 47, 255)
GREEN = (71, 94, 62, 255)
PURPLE_D = (63, 50, 75, 255)
PURPLE = (91, 69, 105, 255)
BROWN_D = (74, 49, 31, 255)
BROWN = (111, 76, 42, 255)
BROWN_H = (153, 108, 58, 255)
HAIR_RED = (132, 61, 42, 255)
HAIR_BLOND = (166, 126, 67, 255)
HAIR_DARK = (57, 44, 38, 255)
WHITE = (220, 215, 195, 255)


def bust_base(cloth_dark, cloth, hair, beard=False, hood=False, armor=False):
    c = C(48, 48)
    # shoulders and torso silhouette
    c.r(8, 35, 32, 13, INK)
    c.r(11, 34, 26, 14, cloth_dark)
    c.r(14, 34, 20, 13, cloth)
    c.r(6, 40, 8, 8, cloth_dark)
    c.r(34, 40, 8, 8, cloth_dark)
    if armor:
        c.r(13, 36, 22, 9, STEEL_D)
        c.r(16, 35, 16, 9, STEEL)
        c.h(18, 36, 10, STEEL_H)
        c.r(23, 35, 2, 10, INK)
    # neck
    c.r(20, 29, 8, 8, SKIN_D)
    c.r(21, 29, 6, 7, SKIN)
    # head, intentionally asymmetrical
    c.r(15, 11, 18, 20, INK)
    c.r(17, 12, 14, 18, SKIN)
    c.r(18, 12, 10, 4, SKIN_H)
    c.r(16, 20, 2, 7, SKIN_D)
    c.r(31, 19, 2, 7, SKIN_D)
    c.s(18, 20, INK); c.s(29, 20, INK)
    c.h(21, 26, 7, SKIN_D)
    c.s(28, 25, SKIN_H)
    # hair / hood
    if hood:
        c.r(12, 8, 24, 7, cloth_dark)
        c.r(14, 6, 20, 5, cloth)
        c.r(12, 12, 5, 15, cloth_dark)
        c.r(31, 12, 5, 15, cloth_dark)
    else:
        c.r(15, 9, 18, 6, hair)
        c.r(14, 12, 4, 10, hair)
        c.r(30, 11, 4, 8, hair)
        c.h(18, 9, 9, hair)
    if beard:
        c.r(17, 25, 14, 7, HAIR_RED if hair == HAIR_RED else hair)
        c.r(20, 31, 8, 4, hair)
        c.s(18, 29, SKIN_H)
    return c


def hero_portrait(kind):
    if kind == 0:  # warrior
        c = bust_base(RED_D, RED, HAIR_RED, beard=True, armor=True)
        c.r(10, 36, 5, 9, GOLD_D); c.r(33, 36, 5, 9, GOLD_D)
    elif kind == 1:  # mage
        c = bust_base(PURPLE_D, PURPLE, HAIR_DARK, hood=True)
        c.r(19, 36, 10, 8, BLUE_D); c.h(21, 37, 6, BLUE)
        c.s(24, 17, (132, 170, 186, 255))
    elif kind == 2:  # rogue
        c = bust_base(DEEP, (70, 66, 62, 255), HAIR_DARK, hood=True)
        c.r(15, 29, 18, 5, RED_D)
        c.s(18, 20, (185, 182, 151, 255)); c.s(29, 20, (185, 182, 151, 255))
    elif kind == 3:  # huntress
        c = bust_base(GREEN_D, GREEN, BROWN, hood=True)
        c.r(13, 8, 4, 16, BROWN_D)
        c.r(35, 9, 2, 24, BROWN_D)
        c.s(36, 8, CREAM); c.s(37, 7, CREAM)
    elif kind == 4:  # duelist
        c = bust_base(BLUE_D, BLUE, HAIR_BLOND, armor=True)
        c.r(31, 8, 3, 9, RED_D); c.r(34, 7, 2, 7, RED)
        c.h(17, 9, 9, HAIR_BLOND)
    else:  # cleric
        c = bust_base((104, 94, 70, 255), CREAM, HAIR_DARK, hood=True)
        c.r(22, 34, 4, 12, GOLD_D); c.r(23, 35, 2, 10, GOLD)
        c.h(19, 40, 10, WHITE)
    return c


def farmer_portrait(expr):
    c = bust_base(BROWN_D, GREEN_D, (138, 128, 108, 255), beard=True, armor=False)
    # straw hat makes silhouette immediately readable
    c.r(9, 7, 30, 4, BROWN_D)
    c.r(12, 4, 23, 5, BROWN)
    c.h(16, 3, 14, BROWN_H)
    c.r(14, 25, 20, 8, (125, 116, 99, 255))
    c.r(18, 31, 13, 5, (166, 157, 137, 255))
    # overwrite eyes/mouth per state
    for x in (18, 29): c.s(x, 20, INK)
    if expr == 1:  # warm
        c.h(21, 27, 7, SKIN_D); c.s(22, 28, SKIN_D); c.s(27, 28, SKIN_D)
    elif expr == 2:  # confused
        c.h(16, 17, 5, BROWN_D); c.h(27, 16, 5, BROWN_D)
        c.r(18, 20, 2, 2, INK); c.r(29, 20, 2, 2, INK)
        c.h(22, 28, 6, SKIN_D)
    elif expr == 3:  # fixated
        eye = (217, 202, 155, 255)
        c.r(17, 19, 4, 3, eye); c.r(28, 19, 4, 3, eye)
        c.s(19, 20, INK); c.s(30, 20, INK)
        c.h(21, 28, 8, RED_D)
    elif expr == 4:  # shaken
        c.h(16, 17, 5, (102, 91, 76, 255)); c.h(27, 17, 5, (102, 91, 76, 255))
        c.s(18, 21, INK); c.s(29, 21, INK)
        c.h(22, 29, 6, SKIN_D); c.h(24, 31, 3, SKIN_D)
    return c


def innkeeper_portrait(expr):
    c = bust_base(RED_D, (121, 67, 54, 255), HAIR_DARK, beard=False, armor=False)
    # older woman: swept auburn hair, cream blouse and vest
    c.r(13, 9, 22, 7, HAIR_RED)
    c.r(12, 12, 5, 13, HAIR_RED)
    c.r(31, 12, 5, 12, HAIR_RED)
    c.r(18, 34, 13, 14, CREAM)
    c.r(13, 35, 6, 13, RED_D); c.r(30, 35, 6, 13, RED_D)
    c.r(23, 35, 3, 12, GOLD_D)
    if expr == 1:  # attentive
        c.h(16, 17, 5, HAIR_DARK); c.h(27, 17, 5, HAIR_DARK)
        c.r(18, 20, 2, 2, INK); c.r(29, 20, 2, 2, INK)
        c.h(21, 28, 7, SKIN_D)
    elif expr == 2:  # serious
        c.h(16, 18, 5, HAIR_DARK); c.h(27, 18, 5, HAIR_DARK)
        c.s(18, 21, INK); c.s(29, 21, INK)
        c.h(21, 29, 8, RED_D)
    else:
        c.s(18, 20, INK); c.s(29, 20, INK)
        c.h(22, 28, 6, SKIN_D)
    return c


def make_portraits():
    frames = []
    for i in range(6): frames.append(hero_portrait(i))
    for i in range(5): frames.append(farmer_portrait(i))
    for i in range(3): frames.append(innkeeper_portrait(i))
    out = C(48 * len(frames), 48)
    for i, frame in enumerate(frames): out.blit(frame, i * 48, 0)
    return out


def innkeeper_frame(phase=0, walking=False):
    c = C(16, 16)
    # shoes and skirt
    c.h(5, 15, 6, INK)
    c.r(5 + (phase if walking else 0), 12, 2, 3, DEEP)
    c.r(9 - (phase if walking else 0), 12, 2, 3, DEEP)
    # burgundy vest / cream blouse
    c.r(4, 8, 8, 5, INK)
    c.r(5, 8, 6, 4, CREAM)
    c.r(5, 9, 2, 4, RED_D); c.r(9, 9, 2, 4, RED_D)
    c.s(8, 9, GOLD)
    # arms
    c.r(3, 9, 2, 3, RED_D); c.s(3, 12, SKIN)
    c.r(12, 9, 1, 3, RED_D); c.s(12, 12, SKIN)
    # head
    c.r(5, 3, 6, 5, INK)
    c.r(6, 4, 4, 4, SKIN)
    c.s(7, 5, INK); c.s(9, 5, INK); c.s(10, 6, SKIN_H)
    # swept auburn hair, deliberately asymmetric
    c.h(5, 3, 6, HAIR_RED); c.h(6, 2, 5, HAIR_RED)
    c.r(4, 4, 2, 4, HAIR_RED); c.r(10, 3, 2, 5, HAIR_RED)
    c.s(11, 2, HAIR_RED)
    return c


def make_innkeeper():
    out = C(64, 16)
    frames = [innkeeper_frame(0, False), innkeeper_frame(0, False),
              innkeeper_frame(-1, True), innkeeper_frame(1, True)]
    # subtle idle change on frame 1
    frames[1].s(11, 2, BROWN_H)
    for i, frame in enumerate(frames): out.blit(frame, i * 16, 0)
    return out


portraits = make_portraits()
innkeeper = make_innkeeper()
print("echoes_dialogue_portraits_v1.png", write(INTERFACES / "echoes_dialogue_portraits_v1.png", portraits.w, portraits.h, portraits.p))
print("echoes_innkeeper_v1.png", write(SPRITES / "echoes_innkeeper_v1.png", innkeeper.w, innkeeper.h, innkeeper.p))
