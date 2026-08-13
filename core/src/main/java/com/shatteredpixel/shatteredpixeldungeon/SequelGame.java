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
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SequelTransitionScene;

/** Small entry/switching helper for the sequel prototype. */
public final class SequelGame {

    private SequelGame() {
    }

    /** Compatibility entry used by older prototype screens. */
    public static void start() {
        ReturningHeroProfile profile = new ReturningHeroProfile();
        if (GamesInProgress.selectedClass != null) profile.heroClass = GamesInProgress.selectedClass;
        start(profile);
    }

    public static void start(ReturningHeroProfile profile) {
        if (profile == null) profile = new ReturningHeroProfile();

        Dungeon.daily = false;
        Dungeon.dailyReplay = false;
        SPDSettings.challenges(0);
        SPDSettings.customSeed("");
        SPDSettings.intro(false);

        GamesInProgress.curSlot = GamesInProgress.firstEmpty();
        if (GamesInProgress.curSlot < 0) return;

        GamesInProgress.selectedClass = profile.heroClass;
        profile.saveToSlot(GamesInProgress.curSlot);

        Dungeon.initSeed();
        Dungeon.init();

        ReturningHero.apply(Dungeon.hero, profile);
        SequelState.get();
        new Amulet().collect();
        Statistics.amuletObtained = true;

        Dungeon.depth = 0;
        Dungeon.branch = 0;

        enter(new FinalStairLevel(), -1);
    }

    public static void enterSurfaceEntrance() {
        enter(new SurfaceEntranceLevel(), -1);
    }

    public static void enterFinalStairFromSurface() {
        FinalStairLevel level = new FinalStairLevel();
        level.create();
        LevelTransition surface = level.getTransition(LevelTransition.Type.SURFACE);
        enterCreated(level, surface == null ? -1 : surface.cell());
    }

    public static void enterMorningcreekOutskirts() {
        enter(new MorningcreekOutskirtsLevel(), -1);
    }

    public static void enterSurfaceFromOutskirts() {
        SurfaceEntranceLevel level = new SurfaceEntranceLevel();
        level.create();
        LevelTransition north = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
        int pos = north == null ? -1 : north.cell() + level.width();
        enterCreated(level, pos);
    }

    private static void enter(Level level, int pos) {
        level.create();
        enterCreated(level, pos);
    }

    private static void enterCreated(Level level, int pos) {
        if (Dungeon.level != null) {
            Level.beforeTransition();
        }

        SequelTransitionScene.enter(level, pos);
    }
}
