/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SurfaceVillager;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesLandmarkTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesSurfaceTilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;

/** Settled farmland outside Morningcreek: fields, irrigation, farmsteads and a clear northbound road. */
public class MorningcreekOutskirtsLevel extends Level {

    public static final int WIDTH=52, HEIGHT=42;
    public static final int SOUTH_X=25, SOUTH_Y=40, NORTH_X=26, NORTH_Y=1;
    public static final int NOTICE_X=31, NOTICE_Y=8;

    private static final String FARM_TILES="environment/tiles_farm_v1.png";
    private static final String SURFACE_WATER="environment/water_surface_v1.png";
    private static final int[][] ROAD={{25,40},{24,34},{26,29},{27,24},{26,20},{27,15},{28,10},{26,1}};

    { color1=0x789b57; color2=0xb0c87a; viewDistance=16; }
    @Override public String tilesTex(){ return FARM_TILES; }
    @Override public String waterTex(){ return SURFACE_WATER; }

    @Override
    public void playLevelMusic(){
        Music.INSTANCE.play(Assets.Music.THEME_1,true);
        ChapterOneAudio.outskirtsAmbience();
        SequelState story=SequelState.get(); if(story!=null) story.syncObjective();
    }

    @Override
    protected boolean build(){
        setSize(WIDTH,HEIGHT);
        for(int y=1;y<HEIGHT-1;y++) for(int x=1;x<WIDTH-1;x++) map[cell(x,y)]=Terrain.GRASS;
        paintRoad(); paintFields(); paintIrrigation(); paintFarmstead(); paintOrchard(); paintTownApproach(); installVisualFoundation();
        int south=cell(SOUTH_X,SOUTH_Y); map[south]=Terrain.ENTRANCE; transitions.add(new LevelTransition(this,south,LevelTransition.Type.REGULAR_ENTRANCE));
        int north=cell(NORTH_X,NORTH_Y); map[north]=Terrain.EXIT; transitions.add(new LevelTransition(this,north,LevelTransition.Type.REGULAR_EXIT));
        SequelState story=SequelState.get(); if(story!=null) story.advanceTo(SequelState.Phase.OUTSKIRTS_REACHED);
        return true;
    }

    @Override
    public void restoreFromBundle(Bundle bundle){
        super.restoreFromBundle(bundle);
        if(width()!=WIDTH||height()!=HEIGHT){ create(); if(Dungeon.hero!=null) Dungeon.hero.pos=-1; return; }
        installVisualFoundation();
    }

    @Override public void buildFlagMaps(){ super.buildFlagMaps(); for(int i=0;i<length();i++) if(map[i]==Terrain.WATER){ passable[i]=false; avoid[i]=true; } }

    private void paintRoad(){
        for(int i=0;i<ROAD.length-1;i++) paintLine(ROAD[i][0],ROAD[i][1],ROAD[i+1][0],ROAD[i+1][1],2,Terrain.EMPTY);
        paintLine(25,30,13,29,1,Terrain.EMPTY_SP); paintLine(27,19,40,18,1,Terrain.EMPTY_SP);
    }
    private void paintFields(){
        for(int y=26;y<=36;y+=3){ for(int x=4;x<=15;x++) map[cell(x,y)]=Terrain.HIGH_GRASS; for(int x=36;x<=47;x++) map[cell(x,y-1)]=Terrain.HIGH_GRASS; }
        for(int y=11;y<=20;y+=3) for(int x=6;x<=16;x++) map[cell(x,y)]=Terrain.HIGH_GRASS;
    }
    private void paintIrrigation(){ for(int x=2;x<WIDTH-2;x++) map[cell(x,23)]=Terrain.WATER; for(int x=24;x<=30;x++) for(int y=22;y<=24;y++) map[cell(x,y)]=Terrain.EMPTY_DECO; }
    private void paintFarmstead(){
        rect(6,27,17,34,Terrain.EMPTY_SP); rect(7,28,13,31,Terrain.WALL); map[cell(15,31)]=Terrain.HIGH_GRASS;
        rect(36,28,46,35,Terrain.EMPTY_SP); rect(38,29,44,32,Terrain.WALL);
    }
    private void paintOrchard(){ for(int y=12;y<=20;y+=4) for(int x=38;x<=47;x+=3) map[cell(x,y)]=Terrain.WALL; }
    private void paintTownApproach(){
        rect(21,5,36,10,Terrain.EMPTY_SP); map[cell(NOTICE_X,NOTICE_Y)]=Terrain.EMPTY_DECO; map[cell(25,7)]=Terrain.EMPTY_DECO;
        rect(5,2,15,6,Terrain.WALL); rect(37,2,47,7,Terrain.WALL); rect(2,26,3,38,Terrain.WALL); rect(48,25,49,38,Terrain.WALL);
    }

    private void installVisualFoundation(){
        customTiles.removeIf(t->t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);
        customWalls.removeIf(t->t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);
        addWall(EchoesLandmarkTilemap.FARMHOUSE,8,28);
        addWall(EchoesLandmarkTilemap.FARMHOUSE,39,29);
        addTile(EchoesLandmarkTilemap.NOTICE_BOARD,30,7);
        addTile(EchoesLandmarkTilemap.SIGNPOST,25,7);
    }
    private void addTile(int kind,int x,int y){ EchoesLandmarkTilemap art=new EchoesLandmarkTilemap(kind); art.pos(x,y); customTiles.add(art); }
    private void addWall(int kind,int x,int y){ EchoesLandmarkTilemap art=new EchoesLandmarkTilemap(kind); art.pos(x,y); customWalls.add(art); }

    private void paintLine(int x1,int y1,int x2,int y2,int radius,int terrain){
        int steps=Math.max(Math.abs(x2-x1),Math.abs(y2-y1));
        for(int i=0;i<=steps;i++){ float t=steps==0?0:i/(float)steps; int x=Math.round(x1+(x2-x1)*t),y=Math.round(y1+(y2-y1)*t);
            for(int dy=-radius;dy<=radius;dy++) for(int dx=-radius;dx<=radius;dx++) if(Math.abs(dx)+Math.abs(dy)<=radius+1&&inside(x+dx,y+dy)) map[cell(x+dx,y+dy)]=terrain; }
    }
    private void rect(int x1,int y1,int x2,int y2,int terrain){ for(int y=y1;y<=y2;y++) for(int x=x1;x<=x2;x++) if(inside(x,y)) map[cell(x,y)]=terrain; }
    private boolean inside(int x,int y){ return x>0&&y>0&&x<WIDTH-1&&y<HEIGHT-1; }
    public int cell(int x,int y){ return x+y*width(); }

    @Override
    public boolean activateTransition(Hero hero,LevelTransition transition){
        if(transition.type==LevelTransition.Type.REGULAR_ENTRANCE){ ChapterOneAudio.stopAmbience(); SequelGame.enterOldRoadFromOutskirts(); return true; }
        if(transition.type==LevelTransition.Type.REGULAR_EXIT){ SequelState story=SequelState.get(); if(story!=null) story.markInvestigationKnown(); ChapterOneAudio.stopAmbience(); SequelGame.enterMorningcreekMainStreet(); return true; }
        return super.activateTransition(hero,transition);
    }

    @Override public Mob createMob(){ return null; }
    @Override protected void createMobs(){
        addVillager(16,31,"田里的农夫","下午好。去镇里就沿这条大路往北。");
        addVillager(18,19,"修篱笆的人","过了水渠就是镇口，别绕田埂。");
        addVillager(39,18,"好奇的孩子","老鸦旅店的招牌是一只特别大的黑乌鸦！");
        addVillager(20,10,"农舍老妇","老鸦旅店？进镇后过井，一直往北。");
    }
    private void addVillager(int x,int y,String name,String line){ SurfaceVillager v=new SurfaceVillager(name,line); v.pos=cell(x,y); mobs.add(v); }
    @Override protected void createItems(){}
    @Override public Actor addRespawner(){ return null; }
    @Override public int randomRespawnCell(Char ch){ return cell(SOUTH_X,SOUTH_Y-1); }
}
