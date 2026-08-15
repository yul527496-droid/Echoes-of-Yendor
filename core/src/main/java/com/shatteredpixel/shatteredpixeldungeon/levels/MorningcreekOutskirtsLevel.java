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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SurfaceVillager;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.noosa.audio.Music;

/** Settled farmland outside Morningcreek, now leading into the playable town. */
public class MorningcreekOutskirtsLevel extends Level {

    public static final int WIDTH = 82;
    public static final int HEIGHT = 66;
    public static final int SOUTH_X = 18;
    public static final int SOUTH_Y = 64;
    public static final int NORTH_X = 48;
    public static final int NORTH_Y = 1;

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

    private static final int[][] ROAD = {
            {18,64},{54,58},{26,50},{54,42},{52,37},
            {30,31},{58,24},{34,17},{59,10},{48,2}
    };

    {
        color1 = 0x789b57;
        color2 = 0xb0c87a;
        viewDistance = 16;
    }

    @Override
    public String tilesTex() { return SURFACE_TILES; }

    @Override
    public String waterTex() { return SURFACE_WATER; }

    @Override
    public void playLevelMusic() {
        Music.INSTANCE.play(Assets.Music.THEME_1, true);
        ChapterOneAudio.outskirtsAmbience();
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) map[cell(x, y)] = Terrain.GRASS;
        }

        paintFields();
        paintHedges();
        paintRoad();
        paintIrrigation();
        paintFarmstead();
        paintOrchard();
        paintNoticeAndTownApproach();
        paintFieldBoundaries();

        int south = cell(SOUTH_X, SOUTH_Y);
        map[south] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, south, LevelTransition.Type.REGULAR_ENTRANCE));

        int north = cell(NORTH_X, NORTH_Y);
        map[north] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, north, LevelTransition.Type.REGULAR_EXIT));

        SequelState story = SequelState.get();
        if (story != null) story.advanceTo(SequelState.Phase.OUTSKIRTS_REACHED);
        return true;
    }

    @Override
    public void buildFlagMaps() {
        super.buildFlagMaps();
        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.WATER) {
                passable[i] = false;
                avoid[i] = true;
            }
        }
    }

    private void paintFieldBoundaries() {
        hedgeShelf(58, 50, 58);
        hedgeShelf(50, 22, 30);
        hedgeShelf(42, 50, 58);
        hedgeShelf(31, 26, 34);
        hedgeShelf(24, 54, 62);
        hedgeShelf(17, 30, 38);
        hedgeShelf(10, 55, 63);
    }

    private void hedgeShelf(int y, int gapLeft, int gapRight) {
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
        paintLine(46, 30, 19, 24, 1, Terrain.EMPTY_SP);
        paintLine(49, 29, 70, 25, 1, Terrain.EMPTY_SP);
    }

    private void paintFields() {
        for (int y = 39; y <= 58; y += 3) {
            for (int x = 5; x <= 20; x++) map[cell(x, y)] = Terrain.HIGH_GRASS;
            for (int x = 58; x <= 76; x++) map[cell(x, y - 1)] = Terrain.HIGH_GRASS;
        }
        for (int y = 20; y <= 36; y += 3) {
            for (int x = 30; x <= 39; x++) map[cell(x, y)] = Terrain.HIGH_GRASS;
        }
    }

    private void paintHedges() {
        rect(2, 45, 4, 62, Terrain.WALL);
        rect(77, 39, 79, 61, Terrain.WALL);
        rect(3, 16, 8, 36, Terrain.WALL);
        rect(73, 13, 79, 34, Terrain.WALL);
    }

    private void paintIrrigation() {
        for (int x = 1; x < WIDTH - 1; x++) {
            int y = 37 + (x > 52 ? 1 : 0);
            map[cell(x, y)] = Terrain.WATER;
        }
        for (int x = 49; x <= 55; x++) {
            for (int y = 36; y <= 39; y++) map[cell(x, y)] = Terrain.EMPTY_DECO;
        }
    }

    private void paintFarmstead() {
        rect(9, 19, 27, 31, Terrain.EMPTY_SP);
        rect(10, 20, 18, 25, Terrain.EMPTY_DECO);
        rect(11, 43, 22, 51, Terrain.EMPTY_SP);
        rect(12, 44, 19, 48, Terrain.EMPTY_DECO);
        map[cell(21, 47)] = Terrain.HIGH_GRASS;
    }

    private void paintOrchard() {
        for (int y = 17; y <= 31; y += 4) {
            for (int x = 60; x <= 75; x += 4) map[cell(x, y)] = Terrain.WALL;
        }
    }

    private void paintNoticeAndTownApproach() {
        rect(44, 9, 62, 16, Terrain.EMPTY_SP);
        map[cell(51, 13)] = Terrain.EMPTY_DECO;
        map[cell(56, 10)] = Terrain.EMPTY_DECO;
        rect(35, 2, 43, 6, Terrain.WALL);
        rect(63, 2, 72, 7, Terrain.WALL);
        rect(24, 3, 31, 8, Terrain.WALL);
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

    public int cell(int x, int y) { return x + y * width(); }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) {
            ChapterOneAudio.stopAmbience();
            SequelGame.enterOldRoadFromOutskirts();
            return true;
        }
        if (transition.type == LevelTransition.Type.REGULAR_EXIT) {
            SequelState story = SequelState.get();
            if (story != null) story.markInvestigationKnown();
            ChapterOneAudio.stopAmbience();
            SequelGame.enterMorningcreekMainStreet();
            return true;
        }
        return super.activateTransition(hero, transition);
    }

    @Override public Mob createMob() { return null; }

    @Override
    protected void createMobs() {
        addVillager(35, 45, "田里的农夫", "下午好。");
        addVillager(31, 31, "修篱笆的人", "去镇里的话沿大路走，别踩田。");
        addVillager(58, 27, "好奇的孩子", "你那把武器是真的吗？");
        addVillager(20, 22, "农舍老妇", "老鸦旅店？一直往北。进镇后看乌鸦招牌。");
    }

    private void addVillager(int x, int y, String name, String line) {
        SurfaceVillager villager = new SurfaceVillager(name, line);
        villager.pos = cell(x, y);
        mobs.add(villager);
    }

    @Override protected void createItems() { }
    @Override public Actor addRespawner() { return null; }
    @Override public int randomRespawnCell(Char ch) { return cell(SOUTH_X, SOUTH_Y - 1); }
}
