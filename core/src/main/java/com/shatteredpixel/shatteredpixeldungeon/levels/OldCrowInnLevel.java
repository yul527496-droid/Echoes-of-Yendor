/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SurfaceVillager;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.noosa.audio.Music;

/** First complete interior in Morningcreek and the true Chapter 1 endpoint. */
public class OldCrowInnLevel extends Level {

    public static final int WIDTH = 36;
    public static final int HEIGHT = 26;
    public static final int DOOR_X = 18;
    public static final int DOOR_Y = 24;
    public static final int LEDGER_X = 18;
    public static final int LEDGER_Y = 7;

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

    { color1 = 0x7b684d; color2 = 0xc49a68; viewDistance = 12; }

    @Override public String tilesTex(){ return SURFACE_TILES; }
    @Override public String waterTex(){ return SURFACE_WATER; }

    @Override
    public void playLevelMusic(){
        Music.INSTANCE.play(Assets.Music.THEME_1, true);
        ChapterOneAudio.stopAmbience();
    }

    @Override
    protected boolean build(){
        setSize(WIDTH,HEIGHT);
        for(int i=0;i<length();i++) map[i] = Terrain.WALL;
        rect(3,3,32,24,Terrain.EMPTY_SP);

        // Entry hall and central common room.
        rect(14,18,22,24,Terrain.EMPTY);
        rect(5,9,30,18,Terrain.EMPTY);

        // Bar, fireplace, tables, blocked upstairs access.
        rect(8,5,25,8,Terrain.EMPTY_SP);
        map[cell(18,7)] = Terrain.EMPTY_DECO; // Descenders' Ledger on the bar.
        map[cell(7,11)] = Terrain.EMPTY_DECO; // fireplace
        map[cell(11,13)] = Terrain.EMPTY_DECO;
        map[cell(15,15)] = Terrain.EMPTY_DECO;
        map[cell(24,13)] = Terrain.EMPTY_DECO;
        map[cell(28,16)] = Terrain.EMPTY_DECO;
        rect(27,4,31,7,Terrain.WALL); // stairs/rooms intentionally blocked in Chapter 1
        map[cell(27,8)] = Terrain.EMPTY_DECO;

        int door = cell(DOOR_X,DOOR_Y);
        map[door] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this,door,LevelTransition.Type.REGULAR_ENTRANCE));

        SequelState state = SequelState.get();
        if(state != null) state.advanceTo(SequelState.Phase.INN_REACHED);
        return true;
    }

    private void rect(int x1,int y1,int x2,int y2,int terrain){
        for(int y=y1;y<=y2;y++) for(int x=x1;x<=x2;x++) if(x>0&&y>0&&x<WIDTH-1&&y<HEIGHT-1) map[cell(x,y)] = terrain;
    }

    public int cell(int x,int y){ return x+y*width(); }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition){
        if(transition.type == LevelTransition.Type.REGULAR_ENTRANCE){
            SequelGame.enterTownFromInn();
            return true;
        }
        return super.activateTransition(hero,transition);
    }

    @Override public Mob createMob(){ return null; }

    @Override
    protected void createMobs(){
        addVillager(18,9,"老鸦旅店老板娘","坐吧。你从南边那条路来，对吗？等你喘口气，我给你看一本东西。");
        addVillager(10,14,"疲惫的商人","旧王道最近不太平。狼只是最普通的麻烦。");
        addVillager(25,15,"披斗篷的旅客","下过地牢的人？这里以前也来过几个。");
    }

    private void addVillager(int x,int y,String name,String line){
        SurfaceVillager v = new SurfaceVillager(name,line);
        v.pos = cell(x,y);
        mobs.add(v);
    }

    @Override protected void createItems(){}
    @Override public Actor addRespawner(){ return null; }
    @Override public int randomRespawnCell(Char ch){ return cell(DOOR_X,DOOR_Y-1); }
}
