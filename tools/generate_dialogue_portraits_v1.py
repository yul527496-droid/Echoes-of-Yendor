#!/usr/bin/env python3
"""Generate Echoes Chapter 1 production dialogue portraits.

The runtime atlas is generated deterministically during CI so the Java portrait
contract cannot silently point at a missing binary asset. Frames are native
48x48 RGBA pixel art with no filtering/anti-aliasing.

Atlas order (26 frames, one horizontal row):
  heroes: Warrior/Mage/Rogue/Huntress/Duelist/Cleric x neutral/alert/concerned
  farmer: neutral/warm/confused/fixated/shaken
  innkeeper: neutral/attentive/serious
"""
from pathlib import Path
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "core/src/main/assets/interfaces/echoes/echoes_dialogue_portraits_v2.png"
W = H = 48
FRAMES = 26

# RGBA helpers ----------------------------------------------------------------
def rgba(hex_rgb, a=255):
    return ((hex_rgb >> 16) & 255, (hex_rgb >> 8) & 255, hex_rgb & 255, a)

TRANSPARENT = (0, 0, 0, 0)

def canvas():
    return [[TRANSPARENT for _ in range(W)] for _ in range(H)]

def put(px, x, y, c):
    if 0 <= x < W and 0 <= y < H:
        px[y][x] = c

def rect(px, x, y, w, h, c):
    for yy in range(y, y+h):
        for xx in range(x, x+w):
            put(px, xx, yy, c)

def line_h(px, x, y, w, c): rect(px, x, y, w, 1, c)
def line_v(px, x, y, h, c): rect(px, x, y, 1, h, c)

def poly_fill(px, points, c):
    # Small scanline polygon filler, integer-only for crisp pixel clusters.
    ys = [p[1] for p in points]
    for y in range(max(0, min(ys)), min(H-1, max(ys)) + 1):
        xs = []
        for i in range(len(points)):
            x1, y1 = points[i]
            x2, y2 = points[(i+1) % len(points)]
            if y1 == y2: continue
            lo, hi = sorted((y1, y2))
            if lo <= y < hi:
                x = x1 + (y-y1) * (x2-x1) / (y2-y1)
                xs.append(x)
        xs.sort()
        for i in range(0, len(xs)-1, 2):
            xa = max(0, int(xs[i] + 0.9999))
            xb = min(W-1, int(xs[i+1]))
            for x in range(xa, xb+1): put(px, x, y, c)

def mirror_x(px):
    return [list(reversed(row)) for row in px]

# Shared portrait construction -------------------------------------------------
SKIN = rgba(0xD9A47E)
SKIN_HI = rgba(0xF0C39B)
SKIN_SH = rgba(0xA8674F)
SKIN_DK = rgba(0x6F433A)
WHITE = rgba(0xF2EAD9)
EYE = rgba(0x17191D)
OUTLINE = rgba(0x292427)
SHADOW = rgba(0x15171B)

class Style:
    def __init__(self, hair, hair_hi, cloth, cloth_hi, accent, skin=SKIN, skin_hi=SKIN_HI, skin_sh=SKIN_SH):
        self.hair = rgba(hair); self.hair_hi = rgba(hair_hi)
        self.cloth = rgba(cloth); self.cloth_hi = rgba(cloth_hi); self.accent = rgba(accent)
        self.skin = skin; self.skin_hi = skin_hi; self.skin_sh = skin_sh

STYLES = {
    "warrior": Style(0x8E382E, 0xC65C3D, 0x556A78, 0x8197A3, 0xC09B55),
    "mage": Style(0xD7D4CC, 0xF6F2E9, 0x4D3F78, 0x7765A8, 0x6BA7D6),
    "rogue": Style(0x2D2D33, 0x4C4B55, 0x3D4E49, 0x607369, 0xB89B55),
    "huntress": Style(0xC9A562, 0xE6CB83, 0x3E6261, 0x5E8982, 0x9B6B45),
    "duelist": Style(0x5A423B, 0x866259, 0x5F3D65, 0x8A5C91, 0xD2A759),
    "cleric": Style(0x6D4A32, 0x9B6A48, 0xE5DED0, 0xFFF6E3, 0xD0A43C),
    "farmer": Style(0x67503B, 0x8C6C4A, 0x65734A, 0x8A9660, 0xA87442),
    "innkeeper": Style(0x704239, 0xA45B46, 0x8D493D, 0xB86755, 0xE0C49A),
}

def face(px, s, expr="neutral", hood=False, hat=False, female=False, beard=False):
    # shoulders / torso
    poly_fill(px, [(8,47),(11,38),(17,34),(31,34),(37,38),(40,47)], OUTLINE)
    poly_fill(px, [(10,47),(13,39),(19,35),(29,35),(35,39),(38,47)], s.cloth)
    rect(px, 14, 41, 20, 6, s.cloth_hi)
    rect(px, 22, 35, 4, 8, s.accent)

    # neck
    rect(px, 20, 31, 8, 7, s.skin_sh)
    rect(px, 21, 31, 6, 6, s.skin)

    # head silhouette
    poly_fill(px, [(14,11),(18,7),(30,7),(34,11),(36,20),(33,30),(28,34),(20,34),(15,29),(12,20)], OUTLINE)
    poly_fill(px, [(16,12),(19,9),(29,9),(32,12),(34,20),(31,29),(27,32),(21,32),(17,28),(14,20)], s.skin)
    rect(px, 18, 13, 12, 6, s.skin_hi)
    rect(px, 15, 21, 3, 5, s.skin_sh)
    rect(px, 30, 21, 3, 5, s.skin_sh)

    # ears
    rect(px, 12, 19, 3, 6, OUTLINE); rect(px, 13, 20, 2, 4, s.skin_sh)
    rect(px, 33, 19, 3, 6, OUTLINE); rect(px, 33, 20, 2, 4, s.skin_sh)

    # hair mass
    if hood:
        poly_fill(px, [(10,18),(12,9),(18,4),(30,4),(36,9),(38,18),(35,16),(32,10),(28,7),(20,7),(16,10),(13,16)], s.hair)
        rect(px, 11, 15, 4, 15, s.hair)
        rect(px, 33, 15, 4, 15, s.hair)
        rect(px, 13, 8, 22, 3, s.hair_hi)
    else:
        poly_fill(px, [(14,14),(15,9),(20,5),(29,6),(34,11),(34,17),(30,14),(28,10),(23,12),(19,10),(17,15)], s.hair)
        rect(px, 16, 9, 8, 3, s.hair_hi)
        if female:
            rect(px, 13, 16, 3, 13, s.hair)
            rect(px, 32, 15, 3, 15, s.hair)

    if hat:
        rect(px, 10, 8, 28, 4, OUTLINE)
        rect(px, 12, 7, 24, 4, s.hair)
        poly_fill(px, [(16,7),(18,2),(30,2),(33,7)], s.hair)
        rect(px, 19, 3, 11, 2, s.hair_hi)

    # brows/eyes by expression
    if expr in ("alert", "confused", "fixated"):
        line_h(px, 17, 19, 5, OUTLINE); line_h(px, 27, 18 if expr == "confused" else 19, 5, OUTLINE)
    elif expr in ("concerned", "shaken", "serious"):
        line_h(px, 17, 18, 4, OUTLINE); line_h(px, 28, 18, 4, OUTLINE)
        put(px,17,19,OUTLINE); put(px,31,19,OUTLINE)
    else:
        line_h(px, 17, 19, 5, OUTLINE); line_h(px, 27, 19, 5, OUTLINE)

    # eyes
    if expr == "fixated":
        rect(px, 18, 21, 3, 3, WHITE); rect(px, 28, 21, 3, 3, WHITE)
        put(px,19,22,s.accent); put(px,29,22,s.accent)
    else:
        rect(px, 18, 21, 3, 2, WHITE); rect(px, 28, 21, 3, 2, WHITE)
        put(px,19,22,EYE); put(px,29,22,EYE)

    # nose
    rect(px, 23, 22, 2, 5, s.skin_sh); put(px,25,26,s.skin_sh)

    # beard / mouth
    if beard:
        poly_fill(px, [(17,26),(20,29),(28,29),(32,26),(30,34),(26,37),(21,36),(18,33)], s.hair)
        rect(px, 20, 28, 9, 2, s.hair_hi)
        if expr == "warm": line_h(px,22,30,5,SKIN_DK)
        elif expr in ("shaken","concerned"): line_h(px,22,31,5,SKIN_DK); put(px,21,30,SKIN_DK)
        else: line_h(px,22,30,5,SKIN_DK)
    else:
        if expr == "warm":
            line_h(px, 21, 28, 7, SKIN_DK); put(px,22,29,SKIN_DK); put(px,27,29,SKIN_DK)
        elif expr in ("shaken","concerned","serious"):
            line_h(px, 21, 30, 7, SKIN_DK); put(px,21,29,SKIN_DK); put(px,27,29,SKIN_DK)
        elif expr == "fixated":
            line_h(px, 21, 29, 7, rgba(0x5C2D35)); rect(px,23,30,3,2,rgba(0x8B4A52))
        else:
            line_h(px, 22, 29, 5, SKIN_DK)

    # expression accents
    if expr == "alert":
        put(px,15,17,s.skin_hi); put(px,33,17,s.skin_hi)
    elif expr == "confused":
        rect(px, 36, 9, 2, 2, s.accent); rect(px, 38, 7, 2, 2, s.accent); put(px,37,12,s.accent)
    elif expr == "concerned":
        put(px,15,25,rgba(0x6BA7D6)); put(px,34,25,rgba(0x6BA7D6))
    elif expr == "shaken":
        rect(px, 10, 18, 2, 5, rgba(0x6BA7D6)); rect(px, 36, 16, 2, 6, rgba(0x6BA7D6))
        put(px,11,24,rgba(0x9ED0EA)); put(px,37,23,rgba(0x9ED0EA))
    elif expr == "fixated":
        put(px,16,22,s.accent); put(px,32,22,s.accent)
        rect(px, 11, 12, 2, 10, rgba(0x6D365E)); rect(px, 35, 12, 2, 10, rgba(0x6D365E))


def hero_frame(kind, expr):
    px = canvas(); s = STYLES[kind]
    if kind == "warrior":
        face(px,s,expr,beard=False)
        rect(px,10,39,5,8,rgba(0x7D8890)); rect(px,33,39,5,8,rgba(0x7D8890))
        rect(px,17,36,14,3,rgba(0xA99567))
    elif kind == "mage":
        face(px,s,expr,hood=True,beard=True)
        poly_fill(px,[(13,12),(17,3),(24,0),(31,3),(35,12)],s.cloth)
        rect(px,18,4,12,2,s.cloth_hi)
    elif kind == "rogue":
        face(px,s,expr,hood=True)
        rect(px,14,25,20,4,rgba(0x26312F))
        rect(px,18,22,3,1,s.accent); rect(px,28,22,3,1,s.accent)
    elif kind == "huntress":
        face(px,s,expr,hood=True,female=True)
        rect(px,9,41,4,7,rgba(0x765A39)); line_v(px,11,35,10,rgba(0xB78A55))
    elif kind == "duelist":
        face(px,s,expr,female=True)
        rect(px,13,13,3,12,s.accent); rect(px,32,13,3,12,s.accent)
        rect(px,35,37,2,10,rgba(0xB8BFC4)); put(px,36,35,rgba(0xE4E8EB))
    elif kind == "cleric":
        face(px,s,expr)
        rect(px,15,7,18,3,rgba(0xEEE5D0)); put(px,24,5,s.accent)
        rect(px,19,38,10,8,rgba(0xF4E8C5)); rect(px,22,39,4,5,s.accent)
    return px


def farmer_frame(expr):
    px=canvas(); s=STYLES["farmer"]
    face(px,s,expr,hat=True,beard=True)
    rect(px,12,40,5,7,rgba(0x79583B)); rect(px,31,40,5,7,rgba(0x79583B))
    if expr == "fixated":
        # unnaturally rigid, brighter eyes and violet contamination edge
        line_h(px,17,18,5,rgba(0x4A263F)); line_h(px,27,18,5,rgba(0x4A263F))
    return px


def innkeeper_frame(expr):
    px=canvas(); s=STYLES["innkeeper"]
    face(px,s,expr,female=True)
    # white apron / blouse and tied hair
    rect(px,17,38,14,9,rgba(0xE8DFCF)); rect(px,20,39,8,8,rgba(0xF8F0DE))
    rect(px,11,14,4,7,s.hair); rect(px,34,13,4,8,s.hair)
    rect(px,34,8,5,5,s.hair); put(px,38,9,s.hair_hi)
    if expr == "attentive":
        put(px,16,17,s.skin_hi); put(px,32,17,s.skin_hi)
    return px

# PNG writer ------------------------------------------------------------------
def write_png(frames):
    width = W * len(frames)
    height = H
    raw = bytearray()
    for y in range(height):
        raw.append(0)  # filter none
        for frame in frames:
            for r,g,b,a in frame[y]:
                raw.extend((r,g,b,a))
    def chunk(kind, payload):
        return struct.pack(">I", len(payload)) + kind + payload + struct.pack(">I", zlib.crc32(kind+payload) & 0xffffffff)
    png = bytearray(b"\x89PNG\r\n\x1a\n")
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(bytes(raw), 9))
    png += chunk(b"IEND", b"")
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_bytes(png)
    print(f"Generated {OUT.relative_to(ROOT)} ({width}x{height}, {len(frames)} frames)")

frames=[]
for hero in ("warrior","mage","rogue","huntress","duelist","cleric"):
    for expr in ("neutral","alert","concerned"):
        frames.append(hero_frame(hero,expr))
for expr in ("neutral","warm","confused","fixated","shaken"):
    frames.append(farmer_frame(expr))
for expr in ("neutral","attentive","serious"):
    frames.append(innkeeper_frame(expr))

assert len(frames) == FRAMES
write_png(frames)
