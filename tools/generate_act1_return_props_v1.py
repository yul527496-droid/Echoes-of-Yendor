#!/usr/bin/env python3
"""Generate project-owned Act 1 Return props on Echoes' native 16px grid."""
from pathlib import Path
import struct, zlib, hashlib

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/"core/src/main/assets/environment/echoes/act1_return/return_props_v1.png"
W,H=256,64
P={
 "ink":(35,39,31,255),"shadow":(55,50,38,255),"wood":(93,66,42,255),"wood_hi":(145,105,61,255),
 "cloth":(112,79,58,255),"cloth_hi":(157,115,78,255),"cloth_dark":(75,59,48,255),
 "stone":(83,87,69,255),"stone_hi":(126,128,97,255),"moss":(78,103,55,255),
 "metal":(111,119,111,255),"paper":(183,166,121,255),"ember":(150,70,38,255),
 "grass":(72,104,54,255),"grass_hi":(111,139,69,255),"soil":(111,79,48,255)
}
px=bytearray((0,0,0,0)*(W*H))
def setp(x,y,c):
 if 0<=x<W and 0<=y<H:
  i=(x+y*W)*4; px[i:i+4]=bytes(c)
def rect(x,y,w,h,c):
 for yy in range(y,y+h):
  for xx in range(x,x+w): setp(xx,yy,c)
def line(x,y,n,c):
 for xx in range(x,x+n): setp(xx,y,c)
def tile(tx,ty): return tx*16,ty*16

def bedroll(tx,variant):
 x,y=tile(tx,0); ox=(variant%2); width=11-(variant==3)
 rect(x+2+ox,y+8,width,5,P["cloth_dark"]); rect(x+3+ox,y+6,width-2,5,P["cloth"])
 line(x+4+ox,y+6,max(2,width-4),P["cloth_hi"]); rect(x+2,y+12,width+1,2,P["shadow"])
 if variant==1: setp(x+11,y+8,P["wood_hi"])
 if variant==2: line(x+4,y+10,5,P["ink"])
 if variant==3: rect(x+10,y+7,3,4,P["cloth_hi"])
for i in range(4): bedroll(i,i)
# dead fire
x,y=tile(4,0); line(x+3,y+11,10,P["shadow"]); line(x+5,y+9,6,P["wood"]); line(x+4,y+10,8,P["wood_hi"]); setp(x+7,y+8,P["ember"]); setp(x+9,y+9,P["ember"])
# cook pot
x,y=tile(5,0); rect(x+5,y+7,7,6,P["ink"]); rect(x+6,y+8,5,4,P["metal"]); line(x+4,y+7,9,P["metal"]); setp(x+5,y+13,P["wood"]); setp(x+11,y+13,P["wood"])
# travel bag
x,y=tile(6,0); rect(x+4,y+7,9,7,P["shadow"]); rect(x+5,y+6,7,7,P["cloth"]); line(x+7,y+5,3,P["cloth_hi"]); line(x+6,y+9,5,P["cloth_hi"])
# crate
x,y=tile(7,0); rect(x+3,y+5,11,9,P["ink"]); rect(x+4,y+6,9,7,P["wood"]); line(x+4,y+7,9,P["wood_hi"]); line(x+5,y+11,7,P["shadow"]); setp(x+8,y+8,P["metal"])
# 2x2 faded tent at (8,0)
x,y=tile(8,0); rect(x+3,y+22,27,3,P["shadow"]); rect(x+6,y+10,22,13,P["cloth_dark"])
for yy in range(7,21):
 left=16-yy//2; right=18+yy//2; line(x+left,y+yy,max(1,right-left),P["cloth"])
line(x+15,y+8,3,P["cloth_hi"]); rect(x+16,y+13,2,13,P["wood"]); line(x+8,y+20,7,P["cloth_hi"])
# paper checklist
x,y=tile(10,0); rect(x+5,y+4,8,11,P["shadow"]); rect(x+4,y+3,8,11,P["paper"]); line(x+6,y+6,4,P["soil"]); line(x+6,y+8,5,P["soil"]); line(x+6,y+10,3,P["soil"])
# maintenance tools
x,y=tile(11,0); rect(x+4,y+11,10,2,P["wood"]); setp(x+5,y+10,P["metal"]); setp(x+6,y+9,P["metal"]); line(x+9,y+6,2,P["metal"]); rect(x+10,y+8,2,6,P["wood_hi"])
# hanging faded cloth
x,y=tile(12,0); rect(x+3,y+4,2,11,P["wood"]); rect(x+12,y+5,2,10,P["wood"]); line(x+4,y+5,9,P["shadow"]); rect(x+6,y+6,5,6,P["cloth"]); setp(x+10,y+11,P["cloth_hi"])
# milestone
x,y=tile(13,0); rect(x+5,y+5,7,10,P["shadow"]); rect(x+4,y+4,7,10,P["stone"]); line(x+5,y+4,5,P["stone_hi"]); line(x+6,y+8,3,P["moss"])
# low wall
x,y=tile(14,0); rect(x+1,y+9,15,6,P["shadow"]); rect(x+1,y+7,15,6,P["stone"]); line(x+2,y+7,5,P["stone_hi"]); line(x+9,y+8,5,P["stone_hi"]); setp(x+7,y+11,P["moss"])
# field edge
x,y=tile(15,0); line(x+1,y+13,15,P["soil"]); line(x+2,y+11,13,P["grass"])
for ox in (3,6,10,13): rect(x+ox,y+6,1,6,P["grass_hi"])
# row 1 standalone props
# sack
x,y=tile(0,1); rect(x+5,y+7,8,7,P["shadow"]); rect(x+4,y+6,8,7,P["cloth"]); line(x+6,y+5,4,P["cloth_hi"])
# farm tool
x,y=tile(1,1); rect(x+8,y+4,2,11,P["wood_hi"]); line(x+4,y+5,6,P["metal"]); setp(x+4,y+6,P["metal"])
# broken cart wood
x,y=tile(2,1); line(x+2,y+11,13,P["wood"]); line(x+5,y+7,8,P["wood_hi"]); rect(x+3,y+9,2,5,P["metal"]); rect(x+12,y+9,2,5,P["metal"])
# offering bowl
x,y=tile(3,1); line(x+4,y+10,9,P["shadow"]); line(x+5,y+8,7,P["stone_hi"]); line(x+6,y+9,5,P["stone"]); setp(x+8,y+7,P["moss"])
# four worn recesses (2x1)
x,y=tile(4,1); rect(x+2,y+7,28,7,P["stone"]); line(x+3,y+7,25,P["stone_hi"])
for ox in (5,11,17,23): rect(x+ox,y+9,4,3,P["shadow"])
# roots
x,y=tile(6,1); line(x+2,y+12,13,P["shadow"]); line(x+5,y+10,7,P["wood"]); setp(x+4,y+9,P["wood"]); setp(x+12,y+9,P["wood"])
# stump
x,y=tile(7,1); rect(x+5,y+7,8,7,P["shadow"]); rect(x+4,y+6,8,7,P["wood"]); line(x+4,y+6,8,P["wood_hi"]); line(x+6,y+8,4,P["shadow"])

sig=b"\x89PNG\r\n\x1a\n"
def chunk(k,d):
 crc=zlib.crc32(d,zlib.crc32(k))&0xffffffff
 return struct.pack(">I",len(d))+k+d+struct.pack(">I",crc)
raw=bytearray()
for yy in range(H): raw.append(0); raw.extend(px[yy*W*4:(yy+1)*W*4])
data=sig+chunk(b"IHDR",struct.pack(">IIBBBBB",W,H,8,6,0,0,0))+chunk(b"IDAT",zlib.compress(bytes(raw),9))+chunk(b"IEND",b"")
OUT.parent.mkdir(parents=True,exist_ok=True); OUT.write_bytes(data)
print(f"generated {OUT} 256x64 sha256={hashlib.sha256(data).hexdigest()}")
