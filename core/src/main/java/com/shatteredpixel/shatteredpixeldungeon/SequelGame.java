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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.levels.FinalStairLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.TrainingGroundLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.LedgerIntroScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SequelTransitionScene;
import com.watabou.utils.FileUtils;

/** Small entry/switching helper for the sequel prototype. */
public final class SequelGame {

    // Slot zero is outside GamesInProgress' normal 1..MAX_SLOTS UI range. It is
    // therefore a safe disposable home for the development tutorial session.
    private static final int TRAINING_PREVIEW_SLOT = 0;

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

    /**
     * Development entry for the complete optional pre-dungeon memory.
     *
     * It uses a real disposable Dungeon session so all mature SPD inventory,
     * targeting and turn systems remain authentic, but strips the warrior's
     * normal starting belongings so every tutorial object comes from the camp.
     */
    public static boolean previewTrainingGround() {
        cleanupTrainingPreviewSlot();

        Dungeon.daily = false;
        Dungeon.dailyReplay = false;
        SPDSettings.challenges(0);
        SPDSettings.customSeed("");
        SPDSettings.intro(false);

        GamesInProgress.curSlot = TRAINING_PREVIEW_SLOT;
        GamesInProgress.selectedClass = HeroClass.WARRIOR;

        Dungeon.initSeed();
        Dungeon.init();

        // The memory begins with an empty pack. This makes the fixed sword,
        // armor, potion, ring, artifact and wand unambiguous for first-timers.
        Dungeon.hero.belongings.clear();
        Dungeon.quickslot.reset();
        Dungeon.hero.HP = Dungeon.hero.HT;

        Dungeon.depth = 0;
        Dungeon.branch = 0;

        enter(new TrainingGroundLevel(), -1);
        return true;
    }

    /** Called only after the player personally steps onto the north stone stair. */
    public static void finishTrainingPreview() {
        cleanupTrainingPreviewSlot();
        GamesInProgress.curSlot = 0;
        ShatteredPixelDungeon.switchNoFade(LedgerIntroScene.class);
    }

    private static void cleanupTrainingPreviewSlot() {
        FileUtils.deleteDir(GamesInProgress.gameFolder(TRAINING_PREVIEW_SLOT));
        GamesInProgress.delete(TRAINING_PREVIEW_SLOT);
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
