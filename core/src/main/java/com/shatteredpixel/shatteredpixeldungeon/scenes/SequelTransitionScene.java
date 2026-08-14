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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.noosa.Game;

/**
 * Moves between sequel maps only after the old GameScene has been destroyed.
 * This keeps map-sized render data and actor positions from the previous area
 * away from the new level.
 */
public class SequelTransitionScene extends PixelScene {

    private static Level nextLevel;
    private static int nextPos;

    public static void enter(Level level, int pos) {
        nextLevel = level;
        nextPos = pos;
        Game.switchScene(SequelTransitionScene.class);
    }

    @Override
    public void create() {
        super.create();

        Level level = nextLevel;
        int pos = nextPos;
        nextLevel = null;
        nextPos = -1;

        if (level == null) {
            Game.switchScene(ReturnHeroScene.class);
            return;
        }

        InterlevelScene.mode = InterlevelScene.Mode.NONE;
        InterlevelScene.curTransition = null;

        // Dungeon.newLevel() clears the global Actor registry before moving to a
        // newly generated floor. Sequel maps are pre-created and switch directly
        // through Dungeon.switchLevel(), so we must mirror that lifecycle here.
        // Otherwise chars from the previous map retain positions sized for the
        // old map and can crash pathfinding on a smaller destination level.
        Actor.clear();
        Dungeon.switchLevel(level, pos);
        Game.switchScene(GameScene.class);
    }
}
