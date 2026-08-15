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
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;

/**
 * Fixed 28x22 surface training ground for the optional pre-dungeon memory.
 * Geometry and approved bespoke assets are intentionally stable; the complete
 * tutorial controller owns progression, while fixed supplies physically exist
 * in camp chests from level creation so nothing pops into existence mid-lesson.
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
    private static final int DUNGEON_MOUTH_X = 13;
    private static final int DUNGEON_MOUTH_Y = 3;

    // Supply containers are present from frame one. The controller may still
    // restore a missing tutorial item as a safety net, but normal play never
    // needs stage-triggered ground spawns anymore.
    private static final int EQUIPMENT_CHEST_X = 16;
    private static final int EQUIPMENT_CHEST_Y = 16;
    private static final int UTILITY_CHEST_X = 17;
    private static final int UTILITY_CHEST_Y = 11;
    private static final int WAND_CHEST_X = 19;
    private static final int WAND_CHEST_Y = 10;

    private static final String TUTORIAL = "echoes_training_tutorial";

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

    private TrainingTutorialController tutorial = new TrainingTutorialController();

    {
        color1 = 0x789b57;
        color2 = 0xb6ca7b;
        viewDistance = 16;
    }

    public TrainingTutorialController tutorial() {
        if (tutorial == null) tutorial = new TrainingTutorialController();
        tutorial.bind(this);
        return tutorial;
    }

    int cellAt(int x, int y) {
        return x + y * width();
    }

    int dungeonMouthCell() {
        return cellAt(DUNGEON_MOUTH_X, DUNGEON_MOUTH_Y);
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
        tutorial.bind(this);

        // Closed hedge/forest shell with a grass clearing inside.
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                map[cellAt(x, y)] = Terrain.WALL;
            }
        }
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                map[cellAt(x, y)] = Terrain.GRASS;
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

        // The south transition exists only so Dungeon.switchLevel(pos=-1) can
        // resolve a legal spawn. Its cell remains ordinary dirt and never looks
        // like another staircase in the memory.
        int start = cellAt(START_X, START_Y);
        map[start] = Terrain.EMPTY;
        transitions.add(new LevelTransition(this, start, LevelTransition.Type.REGULAR_ENTRANCE));

        // The north transition is the real end trigger. activateTransition()
        // intercepts it so the memory fades out instead of loading SewerLevel.
        transitions.add(new LevelTransition(
                this,
                dungeonMouthCell(),
                LevelTransition.Type.REGULAR_EXIT
        ));

        DungeonMouth mouth = new DungeonMouth();
        mouth.pos(ENTRANCE_ART_X, ENTRANCE_ART_Y);
        customTiles.add(mouth);

        return true;
    }

    private void paintMainPath() {
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

        map[cellAt(6, 18)] = Terrain.EMBERS;
        map[cellAt(5, 16)] = Terrain.HIGH_GRASS;
        map[cellAt(10, 19)] = Terrain.HIGH_GRASS;
        map[cellAt(6, 16)] = Terrain.HIGH_GRASS;
    }

    private void paintEquipmentApron() {
        paintCells(Terrain.EMPTY_SP, new int[][]{
                {15, 16}, {16, 16},
                {16, 17}, {17, 17}
        });
        paintRows(Terrain.EMPTY, new int[][]{
                {16, 13, 15},
                {17, 13, 16}
        });
        map[cellAt(17, 16)] = Terrain.HIGH_GRASS;
    }

    private void paintDummyYard() {
        paintRows(Terrain.EMPTY, new int[][]{
                {12, 8, 10},
                {13, 7, 11},
                {14, 8, 10}
        });
        map[cellAt(7, 13)] = Terrain.HIGH_GRASS;
        map[cellAt(11, 14)] = Terrain.HIGH_GRASS;
    }

    private void paintItemBenchArea() {
        paintCells(Terrain.EMPTY_SP, new int[][]{
                {16, 11}, {17, 11}, {17, 12}
        });
        paintRows(Terrain.EMPTY, new int[][]{
                {11, 15, 17},
                {12, 15, 17}
        });
        map[cellAt(18, 12)] = Terrain.HIGH_GRASS;
    }

    private void paintWandRange() {
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
        map[cellAt(10, 7)] = Terrain.HIGH_GRASS;
        map[cellAt(15, 8)] = Terrain.HIGH_GRASS;
        map[cellAt(12, 6)] = Terrain.GRASS;
    }

    private void paintDungeonApproach() {
        paintRows(Terrain.EMPTY_SP, new int[][]{
                {1, 11, 15},
                {2, 11, 15},
                {3, 11, 15},
                {4, 12, 14}
        });
        map[cellAt(11, 3)] = Terrain.GRASS;
        map[cellAt(15, 3)] = Terrain.GRASS;
        map[cellAt(13, 4)] = Terrain.EMPTY;
        map[cellAt(14, 4)] = Terrain.EMPTY;
        map[cellAt(13, 5)] = Terrain.EMPTY;
        map[cellAt(14, 5)] = Terrain.EMPTY;
    }

    private void paintWearBreaks() {
        paintCells(Terrain.GRASS, new int[][]{
                {13, 7}, {14, 9}, {15, 12},
                {12, 15}, {13, 18},
                {8, 18}, {10, 17},
                {8, 12}, {10, 14},
                {20, 9}, {18, 11}
        });
    }

    private void paintEdgeGrowth() {
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
                map[cellAt(x, y)] = terrain;
            }
        }
    }

    private void paintCells(int terrain, int[][] cells) {
        for (int[] p : cells) {
            map[cellAt(p[0], p[1])] = terrain;
        }
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) {
            return false;
        }
        if (transition.type == LevelTransition.Type.REGULAR_EXIT
                && transition.inside(dungeonMouthCell())) {
            tutorial().onDungeonMouth();
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
        TrainingMentor mentor = new TrainingMentor();
        mentor.pos = cellAt(MENTOR_X, MENTOR_Y);
        mobs.add(mentor);

        TrainingDummy dummy = new TrainingDummy();
        dummy.pos = cellAt(DUMMY_X, DUMMY_Y);
        mobs.add(dummy);

        addTarget(22, 8);
        addTarget(23, 8);
        addTarget(21, 9);
    }

    private void addTarget(int x, int y) {
        TrainingTarget target = new TrainingTarget();
        target.pos = cellAt(x, y);
        mobs.add(target);
    }

    @Override
    protected void createItems() {
        // These are real SPD heaps with CHEST presentation, created before the
        // scene starts. Opening them uses the mature chest/heap interaction.
        trainingChest(
                EQUIPMENT_CHEST_X,
                EQUIPMENT_CHEST_Y,
                new WornShortsword().identify(),
                new ClothArmor().identify()
        );

        trainingChest(
                UTILITY_CHEST_X,
                UTILITY_CHEST_Y,
                new PotionOfHealing().identify(),
                new RingOfAccuracy(),
                new ScrollOfIdentify().identify(),
                new TalismanOfForesight().identify()
        );

        trainingChest(
                WAND_CHEST_X,
                WAND_CHEST_Y,
                new WandOfMagicMissile().identify()
        );
    }

    private void trainingChest(int x, int y, Item... items) {
        Heap heap = null;
        int cell = cellAt(x, y);
        for (Item item : items) {
            heap = drop(item, cell);
        }
        if (heap != null) {
            heap.type = Heap.Type.CHEST;
            heap.seen = false;
        }
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    public int randomRespawnCell(Char ch) {
        return cellAt(START_X, START_Y);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TUTORIAL, tutorial());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(TUTORIAL)) {
            tutorial = (TrainingTutorialController) bundle.get(TUTORIAL);
        } else {
            tutorial = new TrainingTutorialController();
        }
        tutorial().bind(this);
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
