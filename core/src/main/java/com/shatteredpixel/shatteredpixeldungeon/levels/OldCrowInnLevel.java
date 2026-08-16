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
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;

/** Warm first complete Morningcreek interior and the true Chapter 1 endpoint. */
public class OldCrowInnLevel extends Level {

    public static final int WIDTH=36, HEIGHT=26;
    public static final int DOOR_X=18, DOOR_Y=24, LEDGER_X=18, LEDGER_Y=7;

    private static final String INN_TILES="environment/tiles_inn_v1.png";
    private static final String SURFACE_WATER="environment/water_surface_v1.png";
    private static final String INN_MUSIC="music/echoes/ledger_tavern.mp3";

    { color1=0x6a5038; color2=0xb98a5d; viewDistance=12; }
    @Override public String tilesTex(){ return INN_TILES; }
    @Override public String waterTex(){ return SURFACE_WATER; }

    @Override
    public void playLevelMusic(){
        ChapterOneAudio.stopAmbience();
        Music.INSTANCE.play(INN_MUSIC,true);
        SequelState story=SequelState.get(); if(story!=null) story.syncObjective();
    }

    @Override
    protected boolean build(){
        setSize(WIDTH,HEIGHT);
        for(int i=0;i<length();i++) map[i]=Terrain.WALL;
        rect(3,3,32,24,Terrain.EMPTY_SP);
        rect(14,18,22,24,Terrain.EMPTY);
        rect(5,9,30,18,Terrain.EMPTY);
        rect(8,5,25,8,Terrain.EMPTY_SP);

        // Furniture footprints. The authored visuals make these read as an inn rather than a dungeon room.
        map[cell(18,7)]=Terrain.EMPTY_DECO;
        map[cell(7,11)]=Terrain.EMPTY_DECO;
        map[cell(11,13)]=Terrain.EMPTY_DECO;
        map[cell(15,15)]=Terrain.EMPTY_DECO;
        map[cell(24,13)]=Terrain.EMPTY_DECO;
        map[cell(28,16)]=Terrain.EMPTY_DECO;
        rect(27,4,31,7,Terrain.WALL);
        map[cell(27,8)]=Terrain.EMPTY_DECO;

        installVisualFoundation();

        int door=cell(DOOR_X,DOOR_Y); map[door]=Terrain.ENTRANCE; transitions.add(new LevelTransition(this,door,LevelTransition.Type.REGULAR_ENTRANCE));
        SequelState state=SequelState.get(); if(state!=null) state.advanceTo(SequelState.Phase.INN_REACHED);
        return true;
    }

    @Override
    public void restoreFromBundle(Bundle bundle){
        super.restoreFromBundle(bundle);
        if(width()!=WIDTH||height()!=HEIGHT){ create(); if(Dungeon.hero!=null) Dungeon.hero.pos=-1; return; }
        installVisualFoundation();
    }

    private void installVisualFoundation(){
        customTiles.removeIf(t->t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);
        customWalls.removeIf(t->t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);
        addWall(EchoesLandmarkTilemap.BAR,15,5);
        addWall(EchoesLandmarkTilemap.FIREPLACE,6,9);
        addTile(EchoesLandmarkTilemap.LEDGER_TABLE,17,7);
        addTile(EchoesLandmarkTilemap.TAVERN_TABLE,10,13);
        addTile(EchoesLandmarkTilemap.TAVERN_TABLE,23,13);
        addTile(EchoesLandmarkTilemap.TAVERN_TABLE,14,16);
    }
    private void addTile(int kind,int x,int y){ EchoesLandmarkTilemap art=new EchoesLandmarkTilemap(kind); art.pos(x,y); customTiles.add(art); }
    private void addWall(int kind,int x,int y){ EchoesLandmarkTilemap art=new EchoesLandmarkTilemap(kind); art.pos(x,y); customWalls.add(art); }

    private void rect(int x1,int y1,int x2,int y2,int terrain){ for(int y=y1;y<=y2;y++) for(int x=x1;x<=x2;x++) if(x>0&&y>0&&x<WIDTH-1&&y<HEIGHT-1) map[cell(x,y)]=terrain; }
    public int cell(int x,int y){ return x+y*width(); }

    @Override public boolean activateTransition(Hero hero,LevelTransition transition){ if(transition.type==LevelTransition.Type.REGULAR_ENTRANCE){ SequelGame.enterTownFromInn(); return true; } return super.activateTransition(hero,transition); }
    @Override public Mob createMob(){ return null; }
    @Override protected void createMobs(){
        addVillager(18,9,"老鸦旅店老板娘","坐吧。你从南边那条路来，对吗？等你喘口气，我给你看一本东西。");
        addVillager(10,14,"疲惫的商人","旧王道最近不太平。狼只是最普通的麻烦。");
        addVillager(25,15,"披斗篷的旅客","下过地牢的人？这里以前也来过几个。");
    }
    private void addVillager(int x,int y,String name,String line){ SurfaceVillager v=new SurfaceVillager(name,line); v.pos=cell(x,y); mobs.add(v); }
    @Override protected void createItems(){}
    @Override public Actor addRespawner(){ return null; }
    @Override public int randomRespawnCell(Char ch){ return cell(DOOR_X,DOOR_Y-1); }
}
