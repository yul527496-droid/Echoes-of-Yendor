/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Echoes of Yendor modifications Copyright (C) 2026
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RoadWolf;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RoadDonkey;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RoadFarmer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.YendorBird;
import com.shatteredpixel.shatteredpixeldungeon.items.RoadsideNote;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Music;

/** First outdoor area immediately beyond the dungeon exit. */
public class SurfaceEntranceLevel extends Level {

    private static final int WIDTH = 38;
    private static final int HEIGHT = 32;

    private static final int ENTRANCE_X = 18;
    private static final int ENTRANCE_Y = 29;
    private static final int NORTH_X = 26;
    private static final int NORTH_Y = 1;

    private static final String SURFACE_TILES = "environment/tiles_surface.png";
    private static final String SURFACE_WATER = "environment/water_surface.png";

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
        paintStream();
        paintForest();
        paintCamp();
        paintDungeonMouth();
        paintDetails();

        int entrance = cell(ENTRANCE_X, ENTRANCE_Y);
        map[entrance] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));

        int north = cell(NORTH_X, NORTH_Y);
        map[north] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, north, LevelTransition.Type.REGULAR_EXIT));

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

    private void paintRoad() {
        for (int y = 1; y <= 29; y++) {
            int center;
            if (y <= 6)       center = 26;
            else if (y <= 10) center = 25;
            else if (y <= 14) center = 24;
            else if (y <= 18) center = 22;
            else if (y <= 22) center = 21;
            else if (y <= 26) center = 19;
            else              center = 18;

            map[cell(center, y)] = Terrain.EMPTY;
            map[cell(center - 1, y)] = (y % 5 == 1) ? Terrain.GRASS : Terrain.EMPTY;
            map[cell(center + 1, y)] = (y % 4 == 2) ? Terrain.GRASS : Terrain.EMPTY;
        }
    }

    private void paintStream() {
        for (int x = 1; x < WIDTH - 1; x++) {
            int top = 16;
            if (x <= 6 || x >= 32) top = 17;
            else if (x >= 8 && x <= 14) top = 15;
            else if (x >= 25 && x <= 30) top = 15;

            int bottom = top + ((x == 11 || x == 27 || x == 34) ? 2 : 1);
            for (int y = top; y <= bottom; y++) {
                map[cell(x, y)] = Terrain.WATER;
            }
        }

        for (int y = 14; y <= 19; y++) {
            for (int x = 20; x <= 22; x++) {
                map[cell(x, y)] = Terrain.EMPTY_DECO;
            }
        }
    }

    private void paintForest() {
        int[][] rows = {
                {3, 2, 6}, {4, 1, 8}, {5, 1, 8}, {6, 2, 8}, {7, 2, 7}, {8, 3, 6},
                {4, 31, 34}, {5, 30, 35}, {6, 30, 36}, {7, 31, 36}, {8, 31, 35}, {9, 32, 34},
                {22, 3, 7}, {23, 2, 8}, {24, 2, 7}, {25, 2, 7}, {26, 3, 8}, {27, 4, 7},
                {22, 31, 35}, {23, 30, 36}, {24, 30, 36}, {25, 31, 35}, {26, 30, 35}, {27, 31, 34}
        };

        for (int[] row : rows) {
            for (int x = row[1]; x <= row[2]; x++) {
                map[cell(x, row[0])] = Terrain.WALL;
            }
        }

        map[cell(2, 11)] = Terrain.WALL;
        map[cell(3, 11)] = Terrain.WALL;
        map[cell(34, 12)] = Terrain.WALL;
        map[cell(35, 12)] = Terrain.WALL;
    }

    private void paintCamp() {
        for (int y = 10; y <= 13; y++) {
            for (int x = 7; x <= 11; x++) {
                if ((x + y) % 4 != 0) map[cell(x, y)] = Terrain.EMPTY_SP;
            }
        }
        map[cell(8, 11)] = Terrain.EMBERS;
        map[cell(7, 13)] = Terrain.HIGH_GRASS;
        map[cell(11, 10)] = Terrain.HIGH_GRASS;
    }

    private void paintDungeonMouth() {
        for (int y = 27; y <= 29; y++) {
            for (int x = 16; x <= 20; x++) {
                if (!(y == 27 && (x == 16 || x == 20))) {
                    map[cell(x, y)] = Terrain.EMPTY_SP;
                }
            }
        }
        map[cell(16, 28)] = Terrain.GRASS;
        map[cell(20, 29)] = Terrain.GRASS;
    }

    private void paintDetails() {
        int[][] highGrass = {
                {11, 12}, {12, 12}, {28, 8}, {29, 8},
                {8, 20}, {9, 20}, {30, 19}, {31, 19},
                {6, 14}, {33, 14}
        };
        for (int[] p : highGrass) {
            map[cell(p[0], p[1])] = Terrain.HIGH_GRASS;
        }

        map[cell(17, 25)] = Terrain.EMPTY;
        map[cell(20, 24)] = Terrain.EMPTY;
        map[cell(23, 10)] = Terrain.EMPTY;
        map[cell(27, 6)] = Terrain.EMPTY;
    }

    private int cell(int x, int y) {
        return x + y * width();
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) {
            SequelGame.enterFinalStairFromSurface();
            return true;
        }
        if (transition.type == LevelTransition.Type.REGULAR_EXIT) {
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
        if (story == null) return;

        if (!story.farmerMet) {
            // Both actors begin beyond the northern fog and only start travelling once
            // the player has left the dungeon apron. They should enter vision by walking.
            RoadFarmer farmer = new RoadFarmer();
            farmer.pos = cell(26, 2);
            mobs.add(farmer);

            RoadDonkey donkey = new RoadDonkey();
            donkey.pos = cell(27, 2);
            mobs.add(donkey);
        } else if (!story.wolvesDefeated) {
            // A recreated map still needs the encounter. These southern staging cells are
            // deliberately far from a hero returning from Morningcreek to avoid pop-in.
            mobs.add(roadWolf(cell(12, 26)));
            mobs.add(roadWolf(cell(27, 26)));
        }

        if (!story.birdGone) {
            YendorBird bird = new YendorBird();
            bird.pos = cell(29, 9);
            mobs.add(bird);
        }
    }

    @Override
    protected void createItems() {
        SequelState story = SequelState.get();
        if (story != null && !story.campRead) {
            drop(new RoadsideNote(), cell(9, 12));
        }
    }

    /** Called by the farmer after he has left. New wildlife must never pop into the hero's FOV. */
    public static void releaseRoadWolves() {
        if (!(Dungeon.level instanceof SurfaceEntranceLevel)) return;

        SequelState story = SequelState.get();
        if (story == null || story.wolvesDefeated) return;

        for (Mob mob : Dungeon.level.mobs) {
            if (mob instanceof RoadWolf) return;
        }

        SurfaceEntranceLevel level = (SurfaceEntranceLevel) Dungeon.level;
        RoadWolf first = new RoadWolf();
        first.pos = level.randomRespawnCell(first);
        if (first.pos == -1) return;
        GameScene.add(first);

        RoadWolf second = new RoadWolf();
        second.pos = level.randomRespawnCell(second);
        if (second.pos != -1) GameScene.add(second, 0.5f);
    }

    private static RoadWolf roadWolf(int pos) {
        RoadWolf wolf = new RoadWolf();
        wolf.pos = pos;
        return wolf;
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    public int randomRespawnCell(Char ch) {
        int cell;
        int tries = 0;
        do {
            cell = com.watabou.utils.Random.Int(length());
            tries++;
        } while (tries < 80 && (
                (Dungeon.level == this && heroFOV[cell])
                        || !passable[cell]
                        || Actor.findChar(cell) != null
                        || distance(cell, Dungeon.hero.pos) < 10));
        return tries >= 80 ? -1 : cell;
    }
}
