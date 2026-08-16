#!/usr/bin/env python3
"""Generate native-pixel Chapter 1 dialogue portraits and Old Crow innkeeper art."""
from pathlib import Path
import hashlib, struct, zlib

ROOT=Path(__file__).resolve().parents[1]
SPRITES=ROOT/"core/src/main/assets/sprites"
INTERFACES=ROOT/"core/src/main/assets/interfaces/echoes"
SPRITES.mkdir(parents=True,exist_ok=True); INTERFACES.mkdir(parents=True,exist_ok=True)
PNG=b"\x89PNG\r\n\x1a\n"

def chunk(t,p):
    c=zlib.crc32(t); c=zlib.crc32(p,c)&0xffffffff
    return struct.pack(">I",len(p))+t+p+struct.pack(">I",c)

def write(path,w,h,p):
    raw=bytearray(); stride=w*4
    for y in range(h): raw.append(0); raw.extend(p[y*stride:(y+1)*stride])
    data=PNG+chunk(b"IHDR",struct.pack(">IIBBBBB",w,h,8,6,0,0,0))+chunk(b"IDAT",zlib.compress(bytes(raw),9))+chunk(b"IEND",b"")
    path.write_bytes(data); return hashlib.sha256(data).hexdigest()

class C:
    def __init__(self,w,h): self.w=w; self.h=h; self.p=bytearray((0,0,0,0)*(w*h))
    def s(self,x,y,col):
        if 0<=x<self.w and 0<=y<self.h:
            i=(x+y*self.w)*4; self.p[i:i+4]=bytes(col)
    def r(self,x,y,w,h,col):
        for yy in range(y,y+h):
            for xx in range(x,x+w): self.s(xx,yy,col)
    def line(self,x,y,n,col):
        for q in range(n): self.s(x+q,y,col)
    def blit(self,src,dx,dy):
        for y in range(src.h):
            for x in range(src.w):
                i=(x+y*src.w)*4
                if src.p[i+3]: self.s(dx+x,dy+y,tuple(src.p[i:i+4]))

INK=(34,30,27,255); DEEP=(52,43,38,255)
SKD=(132,82,57,255); SK=(190,128,87,255); SKH=(224,166,112,255)
STEEL0=(65,72,74,255); STEEL1=(102,112,111,255); STEEL2=(157,163,151,255)
CREAM=(207,197,166,255); WHITE=(220,215,195,255)
GOLD0=(124,91,45,255); GOLD1=(177,133,64,255)
RED0=(93,45,35,255); RED1=(145,66,48,255)
BLUE0=(48,58,79,255); BLUE1=(70,87,112,255)
GREEN0=(48,67,47,255); GREEN1=(71,94,62,255)
PURPLE0=(63,50,75,255); PURPLE1=(91,69,105,255)
BROWN0=(74,49,31,255); BROWN1=(111,76,42,255); BROWN2=(153,108,58,255)
HAIR_RED=(132,61,42,255); HAIR_BLOND=(166,126,67,255); HAIR_DARK=(57,44,38,255)

def bust(cloth0,cloth1,hair,hood=False,armor=False,beard=False):
    c=C(48,48)
    c.r(8,35,32,13,INK); c.r(11,34,26,14,cloth0); c.r(14,34,20,13,cloth1)
    c.r(6,40,8,8,cloth0); c.r(34,40,8,8,cloth0)
    if armor:
        c.r(13,36,22,9,STEEL0); c.r(16,35,16,9,STEEL1); c.line(18,36,10,STEEL2); c.r(23,35,2,10,INK)
    c.r(20,29,8,8,SKD); c.r(21,29,6,7,SK)
    c.r(15,11,18,20,INK); c.r(17,12,14,18,SK); c.r(18,12,10,4,SKH)
    c.r(16,20,2,7,SKD); c.r(31,19,2,7,SKD); c.s(18,20,INK); c.s(29,20,INK); c.line(21,26,7,SKD); c.s(28,25,SKH)
    if hood:
        c.r(12,8,24,7,cloth0); c.r(14,6,20,5,cloth1); c.r(12,12,5,15,cloth0); c.r(31,12,5,15,cloth0)
    else:
        c.r(15,9,18,6,hair); c.r(14,12,4,10,hair); c.r(30,11,4,8,hair); c.line(18,9,9,hair)
    if beard:
        c.r(17,25,14,7,hair); c.r(20,31,8,4,hair); c.s(18,29,SKH)
    return c

def hero(i):
    if i==0:
        c=bust(RED0,RED1,HAIR_RED,armor=True,beard=True); c.r(10,36,5,9,GOLD0); c.r(33,36,5,9,GOLD0)
    elif i==1:
        c=bust(PURPLE0,PURPLE1,HAIR_DARK,hood=True); c.r(19,36,10,8,BLUE0); c.line(21,37,6,BLUE1); c.s(24,17,(132,170,186,255))
    elif i==2:
        c=bust(DEEP,(70,66,62,255),HAIR_DARK,hood=True); c.r(15,29,18,5,RED0); c.s(18,20,(185,182,151,255)); c.s(29,20,(185,182,151,255))
    elif i==3:
        c=bust(GREEN0,GREEN1,BROWN1,hood=True); c.r(13,8,4,16,BROWN0); c.r(35,9,2,24,BROWN0); c.s(36,8,CREAM); c.s(37,7,CREAM)
    elif i==4:
        c=bust(BLUE0,BLUE1,HAIR_BLOND,armor=True); c.r(31,8,3,9,RED0); c.r(34,7,2,7,RED1); c.line(17,9,9,HAIR_BLOND)
    else:
        c=bust((104,94,70,255),CREAM,HAIR_DARK,hood=True); c.r(22,34,4,12,GOLD0); c.r(23,35,2,10,GOLD1); c.line(19,40,10,WHITE)
    return c

def farmer(expr):
    hair=(138,128,108,255)
    c=bust(BROWN0,GREEN0,hair,beard=True)
    c.r(9,7,30,4,BROWN0); c.r(12,4,23,5,BROWN1); c.line(16,3,14,BROWN2)
    c.r(14,25,20,8,(125,116,99,255)); c.r(18,31,13,5,(166,157,137,255))
    if expr==1:
        c.line(21,27,7,SKD); c.s(22,28,SKD); c.s(27,28,SKD)
    elif expr==2:
        c.line(16,17,5,BROWN0); c.line(27,16,5,BROWN0); c.r(18,20,2,2,INK); c.r(29,20,2,2,INK); c.line(22,28,6,SKD)
    elif expr==3:
        eye=(217,202,155,255); c.r(17,19,4,3,eye); c.r(28,19,4,3,eye); c.s(19,20,INK); c.s(30,20,INK); c.line(21,28,8,RED0)
    elif expr==4:
        c.line(16,17,5,(102,91,76,255)); c.line(27,17,5,(102,91,76,255)); c.s(18,21,INK); c.s(29,21,INK); c.line(22,29,6,SKD); c.line(24,31,3,SKD)
    return c

def innkeeper(expr):
    c=bust(RED0,(121,67,54,255),HAIR_DARK)
    c.r(13,9,22,7,HAIR_RED); c.r(12,12,5,13,HAIR_RED); c.r(31,12,5,12,HAIR_RED)
    c.r(18,34,13,14,CREAM); c.r(13,35,6,13,RED0); c.r(30,35,6,13,RED0); c.r(23,35,3,12,GOLD0)
    if expr==1:
        c.line(16,17,5,HAIR_DARK); c.line(27,17,5,HAIR_DARK); c.r(18,20,2,2,INK); c.r(29,20,2,2,INK); c.line(21,28,7,SKD)
    elif expr==2:
        c.line(16,18,5,HAIR_DARK); c.line(27,18,5,HAIR_DARK); c.s(18,21,INK); c.s(29,21,INK); c.line(21,29,8,RED0)
    return c

def portraits():
    frames=[hero(i) for i in range(6)]+[farmer(i) for i in range(5)]+[innkeeper(i) for i in range(3)]
    out=C(48*len(frames),48)
    for i,f in enumerate(frames): out.blit(f,i*48,0)
    return out

def inn_sprite(phase=0,walk=False):
    c=C(16,16); c.line(5,15,6,INK)
    c.r(5+(phase if walk else 0),12,2,3,DEEP); c.r(9-(phase if walk else 0),12,2,3,DEEP)
    c.r(4,8,8,5,INK); c.r(5,8,6,4,CREAM); c.r(5,9,2,4,RED0); c.r(9,9,2,4,RED0); c.s(8,9,GOLD1)
    c.r(3,9,2,3,RED0); c.s(3,12,SK); c.r(12,9,1,3,RED0); c.s(12,12,SK)
    c.r(5,3,6,5,INK); c.r(6,4,4,4,SK); c.s(7,5,INK); c.s(9,5,INK); c.s(10,6,SKH)
    c.line(5,3,6,HAIR_RED); c.line(6,2,5,HAIR_RED); c.r(4,4,2,4,HAIR_RED); c.r(10,3,2,5,HAIR_RED); c.s(11,2,HAIR_RED)
    return c

def inn_sheet():
    out=C(64,16); frames=[inn_sprite(),inn_sprite(),inn_sprite(-1,True),inn_sprite(1,True)]; frames[1].s(11,2,BROWN2)
    for i,f in enumerate(frames): out.blit(f,i*16,0)
    return out

p=portraits(); s=inn_sheet()
print("echoes_dialogue_portraits_v1.png",write(INTERFACES/"echoes_dialogue_portraits_v1.png",p.w,p.h,p.p))
print("echoes_innkeeper_v1.png",write(SPRITES/"echoes_innkeeper_v1.png",s.w,s.h,s.p))
