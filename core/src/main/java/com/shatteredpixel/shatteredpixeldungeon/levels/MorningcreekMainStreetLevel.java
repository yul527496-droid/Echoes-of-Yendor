/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

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
import com.watabou.utils.Bundle;

/** Dense Morningcreek main street: gate, well, shops, residents and the Old Crow Inn. */
public class MorningcreekMainStreetLevel extends Level {

    public static final int WIDTH=52, HEIGHT=38;
    public static final int SOUTH_X=26, SOUTH_Y=36, INN_X=26, INN_Y=7;

    private static final String TOWN_TILES="environment/tiles_town_v1.png";
    private static final String SURFACE_WATER="environment/water_surface_v1.png";

    { color1=0x747b5a; color2=0xbda974; viewDistance=16; }
    @Override public String tilesTex(){ return TOWN_TILES; }
    @Override public String waterTex(){ return SURFACE_WATER; }

    @Override public void playLevelMusic(){
        ChapterOneAudio.outskirtsAmbience();
        SequelState story=SequelState.get(); if(story!=null) story.syncObjective();
    }

    @Override protected boolean build(){
        setSize(WIDTH,HEIGHT);
        for(int y=1;y<HEIGHT-1;y++) for(int x=1;x<WIDTH-1;x++) map[cell(x,y)]=Terrain.GRASS;

        // Compact street with a broader civic square only around the well.
        rect(23,1,29,36,Terrain.EMPTY);
        rect(19,17,33,24,Terrain.EMPTY_SP);
        rect(24,19,28,22,Terrain.WATER);
        map[cell(26,20)]=Terrain.EMPTY_DECO;
        for(int y=27;y<=34;y+=3){ map[cell(23,y)]=Terrain.EMPTY_DECO; map[cell(29,y)]=Terrain.EMPTY_DECO; }

        building(4,6,17,14,15,13);
        building(34,7,47,15,36,14);
        building(5,23,18,32,16,24);
        building(33,24,47,33,35,25);

        rect(15,2,37,8,Terrain.WALL);
        rect(18,5,34,8,Terrain.EMPTY_SP);
        map[cell(INN_X,INN_Y)]=Terrain.EMPTY;
        map[cell(30,9)]=Terrain.EMPTY_DECO;
        map[cell(20,31)]=Terrain.EMPTY_DECO;
        map[cell(31,18)]=Terrain.EMPTY_DECO;
        map[cell(20,10)]=Terrain.EMPTY_DECO;
        map[cell(21,22)]=Terrain.EMPTY_DECO;
        map[cell(32,23)]=Terrain.EMPTY_DECO;

        installVisualFoundation();
        int south=cell(SOUTH_X,SOUTH_Y); map[south]=Terrain.ENTRANCE; transitions.add(new LevelTransition(this,south,LevelTransition.Type.REGULAR_ENTRANCE));
        int inn=cell(INN_X,INN_Y); map[inn]=Terrain.EXIT; transitions.add(new LevelTransition(this,inn,LevelTransition.Type.REGULAR_EXIT));
        SequelState state=SequelState.get(); if(state!=null) state.advanceTo(SequelState.Phase.MAIN_STREET_REACHED);
        return true;
    }

    @Override public void restoreFromBundle(Bundle bundle){ super.restoreFromBundle(bundle); if(width()!=WIDTH||height()!=HEIGHT){ create(); if(Dungeon.hero!=null) Dungeon.hero.pos=-1; return; } installVisualFoundation(); }

    private void building(int x1,int y1,int x2,int y2,int doorX,int doorY){ rect(x1,y1,x2,y2,Terrain.WALL); rect(x1+2,y1+2,x2-2,y2-2,Terrain.EMPTY_SP); map[cell(doorX,doorY)]=Terrain.EMPTY; }

    private void installVisualFoundation(){
        customTiles.removeIf(t->t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);
        customWalls.removeIf(t->t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);
        addWall(EchoesLandmarkTilemap.TOWN_GATE,23,33);
        addWall(EchoesLandmarkTilemap.BLACKSMITH,6,8);
        addWall(EchoesLandmarkTilemap.SHOP,36,9);
        addTile(EchoesLandmarkTilemap.WELL,25,19);
        addWall(EchoesLandmarkTilemap.OLD_CROW_INN,22,2);
        addTile(EchoesLandmarkTilemap.OLD_CROW_SIGN,30,7);
        addTile(EchoesLandmarkTilemap.NOTICE_BOARD,19,29);
    }
    private void addTile(int kind,int x,int y){ EchoesLandmarkTilemap art=new EchoesLandmarkTilemap(kind); art.pos(x,y); customTiles.add(art); }
    private void addWall(int kind,int x,int y){ EchoesLandmarkTilemap art=new EchoesLandmarkTilemap(kind); art.pos(x,y); customWalls.add(art); }
    private void rect(int x1,int y1,int x2,int y2,int terrain){ for(int y=y1;y<=y2;y++) for(int x=x1;x<=x2;x++) if(x>0&&y>0&&x<WIDTH-1&&y<HEIGHT-1) map[cell(x,y)]=terrain; }
    public int cell(int x,int y){ return x+y*width(); }

    @Override public boolean activateTransition(Hero hero,LevelTransition transition){
        if(transition.type==LevelTransition.Type.REGULAR_ENTRANCE){ ChapterOneAudio.stopAmbience(); SequelGame.enterOutskirtsFromTown(); return true; }
        if(transition.type==LevelTransition.Type.REGULAR_EXIT){ ChapterOneAudio.stopAmbience(); SequelGame.enterOldCrowInn(); return true; }
        return super.activateTransition(hero,transition);
    }

    @Override public Mob createMob(){ return null; }
    @Override protected void createMobs(){
        addVillager(20,28,"守门人","老王道来的？主街一直向北。老鸦旅店就在乌鸦招牌下面。");
        addVillager(34,20,"井边妇人","过了井继续往北走，你不会错过那只黑乌鸦。");
        addVillager(18,11,"铁匠学徒","旅店在街尽头。老板娘记得每个下过地牢的人。");
        addVillager(31,30,"送货人","今天旅店里人不少。别堵在门口就行。");
        addVillager(22,17,"买菜的镇民","井北边就是老鸦旅店。那块招牌比门还醒目。");
        addVillager(37,28,"店铺伙计","如果你找过去的下行者，先问旅店老板娘。");
    }
    private void addVillager(int x,int y,String name,String line){ SurfaceVillager v=new SurfaceVillager(name,line); v.pos=cell(x,y); mobs.add(v); }
    @Override protected void createItems(){}
    @Override public Actor addRespawner(){ return null; }
    @Override public int randomRespawnCell(Char ch){ return cell(SOUTH_X,SOUTH_Y-1); }
}
