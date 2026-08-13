/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Music;

/**
 * First look at settled surface land. The town proper is intentionally left for 0.0.6.
 */
public class MorningcreekOutskirtsLevel extends Level {

    private static final int WIDTH = 34;
    private static final int HEIGHT = 26;
    private static final int SOUTH_X = 16;
    private static final int SOUTH_Y = 24;
    private static final int NORTH_X = 17;
    private static final int NORTH_Y = 1;

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

    {
        color1 = 0x789b57;
        color2 = 0xb0c87a;
        viewDistance = 16;
    }

    @Override
    public String tilesTex() {
        return SURFACE_TILES;
    }

    @Override
    public String waterTex() {
        return SURFACE_WATER;
    }

    @Override
    public void playLevelMusic() {
        Music.INSTANCE.play(Assets.Music.THEME_1, true);
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);

        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                map[cell(x, y)] = Terrain.GRASS;
            }
        }

        paintRoad();
        paintFields();
        paintHedges();

        int south = cell(SOUTH_X, SOUTH_Y);
        map[south] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, south, LevelTransition.Type.REGULAR_ENTRANCE));

        int north = cell(NORTH_X, NORTH_Y);
        map[north] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, north, LevelTransition.Type.REGULAR_EXIT));

        return true;
    }

    private void paintRoad() {
        for (int y = 1; y <= 24; y++) {
            int center = y < 8 ? 17 : (y < 17 ? 16 : 15);
            map[cell(center, y)] = Terrain.EMPTY;
            map[cell(center + 1, y)] = Terrain.EMPTY;
            if (y % 4 != 0) map[cell(center - 1, y)] = Terrain.EMPTY;
        }

        // A little widened verge suggests the road is nearing inhabited land.
        for (int x = 13; x <= 19; x++) {
            map[cell(x, 8)] = Terrain.EMPTY_SP;
            map[cell(x, 9)] = Terrain.EMPTY_SP;
        }
    }

    private void paintFields() {
        // Simple furrow-like strips made from the existing outdoor palette.
        for (int y = 5; y <= 20; y += 3) {
            for (int x = 3; x <= 11; x++) {
                map[cell(x, y)] = Terrain.HIGH_GRASS;
            }
            for (int x = 22; x <= 30; x++) {
                map[cell(x, y + 1)] = Terrain.HIGH_GRASS;
            }
        }

        // Small work clearings among the fields.
        for (int x = 5; x <= 9; x++) map[cell(x, 13)] = Terrain.EMPTY_SP;
        for (int x = 24; x <= 28; x++) map[cell(x, 15)] = Terrain.EMPTY_SP;
    }

    private void paintHedges() {
        // Darker solid edges read as hedges/woodland while also keeping the prototype compact.
        for (int y = 3; y <= 22; y++) {
            if (y == 10 || y == 18) continue;
            map[cell(2, y)] = Terrain.WALL;
            map[cell(31, y)] = Terrain.WALL;
        }

        for (int x = 4; x <= 9; x++) map[cell(x, 3)] = Terrain.WALL;
        for (int x = 25; x <= 30; x++) map[cell(x, 4)] = Terrain.WALL;
    }

    private int cell(int x, int y) {
        return x + y * width();
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) {
            SequelGame.enterSurfaceFromOutskirts();
            return true;
        }
        if (transition.type == LevelTransition.Type.REGULAR_EXIT) {
            GLog.p("Morningcreek lies just beyond the fields. The town proper opens in 0.0.6.");
            return false;
        }
        return super.activateTransition(hero, transition);
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

    @Override
    protected void createItems() {
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    public int randomRespawnCell(Char ch) {
        return cell(SOUTH_X, SOUTH_Y);
    }
}
