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
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
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
        color1 = 0x6a8a4f;
        color2 = 0x8cab62;
        viewDistance = 12;
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
        // Temporary music only; the surface will get its own theme later.
        Music.INSTANCE.play(Assets.Music.THEME_1, true);
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);

        // Open meadow inside a solid forest/rock border.
        for (int y = 1; y < HEIGHT - 1; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                map[cell(x, y)] = Terrain.GRASS;
            }
        }

        // The old road bends north rather than forming a straight corridor.
        paintRoad(26, 1, 6);
        paintRoad(25, 7, 10);
        paintRoad(23, 11, 14);
        paintRoad(21, 15, 20);
        paintRoad(19, 21, 25);
        paintRoad(18, 26, 29);

        // A shallow stream cuts across the road; the dry gap is the bridge footprint.
        for (int y = 16; y <= 17; y++) {
            for (int x = 1; x < WIDTH - 1; x++) {
                map[cell(x, y)] = Terrain.WATER;
            }
            for (int x = 20; x <= 22; x++) {
                map[cell(x, y)] = Terrain.EMPTY;
            }
        }

        // Crumbling stone/overgrowth around the dungeon mouth.
        for (int x = 14; x <= 22; x++) {
            map[cell(x, 24)] = Terrain.WALL;
            map[cell(x, 30)] = Terrain.WALL;
        }
        for (int y = 24; y <= 30; y++) {
            map[cell(14, y)] = Terrain.WALL;
            map[cell(22, y)] = Terrain.WALL;
        }
        for (int x = 17; x <= 19; x++) {
            map[cell(x, 24)] = Terrain.EMPTY;
        }

        // Dense tree masses keep the area readable and create a natural boundary.
        fillWalls(2, 3, 8, 7);
        fillWalls(30, 4, 35, 9);
        fillWalls(3, 21, 8, 27);
        fillWalls(29, 22, 35, 28);

        int entrance = cell(ENTRANCE_X, ENTRANCE_Y);
        map[entrance] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));

        int north = cell(NORTH_X, NORTH_Y);
        map[north] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, north, LevelTransition.Type.REGULAR_EXIT));

        // Small vegetation and road variations prevent large repeated patches.
        map[cell(11, 12)] = Terrain.HIGH_GRASS;
        map[cell(12, 12)] = Terrain.HIGH_GRASS;
        map[cell(28, 8)] = Terrain.HIGH_GRASS;
        map[cell(29, 8)] = Terrain.HIGH_GRASS;
        map[cell(24, 20)] = Terrain.EMPTY_DECO;

        return true;
    }

    private void paintRoad(int centerX, int top, int bottom) {
        for (int y = top; y <= bottom; y++) {
            for (int x = centerX - 1; x <= centerX + 1; x++) {
                map[cell(x, y)] = Terrain.EMPTY;
            }
        }
    }

    private void fillWalls(int left, int top, int right, int bottom) {
        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                map[cell(x, y)] = Terrain.WALL;
            }
        }
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
            GLog.p("The old road continues toward Morningcreek. This route opens in the next prototype step.");
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
        return cell(ENTRANCE_X, ENTRANCE_Y);
    }
}
