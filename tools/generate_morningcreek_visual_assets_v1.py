#!/usr/bin/env python3
from pathlib import Path
import hashlib, struct, zlib

ROOT = Path('.')
OUT = ROOT / 'core/src/main/assets/environment/echoes/morningcreek'
T = 16
PNG_SIG=b'\x89PNG\r\n\x1a\n'

P={
 'ink':(28,30,27,255),'shadow':(45,42,37,255),'deep':(57,52,44,255),
 'stone0':(63,66,60,255),'stone1':(82,84,76,255),'stone2':(108,108,95,255),'stone3':(137,133,112,255),
 'wood0':(67,45,31,255),'wood1':(91,60,38,255),'wood2':(125,82,48,255),'wood3':(163,111,62,255),
 'plaster0':(113,103,82,255),'plaster1':(150,137,105,255),'plaster2':(184,168,126,255),
 'roof0':(70,46,42,255),'roof1':(96,58,49,255),'roof2':(127,75,58,255),'roof3':(157,96,65,255),
 'slate0':(48,56,60,255),'slate1':(66,74,77,255),'slate2':(91,96,94,255),'slate3':(124,125,115,255),
 'green0':(54,67,47,255),'green1':(74,89,58,255),'green2':(101,116,70,255),'green3':(133,143,82,255),
 'clothR':(145,61,50,255),'clothY':(185,142,72,255),'clothB':(63,91,116,255),'water':(60,116,128,255),
 'herb':(84,119,64,255),'herbHi':(126,151,78,255),'soil':(88,62,42,255),'straw':(177,143,77,255),
 'iron':(58,61,60,255),'ironHi':(106,109,103,255),'paper':(201,190,149,255),'flame':(226,153,59,255),
}

def chunk(t,p):
    c=zlib.crc32(t); c=zlib.crc32(p,c)&0xffffffff
    return struct.pack('>I',len(p))+t+p+struct.pack('>I',c)

def write_png(path,w,h,px):
    raw=bytearray(); stride=w*4
    for y in range(h): raw.append(0); raw.extend(px[y*stride:(y+1)*stride])
    data=PNG_SIG+chunk(b'IHDR',struct.pack('>IIBBBBB',w,h,8,6,0,0,0))+chunk(b'IDAT',zlib.compress(bytes(raw),9))+chunk(b'IEND',b'')
    path.parent.mkdir(parents=True,exist_ok=True); path.write_bytes(data)
    return hashlib.sha256(data).hexdigest()

class C:
    def __init__(self,w,h): self.w=w; self.height=h; self.p=bytearray((0,0,0,0)*(w*h))
    def set(self,x,y,c):
        if 0<=x<self.w and 0<=y<self.height:
            i=(x+y*self.w)*4; self.p[i:i+4]=bytes(c)
    def rect(self,x,y,w,h,c):
        for yy in range(y,y+h):
            for xx in range(x,x+w): self.set(xx,yy,c)
    def h(self,x,y,n,c):
        for i in range(n): self.set(x+i,y,c)
    def v(self,x,y,n,c):
        for i in range(n): self.set(x,y+i,c)

STYLES=[
 ('house_warm',('roof0','roof1','roof2','roof3'),('wood0','plaster0','plaster1','plaster2'),'wood3'),
 ('house_stone',('roof0','roof1','roof2','roof3'),('stone0','stone1','stone2','stone3'),'wood2'),
 ('inn',('roof0','roof1','roof2','roof3'),('wood0','wood1','plaster1','plaster2'),'clothR'),
 ('civic',('slate0','slate1','slate2','slate3'),('stone0','stone1','stone2','stone3'),'clothB'),
 ('tower',('slate0','slate1','slate2','slate3'),('deep','stone0','stone1','stone2'),'ironHi'),
 ('warehouse',('roof0','roof1','roof2','roof3'),('wood0','wood1','wood2','wood3'),'iron'),
 ('clinic',('green0','green1','green2','green3'),('stone0','plaster0','plaster1','plaster2'),'herbHi'),
 ('stable',('roof0','roof1','roof2','roof3'),('wood0','wood1','wood2','straw'),'straw'),
 ('blacksmith',('slate0','slate1','slate2','slate3'),('stone0','stone1','wood1','wood2'),'ironHi'),
 ('house_green',('green0','green1','green2','green3'),('wood0','plaster0','plaster1','plaster2'),'wood3'),
]
# 80 painted modules plus one fully transparent 16x16 module at atlas index 80.
structures=C(256,96)
for sid,(name,rp,wp,accent) in enumerate(STYLES):
    base=sid*8
    for mod in range(8):
        idx=base+mod; tx=(idx%16)*T; ty=(idx//16)*T
        if mod<=4:
            structures.rect(tx,ty,T,T,P[rp[1]])
            structures.h(tx,ty,T,P[rp[3]])
            structures.h(tx,ty+T-1,T,P[rp[0]])
            for yy in (4,9,13): structures.h(tx+1,ty+yy,14,P[rp[2] if yy<10 else rp[0]])
            if mod==1: structures.h(tx,ty,16,P[rp[3]]); structures.h(tx,ty+1,16,P[rp[2]])
            if mod==2: structures.rect(tx,ty+11,16,5,P[wp[1]]); structures.h(tx,ty+11,16,P[rp[0]])
            if mod==3: structures.v(tx,ty,16,P[rp[3]])
            if mod==4: structures.v(tx+15,ty,16,P[rp[0]])
        else:
            structures.rect(tx,ty,T,T,P[wp[2]])
            structures.h(tx,ty,T,P[wp[3]])
            structures.h(tx,ty+14,T,P[wp[0]])
            structures.h(tx,ty+15,T,P['ink'])
            structures.rect(tx,ty,16,3,P[rp[0]])
            if sid in (0,2,5,7,9):
                structures.v(tx+3,ty+3,11,P[wp[0]]); structures.v(tx+12,ty+3,11,P[wp[0]])
                structures.h(tx+3,ty+8,10,P[wp[1]])
            else:
                for xx in (4,11): structures.v(tx+xx,ty+4,8,P[wp[1]])
            if mod==6:
                structures.rect(tx+5,ty+5,6,6,P['ink']); structures.rect(tx+6,ty+6,4,4,P['clothB' if sid in (3,4) else 'paper'])
                structures.set(tx+6,ty+6,P['slate3']); structures.set(tx+9,ty+6,P['slate3'])
            elif mod==7:
                door=P['wood0'] if sid not in (3,4,8) else P['deep']
                structures.rect(tx+4,ty+4,8,11,door); structures.rect(tx+5,ty+5,6,9,P['wood1'])
                structures.set(tx+10,ty+10,P[accent]); structures.h(tx+3,ty+3,10,P[wp[0]])
for sid in range(len(STYLES)):
    idx=sid*8; tx=(idx%16)*16; ty=(idx//16)*16
    structures.set(tx+3,ty+6,P[STYLES[sid][1][3]]); structures.set(tx+11,ty+11,P[STYLES[sid][1][0]])

props=C(256,64)
for x in range(16):
    props.set(x,0,P['stone3']); props.set(x,15,P['stone0'])
for y in range(1,15):
    props.h(0,y,16,P['stone2' if y%4 else 'stone1'])
    if y%4==0: props.h(1,y,6,P['stone3']); props.h(9,y,6,P['stone3'])
x=16; props.rect(x,6,16,8,P['stone1']); props.h(x,5,16,P['stone3']); props.h(x,13,16,P['stone0'])
for xx in (1,7,13): props.rect(x+xx,3,2,11,P['stone2'])
x=32; props.rect(x+4,2,8,13,P['stone1']); props.rect(x+3,5,10,10,P['stone2']); props.h(x+3,5,10,P['stone3']); props.h(x+3,14,10,P['stone0'])
x=48; props.rect(x,8,16,7,P['stone1']); props.h(x,7,16,P['stone3']); props.h(x,14,16,P['stone0'])
x=64
for xx in (2,7,12): props.rect(x+xx,4,2,11,P['wood1'])
props.rect(x,8,16,2,P['wood2'])
x=80; props.rect(x+7,5,2,11,P['wood1']); props.rect(x+2,3,12,7,P['wood2']); props.rect(x+3,4,10,5,P['wood1'])
x=96; props.rect(x+6,5,4,8,P['iron']); props.rect(x+5,8,6,5,P['ironHi']); props.rect(x+7,6,2,4,P['flame'])
x=112; props.rect(x+2,7,12,3,P['wood2']); props.rect(x+3,10,2,5,P['wood0']); props.rect(x+11,10,2,5,P['wood0'])
x=128; props.rect(x+3,4,10,10,P['wood2']); props.h(x+3,4,10,P['wood3']); props.h(x+3,13,10,P['wood0']); props.v(x+7,4,10,P['wood1']); props.v(x+8,4,10,P['wood1'])
x=144; props.rect(x+5,3,7,12,P['wood2']); props.rect(x+4,5,9,8,P['wood1']); props.h(x+4,5,9,P['iron']); props.h(x+4,11,9,P['iron']); props.h(x+5,3,7,P['wood3'])
x=160; props.rect(x+5,6,7,9,P['straw']); props.h(x+6,5,5,P['paper']); props.set(x+7,4,P['wood1']); props.set(x+9,4,P['wood1'])
x=176
for xx,yy in ((6,5),(5,6),(4,8),(5,10),(7,11),(10,10),(11,8),(10,6),(8,5)): props.set(x+xx,yy,P['straw'])
x=192; props.rect(x+6,5,4,10,P['wood0']); props.h(x+5,5,6,P['wood2']); props.h(x+6,14,4,P['ink'])
x=208; props.rect(x+1,9,14,6,P['soil'])
for xx in (3,7,11): props.set(x+xx,8,P['herbHi']); props.set(x+xx-1,10,P['herb']); props.set(x+xx+1,10,P['herb'])
x=224; props.rect(x+1,10,14,5,P['soil'])
for xx,c in ((3,'clothR'),(7,'clothY'),(11,'clothB')): props.set(x+xx,8,P[c]); props.set(x+xx,9,P['green2'])
x=240
for yy in (7,10,13): props.h(x+2,yy,12,P['wood1']); props.set(x+3,yy-1,P['wood3']); props.set(x+12,yy-1,P['wood0'])

def R(tx,ty,x,y,w,h,c): props.rect(tx*16+x,ty*16+y,w,h,P[c])
def H(tx,ty,x,y,n,c): props.h(tx*16+x,ty*16+y,n,P[c])
R(0,2,5,13,22,14,'stone1'); R(0,2,7,11,18,13,'stone2'); H(0,2,7,11,18,'stone3'); R(0,2,9,14,14,7,'water'); R(0,2,8,3,2,11,'wood1'); R(0,2,22,3,2,11,'wood1'); H(0,2,8,3,16,'wood2')
R(2,2,4,7,24,14,'wood1'); R(2,2,6,9,20,10,'wood2'); R(2,2,8,10,7,5,'paper'); R(2,2,17,11,7,4,'paper'); R(2,2,8,21,2,10,'wood0'); R(2,2,22,21,2,10,'wood0')
R(4,2,3,13,26,3,'wood2'); R(4,2,5,16,3,15,'wood0'); R(4,2,24,16,3,15,'wood0')
for i,c in enumerate(('clothR','clothY','clothR','clothY')): R(4,2,3+i*7,5,7,7,c)
R(4,2,5,19,22,6,'wood1')
R(6,2,3,13,26,3,'wood2'); R(6,2,5,16,3,15,'wood0'); R(6,2,24,16,3,15,'wood0')
for i,c in enumerate(('clothB','paper','clothB','paper')): R(6,2,3+i*7,5,7,7,c)
R(6,2,5,19,22,6,'wood1')
R(8,2,4,12,20,9,'wood2'); R(8,2,7,8,17,5,'wood1'); R(8,2,3,21,4,7,'iron'); R(8,2,21,21,4,7,'iron'); R(8,2,24,15,7,2,'wood1')
R(10,2,4,5,3,26,'wood0'); R(10,2,25,5,3,26,'wood0'); R(10,2,4,5,24,3,'wood2'); R(10,2,14,8,2,16,'straw'); R(10,2,11,22,8,4,'wood2')
R(12,2,3,5,42,8,'wood1'); H(12,2,6,4,35,'wood3'); H(12,2,7,12,33,'wood0'); R(12,2,21,1,2,11,'wood2')
R(15,2,2,8,12,22,'wood1'); R(15,2,3,9,10,8,'wood2'); R(15,2,5,18,6,10,'ink'); H(15,2,1,7,14,'roof2')

OUT.mkdir(parents=True,exist_ok=True)
sha1=write_png(OUT/'town_structures_v1.png',structures.w,structures.height,structures.p)
sha2=write_png(OUT/'town_props_v1.png',props.w,props.height,props.p)
print('structures',sha1,'props',sha2)
