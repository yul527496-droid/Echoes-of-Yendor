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
 * This pass deliberately contains no tutorial scripting, item grants or combat
 * gates. It only locks the walkable geometry and positions of the first bespoke
 * visual assets so the area can be judged at real game scale before mechanics
 * are layered on top.
 */
public class TrainingGroundLevel extends Level {

    private static final int WIDTH = 34;
    private static final int HEIGHT = 26;

    private static final int START_X = 17;
    private static final int START_Y = 23;

    private static final int MENTOR_X = 11;
    private static final int MENTOR_Y = 20;
    private static final int DUMMY_X = 10;
    private static final int DUMMY_Y = 15;

    private static final int ENTRANCE_ART_X = 15;
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

        // Start from a closed forest/hedge shell, then carve the camp from it.
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

        int start = cell(START_X, START_Y);
        map[start] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, start, LevelTransition.Type.REGULAR_ENTRANCE));

        DungeonMouth mouth = new DungeonMouth();
        mouth.pos(ENTRANCE_ART_X, ENTRANCE_ART_Y);
        customTiles.add(mouth);

        return true;
    }

    private void paintMainPath() {
        for (int y = 4; y <= START_Y; y++) {
            int center;
            if (y <= 8) center = 16;
            else if (y <= 13) center = 18;
            else if (y <= 18) center = 15;
            else center = 17;

            map[cell(center, y)] = Terrain.EMPTY;
            map[cell(center - 1, y)] = Terrain.EMPTY;
            if (y % 3 != 0) map[cell(center + 1, y)] = Terrain.EMPTY;
        }
    }

    private void paintCampStart() {
        fillRect(7, 18, 14, 22, Terrain.EMPTY_SP);
        map[cell(8, 21)] = Terrain.EMBERS;
        map[cell(7, 19)] = Terrain.HIGH_GRASS;
        map[cell(14, 22)] = Terrain.HIGH_GRASS;
    }

    private void paintEquipmentApron() {
        fillRect(19, 18, 24, 21, Terrain.EMPTY_SP);
        map[cell(23, 18)] = Terrain.HIGH_GRASS;
    }

    private void paintDummyYard() {
        fillRect(7, 13, 13, 17, Terrain.EMPTY_SP);
        map[cell(7, 13)] = Terrain.GRASS;
        map[cell(13, 17)] = Terrain.GRASS;
    }

    private void paintItemBenchArea() {
        fillRect(14, 12, 20, 15, Terrain.EMPTY_SP);
        map[cell(14, 12)] = Terrain.GRASS;
        map[cell(20, 15)] = Terrain.GRASS;
    }

    private void paintWandRange() {
        fillRect(23, 9, 30, 13, Terrain.EMPTY_SP);
        for (int y = 10; y <= 12; y++) {
            map[cell(22, y)] = Terrain.HIGH_GRASS;
        }
        map[cell(24, 13)] = Terrain.GRASS;
    }

    private void paintFinalPracticeArea() {
        fillRect(13, 6, 20, 10, Terrain.EMPTY_SP);
        map[cell(13, 6)] = Terrain.HIGH_GRASS;
        map[cell(20, 6)] = Terrain.HIGH_GRASS;
        map[cell(13, 10)] = Terrain.GRASS;
        map[cell(20, 10)] = Terrain.GRASS;
    }

    private void paintDungeonApproach() {
        fillRect(14, 1, 18, 5, Terrain.EMPTY_SP);
        map[cell(16, 4)] = Terrain.EMPTY;
        map[cell(16, 5)] = Terrain.EMPTY;
    }

    private void paintEdgeGrowth() {
        for (int y = 3; y <= 22; y += 4) {
            map[cell(2, y)] = Terrain.WALL;
            map[cell(31, y + (y < 20 ? 1 : 0))] = Terrain.WALL;
        }
        int[][] tufts = {
                {5, 7}, {6, 8}, {27, 5}, {29, 6}, {4, 16}, {30, 16},
                {5, 23}, {28, 22}, {21, 4}, {11, 8}
        };
        for (int[] p : tufts) map[cell(p[0], p[1])] = Terrain.HIGH_GRASS;
    }

    private void fillRect(int left, int top, int right, int bottom, int terrain) {
        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                map[cell(x, y)] = terrain;
            }
        }
    }

    private int cell(int x, int y) {
        return x + y * width();
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        // The south transition currently exists only so switchLevel can resolve a
        // deterministic spawn point. Leaving/entering the memory is wired later.
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

        addTarget(27, 10);
        addTarget(28, 10);
        addTarget(29, 10);
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
