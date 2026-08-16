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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RoadDonkey;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RoadFarmer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.YendorBird;
import com.shatteredpixel.shatteredpixeldungeon.items.RoadsideNote;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesLandmarkTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesSurfaceTilemap;
import com.watabou.utils.Bundle;

/** Surface return: ruin mouth, dirt camp, meandering stream and a readable road north. */
public class SurfaceEntranceLevel extends Level {

    public static final int WIDTH=48, HEIGHT=38;
    public static final int ENTRANCE_X=24, ENTRANCE_Y=36;
    public static final int NORTH_X=32, NORTH_Y=1;
    public static final int FARMER_MEET_X=31, FARMER_MEET_Y=9;
    public static final int FARMER_LEAVE_X=32, FARMER_LEAVE_Y=2;
    public static final int STREAM_Y=21;
    public static final int CAMP_X1=7, CAMP_X2=17, CAMP_Y1=24, CAMP_Y2=30;
    private static final String SURFACE_TILES="environment/tiles_surface_v1.png";
    private static final String SURFACE_WATER="environment/water_surface_v1.png";
    private static final int[][] ROAD={{24,36},{23,32},{25,28},{27,24},{27,21},{28,17},{30,13},{31,9},{32,1}};

    { color1=0x668f4d; color2=0xa8c36d; viewDistance=16; }
    @Override public String tilesTex(){ return SURFACE_TILES; }
    @Override public String waterTex(){ return SURFACE_WATER; }

    @Override public void playLevelMusic(){
        ChapterOneAudio.surfaceAmbience();
        SequelState story=SequelState.get(); if(story!=null) story.syncObjective();
    }

    @Override protected boolean build(){
        setSize(WIDTH,HEIGHT);
        fillInterior(Terrain.GRASS);
        paintForestMasses();
        paintRoad();
        paintCamp();
        paintStreamAndBridge();
        paintDungeonMouth();
        paintLandmarks();
        installVisualFoundation();
        int entrance=cell(ENTRANCE_X,ENTRANCE_Y); map[entrance]=Terrain.ENTRANCE; transitions.add(new LevelTransition(this,entrance,LevelTransition.Type.REGULAR_ENTRANCE));
        int north=cell(NORTH_X,NORTH_Y); map[north]=Terrain.EXIT; transitions.add(new LevelTransition(this,north,LevelTransition.Type.REGULAR_EXIT));
        SequelState story=SequelState.get(); if(story!=null) story.advanceTo(SequelState.Phase.SURFACE_REACHED);
        return true;
    }

    @Override public void restoreFromBundle(Bundle bundle){
        super.restoreFromBundle(bundle);
        if(width()!=WIDTH||height()!=HEIGHT){ create(); if(Dungeon.hero!=null) Dungeon.hero.pos=-1; return; }
        installVisualFoundation();
    }

    @Override public void buildFlagMaps(){
        super.buildFlagMaps();
        for(int i=0;i<length();i++) if(map[i]==Terrain.WATER){ passable[i]=false; avoid[i]=true; }
    }

    private void fillInterior(int terrain){ for(int y=1;y<HEIGHT-1;y++) for(int x=1;x<WIDTH-1;x++) map[cell(x,y)]=terrain; }

    private void paintRoad(){
        for(int i=0;i<ROAD.length-1;i++) paintLine(ROAD[i][0],ROAD[i][1],ROAD[i+1][0],ROAD[i+1][1],1,Terrain.EMPTY);
        paintLine(24,27,15,27,1,Terrain.EMPTY);
        int[][] worn={{23,34},{24,31},{26,26},{29,16},{30,11},{31,6},{21,27},{17,27}};
        for(int[] p:worn) if(inside(p[0],p[1])) map[cell(p[0],p[1])]=Terrain.EMPTY_DECO;
    }

    private void paintStreamAndBridge(){
        for(int x=1;x<WIDTH-1;x++){
            int bend=x<8?1:x<17?0:x<24?-1:x<34?0:x<41?1:0;
            int center=STREAM_Y+bend;
            map[cell(x,center)]=Terrain.WATER;
            map[cell(x,Math.min(HEIGHT-2,center+1))]=Terrain.WATER;
            if(x%7==2&&inside(x,center-1)) map[cell(x,center-1)]=Terrain.HIGH_GRASS;
        }
        rect(25,19,29,24,Terrain.EMPTY);
        rect(26,20,28,23,Terrain.EMPTY_DECO);
    }

    private void paintCamp(){
        rect(CAMP_X1,CAMP_Y1,CAMP_X2,CAMP_Y2,Terrain.EMPTY);
        map[cell(11,27)]=Terrain.EMBERS;
        map[cell(9,26)]=Terrain.EMPTY_DECO;
        map[cell(15,29)]=Terrain.EMPTY_DECO;
        map[cell(8,29)]=Terrain.HIGH_GRASS;
        map[cell(16,25)]=Terrain.HIGH_GRASS;
    }

    private void paintDungeonMouth(){
        rect(19,32,29,36,Terrain.EMPTY_SP);
        rect(21,34,27,36,Terrain.EMPTY);
        rect(22,35,26,36,Terrain.EMPTY_DECO);
        map[cell(19,33)]=Terrain.HIGH_GRASS;
        map[cell(29,34)]=Terrain.HIGH_GRASS;
    }

    private void paintForestMasses(){
        rect(2,2,12,17,Terrain.WALL); rect(2,31,12,36,Terrain.WALL);
        rect(38,2,45,14,Terrain.WALL); rect(39,26,45,36,Terrain.WALL);
        rect(3,20,7,23,Terrain.WALL); rect(39,17,45,19,Terrain.WALL);
        int[][] clear={{12,5},{12,9},{11,14},{4,17},{7,17},{38,7},{38,12},{39,28},{39,33},{7,31},{11,32},{39,18}};
        for(int[] p:clear) if(inside(p[0],p[1])) map[cell(p[0],p[1])]=Terrain.GRASS;
        int[][] fringe={{13,4},{13,8},{12,13},{5,18},{9,18},{37,6},{37,11},{38,29},{38,34},{13,32},{8,30},{38,18},{42,20}};
        for(int[] p:fringe) if(inside(p[0],p[1])&&map[cell(p[0],p[1])]!=Terrain.WALL) map[cell(p[0],p[1])]=Terrain.HIGH_GRASS;
    }

    private void paintLandmarks(){
        rect(16,30,19,32,Terrain.EMPTY_DECO);
        map[cell(33,15)]=Terrain.EMPTY_DECO; map[cell(34,12)]=Terrain.EMPTY_DECO; map[cell(30,6)]=Terrain.EMPTY_DECO;
        // Real terrain clusters: large meadows now have readable quiet/dense patches instead of one uniform carpet.
        int[][] grass={
                {20,30},{29,30},{20,25},{31,18},{26,15},{34,7},{18,28},{35,22},
                {15,6},{16,6},{15,7},{19,9},{20,9},{19,10},{14,14},{15,14},{14,15},
                {18,17},{19,17},{18,18},{11,19},{12,19},{13,19},{34,10},{35,10},{35,11},
                {33,16},{34,16},{34,17},{36,25},{37,25},{36,26},{31,27},{32,27}
        };
        for(int[] p:grass) if(inside(p[0],p[1])&&map[cell(p[0],p[1])]==Terrain.GRASS) map[cell(p[0],p[1])]=Terrain.HIGH_GRASS;
    }

    private void installVisualFoundation(){
        customTiles.removeIf(t->t instanceof EchoesSurfaceTilemap||t instanceof EchoesLandmarkTilemap);
        customWalls.removeIf(t->t instanceof EchoesSurfaceTilemap||t instanceof EchoesLandmarkTilemap);

        addSurfaceTile(EchoesSurfaceTilemap.DUNGEON_MOUTH,21,32);
        addSurfaceTile(EchoesSurfaceTilemap.CAMP_SCENE,8,24);
        addSurfaceTile(EchoesSurfaceTilemap.BRIDGE_SCENE,25,19);
        addLandmarkTile(EchoesLandmarkTilemap.SIGNPOST,30,6);

        int[][] forestA={{4,4},{8,9},{3,13},{39,4},{40,10},{40,28},{4,32}};
        for(int[] p:forestA) addSurfaceWall(EchoesSurfaceTilemap.FOREST_EDGE_A,p[0],p[1]);
        int[][] forestB={{9,3},{4,8},{39,7},{42,14},{41,31},{8,34}};
        for(int[] p:forestB) addSurfaceWall(EchoesSurfaceTilemap.FOREST_EDGE_B,p[0],p[1]);
        int[][] forestDeep={{2,3},{2,11},{42,3},{42,9},{42,27},{2,34}};
        for(int[] p:forestDeep) addSurfaceWall(EchoesSurfaceTilemap.FOREST_DEEP,p[0],p[1]);
        addSurfaceTile(EchoesSurfaceTilemap.FOREST_LOG,13,16);
        addSurfaceTile(EchoesSurfaceTilemap.FOREST_ROCKS,36,14);
        addSurfaceTile(EchoesSurfaceTilemap.FOREST_BUSH,12,8);
        addSurfaceTile(EchoesSurfaceTilemap.FOREST_STUMP,38,25);

        int[][] roadDetails={
                {EchoesSurfaceTilemap.ROAD_RUTS,24,34},{EchoesSurfaceTilemap.ROAD_VERGE_L,22,31},
                {EchoesSurfaceTilemap.ROAD_VERGE_R,25,30},{EchoesSurfaceTilemap.ROAD_STONES,25,27},
                {EchoesSurfaceTilemap.ROAD_TRAMPLE,27,24},{EchoesSurfaceTilemap.ROAD_MUD,28,18},
                {EchoesSurfaceTilemap.ROAD_SCAR,29,14},{EchoesSurfaceTilemap.ROAD_WEEDS,31,10},
                {EchoesSurfaceTilemap.ROAD_RUTS,31,5},{EchoesSurfaceTilemap.ROAD_STONES,18,27}
        };
        for(int[] p:roadDetails) addSurfaceTile(p[0],p[1],p[2]);

        int[][] riverDetails={
                {EchoesSurfaceTilemap.RIVER_REEDS,5,20},{EchoesSurfaceTilemap.RIVER_BANK_TOP,10,20},
                {EchoesSurfaceTilemap.RIVER_STONES,15,21},{EchoesSurfaceTilemap.RIVER_RIPPLE,20,20},
                {EchoesSurfaceTilemap.RIVER_FOAM,23,21},{EchoesSurfaceTilemap.RIVER_WET_GRASS,32,20},
                {EchoesSurfaceTilemap.RIVER_ROOTS,38,21},{EchoesSurfaceTilemap.RIVER_BANK_BOTTOM,43,22}
        };
        for(int[] p:riverDetails) addSurfaceTile(p[0],p[1],p[2]);

        // Visual clusters, not confetti. Each 2x2 patch has a local material story.
        grassCluster(14,5, EchoesSurfaceTilemap.GRASS_TUFT_A,EchoesSurfaceTilemap.GRASS_FLOWERS,EchoesSurfaceTilemap.GRASS_TUFT_B,EchoesSurfaceTilemap.GRASS_MIX);
        grassCluster(18,9, EchoesSurfaceTilemap.GRASS_STONES,EchoesSurfaceTilemap.GRASS_TUFT_A,EchoesSurfaceTilemap.GRASS_MIX,EchoesSurfaceTilemap.GRASS_FLOWERS);
        grassCluster(13,14,EchoesSurfaceTilemap.GRASS_TUFT_B,EchoesSurfaceTilemap.GRASS_MIX,EchoesSurfaceTilemap.GRASS_STONES,EchoesSurfaceTilemap.GRASS_TUFT_A);
        grassCluster(18,17,EchoesSurfaceTilemap.GRASS_TUFT_A,EchoesSurfaceTilemap.GRASS_TUFT_B,EchoesSurfaceTilemap.GRASS_FLOWERS,EchoesSurfaceTilemap.GRASS_MIX);
        grassCluster(10,18,EchoesSurfaceTilemap.GRASS_STONES,EchoesSurfaceTilemap.GRASS_TUFT_A,EchoesSurfaceTilemap.GRASS_TUFT_B,EchoesSurfaceTilemap.GRASS_MIX);
        grassCluster(34,9, EchoesSurfaceTilemap.GRASS_TUFT_A,EchoesSurfaceTilemap.GRASS_FLOWERS,EchoesSurfaceTilemap.GRASS_MIX,EchoesSurfaceTilemap.GRASS_STONES);
        grassCluster(33,15,EchoesSurfaceTilemap.GRASS_STONES,EchoesSurfaceTilemap.GRASS_TUFT_B,EchoesSurfaceTilemap.GRASS_FLOWERS,EchoesSurfaceTilemap.GRASS_MIX);
        grassCluster(35,24,EchoesSurfaceTilemap.GRASS_DAMP,EchoesSurfaceTilemap.GRASS_TUFT_A,EchoesSurfaceTilemap.GRASS_MIX,EchoesSurfaceTilemap.GRASS_STONES);
        grassCluster(30,27,EchoesSurfaceTilemap.GRASS_TUFT_B,EchoesSurfaceTilemap.GRASS_FLOWERS,EchoesSurfaceTilemap.GRASS_STONES,EchoesSurfaceTilemap.GRASS_MIX);
        grassCluster(17,22,EchoesSurfaceTilemap.GRASS_DAMP,EchoesSurfaceTilemap.GRASS_DAMP,EchoesSurfaceTilemap.GRASS_TUFT_A,EchoesSurfaceTilemap.GRASS_STONES);
        grassCluster(7,29, EchoesSurfaceTilemap.GRASS_TRAMPLED,EchoesSurfaceTilemap.GRASS_BARE,EchoesSurfaceTilemap.GRASS_TUFT_A,EchoesSurfaceTilemap.GRASS_MIX);
        grassCluster(17,31,EchoesSurfaceTilemap.GRASS_TRAMPLED,EchoesSurfaceTilemap.GRASS_BARE,EchoesSurfaceTilemap.GRASS_STONES,EchoesSurfaceTilemap.GRASS_TUFT_B);
    }

    private void grassCluster(int x,int y,int a,int b,int c,int d){
        addSurfaceTile(a,x,y); addSurfaceTile(b,x+1,y); addSurfaceTile(c,x,y+1); addSurfaceTile(d,x+1,y+1);
    }
    private void addSurfaceTile(int kind,int x,int y){ EchoesSurfaceTilemap art=new EchoesSurfaceTilemap(kind); art.pos(x,y); customTiles.add(art); }
    private void addSurfaceWall(int kind,int x,int y){ EchoesSurfaceTilemap art=new EchoesSurfaceTilemap(kind); art.pos(x,y); customWalls.add(art); }
    private void addLandmarkTile(int kind,int x,int y){ EchoesLandmarkTilemap art=new EchoesLandmarkTilemap(kind); art.pos(x,y); customTiles.add(art); }

    private void paintLine(int x1,int y1,int x2,int y2,int radius,int terrain){
        int steps=Math.max(Math.abs(x2-x1),Math.abs(y2-y1));
        for(int i=0;i<=steps;i++){
            float t=steps==0?0:i/(float)steps;
            int x=Math.round(x1+(x2-x1)*t), y=Math.round(y1+(y2-y1)*t);
            for(int dy=-radius;dy<=radius;dy++) for(int dx=-radius;dx<=radius;dx++) if(Math.abs(dx)+Math.abs(dy)<=radius+1&&inside(x+dx,y+dy)) map[cell(x+dx,y+dy)]=terrain;
        }
    }
    private void rect(int x1,int y1,int x2,int y2,int terrain){ for(int y=y1;y<=y2;y++) for(int x=x1;x<=x2;x++) if(inside(x,y)) map[cell(x,y)]=terrain; }
    private boolean inside(int x,int y){ return x>0&&y>0&&x<WIDTH-1&&y<HEIGHT-1; }
    public int cell(int x,int y){ return x+y*width(); }

    @Override public boolean activateTransition(Hero hero,LevelTransition transition){
        if(transition.type==LevelTransition.Type.REGULAR_ENTRANCE){ ChapterOneAudio.stopAmbience(); SequelGame.enterFinalStairFromSurface(); return true; }
        if(transition.type==LevelTransition.Type.REGULAR_EXIT){
            SequelState story=SequelState.get();
            if(story==null||!story.investigationKnown){ com.shatteredpixel.shatteredpixeldungeon.utils.GLog.p("那位赶车老人还没走远。刚才发生的事不能就这样丢在身后。"); return false; }
            ChapterOneAudio.stopAmbience(); SequelGame.enterOldKingsRoad(); return true;
        }
        return super.activateTransition(hero,transition);
    }

    @Override public Mob createMob(){ return null; }
    @Override protected void createMobs(){
        SequelState story=SequelState.get(); if(story==null) return;
        if(!story.isAtLeast(SequelState.Phase.FARMER_DEPARTED)){
            boolean alreadyMet=story.isAtLeast(SequelState.Phase.FARMER_NORMAL_TALK_DONE);
            RoadFarmer farmer=new RoadFarmer(); farmer.pos=alreadyMet?cell(FARMER_MEET_X,FARMER_MEET_Y):cell(34,3); mobs.add(farmer);
            RoadDonkey donkey=new RoadDonkey(); donkey.pos=alreadyMet?cell(FARMER_MEET_X+1,FARMER_MEET_Y-1):cell(35,3); mobs.add(donkey);
        }
        if(!story.birdGone){ YendorBird bird=new YendorBird(); bird.pos=cell(27,29); mobs.add(bird); }
    }
    @Override protected void createItems(){ SequelState story=SequelState.get(); if(story!=null&&!story.campNoteTaken) drop(new RoadsideNote(),cell(12,28)); }
    @Override public Actor addRespawner(){ return null; }
    @Override public int randomRespawnCell(Char ch){ return cell(ENTRANCE_X,ENTRANCE_Y-1); }
}
