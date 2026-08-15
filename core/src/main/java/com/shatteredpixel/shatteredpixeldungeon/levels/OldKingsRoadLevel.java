/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RoadWolf;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.noosa.audio.Music;

/** Long quiet road between the dungeon valley and the settled fields around Morningcreek. */
public class OldKingsRoadLevel extends Level {

    public static final int WIDTH = 86;
    public static final int HEIGHT = 56;
    public static final int SOUTH_X = 18;
    public static final int SOUTH_Y = 54;
    public static final int NORTH_X = 72;
    public static final int NORTH_Y = 1;

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

    private static final int[][] ROAD = {
            {18,54},{59,47},{24,39},{59,30},{30,22},
            {60,14},{70,7},{72,1}
    };

    {
        color1 = 0x6e8f50;
        color2 = 0x94ad61;
        viewDistance = 15;
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
        ChapterOneAudio.oldRoadAmbience();
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) map[cell(x, y)] = Terrain.GRASS;
        }

        paintForest();
        paintRoad();
        paintShrine();
        paintWolfArena();
        paintBrokenWagon();
        paintSignpost();
        paintJourneyBarriers();

        int south = cell(SOUTH_X, SOUTH_Y);
        map[south] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, south, LevelTransition.Type.REGULAR_ENTRANCE));

        int north = cell(NORTH_X, NORTH_Y);
        map[north] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, north, LevelTransition.Type.REGULAR_EXIT));

        SequelState story = SequelState.get();
        if (story != null) story.advanceTo(SequelState.Phase.OLD_ROAD_REACHED);
        return true;
    }


    private void paintJourneyBarriers() {
        forestShelf(47, 55, 63);
        forestShelf(39, 20, 28);
        // This broad gate is the wolf clearing itself: the road cannot bypass the
        // encounter, but the combat space remains wide and natural.
        forestShelf(32, 50, 66);
        forestShelf(22, 26, 34);
        forestShelf(14, 56, 64);
        forestShelf(7, 66, 74);
    }

    private void forestShelf(int y, int gapLeft, int gapRight) {
        for (int yy = y; yy <= y + 1; yy++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                if (x < gapLeft || x > gapRight) map[cell(x, yy)] = Terrain.WALL;
            }
        }
        for (int x = Math.max(1, gapLeft - 3); x <= Math.min(WIDTH - 2, gapRight + 3); x++) {
            if (x < gapLeft || x > gapRight) map[cell(x, Math.max(1, y - 1))] = Terrain.HIGH_GRASS;
        }
    }

    private void paintRoad() {
        for (int i = 0; i < ROAD.length - 1; i++) {
            paintLine(ROAD[i][0], ROAD[i][1], ROAD[i + 1][0], ROAD[i + 1][1], 2, Terrain.EMPTY);
        }
        // Shrine spur.
        paintLine(31, 37, 18, 30, 1, Terrain.EMPTY_SP);
    }

    private void paintForest() {
        rect(2, 2, 24, 18, Terrain.WALL);
        rect(2, 35, 15, 53, Terrain.WALL);
        rect(68, 13, 83, 31, Terrain.WALL);
        rect(71, 37, 83, 53, Terrain.WALL);
        rect(35, 2, 42, 8, Terrain.WALL);

        for (int y = 4; y < HEIGHT - 4; y += 3) {
            int x = 18 + (y * 11) % 47;
            if (inside(x, y) && map[cell(x, y)] != Terrain.WALL) {
                map[cell(x, y)] = Terrain.HIGH_GRASS;
            }
        }
    }

    private void paintShrine() {
        rect(13, 27, 20, 33, Terrain.EMPTY_SP);
        map[cell(16, 29)] = Terrain.EMPTY_DECO;
        map[cell(15, 29)] = Terrain.EMBERS;
    }

    private void paintWolfArena() {
        rect(48, 25, 66, 34, Terrain.EMPTY);
        for (int x = 49; x <= 65; x += 4) {
            map[cell(x, 25)] = Terrain.HIGH_GRASS;
            map[cell(x, 34)] = Terrain.HIGH_GRASS;
        }
        map[cell(54, 28)] = Terrain.HIGH_GRASS;
        map[cell(63, 31)] = Terrain.HIGH_GRASS;
    }

    private void paintBrokenWagon() {
        rect(37, 17, 47, 22, Terrain.EMPTY_SP);
        map[cell(42, 19)] = Terrain.EMPTY_DECO;
        map[cell(43, 19)] = Terrain.EMPTY_DECO;
        map[cell(44, 19)] = Terrain.EMPTY_DECO;
    }

    private void paintSignpost() {
        rect(62, 6, 72, 11, Terrain.EMPTY_SP);
        map[cell(67, 8)] = Terrain.EMPTY_DECO;
    }

    private void paintLine(int x1, int y1, int x2, int y2, int radius, int terrain) {
        int steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
        for (int i = 0; i <= steps; i++) {
            float t = steps == 0 ? 0 : i / (float)steps;
            int x = Math.round(x1 + (x2 - x1) * t);
            int y = Math.round(y1 + (y2 - y1) * t);
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    if (Math.abs(dx) + Math.abs(dy) <= radius + 1 && inside(x + dx, y + dy)) {
                        map[cell(x + dx, y + dy)] = terrain;
                    }
                }
            }
        }
    }

    private void rect(int x1, int y1, int x2, int y2, int terrain) {
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) if (inside(x, y)) map[cell(x, y)] = terrain;
        }
    }

    private boolean inside(int x, int y) {
        return x > 0 && y > 0 && x < WIDTH - 1 && y < HEIGHT - 1;
    }

    public int cell(int x, int y) {
        return x + y * width();
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) {
            ChapterOneAudio.stopAmbience();
            SequelGame.enterSurfaceFromOldRoad();
            return true;
        }
        if (transition.type == LevelTransition.Type.REGULAR_EXIT) {
            SequelState story = SequelState.get();
            if (story != null && !story.wolvesDefeated
                    && !story.isAtLeast(SequelState.Phase.WOLVES_DEFEATED)) {
                com.shatteredpixel.shatteredpixeldungeon.utils.GLog.p("旧王道前方还有野兽徘徊。");
                return false;
            }
            ChapterOneAudio.stopAmbience();
            SequelGame.enterMorningcreekOutskirts();
            return true;
        }
        return super.activateTransition(hero, transition);
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
        SequelState story = SequelState.get();
        if (story == null || story.wolvesDefeated || story.isAtLeast(SequelState.Phase.WOLVES_DEFEATED)) return;

        RoadWolf first = new RoadWolf();
        first.pos = cell(61, 28);
        mobs.add(first);

        RoadWolf second = new RoadWolf();
        second.pos = cell(52, 26);
        mobs.add(second);
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
        return cell(SOUTH_X, SOUTH_Y - 1);
    }
}
