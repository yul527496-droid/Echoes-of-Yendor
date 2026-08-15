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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RoadDonkey;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RoadFarmer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.YendorBird;
import com.shatteredpixel.shatteredpixeldungeon.items.RoadsideNote;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.noosa.audio.Music;

/** The first large surface journey: dungeon mouth, camp, stream, and the roadside farmer. */
public class SurfaceEntranceLevel extends Level {

    public static final int WIDTH = 72;
    public static final int HEIGHT = 58;

    public static final int ENTRANCE_X = 35;
    public static final int ENTRANCE_Y = 55;
    public static final int NORTH_X = 57;
    public static final int NORTH_Y = 1;

    public static final int FARMER_MEET_X = 52;
    public static final int FARMER_MEET_Y = 14;
    public static final int FARMER_LEAVE_X = 58;
    public static final int FARMER_LEAVE_Y = 2;

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

    private static final int[][] ROAD = {
            {35,55}, {19,49}, {52,42}, {22,35}, {47,29},
            {60,24}, {27,17}, {55,14}, {58,10}, {57,1}
    };

    {
        color1 = 0x739a55;
        color2 = 0xa2bd6b;
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
        ChapterOneAudio.surfaceAmbience();
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);
        fillInterior(Terrain.GRASS);

        paintForestMasses();
        paintRoad();
        paintJourneyBarriers();
        paintCamp();
        paintStreamAndBridge();
        paintDungeonMouth();
        paintDetails();

        int entrance = cell(ENTRANCE_X, ENTRANCE_Y);
        map[entrance] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));

        int north = cell(NORTH_X, NORTH_Y);
        map[north] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, north, LevelTransition.Type.REGULAR_EXIT));

        SequelState story = SequelState.get();
        if (story != null) story.advanceTo(SequelState.Phase.SURFACE_REACHED);
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

    private void fillInterior(int terrain) {
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) map[cell(x, y)] = terrain;
        }
    }


    /**
     * Broad woodland shelves make the valley read as a sequence of places instead
     * of one open field that can be crossed diagonally in a few seconds. Each shelf
     * leaves a generous road opening; these are landscape boundaries, not maze walls.
     */
    private void paintJourneyBarriers() {
        forestShelf(49, 15, 22);
        forestShelf(42, 49, 56);
        forestShelf(35, 18, 25);
        forestShelf(24, 56, 63);
        forestShelf(17, 24, 31);
        forestShelf(10, 54, 61);
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
        paintLine(24, 45, 16, 38, 1, Terrain.EMPTY);
        paintLine(16, 38, 13, 36, 1, Terrain.EMPTY_SP);
    }

    private void paintStreamAndBridge() {
        for (int x = 1; x < WIDTH - 1; x++) {
            int center = 29 + (x < 18 ? 1 : 0) - (x > 58 ? 1 : 0);
            for (int y = center - 1; y <= center + 1; y++) map[cell(x, y)] = Terrain.WATER;
        }
        for (int x = 45; x <= 49; x++) {
            for (int y = 27; y <= 31; y++) map[cell(x, y)] = Terrain.EMPTY_DECO;
        }
        // Optional western ford: still requires leaving the main road and cannot skip the farmer.
        for (int x = 10; x <= 15; x++) {
            for (int y = 29; y <= 31; y++) map[cell(x, y)] = Terrain.EMPTY_SP;
        }
    }

    private void paintCamp() {
        rect(8, 32, 22, 40, Terrain.EMPTY_SP);
        rect(9, 32, 12, 34, Terrain.HIGH_GRASS);
        rect(18, 38, 22, 40, Terrain.HIGH_GRASS);
        map[cell(14, 35)] = Terrain.EMBERS;
        map[cell(10, 36)] = Terrain.EMPTY_DECO;
        map[cell(20, 37)] = Terrain.EMPTY_DECO;
    }

    private void paintDungeonMouth() {
        rect(29, 50, 41, 56, Terrain.EMPTY_SP);
        rect(31, 52, 39, 56, Terrain.EMPTY);
        rect(32, 54, 38, 56, Terrain.EMPTY_DECO);
    }

    private void paintForestMasses() {
        rect(2, 2, 25, 12, Terrain.WALL);
        rect(2, 13, 11, 27, Terrain.WALL);
        rect(61, 2, 69, 18, Terrain.WALL);
        rect(59, 20, 69, 26, Terrain.WALL);
        rect(2, 42, 13, 55, Terrain.WALL);
        rect(58, 38, 69, 55, Terrain.WALL);

        // Break up the masses so they read as woodland rather than dungeon rectangles.
        for (int y = 3; y < 55; y += 4) {
            int x = 4 + (y * 7) % 17;
            if (inside(x, y)) map[cell(x, y)] = Terrain.HIGH_GRASS;
            int rx = 66 - (y * 5) % 9;
            if (inside(rx, y)) map[cell(rx, y)] = Terrain.HIGH_GRASS;
        }
    }

    private void paintDetails() {
        int[][] grass = {
                {26,47},{28,45},{31,44},{44,39},{56,34},{57,23},
                {34,18},{41,18},{49,12},{52,10},{23,42},{17,41}
        };
        for (int[] p : grass) map[cell(p[0], p[1])] = Terrain.HIGH_GRASS;
    }

    private void paintLine(int x1, int y1, int x2, int y2, int radius, int terrain) {
        int steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
        for (int i = 0; i <= steps; i++) {
            float t = steps == 0 ? 0 : i / (float) steps;
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
            SequelGame.enterFinalStairFromSurface();
            return true;
        }
        if (transition.type == LevelTransition.Type.REGULAR_EXIT) {
            SequelState story = SequelState.get();
            if (story == null || !story.investigationKnown) {
                com.shatteredpixel.shatteredpixeldungeon.utils.GLog.p("那位赶车老人还没走远。刚才发生的事不能就这样丢在身后。");
                return false;
            }
            ChapterOneAudio.stopAmbience();
            SequelGame.enterOldKingsRoad();
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
        if (story == null) return;

        if (!story.isAtLeast(SequelState.Phase.FARMER_DEPARTED)) {
            boolean alreadyMet = story.isAtLeast(SequelState.Phase.FARMER_NORMAL_TALK_DONE);

            RoadFarmer farmer = new RoadFarmer();
            farmer.pos = alreadyMet ? cell(FARMER_MEET_X, FARMER_MEET_Y) : cell(60, 3);
            mobs.add(farmer);

            RoadDonkey donkey = new RoadDonkey();
            donkey.pos = alreadyMet ? cell(FARMER_MEET_X + 1, FARMER_MEET_Y - 1) : cell(61, 3);
            mobs.add(donkey);
        }

        if (!story.birdGone) {
            YendorBird bird = new YendorBird();
            bird.pos = cell(50, 43);
            mobs.add(bird);
        }
    }

    @Override
    protected void createItems() {
        SequelState story = SequelState.get();
        if (story != null && !story.campNoteTaken) drop(new RoadsideNote(), cell(13, 37));
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    public int randomRespawnCell(Char ch) {
        return cell(ENTRANCE_X, ENTRANCE_Y - 1);
    }
}
