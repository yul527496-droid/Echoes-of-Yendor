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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.levels.FinalStairLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Game;

/** Starts the sequel without coupling the prototype to the original dungeon flow. */
public final class SequelGame {

    private SequelGame() {
    }

    public static void start() {
        Dungeon.daily = false;
        Dungeon.dailyReplay = false;
        SPDSettings.challenges(0);
        SPDSettings.customSeed("");

        GamesInProgress.curSlot = GamesInProgress.firstEmpty();
        Dungeon.initSeed();
        Dungeon.init();

        new Amulet().collect();
        Statistics.amuletObtained = true;

        Dungeon.depth = 0;
        Dungeon.branch = 0;

        Level level = new FinalStairLevel();
        level.create();
        Dungeon.switchLevel(level, -1);
        Game.switchScene(GameScene.class);
    }
}
