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
import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.noosa.audio.Music;

/** First playable map in Echoes of Yendor: the last climb out of the dungeon. */
public class FinalStairLevel extends Level {

    private static final int WIDTH = 13;
    private static final int HEIGHT = 19;
    private static final int START_X = 6;
    private static final int START_Y = 15;
    private static final int SURFACE_X = 6;
    private static final int SURFACE_Y = 2;

    {
        color1 = 0x48763c;
        color2 = 0x59994a;
        viewDistance = 10;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_SEWERS;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_SEWERS;
    }

    @Override
    public void playLevelMusic() {
        Music.INSTANCE.play(Assets.Music.THEME_FINALE, true);
        SequelState story = SequelState.get();
        if (story != null) story.syncObjective();
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);

        carve(3, 13, 9, 16);
        carve(5, 8, 7, 13);
        carve(4, 5, 8, 8);
        carve(5, 2, 7, 5);

        int start = cell(START_X, START_Y);
        int surface = cell(SURFACE_X, SURFACE_Y);

        map[start] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, start, LevelTransition.Type.REGULAR_ENTRANCE));

        map[surface] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, surface, LevelTransition.Type.SURFACE));

        map[cell(3, 15)] = Terrain.WATER;
        map[cell(9, 14)] = Terrain.WATER;
        map[cell(4, 7)] = Terrain.EMPTY_DECO;
        map[cell(8, 6)] = Terrain.EMPTY_DECO;

        return true;
    }

    private void carve(int left, int top, int right, int bottom) {
        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                map[cell(x, y)] = Terrain.EMPTY;
            }
        }
    }

    private int cell(int x, int y) {
        return x + y * width();
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.SURFACE) {
            ChapterOneAudio.leaveDungeonForSurface(new Runnable() {
                @Override public void run() {
                    SequelGame.enterSurfaceEntrance();
                }
            });
            return true;
        }
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) {
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
        return cell(START_X, START_Y);
    }
}
