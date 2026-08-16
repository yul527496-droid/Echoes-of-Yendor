#!/usr/bin/env python3
"""Generate the second-pass Chapter 1 surface character sheets on SPD's 16px grammar."""
from pathlib import Path
import hashlib
import struct
import zlib

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/"core/src/main/assets/sprites"
OUT.mkdir(parents=True,exist_ok=True)
PNG=b"\x89PNG\r\n\x1a\n"

def chunk(t,p):
    c=zlib.crc32(t); c=zlib.crc32(p,c)&0xffffffff
    return struct.pack(">I",len(p))+t+p+struct.pack(">I",c)

def write(path,w,h,p):
    raw=bytearray(); stride=w*4
    for y in range(h): raw.append(0); raw.extend(p[y*stride:(y+1)*stride])
    data=PNG+chunk(b"IHDR",struct.pack(">IIBBBBB",w,h,8,6,0,0,0))+chunk(b"IDAT",zlib.compress(bytes(raw),9))+chunk(b"IEND",b"")
    path.write_bytes(data)
    return hashlib.sha256(data).hexdigest()

class C:
    def __init__(self,w,h): self.w=w; self.h=h; self.p=bytearray((0,0,0,0)*(w*h))
    def s(self,x,y,col):
        if 0<=x<self.w and 0<=y<self.h:
            i=(x+y*self.w)*4; self.p[i:i+4]=bytes(col)
    def r(self,x,y,w,h,col):
        for yy in range(y,y+h):
            for xx in range(x,x+w): self.s(xx,yy,col)
    def hline(self,x,y,n,col):
        for q in range(n): self.s(x+q,y,col)
    def blit(self,src,dx,dy):
        for y in range(src.h):
            for x in range(src.w):
                i=(x+y*src.w)*4
                if src.p[i+3]: self.s(dx+x,dy+y,tuple(src.p[i:i+4]))

ink=(39,35,30,255); deep=(50,45,38,255); skin=(205,156,108,255); skinhi=(232,190,137,255)
brown=(92,58,34,255); brown2=(132,83,42,255); brownhi=(172,119,60,255); cream=(210,199,159,255)
rust=(151,70,39,255); pants=(67,64,56,255); boot=(49,39,31,255)
gray0=(56,60,57,255); gray1=(91,96,89,255); gray2=(130,133,119,255); grayhi=(171,170,144,255)
blue=(61,86,105,255); green=(71,94,62,255); red=(128,57,45,255); ochre=(154,113,54,255); teal=(55,98,91,255); purple=(91,68,100,255)

def farmer_frame(step=0,run=False):
    c=C(16,16); c.hline(5,15,6,ink)
    if run:
        c.r(5+step,12,2,3,pants); c.r(9-step,12,2,3,pants); c.s(4+step,15,boot); c.s(10-step,15,boot)
    else:
        c.r(5,12,2,3,pants); c.r(9,12,2,3,pants); c.s(5,15,boot); c.s(10,15,boot)
    c.r(4,8,8,5,ink); c.r(5,8,6,4,cream); c.r(5,9,2,4,rust); c.r(9,9,2,4,rust); c.s(8,9,brown2)
    c.r(3,9,2,3,ink); c.s(3,10,skin); c.r(12,9,1,3,ink); c.s(12,10,skin)
    head_y=4-(1 if (not run and step) else 0)
    c.r(5,head_y,6,5,ink); c.r(6,head_y,4,4,skin); c.s(6,head_y+1,skinhi); c.s(7,head_y+2,ink); c.s(9,head_y+2,ink); c.s(10,head_y+2,skinhi)
    c.hline(7,head_y+4,4,cream); c.s(6,head_y+4,cream)
    c.hline(4,2,8,brown); c.hline(5,1,6,brown2); c.hline(6,0,4,brownhi); c.s(3,2,deep); c.s(12,2,deep)
    return c

def farmer_sheet():
    out=C(64,16)
    for i,f in enumerate([farmer_frame(0,False),farmer_frame(1,False),farmer_frame(-1,True),farmer_frame(1,True)]): out.blit(f,i*16,0)
    return out

def donkey_frame(phase=0,run=False):
    c=C(32,16)
    c.r(1,7,12,6,ink); c.r(2,7,10,5,brown2); c.hline(3,8,8,brownhi); c.hline(1,12,12,brown)
    for x,y in [(3,13),(4,14),(5,13),(10,13),(11,14),(12,13)]: c.s(x,y,ink)
    c.hline(12,9,5,brown)
    c.r(15,7,9,5,ink); c.r(16,7,8,4,gray1); c.hline(17,8,6,gray2)
    c.r(22,5,3,5,ink); c.r(23,5,2,4,gray1); c.r(24,5,5,4,ink); c.r(25,6,4,3,gray2); c.s(29,7,grayhi); c.s(27,6,(194,167,68,255)); c.s(28,6,ink)
    ear=phase%2; c.s(24,3-ear,gray1); c.s(25,4-ear,ink); c.s(27,3+ear,gray1); c.s(26,4,ink)
    c.s(15,8,ink); c.s(14,7,gray1); c.s(13,6 if phase else 7,ink)
    if run and phase%2==0:
        c.r(17,11,2,4,gray0); c.r(22,11,2,3,gray0); c.s(16,15,ink); c.s(23,14,ink)
    elif run:
        c.r(17,11,2,3,gray0); c.r(22,11,2,4,gray0); c.s(18,14,ink); c.s(24,15,ink)
    else:
        c.r(17,11,2,4,gray0); c.r(22,11,2,4,gray0); c.s(17,15,ink); c.s(23,15,ink)
    return c

def donkey_sheet():
    out=C(128,16)
    for i,f in enumerate([donkey_frame(0,False),donkey_frame(1,False),donkey_frame(0,True),donkey_frame(1,True)]): out.blit(f,i*32,0)
    return out

def bird_frame(phase=0,fly=False):
    c=C(16,16)
    if not fly:
        c.hline(5,11,6,ink); c.r(6,8,5,4,gray0); c.r(7,8,3,3,gray1); c.s(10,8,grayhi); c.s(11,9,ink); c.s(9,9,(184,155,65,255)); c.s(5,9,gray1)
        c.s(7,12,ink); c.s(9,12,ink); c.s(7,13,brown); c.s(9,13,brown)
        if phase: c.s(5,8,gray2)
    else:
        c.r(6,9,5,3,gray0); c.r(7,9,3,2,gray1); c.s(11,9,grayhi); c.s(12,10,ink); c.s(10,9,(184,155,65,255))
        wing=[(5,8),(4,7),(3,6),(2,6),(5,10),(4,11),(3,12),(8,8),(8,7),(9,6),(10,5),(11,5),(8,11),(9,12),(10,13)] if phase==0 else [(5,9),(4,9),(3,10),(2,10),(8,9),(9,8),(10,8),(11,7)]
        for x,y in wing: c.s(x,y,gray1 if x<8 else gray2)
    return c

def bird_sheet():
    out=C(64,16)
    for i,f in enumerate([bird_frame(0,False),bird_frame(1,False),bird_frame(0,True),bird_frame(1,True)]): out.blit(f,i*16,0)
    return out

def wolf_frame(phase=0,attack=False):
    c=C(16,16); c.s(2,8-phase,ink); c.s(3,9-phase,gray0); c.s(4,10,gray1)
    c.r(4,8,7,5,ink); c.r(5,8,6,4,gray1); c.hline(6,8,4,gray2); c.s(5,10,gray0)
    c.r(10,7,3,5,ink); c.r(11,7,2,4,gray2); c.r(12,8,3,3,ink); c.r(13,8,2,2,grayhi); c.s(15,9,ink); c.s(13,8,(191,155,60,255))
    c.s(10,6,ink); c.s(11,5,gray1); c.s(12,6,ink)
    if attack: c.s(14,11,(220,203,169,255)); c.s(15,10,ink)
    if phase%2==0:
        c.r(5,12,2,3,gray0); c.r(9,12,2,3,gray0); c.s(5,15,ink); c.s(10,15,ink)
    else:
        c.r(6,12,2,3,gray0); c.r(10,12,2,2,gray0); c.s(5,15,ink); c.s(12,14,ink)
    return c

def wolf_sheet():
    out=C(64,16)
    for i,f in enumerate([wolf_frame(0),wolf_frame(1),wolf_frame(1,True),wolf_frame(0,True)]): out.blit(f,i*16,0)
    return out

def villager_frame(idx,phase):
    pals=[(blue,cream),(green,(190,153,104,255)),(red,cream),(ochre,(202,174,122,255)),(teal,cream),(purple,(198,165,116,255)),((92,72,52,255),cream),((104,88,55,255),(200,163,104,255))]
    cloth,skincol=pals[idx]; c=C(16,16); oy=-1 if phase and idx in (1,4) else 0
    c.hline(5,15,6,ink); c.r(5,12,2,3,pants); c.r(9,12,2,3,pants); c.s(5,15,boot); c.s(10,15,boot)
    c.r(4,8+oy,8,5,ink); c.r(5,8+oy,6,4,cloth); c.s(6,9+oy,cream); c.s(9,10+oy,deep)
    c.r(5,4+oy,6,5,ink); c.r(6,5+oy,4,3,skincol); c.s(7,6+oy,ink); c.s(9,6+oy,ink)
    if idx==0: c.hline(5,4+oy,6,brown)
    elif idx==1: c.hline(4,3+oy,8,ochre); c.hline(6,2+oy,4,ochre)
    elif idx==2: c.hline(5,4+oy,6,ink); c.s(5,5+oy,ink)
    elif idx==3: c.hline(4,4+oy,8,brown2); c.s(4,3+oy,brownhi)
    elif idx==4: c.hline(5,4+oy,6,(126,112,75,255)); c.s(10,3+oy,(126,112,75,255))
    elif idx==5: c.hline(5,4+oy,6,deep); c.s(8,3+oy,deep)
    elif idx==6: c.hline(4,4+oy,8,(78,55,39,255)); c.hline(5,3+oy,6,(78,55,39,255))
    else: c.hline(5,4+oy,6,(161,149,124,255))
    return c

def villagers_sheet():
    out=C(256,16)
    for i in range(8):
        out.blit(villager_frame(i,0),(i*2)*16,0); out.blit(villager_frame(i,1),(i*2+1)*16,0)
    return out

assets={
    "echoes_farmer_v2.png":farmer_sheet(),
    "echoes_donkey_cart_v2.png":donkey_sheet(),
    "echoes_bird_v2.png":bird_sheet(),
    "echoes_wolf_v2.png":wolf_sheet(),
    "surface_villagers_v3.png":villagers_sheet(),
}
for name,canvas in assets.items():
    print(name,canvas.w,canvas.h,write(OUT/name,canvas.w,canvas.h,canvas.p))
