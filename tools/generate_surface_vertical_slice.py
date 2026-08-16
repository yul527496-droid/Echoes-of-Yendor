#!/usr/bin/env python3
"""Generate Chapter 1 Surface Entrance vertical-slice pixel art.

Project-owned deterministic pixel art. Python stdlib only. Every runtime asset is
expressed as editable pixel operations on SPD's 16px tile grid; there is no embedded
base64 art blob and no interpolation/resampling step.
"""
from pathlib import Path
import hashlib
import struct
import zlib

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "core/src/main/assets/environment/echoes/ch1_surface"
PREVIEW = ROOT / "artifacts/ch1_surface_vertical_slice_preview.png"
OUT.mkdir(parents=True, exist_ok=True)
T = 16

P = {
    "ink": (31, 42, 30, 255), "deep": (38, 55, 37, 255),
    "leaf0": (48, 73, 43, 255), "leaf1": (61, 91, 49, 255),
    "leaf2": (78, 108, 58, 255), "leaf3": (105, 130, 68, 255),
    "grass_dark": (74, 105, 55, 255), "grass_hi": (121, 148, 76, 255),
    "dry": (151, 129, 76, 255), "flower": (199, 183, 105, 255),
    "soil0": (81, 59, 39, 255), "soil1": (105, 75, 46, 255),
    "soil2": (132, 95, 56, 255), "soil3": (159, 122, 69, 255),
    "stone0": (59, 63, 51, 255), "stone1": (82, 86, 68, 255),
    "stone2": (109, 111, 86, 255), "moss": (74, 98, 53, 255),
    "water0": (49, 104, 113, 255), "water1": (62, 126, 136, 255),
    "water2": (91, 153, 157, 255), "foam": (141, 184, 178, 255),
    "wood0": (63, 43, 29, 255), "wood1": (91, 61, 36, 255),
    "wood2": (126, 85, 47, 255), "wood3": (166, 119, 66, 255),
    "cloth0": (71, 55, 43, 255), "cloth1": (116, 85, 57, 255),
    "cloth2": (154, 112, 70, 255), "ember": (216, 116, 45, 255),
    "fire": (236, 177, 64, 255),
}

PNG_SIG = b"\x89PNG\r\n\x1a\n"

def chunk(kind, payload):
    crc = zlib.crc32(kind)
    crc = zlib.crc32(payload, crc) & 0xFFFFFFFF
    return struct.pack(">I", len(payload)) + kind + payload + struct.pack(">I", crc)

def write_png(path, w, h, px):
    raw = bytearray()
    stride = w * 4
    for y in range(h):
        raw.append(0)
        raw.extend(px[y * stride:(y + 1) * stride])
    ihdr = struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0)
    data = PNG_SIG + chunk(b"IHDR", ihdr) + chunk(b"IDAT", zlib.compress(bytes(raw), 9)) + chunk(b"IEND", b"")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(data)
    return hashlib.sha256(data).hexdigest()

class Canvas:
    def __init__(self, w, h, bg=(0, 0, 0, 0)):
        self.w, self.h = w, h
        self.p = bytearray(bg * (w * h))
    def set(self, x, y, color):
        if 0 <= x < self.w and 0 <= y < self.h:
            i = (x + y * self.w) * 4
            self.p[i:i + 4] = bytes(color)
    def rect(self, x, y, w, h, color):
        for yy in range(y, y + h):
            for xx in range(x, x + w): self.set(xx, yy, color)
    def hline(self, x, y, n, color):
        for i in range(n): self.set(x + i, y, color)
    def vline(self, x, y, n, color):
        for i in range(n): self.set(x, y + i, color)
    def blit(self, src, dx, dy):
        for y in range(src.h):
            for x in range(src.w):
                i = (x + y * src.w) * 4
                if src.p[i + 3]: self.set(dx + x, dy + y, tuple(src.p[i:i + 4]))

def grass_tuft(c, x, y, shade="grass_hi", wide=False):
    col = P[shade]
    c.set(x, y + 2, col); c.set(x + 2, y + 2, col); c.set(x + 1, y + 1, col)
    if wide: c.set(x + 3, y + 2, col); c.set(x + 2, y + 1, col)

def pebble(c, x, y, light=False):
    c.hline(x, y + 1, 3, P["stone0"])
    c.hline(x + 1, y, 2, P["stone2" if light else "stone1"])

def root_cluster(c, x, y):
    c.hline(x, y, 4, P["deep"]); c.set(x + 1, y - 1, P["deep"]); c.set(x + 3, y - 1, P["deep"])

def leaf_blob(c, x, y, variant=0):
    pts = [(2,0),(3,0),(5,0),(1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(0,2),(1,2),(2,2),(3,2),(4,2),(5,2),(6,2),(7,2),(0,3),(1,3),(2,3),(3,3),(4,3),(5,3),(6,3),(7,3),(1,4),(2,4),(3,4),(4,4),(5,4),(6,4),(1,5),(2,5),(3,5),(4,5),(5,5),(2,6),(3,6),(4,6)]
    for px, py in pts: c.set(x + px, y + py, P["leaf0"])
    mids = [(2,1),(3,1),(5,1),(1,2),(2,2),(4,2),(5,2),(6,2),(1,3),(3,3),(4,3),(6,3),(2,4),(3,4),(5,4),(3,5)]
    for px, py in mids: c.set(x + px, y + py, P["leaf1"])
    highs = [(3,1),(5,2),(2,2),(1,3),(4,3),(3,4)] if variant == 0 else [(2,1),(5,1),(1,2),(4,2),(6,3),(2,4)]
    for px, py in highs: c.set(x + px, y + py, P["leaf2"])
    if variant == 2: c.set(x + 3, y + 1, P["leaf3"]); c.set(x + 4, y + 2, P["leaf3"])

def tree_cluster(variant=0, deep=False):
    c = Canvas(32, 32)
    for sx, ln in [(2,7),(11,5),(20,8)]: c.hline(sx, 29, ln, P["ink"])
    for x, y in [(7,15),(21,13)]:
        c.rect(x, y, 3, 13, P["wood0"]); c.vline(x + 2, y + 1, 10, P["wood1"]); c.set(x - 1, y + 9, P["wood0"])
    blobs = [(1,6,variant),(7,2,(variant+1)%3),(14,6,(variant+2)%3),(20,3,variant)]
    for x, y, v in blobs: leaf_blob(c, x, y, v)
    if deep:
        c.rect(5, 20, 22, 5, P["deep"])
        for x in range(7, 25, 6): c.set(x, 21, P["leaf0"])
    else:
        grass_tuft(c, 3, 25, "grass_hi", True); grass_tuft(c, 23, 24, "grass_hi")
    return c

def make_grass():
    c = Canvas(128, 32)
    for i in range(8):
        x = i * 16
        if i == 0: grass_tuft(c,x+4,9); grass_tuft(c,x+10,5,"grass_dark")
        elif i == 1: grass_tuft(c,x+3,5,"grass_dark",True); grass_tuft(c,x+9,10)
        elif i == 2: c.hline(x+3,8,4,P["dry"]); c.hline(x+8,10,5,P["soil2"]); c.set(x+6,7,P["dry"])
        elif i == 3: c.hline(x+2,12,12,P["grass_dark"]); c.set(x+4,11,P["water1"]); c.set(x+10,11,P["water1"]); grass_tuft(c,x+7,8)
        elif i == 4: pebble(c,x+4,9); pebble(c,x+10,5,True)
        elif i == 5: grass_tuft(c,x+3,10); c.set(x+5,7,P["flower"]); grass_tuft(c,x+10,6,"grass_dark"); c.set(x+12,4,P["flower"])
        elif i == 6: c.hline(x+2,10,5,P["soil1"]); c.hline(x+8,7,6,P["soil2"]); c.set(x+4,9,P["soil3"]); c.set(x+11,6,P["soil3"])
        else: grass_tuft(c,x+2,6,"grass_dark"); grass_tuft(c,x+8,9,"grass_hi",True); pebble(c,x+11,3)
    for i in range(8):
        x, y = i * 16, 16
        if i % 2 == 0:
            c.hline(x+1,y+13,14,P["grass_dark"]); grass_tuft(c,x+4,y+8); grass_tuft(c,x+10,y+6)
        else:
            c.hline(x+2,y+2,12,P["grass_dark"]); grass_tuft(c,x+3,y+3); grass_tuft(c,x+10,y+7,"grass_dark")
    return c

def make_forest():
    c = Canvas(128, 64)
    c.blit(tree_cluster(0, False), 0, 0); c.blit(tree_cluster(1, False), 32, 0); c.blit(tree_cluster(2, True), 64, 0)
    leaf_blob(c,100,4,2); root_cluster(c,102,13)
    c.rect(117,6,5,7,P["wood0"]); c.hline(116,6,7,P["wood2"]); c.hline(117,7,5,P["wood1"]); root_cluster(c,115,13)
    c.hline(99,26,25,P["wood0"]); c.hline(101,24,20,P["wood2"]); c.hline(102,25,18,P["wood1"]); c.rect(101,23,3,3,P["wood3"]); c.rect(119,23,3,4,P["wood0"])
    grass_tuft(c,99,28); grass_tuft(c,121,27,"grass_dark")
    for base in (0,32,64):
        for ox in (3,10,19,26): grass_tuft(c,base+ox,36+(ox%3),"grass_hi" if ox%2 else "grass_dark",ox%3==0)
        c.hline(base+1,47,30,P["deep"])
    c.hline(100,43,22,P["stone0"]); c.rect(104,38,8,5,P["stone1"]); c.rect(111,40,9,3,P["stone2"]); c.hline(106,38,5,P["moss"]); c.hline(113,40,5,P["moss"])
    return c

def make_road():
    c = Canvas(128, 32)
    c.vline(1,1,14,P["grass_dark"]); c.vline(2,4,10,P["soil1"]); grass_tuft(c,3,8)
    x=16; c.vline(x+14,1,14,P["grass_dark"]); c.vline(x+13,4,10,P["soil1"]); grass_tuft(c,x+9,8)
    x=32; c.vline(x+5,2,12,P["soil0"]); c.vline(x+10,1,13,P["soil0"]); c.set(x+6,5,P["soil3"]); c.set(x+9,10,P["soil3"])
    x=48; pebble(c,x+3,9); pebble(c,x+10,4,True); c.set(x+8,12,P["stone0"])
    x=64; c.hline(x+3,9,10,P["soil0"]); c.hline(x+5,8,6,P["soil1"]); c.set(x+4,10,P["soil2"]); c.set(x+11,10,P["soil2"])
    x=80; c.hline(x+2,5,5,P["soil2"]); c.hline(x+8,10,6,P["soil1"]); c.hline(x+5,12,4,P["dry"])
    x=96
    for yy,xx in [(2,4),(4,5),(6,5),(8,6),(10,6),(12,7)]: c.hline(x+xx,yy,2,P["soil0"])
    x=112; grass_tuft(c,x+2,9,"grass_dark",True); grass_tuft(c,x+8,5); c.set(x+12,11,P["dry"])
    for i in range(8):
        x,y=i*16,16
        if i%2==0:
            for xx,yy in [(1,10),(2,10),(3,9),(5,11),(6,10),(9,12),(11,11)]: c.set(x+xx,y+yy,P["grass_dark"])
            c.hline(x+4,y+13,9,P["soil1"])
        else:
            c.hline(x+1,y+2,11,P["soil1"]); c.hline(x+4,y+3,10,P["grass_dark"]); grass_tuft(c,x+8,y+5)
    return c

def make_river():
    c=Canvas(128,32)
    x=0; c.hline(x+1,13,14,P["soil0"]); c.hline(x+2,12,12,P["moss"]); grass_tuft(c,x+4,7,"grass_dark"); grass_tuft(c,x+10,9)
    x=16; c.hline(x+1,2,14,P["soil0"]); c.hline(x+2,3,12,P["moss"]); grass_tuft(c,x+3,3); grass_tuft(c,x+10,5,"grass_dark")
    x=32
    for xx,hh in [(4,7),(7,10),(10,6)]: c.vline(x+xx,13-hh,hh,P["grass_dark"]); c.set(x+xx-1,8,P["grass_hi"])
    x=48; c.hline(x+2,6,6,P["water2"]); c.hline(x+9,10,5,P["foam"]); c.hline(x+4,12,4,P["water1"])
    x=64; pebble(c,x+2,9,True); pebble(c,x+7,5); pebble(c,x+11,11,True)
    x=80; c.hline(x+2,8,5,P["foam"]); c.hline(x+9,5,5,P["water2"]); c.set(x+7,11,P["foam"])
    x=96; c.hline(x+1,13,14,P["grass_dark"]); c.hline(x+3,12,10,P["water0"]); grass_tuft(c,x+5,8)
    x=112; c.hline(x+2,12,12,P["soil0"]); c.hline(x+4,11,8,P["wood0"]); c.vline(x+5,6,6,P["wood0"]); c.vline(x+10,8,4,P["wood0"])
    for i in range(8):
        x,y=i*16,16; c.hline(x+2,y+6+(i%3),5,P["water2"]); c.hline(x+9,y+11-(i%2),4,P["foam"])
        if i in (1,4,6): c.set(x+6,y+4,P["water1"]); c.set(x+7,y+4,P["water1"])
    return c

def make_bridge():
    c=Canvas(64,48)
    c.hline(3,43,58,P["ink"]); c.hline(7,44,49,P["ink"])
    c.rect(4,6,4,34,P["wood0"]); c.rect(56,6,4,34,P["wood0"])
    c.hline(3,7,8,P["wood2"]); c.hline(53,7,8,P["wood2"]); c.hline(3,34,8,P["wood2"]); c.hline(53,34,8,P["wood2"])
    widths=[7,8,7,9,7,8]; xx=9
    for i,w in enumerate(widths):
        top=10+(i%2); bottom=39-(1 if i in (1,4) else 0)
        c.rect(xx,top,w,bottom-top,P["wood1"]); c.vline(xx,top,bottom-top,P["wood0"]); c.vline(xx+w-1,top,bottom-top,P["wood0"])
        c.hline(xx+1,top+2,max(1,w-2),P["wood3"])
        if i%2==0: c.hline(xx+2,28,max(1,w-3),P["wood2"])
        xx += w
    for nx in (12,24,38,51): c.set(nx,13,P["ink"]); c.set(nx,35,P["ink"])
    grass_tuft(c,1,35,"grass_dark"); grass_tuft(c,58,36); c.hline(0,40,8,P["soil1"]); c.hline(56,40,8,P["soil1"])
    return c

def make_camp_ruin():
    c=Canvas(192,64)
    c.hline(3,55,25,P["ink"]); c.hline(34,55,20,P["ink"]); c.hline(62,56,27,P["ink"])
    for y in range(24,48):
        span=max(0,(y-24)//2); x0=10-span//2; x1=41+span//2
        if y<45: c.hline(max(4,x0),y,min(46,x1)-max(4,x0),P["cloth0"])
    for y in range(26,45):
        width=max(2,y-25); c.hline(21-width//2,y,width//2+2,P["cloth1"])
    for y in range(29,45,3): c.set(22,y,P["cloth2"])
    c.vline(22,24,26,P["wood2"]); c.set(21,23,P["wood3"]); c.rect(23,37,12,12,P["wood0"]); c.hline(25,37,8,P["ink"])
    for x,y in [(51,48),(55,46),(59,48),(55,51)]: pebble(c,x,y)
    c.set(55,46,P["ember"]); c.set(56,44,P["fire"]); c.set(54,45,P["fire"]); c.set(56,47,P["ember"])
    c.rect(66,42,15,11,P["wood0"]); c.rect(68,40,13,11,P["wood1"]); c.hline(69,41,11,P["wood3"]); c.vline(74,41,9,P["wood0"])
    c.rect(61,55,24,4,P["cloth0"]); c.hline(64,54,18,P["cloth2"]); c.hline(4,58,86,P["soil1"]); grass_tuft(c,6,51,"grass_dark"); grass_tuft(c,86,50)
    ox=96; c.hline(ox+5,57,83,P["ink"]); c.hline(ox+13,58,66,P["deep"])
    blocks=[(ox+12,34,12,17),(ox+21,23,13,14),(ox+31,16,14,13),(ox+44,12,15,12),(ox+58,17,15,13),(ox+70,25,13,14),(ox+78,36,10,17)]
    for i,(x,y,w,h) in enumerate(blocks):
        c.rect(x,y,w,h,P["stone0"]); c.hline(x+1,y+1,max(1,w-2),P["stone2"]); c.vline(x+w-1,y+2,max(1,h-3),P["stone1"])
        if i%2==0: c.hline(x+2,y+4,max(1,w-5),P["moss"])
    for y in range(29,56):
        inset=5 if y<33 else 2 if y<36 else 0; c.hline(ox+34+inset,y,29-2*inset,P["ink"])
    c.hline(ox+38,49,21,P["deep"]); c.hline(ox+40,53,18,P["stone1"]); c.hline(ox+43,56,15,P["stone2"])
    grass_tuft(c,ox+8,50,"grass_hi",True); grass_tuft(c,ox+83,51,"grass_dark"); c.hline(ox+22,22,8,P["moss"]); c.hline(ox+61,18,7,P["moss"])
    return c

assets = {
    "grass.png": make_grass(), "forest.png": make_forest(), "road.png": make_road(),
    "river.png": make_river(), "bridge.png": make_bridge(), "camp_ruin.png": make_camp_ruin(),
}
for name, cv in assets.items():
    print(name, cv.w, cv.h, write_png(OUT / name, cv.w, cv.h, cv.p))

# Deterministic source-art review board. This deliberately is not labelled an engine screenshot.
board=Canvas(768,432,(72,104,55,255))
for y in range(10,430,31):
    for x in range((y*7)%37,768,47): board.set(x,y,P["grass_hi"])
forest=assets["forest.png"]
for dx,srcx in [(20,0),(90,32),(162,0),(234,64),(600,32),(672,0)]:
    for yy in range(32):
        for xx in range(32):
            i=((srcx+xx)+yy*forest.w)*4
            if forest.p[i+3]: board.rect(dx+xx*2,22+yy*2,2,2,tuple(forest.p[i:i+4]))
road_pts=[(395,0),(390,65),(410,120),(402,180),(421,244),(408,310),(425,432)]
for idx in range(len(road_pts)-1):
    x1,y1=road_pts[idx]; x2,y2=road_pts[idx+1]; steps=max(abs(x2-x1),abs(y2-y1))
    for s in range(steps+1):
        t=s/max(1,steps); cx=round(x1+(x2-x1)*t); cy=round(y1+(y2-y1)*t); width=34+(2 if ((cy//31)%2) else -2)
        board.hline(cx-width//2,cy,width,P["soil2"])
        if cy%17==0: board.set(cx-8,cy,P["soil0"]); board.set(cx+10,cy,P["soil0"])
for x in range(768):
    cy=222+(1 if x<170 else -2 if x<310 else 0 if x<520 else 3 if x<660 else 1)
    for yy in range(cy-11,cy+12): board.set(x,yy,P["water1"])
    if x%43<10: board.set(x,cy-5,P["water2"])
    if x%67<7: board.set(x,cy+6,P["foam"])
br=assets["bridge.png"]
for yy in range(br.h):
    for xx in range(br.w):
        i=(xx+yy*br.w)*4
        if br.p[i+3]: board.rect(357+xx*2,174+yy*2,2,2,tuple(br.p[i:i+4]))
camp=assets["camp_ruin.png"]
for yy in range(64):
    for xx in range(96):
        i=(xx+yy*camp.w)*4
        if camp.p[i+3]: board.rect(58+xx*2,285+yy*2,2,2,tuple(camp.p[i:i+4]))
for yy in range(64):
    for xx in range(96,192):
        i=(xx+yy*camp.w)*4
        if camp.p[i+3]: board.rect(536+(xx-96)*2,286+yy*2,2,2,tuple(camp.p[i:i+4]))
PREVIEW.parent.mkdir(parents=True,exist_ok=True)
write_png(PREVIEW,board.w,board.h,board.p)
print("preview", PREVIEW)
