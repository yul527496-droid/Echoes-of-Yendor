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

/** Dense first slice of Morningcreek: gate, well, shops, residents and the Old Crow Inn. */
public class MorningcreekMainStreetLevel extends Level {

    public static final int WIDTH = 52;
    public static final int HEIGHT = 38;
    public static final int SOUTH_X = 26;
    public static final int SOUTH_Y = 36;
    public static final int INN_X = 26;
    public static final int INN_Y = 1;

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

    { color1 = 0x80985f; color2 = 0xc2b27b; viewDistance = 16; }

    @Override public String tilesTex() { return SURFACE_TILES; }
    @Override public String waterTex() { return SURFACE_WATER; }

    @Override
    public void playLevelMusic() {
        Music.INSTANCE.play(Assets.Music.THEME_1, true);
        ChapterOneAudio.outskirtsAmbience();
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);
        for (int y = 1; y < HEIGHT-1; y++) for (int x = 1; x < WIDTH-1; x++) map[cell(x,y)] = Terrain.GRASS;

        // A straight, readable civic spine. Short side spaces are optional, never required zig-zags.
        rect(22, 1, 30, 36, Terrain.EMPTY);
        rect(18, 17, 34, 24, Terrain.EMPTY_SP); // well square
        rect(24, 19, 28, 22, Terrain.WATER);
        map[cell(26,20)] = Terrain.EMPTY_DECO; // well lip / landmark

        // West/east building masses create a recognizable town street.
        building(4, 6, 17, 14, 15, 13);   // smithy
        building(34, 7, 47, 15, 36, 14);  // general shop
        building(5, 23, 18, 32, 16, 24);  // homes
        building(33, 24, 47, 33, 35, 25); // homes

        // Old Crow Inn dominates the north end; raven sign is visible on approach.
        rect(15, 2, 37, 8, Terrain.WALL);
        rect(18, 5, 34, 8, Terrain.EMPTY_SP);
        map[cell(26,7)] = Terrain.EMPTY;
        map[cell(30,9)] = Terrain.EMPTY_DECO; // raven sign

        // Street signs / notice clutter make navigation legible.
        map[cell(20,31)] = Terrain.EMPTY_DECO;
        map[cell(31,18)] = Terrain.EMPTY_DECO;
        map[cell(20,10)] = Terrain.EMPTY_DECO;

        int south = cell(SOUTH_X,SOUTH_Y);
        map[south] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, south, LevelTransition.Type.REGULAR_ENTRANCE));
        int inn = cell(INN_X,INN_Y);
        map[inn] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, inn, LevelTransition.Type.REGULAR_EXIT));

        SequelState state = SequelState.get();
        if (state != null) state.advanceTo(SequelState.Phase.MAIN_STREET_REACHED);
        return true;
    }

    private void building(int x1,int y1,int x2,int y2,int doorX,int doorY){
        rect(x1,y1,x2,y2,Terrain.WALL);
        rect(x1+2,y1+2,x2-2,y2-2,Terrain.EMPTY_SP);
        map[cell(doorX,doorY)] = Terrain.EMPTY;
    }

    private void rect(int x1,int y1,int x2,int y2,int terrain){
        for(int y=y1;y<=y2;y++) for(int x=x1;x<=x2;x++) if(x>0&&y>0&&x<WIDTH-1&&y<HEIGHT-1) map[cell(x,y)] = terrain;
    }

    public int cell(int x,int y){ return x + y*width(); }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) {
            SequelGame.enterOutskirtsFromTown();
            return true;
        }
        if (transition.type == LevelTransition.Type.REGULAR_EXIT) {
            SequelGame.enterOldCrowInn();
            return true;
        }
        return super.activateTransition(hero, transition);
    }

    @Override public Mob createMob(){ return null; }

    @Override
    protected void createMobs(){
        addVillager(20,28,"守门人","老王道来的？主街一直向北。老鸦旅店就在乌鸦招牌下面。");
        addVillager(34,20,"井边妇人","过了井继续往北走，你不会错过那只黑乌鸦。");
        addVillager(18,11,"铁匠学徒","旅店在街尽头。老板娘记得每个下过地牢的人。");
        addVillager(31,30,"送货人","今天旅店里人不少。别堵在门口就行。");
    }

    private void addVillager(int x,int y,String name,String line){
        SurfaceVillager v = new SurfaceVillager(name,line);
        v.pos = cell(x,y);
        mobs.add(v);
    }

    @Override protected void createItems(){}
    @Override public Actor addRespawner(){ return null; }
    @Override public int randomRespawnCell(Char ch){ return cell(SOUTH_X,SOUTH_Y-1); }
}
