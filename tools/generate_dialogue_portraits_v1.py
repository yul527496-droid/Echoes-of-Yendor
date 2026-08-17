#!/usr/bin/env python3
"""Deterministically generate the Chapter 1 production dialogue portrait atlas.

26 native 48x48 RGBA frames, one horizontal row:
- Warrior/Mage/Rogue/Huntress/Duelist/Cleric x neutral/alert/concerned
- Farmer x neutral/warm/confused/fixated/shaken
- Innkeeper x neutral/attentive/serious

The portraits are authored as dedicated Echoes pixel art rather than crops of SPD splash art.
"""
from pathlib import Path
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "core/src/main/assets/interfaces/echoes/echoes_dialogue_portraits_v2.png"
W = H = 48
FRAMES = 26
T = (0, 0, 0, 0)


def C(n, a=255):
    return ((n >> 16) & 255, (n >> 8) & 255, n & 255, a)


def cv():
    return [[T for _ in range(W)] for _ in range(H)]


def p(px, x, y, c):
    if 0 <= x < W and 0 <= y < H:
        px[y][x] = c


def rect(px, x, y, w, h, c):
    for yy in range(y, y + h):
        for xx in range(x, x + w):
            p(px, xx, yy, c)


def hline(px, x, y, w, c):
    rect(px, x, y, w, 1, c)


def vline(px, x, y, h, c):
    rect(px, x, y, 1, h, c)


def poly(px, pts, c):
    ys = [q[1] for q in pts]
    for y in range(max(0, min(ys)), min(H - 1, max(ys)) + 1):
        xs = []
        for i, (x1, y1) in enumerate(pts):
            x2, y2 = pts[(i + 1) % len(pts)]
            if y1 == y2:
                continue
            lo, hi = sorted((y1, y2))
            if lo <= y < hi:
                xs.append(x1 + (y - y1) * (x2 - x1) / (y2 - y1))
        xs.sort()
        for i in range(0, len(xs) - 1, 2):
            xa = max(0, int(xs[i] + .999))
            xb = min(W - 1, int(xs[i + 1]))
            for x in range(xa, xb + 1):
                p(px, x, y, c)


OUTLINE = C(0x241F22)
EYE = C(0x111419)
WHITE = C(0xF5EEDD)
METAL = C(0xAAB4BA)
METAL_HI = C(0xE1E6E6)
SKIN = C(0xD9A47E)
SKIN_HI = C(0xF0C39B)
SKIN_SH = C(0xA8674F)
SKIN_DK = C(0x6F433A)
SKIN_DARK = C(0x8F5E4D)
SKIN_DARK_HI = C(0xB97A64)
SKIN_DARK_SH = C(0x633D38)
GRAY_HI = C(0xB8B1AA)
GRAY_DK = C(0x4A4546)


class Style:
    def __init__(self, hair, hair_hi, cloth, cloth_hi, accent,
                 skin=SKIN, skin_hi=SKIN_HI, skin_sh=SKIN_SH):
        self.h = C(hair)
        self.hh = C(hair_hi)
        self.c = C(cloth)
        self.ch = C(cloth_hi)
        self.a = C(accent)
        self.s = skin
        self.sh = skin_hi
        self.ss = skin_sh


ST = {
    "warrior": Style(0x9E3E2F, 0xD26042, 0x526675, 0x8196A2, 0xC49A4C),
    "mage": Style(0xB9B8B2, 0xEEECE5, 0x49396F, 0x715A9B, 0x6BA8D4),
    "rogue": Style(0x24262A, 0x464A4F, 0x334541, 0x52665E, 0x74A064),
    "huntress": Style(0xC99B4C, 0xF0CB72, 0x315C6A, 0x4C8190, 0xA86C3A),
    "duelist": Style(0x30252B, 0x5D4652, 0x55345E, 0x80518B, 0xD4A653,
                     SKIN_DARK, SKIN_DARK_HI, SKIN_DARK_SH),
    "cleric": Style(0x704A32, 0xA36B45, 0xD8D6D1, 0xF4EFE4, 0xD3A637),
    "farmer": Style(0x8D744D, 0xC3A46A, 0x586D43, 0x7E925E, 0xA76C3D),
    "innkeeper": Style(0x733C32, 0xA85441, 0x824035, 0xAD5D4B, 0xE1C59A),
}


def shoulders(px, s, armor=False, apron=False, scarf=False):
    poly(px, [(6, 47), (10, 38), (17, 33), (31, 33), (38, 38), (42, 47)], OUTLINE)
    poly(px, [(9, 47), (12, 39), (18, 35), (30, 35), (36, 39), (39, 47)], s.c)
    if armor:
        rect(px, 10, 39, 7, 7, METAL)
        rect(px, 31, 39, 7, 7, METAL)
        rect(px, 11, 39, 5, 2, METAL_HI)
        rect(px, 32, 39, 5, 2, METAL_HI)
    if scarf:
        poly(px, [(17, 34), (31, 34), (29, 41), (24, 38), (19, 41)], s.a)
        rect(px, 21, 36, 7, 2, s.ch)
    if apron:
        poly(px, [(17, 36), (31, 36), (34, 47), (14, 47)], C(0xE8DFCF))
        poly(px, [(19, 38), (29, 38), (31, 47), (17, 47)], C(0xFFF5E2))
        rect(px, 22, 37, 4, 7, s.a)


def head(px, s, expr="neutral", female=False, older=False, beard=None,
         hood=False, mask=False, hat=False, scar=False):
    rect(px, 20, 31, 8, 7, s.ss)
    rect(px, 21, 31, 6, 6, s.s)

    poly(px, [(13, 12), (17, 7), (30, 7), (35, 12), (36, 21),
              (33, 29), (28, 34), (20, 34), (15, 29), (12, 21)], OUTLINE)
    poly(px, [(15, 13), (18, 9), (29, 9), (33, 13), (34, 21),
              (31, 28), (27, 32), (21, 32), (17, 28), (14, 21)], s.s)
    rect(px, 18, 12, 12, 5, s.sh)
    rect(px, 15, 22, 2, 5, s.ss)
    rect(px, 31, 22, 2, 5, s.ss)

    rect(px, 11, 19, 3, 6, OUTLINE)
    rect(px, 12, 20, 2, 4, s.ss)
    rect(px, 34, 19, 3, 6, OUTLINE)
    rect(px, 34, 20, 2, 4, s.ss)

    if hood:
        poly(px, [(9, 20), (11, 10), (17, 4), (30, 4), (37, 10), (39, 20),
                  (35, 18), (32, 11), (28, 8), (20, 8), (16, 11), (13, 18)], s.c)
        rect(px, 10, 16, 4, 15, s.c)
        rect(px, 34, 16, 4, 15, s.c)
        hline(px, 13, 9, 22, s.ch)
        if female:
            rect(px, 18, 9, 10, 3, s.h)
            rect(px, 18, 10, 4, 4, s.hh)
    else:
        poly(px, [(14, 15), (15, 9), (20, 5), (29, 6), (34, 11), (34, 17),
                  (30, 14), (28, 10), (24, 12), (20, 10), (17, 15)], s.h)
        rect(px, 17, 8, 8, 3, s.hh)
        if female:
            rect(px, 13, 15, 3, 14, s.h)
            rect(px, 32, 15, 3, 14, s.h)

    if hat:
        # Farmer's broad straw hat is intentionally much wider than the head silhouette.
        rect(px, 7, 9, 34, 4, OUTLINE)
        rect(px, 9, 8, 30, 4, s.h)
        poly(px, [(14, 8), (17, 2), (31, 2), (34, 8)], s.h)
        rect(px, 18, 3, 12, 2, s.hh)
        rect(px, 12, 8, 24, 2, C(0x6C4D31))

    if expr in ("alert", "fixated"):
        hline(px, 16, 18, 6, OUTLINE)
        hline(px, 27, 18, 6, OUTLINE)
        p(px, 16, 17, OUTLINE)
        p(px, 32, 17, OUTLINE)
    elif expr == "confused":
        hline(px, 16, 19, 6, OUTLINE)
        hline(px, 28, 17, 5, OUTLINE)
        p(px, 28, 18, OUTLINE)
    elif expr in ("concerned", "shaken", "serious"):
        hline(px, 17, 18, 5, OUTLINE)
        hline(px, 27, 18, 5, OUTLINE)
        p(px, 17, 19, OUTLINE)
        p(px, 31, 19, OUTLINE)
    elif expr in ("warm", "attentive"):
        hline(px, 17, 19, 5, OUTLINE)
        hline(px, 27, 19, 5, OUTLINE)
        p(px, 18, 18, OUTLINE)
        p(px, 30, 18, OUTLINE)
    else:
        hline(px, 17, 19, 5, OUTLINE)
        hline(px, 27, 19, 5, OUTLINE)

    if expr == "fixated":
        rect(px, 18, 21, 3, 3, WHITE)
        rect(px, 28, 21, 3, 3, WHITE)
        p(px, 19, 22, C(0x9B54A4))
        p(px, 29, 22, C(0x9B54A4))
        p(px, 18, 23, C(0x6C365F))
        p(px, 30, 23, C(0x6C365F))
    else:
        rect(px, 18, 21, 3, 2, WHITE)
        rect(px, 28, 21, 3, 2, WHITE)
        p(px, 19, 22, EYE)
        p(px, 29, 22, EYE)

    rect(px, 23, 22, 2, 5, s.ss)
    p(px, 25, 26, s.ss)
    if older:
        p(px, 16, 24, s.ss)
        p(px, 32, 24, s.ss)
        hline(px, 20, 17, 3, s.ss)
        hline(px, 26, 17, 3, s.ss)
    if scar:
        for x, y in ((31, 14), (30, 15), (29, 16), (28, 17)):
            p(px, x, y, C(0x8D5148))

    if beard:
        bc, bh = beard
        poly(px, [(16, 26), (19, 29), (29, 29), (33, 26),
                  (31, 34), (27, 37), (21, 36), (17, 33)], bc)
        rect(px, 19, 27, 11, 3, bh)
        p(px, 18, 29, bh)
        p(px, 30, 29, bh)
        mouth_y = 31 if expr in ("concerned", "shaken", "serious") else 30
        hline(px, 22, mouth_y, 5, SKIN_DK)
        if expr == "warm":
            p(px, 22, 31, SKIN_DK)
            p(px, 26, 31, SKIN_DK)
    elif mask:
        poly(px, [(14, 25), (18, 23), (30, 23), (34, 25), (32, 32), (16, 32)], C(0x334D3A))
        hline(px, 17, 25, 14, C(0x58705A))
        hline(px, 20, 29, 8, C(0x24352B))
    else:
        if expr == "warm":
            hline(px, 21, 28, 7, SKIN_DK)
            p(px, 22, 29, SKIN_DK)
            p(px, 27, 29, SKIN_DK)
        elif expr in ("concerned", "shaken", "serious"):
            hline(px, 21, 30, 7, SKIN_DK)
            p(px, 21, 29, SKIN_DK)
            p(px, 27, 29, SKIN_DK)
        elif expr == "alert":
            hline(px, 21, 29, 7, SKIN_DK)
        elif expr == "fixated":
            hline(px, 21, 29, 7, C(0x5A2B32))
            rect(px, 23, 30, 3, 2, C(0x87434D))
        else:
            hline(px, 22, 29, 5, SKIN_DK)

    if expr == "confused":
        rect(px, 37, 8, 2, 2, s.a)
        rect(px, 39, 6, 2, 2, s.a)
        p(px, 38, 11, s.a)
    elif expr in ("concerned", "shaken"):
        rect(px, 10, 19, 2, 4, C(0x6BA9D1))
        p(px, 11, 24, C(0xA5D8EC))
        if expr == "shaken":
            rect(px, 37, 16, 2, 6, C(0x6BA9D1))
            p(px, 38, 23, C(0xA5D8EC))
    elif expr == "fixated":
        rect(px, 9, 14, 2, 9, C(0x65365E))
        rect(px, 37, 14, 2, 9, C(0x65365E))
        p(px, 11, 16, C(0x9B54A4))
        p(px, 36, 17, C(0x9B54A4))


def hero(kind, expr):
    px = cv()
    s = ST[kind]
    if kind == "warrior":
        shoulders(px, s, armor=True)
        head(px, s, expr, beard=(C(0x8E382E), C(0xC55B3E)), scar=True)
        rect(px, 18, 36, 12, 3, C(0xA77C46))
        p(px, 24, 37, C(0xE0B75B))
    elif kind == "mage":
        shoulders(px, s, scarf=True)
        head(px, s, expr, older=True, beard=(C(0x8D8C89), C(0xD9D7D1)))
        rect(px, 13, 14, 3, 11, s.h)
        rect(px, 33, 14, 3, 11, s.h)
        rect(px, 22, 35, 4, 3, s.a)
        p(px, 24, 34, C(0xA9D8EF))
    elif kind == "rogue":
        shoulders(px, s, scarf=True)
        head(px, s, expr, hood=True, mask=True)
        rect(px, 9, 40, 3, 7, C(0x232B29))
        rect(px, 36, 40, 3, 7, C(0x232B29))
        p(px, 11, 39, s.a)
    elif kind == "huntress":
        shoulders(px, s)
        head(px, s, expr, female=True, hood=True)
        rect(px, 14, 15, 3, 14, s.h)
        rect(px, 31, 14, 3, 14, s.h)
        rect(px, 15, 15, 2, 7, s.hh)
        vline(px, 10, 35, 12, C(0x8A6038))
        p(px, 9, 35, C(0xC09054))
    elif kind == "duelist":
        shoulders(px, s, scarf=True)
        head(px, s, expr, female=True)
        rect(px, 14, 12, 20, 2, s.a)
        rect(px, 33, 13, 3, 8, s.a)
        rect(px, 36, 37, 2, 10, METAL)
        p(px, 37, 35, METAL_HI)
        hline(px, 33, 40, 7, C(0xD4A653))
    elif kind == "cleric":
        shoulders(px, s, armor=True)
        head(px, s, expr)
        poly(px, [(15, 35), (33, 35), (31, 42), (17, 42)], C(0xF1EBDD))
        rect(px, 22, 36, 4, 7, s.a)
        p(px, 24, 35, C(0xF7D873))
        hline(px, 19, 44, 10, C(0xB78A2F))
    return px


def farmer(expr):
    px = cv()
    s = ST["farmer"]
    shoulders(px, s)
    rect(px, 15, 37, 4, 10, C(0x75583B))
    rect(px, 29, 37, 4, 10, C(0x75583B))
    rect(px, 18, 42, 12, 5, C(0x6B543C))
    head(px, s, expr, older=True, hat=True, beard=(GRAY_DK, GRAY_HI))
    hline(px, 9, 9, 30, C(0xC8A767))
    p(px, 16, 25, C(0x8F5B4D))
    p(px, 32, 25, C(0x8F5B4D))
    return px


def innkeeper(expr):
    px = cv()
    s = ST["innkeeper"]
    shoulders(px, s, apron=True)
    head(px, s, expr, female=True)
    rect(px, 34, 7, 6, 6, OUTLINE)
    rect(px, 35, 8, 5, 5, s.h)
    p(px, 38, 8, s.hh)
    poly(px, [(18, 35), (24, 39), (30, 35), (29, 38), (24, 42), (19, 38)], C(0xF3E7D5))
    p(px, 24, 39, s.a)
    if expr == "attentive":
        p(px, 17, 18, s.sh)
        p(px, 31, 18, s.sh)
    return px


frames = []
for kind in ("warrior", "mage", "rogue", "huntress", "duelist", "cleric"):
    for expr in ("neutral", "alert", "concerned"):
        frames.append(hero(kind, expr))
for expr in ("neutral", "warm", "confused", "fixated", "shaken"):
    frames.append(farmer(expr))
for expr in ("neutral", "attentive", "serious"):
    frames.append(innkeeper(expr))
assert len(frames) == FRAMES

raw = bytearray()
width = W * FRAMES
for y in range(H):
    raw.append(0)
    for frame in frames:
        for pixel in frame[y]:
            raw.extend(pixel)


def chunk(kind, payload):
    return (struct.pack(">I", len(payload)) + kind + payload
            + struct.pack(">I", zlib.crc32(kind + payload) & 0xffffffff))


png = bytearray(b"\x89PNG\r\n\x1a\n")
png += chunk(b"IHDR", struct.pack(">IIBBBBB", width, H, 8, 6, 0, 0, 0))
png += chunk(b"IDAT", zlib.compress(bytes(raw), 9))
png += chunk(b"IEND", b"")
OUT.parent.mkdir(parents=True, exist_ok=True)
OUT.write_bytes(png)
print(f"Generated {OUT.relative_to(ROOT)} ({width}x{H}, {FRAMES} frames)")
