/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrainingDummy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrainingMentor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrainingTarget;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;

/**
 * Fixed graybox for the optional pre-dungeon memory tutorial.
 *
 * Geometry pass 2 keeps the same bespoke assets, but compresses the walk and
 * replaces the first pass's large stone rectangles with grass, worn dirt and
 * only a few purposeful stone patches. The goal is to read as one small camp,
 * not a set of disconnected debug platforms.
 */
public class TrainingGroundLevel extends Level {

    private static final int WIDTH = 28;
    private static final int HEIGHT = 22;

    private static final int START_X = 14;
    private static final int START_Y = 20;

    private static final int MENTOR_X = 8;
    private static final int MENTOR_Y = 17;
    private static final int DUMMY_X = 9;
    private static final int DUMMY_Y = 13;

    private static final int ENTRANCE_ART_X = 12;
    private static final int ENTRANCE_ART_Y = 1;

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

    {
        color1 = 0x789b57;
        color2 = 0xb6ca7b;
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

        // Closed hedge/forest shell with a grass clearing inside.
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                map[cell(x, y)] = Terrain.WALL;
            }
        }
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                map[cell(x, y)] = Terrain.GRASS;
            }
        }

        paintMainPath();
        paintCampStart();
        paintEquipmentApron();
        paintDummyYard();
        paintItemBenchArea();
        paintWandRange();
        paintFinalPracticeArea();
        paintDungeonApproach();
        paintEdgeGrowth();

        // The preview still needs a transition cell so Dungeon.switchLevel can
        // resolve pos=-1. It deliberately stays ordinary dirt so no south-side
        // staircase/ladder is visible in the memory itself.
        int start = cell(START_X, START_Y);
        map[start] = Terrain.EMPTY;
        transitions.add(new LevelTransition(this, start, LevelTransition.Type.REGULAR_ENTRANCE));

        DungeonMouth mouth = new DungeonMouth();
        mouth.pos(ENTRANCE_ART_X, ENTRANCE_ART_Y);
        customTiles.add(mouth);

        return true;
    }

    private void paintMainPath() {
        paintRows(Terrain.EMPTY, new int[][]{
                {4, 12, 14},
                {5, 12, 14},
                {6, 11, 14},
                {7, 12, 15},
                {8, 13, 15},
                {9, 13, 15},
                {10, 14, 16},
                {11, 14, 16},
                {12, 13, 15},
                {13, 12, 14},
                {14, 11, 13},
                {15, 11, 13},
                {16, 11, 13},
                {17, 11, 14},
                {18, 12, 14},
                {19, 13, 15},
                {20, 13, 15}
        });
    }

    private void paintCampStart() {
        paintRows(Terrain.EMPTY, new int[][]{
                {16, 6, 10},
                {17, 5, 11},
                {18, 6, 11},
                {19, 7, 10}
        });
        // Short worn connector to the main path.
        paintRows(Terrain.EMPTY, new int[][]{
                {17, 9, 12},
                {18, 9, 12}
        });

        map[cell(6, 18)] = Terrain.EMBERS;
        map[cell(5, 16)] = Terrain.HIGH_GRASS;
        map[cell(10, 19)] = Terrain.HIGH_GRASS;
    }

    private void paintEquipmentApron() {
        // A small hard-standing for laid-out equipment, not a whole courtyard.
        paintCells(Terrain.EMPTY_SP, new int[][]{
                {17, 16}, {18, 16},
                {17, 17}, {18, 17}, {19, 17},
                {18, 18}
        });
        paintRows(Terrain.EMPTY, new int[][]{
                {17, 14, 17},
                {18, 14, 18}
        });
        map[cell(19, 16)] = Terrain.HIGH_GRASS;
    }

    private void paintDummyYard() {
        paintRows(Terrain.EMPTY, new int[][]{
                {12, 7, 11},
                {13, 6, 12},
                {14, 7, 11}
        });
        map[cell(6, 12)] = Terrain.HIGH_GRASS;
        map[cell(11, 14)] = Terrain.HIGH_GRASS;
    }

    private void paintItemBenchArea() {
        // Only a few stone slabs where the later fixed tutorial items will sit.
        paintCells(Terrain.EMPTY_SP, new int[][]{
                {16, 11}, {17, 11},
                {16, 12}, {17, 12}, {18, 12}
        });
        map[cell(18, 11)] = Terrain.HIGH_GRASS;
    }

    private void paintWandRange() {
        // Worn firing lane over grass. Targets themselves stay on dirt, not a
        // rectangular stone platform.
        paintRows(Terrain.EMPTY, new int[][]{
                {9, 20, 24},
                {10, 18, 23},
                {11, 17, 20}
        });
        paintCells(Terrain.HIGH_GRASS, new int[][]{
                {19, 8}, {24, 8}, {25, 10}, {20, 11}
        });
    }

    private void paintFinalPracticeArea() {
        paintRows(Terrain.EMPTY, new int[][]{
                {6, 10, 15},
                {7, 9, 16},
                {8, 10, 16}
        });
        map[cell(9, 6)] = Terrain.HIGH_GRASS;
        map[cell(16, 8)] = Terrain.HIGH_GRASS;
    }

    private void paintDungeonApproach() {
        // The dungeon mouth is the one place that earns a deliberate stone apron.
        paintRows(Terrain.EMPTY_SP, new int[][]{
                {1, 11, 15},
                {2, 11, 15},
                {3, 11, 15},
                {4, 11, 15},
                {5, 12, 14}
        });
        map[cell(11, 4)] = Terrain.GRASS;
        map[cell(15, 4)] = Terrain.GRASS;
        map[cell(13, 5)] = Terrain.EMPTY;
        map[cell(14, 5)] = Terrain.EMPTY;
    }

    private void paintEdgeGrowth() {
        // Fixed irregular intrusions break the rectangular clearing without
        // turning the tutorial into a maze.
        paintCells(Terrain.WALL, new int[][]{
                {1, 4}, {1, 5}, {2, 5},
                {1, 10}, {2, 10}, {1, 11},
                {1, 18}, {2, 18},
                {26, 3}, {25, 3},
                {26, 7}, {25, 7}, {26, 8},
                {26, 15}, {25, 16},
                {26, 19}
        });

        paintCells(Terrain.HIGH_GRASS, new int[][]{
                {4, 4}, {5, 5}, {7, 7},
                {22, 4}, {23, 5},
                {3, 9}, {5, 11}, {24, 12},
                {3, 15}, {4, 16}, {23, 16},
                {5, 20}, {21, 19}, {24, 18},
                {10, 4}, {18, 6}
        });
    }

    private void paintRows(int terrain, int[][] rows) {
        for (int[] row : rows) {
            int y = row[0];
            for (int x = row[1]; x <= row[2]; x++) {
                map[cell(x, y)] = terrain;
            }
        }
    }

    private void paintCells(int terrain, int[][] cells) {
        for (int[] p : cells) {
            map[cell(p[0], p[1])] = terrain;
        }
    }

    private int cell(int x, int y) {
        return x + y * width();
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        // Development preview only. Leaving/entering the memory is wired later.
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) return false;
        return super.activateTransition(hero, transition);
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
        TrainingMentor mentor = new TrainingMentor();
        mentor.pos = cell(MENTOR_X, MENTOR_Y);
        mobs.add(mentor);

        TrainingDummy dummy = new TrainingDummy();
        dummy.pos = cell(DUMMY_X, DUMMY_Y);
        mobs.add(dummy);

        addTarget(22, 8);
        addTarget(23, 8);
        addTarget(21, 9);
    }

    private void addTarget(int x, int y) {
        TrainingTarget target = new TrainingTarget();
        target.pos = cell(x, y);
        mobs.add(target);
    }

    @Override
    protected void createItems() {
        // Deliberately empty in the geometry pass. Real tutorial items are fixed,
        // not random, and will be added alongside the scripting state machine.
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    public int randomRespawnCell(Char ch) {
        return cell(START_X, START_Y);
    }

    /** 3x3 custom tile using the approved 48x48 entrance artwork. */
    public static class DungeonMouth extends CustomTilemap {

        private static final String TEXTURE =
                "environment/custom_tiles/training_dungeon_entrance.png";

        {
            texture = TEXTURE;
            tileW = tileH = 3;
        }

        @Override
        public Tilemap create() {
            Tilemap visual = super.create();
            visual.map(mapSimpleImage(0, 0, 48), 3);
            return visual;
        }

        @Override
        public String name(int tileX, int tileY) {
            return tileX == 1 && tileY == 2 ? "地下城入口" : null;
        }

        @Override
        public String desc(int tileX, int tileY) {
            return tileX == 1 && tileY == 2
                    ? "通往地下的旧石阶。此刻的你还没有真正踏进去。"
                    : null;
        }
    }
}
