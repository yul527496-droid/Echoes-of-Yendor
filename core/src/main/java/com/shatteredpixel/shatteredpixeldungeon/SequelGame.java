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
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.FinalStairLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.TrainingGroundLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.LedgerIntroScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SequelTransitionScene;
import com.watabou.utils.FileUtils;

/** Entry/switching helper for the sequel campaign. */
public final class SequelGame {

    private static final int TRAINING_MEMORY_SLOT = 0;

    private SequelGame() {
    }

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
        ChapterOneAudio.reset();

        ReturningHero.apply(Dungeon.hero, profile);
        SequelState.get();
        new Amulet().collect();
        Statistics.amuletObtained = true;

        Dungeon.depth = 0;
        Dungeon.branch = 0;

        enter(new FinalStairLevel(), -1);
    }

    public static boolean startTrainingMemory() {
        cleanupTrainingMemorySlot();

        Dungeon.daily = false;
        Dungeon.dailyReplay = false;
        SPDSettings.challenges(0);
        SPDSettings.customSeed("");
        SPDSettings.intro(false);

        GamesInProgress.curSlot = TRAINING_MEMORY_SLOT;
        GamesInProgress.selectedClass = HeroClass.WARRIOR;

        Dungeon.initSeed();
        Dungeon.init();
        ChapterOneAudio.stopAmbience();

        Dungeon.hero.belongings.clear();
        Dungeon.quickslot.reset();
        Dungeon.hero.HP = Dungeon.hero.HT;

        Dungeon.depth = 0;
        Dungeon.branch = 0;

        TrainingGroundLevel training = new TrainingGroundLevel();
        training.create();
        removePreplacedTrainingPotion(training);

        enterCreated(training, -1);
        return true;
    }

    private static void removePreplacedTrainingPotion(TrainingGroundLevel training) {
        if (training == null || training.heaps == null) return;

        for (Heap heap : training.heaps.valueList()) {
            for (Item item : heap.items.toArray(new Item[0])) {
                if (item instanceof PotionOfHealing) {
                    heap.remove(item);
                    return;
                }
            }
        }
    }

    public static void finishTrainingMemory() {
        EchoesOnboarding.trainingDone(true);
        cleanupTrainingMemorySlot();
        GamesInProgress.curSlot = 0;
        LedgerIntroScene.resumeAfterTraining();
        ShatteredPixelDungeon.switchNoFade(LedgerIntroScene.class);
    }

    // Compatibility bridge for the locked RC1 tutorial controller. The preview
    // entry alias was removed; this one remains only until that caller is renamed.
    public static void finishTrainingPreview() {
        finishTrainingMemory();
    }

    private static void cleanupTrainingMemorySlot() {
        FileUtils.deleteDir(GamesInProgress.gameFolder(TRAINING_MEMORY_SLOT));
        GamesInProgress.delete(TRAINING_MEMORY_SLOT);
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

    public static void enterOldKingsRoad() {
        enter(new OldKingsRoadLevel(), -1);
    }

    public static void enterSurfaceFromOldRoad() {
        SurfaceEntranceLevel level = new SurfaceEntranceLevel();
        level.create();
        LevelTransition north = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
        int pos = north == null ? -1 : north.cell() + level.width();
        enterCreated(level, pos);
    }

    public static void enterMorningcreekOutskirts() {
        enter(new MorningcreekOutskirtsLevel(), -1);
    }

    public static void enterOldRoadFromOutskirts() {
        OldKingsRoadLevel level = new OldKingsRoadLevel();
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
        if (Dungeon.level != null) Level.beforeTransition();
        SequelTransitionScene.enter(level, pos);
    }
}
