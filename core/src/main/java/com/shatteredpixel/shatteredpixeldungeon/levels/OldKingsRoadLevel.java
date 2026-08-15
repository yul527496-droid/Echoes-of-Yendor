/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RoadWolf;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.noosa.audio.Music;

/** Compact old road: human-made ruins, shrine, wagon and one readable wolf clearing. */
public class OldKingsRoadLevel extends Level {

    public static final int WIDTH = 56;
    public static final int HEIGHT = 40;
    public static final int SOUTH_X = 27;
    public static final int SOUTH_Y = 38;
    public static final int NORTH_X = 30;
    public static final int NORTH_Y = 1;

    public static final int SHRINE_X = 14;
    public static final int SHRINE_Y = 27;
    public static final int WAGON_X = 40;
    public static final int WAGON_Y = 13;

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

    private static final int[][] ROAD = {
            {27,38},{27,33},{25,29},{28,25},{32,22},{31,18},{35,14},{31,10},{30,1}
    };

    { color1 = 0x6e8f50; color2 = 0x94ad61; viewDistance = 15; }

    @Override public String tilesTex(){ return SURFACE_TILES; }
    @Override public String waterTex(){ return SURFACE_WATER; }

    @Override
    public void playLevelMusic(){
        Music.INSTANCE.play(Assets.Music.THEME_1,true);
        ChapterOneAudio.oldRoadAmbience();
    }

    @Override
    protected boolean build(){
        setSize(WIDTH,HEIGHT);
        for(int y=1;y<HEIGHT-1;y++) for(int x=1;x<WIDTH-1;x++) map[cell(x,y)] = Terrain.GRASS;

        paintForest();
        paintRoad();
        paintShrine();
        paintWolfArena();
        paintBrokenWagon();
        paintSignpost();

        int south = cell(SOUTH_X,SOUTH_Y);
        map[south] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this,south,LevelTransition.Type.REGULAR_ENTRANCE));
        int north = cell(NORTH_X,NORTH_Y);
        map[north] = Terrain.EXIT;
        transitions.add(new LevelTransition(this,north,LevelTransition.Type.REGULAR_EXIT));

        SequelState story = SequelState.get();
        if(story != null) story.advanceTo(SequelState.Phase.OLD_ROAD_REACHED);
        return true;
    }

    private void paintRoad(){
        for(int i=0;i<ROAD.length-1;i++) paintLine(ROAD[i][0],ROAD[i][1],ROAD[i+1][0],ROAD[i+1][1],2,Terrain.EMPTY);
        // Short shrine detour: visible from the trunk and only a few seconds away.
        paintLine(25,29,14,27,1,Terrain.EMPTY_SP);
        // Wagon detour reconnects quickly instead of becoming a second maze.
        paintLine(34,15,40,13,1,Terrain.EMPTY_SP);
    }

    private void paintForest(){
        rect(2,2,17,16,Terrain.WALL);
        rect(2,31,18,38,Terrain.WALL);
        rect(43,2,53,18,Terrain.WALL);
        rect(44,27,53,38,Terrain.WALL);
        rect(3,18,9,25,Terrain.WALL);
        rect(47,20,53,25,Terrain.WALL);
        for(int y=4;y<HEIGHT-3;y+=4){
            int x=18+(y*7)%22;
            if(inside(x,y) && map[cell(x,y)]!=Terrain.WALL) map[cell(x,y)] = Terrain.HIGH_GRASS;
        }
    }

    private void paintShrine(){
        rect(11,24,17,30,Terrain.EMPTY_SP);
        map[cell(SHRINE_X,SHRINE_Y)] = Terrain.EMPTY_DECO;
        map[cell(SHRINE_X-1,SHRINE_Y)] = Terrain.EMBERS;
    }

    private void paintWolfArena(){
        rect(23,18,40,26,Terrain.EMPTY);
        for(int x=24;x<=39;x+=4){
            map[cell(x,18)] = Terrain.HIGH_GRASS;
            map[cell(x,26)] = Terrain.HIGH_GRASS;
        }
        map[cell(29,21)] = Terrain.HIGH_GRASS;
        map[cell(37,24)] = Terrain.HIGH_GRASS;
    }

    private void paintBrokenWagon(){
        rect(37,10,44,16,Terrain.EMPTY_SP);
        map[cell(WAGON_X,WAGON_Y)] = Terrain.EMPTY_DECO;
        map[cell(WAGON_X+1,WAGON_Y)] = Terrain.EMPTY_DECO;
        map[cell(WAGON_X+2,WAGON_Y)] = Terrain.EMPTY_DECO;
    }

    private void paintSignpost(){
        rect(27,5,35,9,Terrain.EMPTY_SP);
        map[cell(31,7)] = Terrain.EMPTY_DECO;
    }

    private void paintLine(int x1,int y1,int x2,int y2,int radius,int terrain){
        int steps=Math.max(Math.abs(x2-x1),Math.abs(y2-y1));
        for(int i=0;i<=steps;i++){
            float t=steps==0?0:i/(float)steps;
            int x=Math.round(x1+(x2-x1)*t);
            int y=Math.round(y1+(y2-y1)*t);
            for(int dy=-radius;dy<=radius;dy++) for(int dx=-radius;dx<=radius;dx++){
                if(Math.abs(dx)+Math.abs(dy)<=radius+1 && inside(x+dx,y+dy)) map[cell(x+dx,y+dy)] = terrain;
            }
        }
    }

    private void rect(int x1,int y1,int x2,int y2,int terrain){
        for(int y=y1;y<=y2;y++) for(int x=x1;x<=x2;x++) if(inside(x,y)) map[cell(x,y)] = terrain;
    }
    private boolean inside(int x,int y){ return x>0&&y>0&&x<WIDTH-1&&y<HEIGHT-1; }
    public int cell(int x,int y){ return x+y*width(); }

    @Override
    public boolean activateTransition(Hero hero,LevelTransition transition){
        if(transition.type==LevelTransition.Type.REGULAR_ENTRANCE){
            ChapterOneAudio.stopAmbience();
            SequelGame.enterSurfaceFromOldRoad();
            return true;
        }
        if(transition.type==LevelTransition.Type.REGULAR_EXIT){
            SequelState story=SequelState.get();
            if(story!=null && !story.wolvesDefeated && !story.isAtLeast(SequelState.Phase.WOLVES_DEFEATED)){
                com.shatteredpixel.shatteredpixeldungeon.utils.GLog.p("旧王道前方还有野兽徘徊。");
                return false;
            }
            ChapterOneAudio.stopAmbience();
            SequelGame.enterMorningcreekOutskirts();
            return true;
        }
        return super.activateTransition(hero,transition);
    }

    @Override public Mob createMob(){ return null; }

    @Override
    protected void createMobs(){
        SequelState story=SequelState.get();
        if(story==null || story.wolvesDefeated || story.isAtLeast(SequelState.Phase.WOLVES_DEFEATED)) return;
        RoadWolf first=new RoadWolf();
        first.pos=cell(36,21);
        mobs.add(first);
        RoadWolf second=new RoadWolf();
        second.pos=cell(29,23);
        mobs.add(second);
    }

    @Override protected void createItems(){}
    @Override public Actor addRespawner(){ return null; }
    @Override public int randomRespawnCell(Char ch){ return cell(SOUTH_X,SOUTH_Y-1); }
}
