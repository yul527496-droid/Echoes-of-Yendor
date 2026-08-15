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
 * Geometry pass 3 keeps the 28x22 footprint and approved bespoke assets from
 * pass 2, but trims the broad dirt fields into narrow worn paths and small
 * activity clearings. Camp life is suggested only with existing terrain
 * vocabulary (embers, a few stone slabs, grass intrusions) so visual acceptance
 * stays separate from the later fixed tutorial-item/state-machine pass.
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
        paintWearBreaks();
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
        // A narrow walked line rather than one continuous brown field. Most rows
        // are only one or two cells wide; local clearings expand it where needed.
        paintRows(Terrain.EMPTY, new int[][]{
                {4, 13, 14},
                {5, 13, 14},
                {6, 12, 14},
                {7, 13, 14},
                {8, 14, 15},
                {9, 14, 15},
                {10, 15, 16},
                {11, 15, 15},
                {12, 14, 15},
                {13, 13, 14},
                {14, 12, 13},
                {15, 12, 13},
                {16, 12, 12},
                {17, 12, 13},
                {18, 13, 14},
                {19, 14, 14},
                {20, 14, 15}
        });
    }

    private void paintCampStart() {
        // Small trampled living area around the mentor and fire.
        paintRows(Terrain.EMPTY, new int[][]{
                {16, 6, 9},
                {17, 5, 10},
                {18, 6, 10},
                {19, 7, 9}
        });
        paintRows(Terrain.EMPTY, new int[][]{
                {17, 9, 12},
                {18, 10, 13}
        });

        map[cell(6, 18)] = Terrain.EMBERS;
        map[cell(5, 16)] = Terrain.HIGH_GRASS;
        map[cell(10, 19)] = Terrain.HIGH_GRASS;
        map[cell(6, 16)] = Terrain.HIGH_GRASS;
    }

    private void paintEquipmentApron() {
        // Only a few stone slabs beside the route. They reserve a visual home for
        // the later equipment tutorial without recreating the pass-1 courtyard.
        paintCells(Terrain.EMPTY_SP, new int[][]{
                {15, 16}, {16, 16},
                {16, 17}, {17, 17}
        });
        paintRows(Terrain.EMPTY, new int[][]{
                {16, 13, 15},
                {17, 13, 16}
        });
        map[cell(17, 16)] = Terrain.HIGH_GRASS;
    }

    private void paintDummyYard() {
        // A compact patch of bare earth around the dummy, with grass biting into
        // the edges so it reads as repeated foot traffic rather than paving.
        paintRows(Terrain.EMPTY, new int[][]{
                {12, 8, 10},
                {13, 7, 11},
                {14, 8, 10}
        });
        map[cell(7, 13)] = Terrain.HIGH_GRASS;
        map[cell(11, 14)] = Terrain.HIGH_GRASS;
    }

    private void paintItemBenchArea() {
        // Three joined slabs, close enough to the main path to look intentional.
        paintCells(Terrain.EMPTY_SP, new int[][]{
                {16, 11}, {17, 11}, {17, 12}
        });
        paintRows(Terrain.EMPTY, new int[][]{
                {11, 15, 17},
                {12, 15, 17}
        });
        map[cell(18, 12)] = Terrain.HIGH_GRASS;
    }

    private void paintWandRange() {
        // Narrow diagonal firing lane. The targets get a small scuffed patch each,
        // not a full rectangular range floor.
        paintRows(Terrain.EMPTY, new int[][]{
                {8, 21, 23},
                {9, 20, 23},
                {10, 18, 20},
                {11, 16, 18}
        });
        paintCells(Terrain.HIGH_GRASS, new int[][]{
                {20, 8}, {24, 9}, {21, 10}, {19, 11}
        });
    }

    private void paintFinalPracticeArea() {
        paintRows(Terrain.EMPTY, new int[][]{
                {6, 11, 14},
                {7, 10, 15},
                {8, 11, 15}
        });
        map[cell(10, 7)] = Terrain.HIGH_GRASS;
        map[cell(15, 8)] = Terrain.HIGH_GRASS;
        map[cell(12, 6)] = Terrain.GRASS;
    }

    private void paintDungeonApproach() {
        // The dungeon mouth remains the one place that earns a deliberate stone
        // apron, tapering immediately into the narrow dirt route below it.
        paintRows(Terrain.EMPTY_SP, new int[][]{
                {1, 11, 15},
                {2, 11, 15},
                {3, 11, 15},
                {4, 12, 14}
        });
        map[cell(11, 3)] = Terrain.GRASS;
        map[cell(15, 3)] = Terrain.GRASS;
        map[cell(13, 4)] = Terrain.EMPTY;
        map[cell(14, 4)] = Terrain.EMPTY;
        map[cell(13, 5)] = Terrain.EMPTY;
        map[cell(14, 5)] = Terrain.EMPTY;
    }

    private void paintWearBreaks() {
        // Grass islands interrupt long runs of dirt. Every chosen cell is away from
        // spawn points and bespoke actors, so this is purely a visual edge pass.
        paintCells(Terrain.GRASS, new int[][]{
                {13, 7}, {14, 9}, {15, 12},
                {12, 15}, {13, 18},
                {8, 18}, {10, 17},
                {8, 12}, {10, 14},
                {20, 9}, {18, 11}
        });
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
                {10, 4}, {18, 6},
                {11, 19}, {18, 17}, {19, 13}
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
