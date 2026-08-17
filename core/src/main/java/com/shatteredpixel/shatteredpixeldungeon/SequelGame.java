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
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekMainStreetLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekTownPrototypeLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnPrototypeLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegionAreaLevel;
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
        // Dungeon.init() resets actors and creates a new hero, but upstream normally clears
        // the old level in Dungeon.newLevel(). Formal sequel maps bypass newLevel(), so clear
        // any level object left by training/another run before touching transition lifecycle.
        Dungeon.level = null;
        ChapterOneAudio.reset();

        ReturningHero.apply(Dungeon.hero, profile);

        // Formal-region prototype state is intentionally parallel to legacy SequelState.
        // The old five-map demo remains in the repository but is no longer the default
        // entry path while Morningcreek Town / Region v0.1 is under spatial review.
        RegionState region = RegionState.get();
        new Amulet().collect();
        Statistics.amuletObtained = true;

        Dungeon.depth = 0;
        Dungeon.branch = 0;

        MorningcreekTownPrototypeLevel town = new MorningcreekTownPrototypeLevel();
        town.create();
        if (region != null) {
            region.discover(RegionState.Location.SOUTH_GATE);
            region.discover(RegionState.Location.SOUTH_CARAVAN_APRON);
            region.restoreExploration(town);
        }
        enterCreated(town, town.cell(MorningcreekTownPrototypeLevel.START_X,
                MorningcreekTownPrototypeLevel.START_Y));
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
        Dungeon.level = null;
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

    public static void finishTrainingPreview() {
        finishTrainingMemory();
    }

    private static void cleanupTrainingMemorySlot() {
        FileUtils.deleteDir(GamesInProgress.gameFolder(TRAINING_MEMORY_SLOT));
        GamesInProgress.delete(TRAINING_MEMORY_SLOT);
    }

    // --- Formal Morningcreek Region v0.1 prototype transitions ---

    public static void enterMorningcreekTownPrototype() {
        MorningcreekTownPrototypeLevel town = new MorningcreekTownPrototypeLevel();
        town.create();
        enterCreated(town, town.cell(MorningcreekTownPrototypeLevel.START_X,
                MorningcreekTownPrototypeLevel.START_Y));
    }

    public static void enterOldCrowInnPrototype() {
        enter(new OldCrowInnPrototypeLevel(), -1);
    }

    public static void enterTownPrototypeFromInn() {
        MorningcreekTownPrototypeLevel town = new MorningcreekTownPrototypeLevel();
        town.create();
        int pos = town.cell(MorningcreekTownPrototypeLevel.INN_DOOR_X + 1,
                MorningcreekTownPrototypeLevel.INN_DOOR_Y);
        enterCreated(town, pos);
    }

    // --- Legacy vertical-slice transitions retained for reference/compatibility ---

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

    public static void enterMorningcreekMainStreet() {
        enter(new MorningcreekMainStreetLevel(), -1);
    }

    public static void enterOutskirtsFromTown() {
        MorningcreekOutskirtsLevel level = new MorningcreekOutskirtsLevel();
        level.create();
        LevelTransition north = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
        int pos = north == null ? -1 : north.cell() + level.width();
        enterCreated(level, pos);
    }

    public static void enterOldCrowInn() {
        enter(new OldCrowInnLevel(), -1);
    }

    public static void enterTownFromInn() {
        MorningcreekMainStreetLevel level = new MorningcreekMainStreetLevel();
        level.create();
        LevelTransition inn = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
        int pos = inn == null ? -1 : inn.cell() + level.width();
        enterCreated(level, pos);
    }

    private static void enter(Level level, int pos) {
        level.create();
        enterCreated(level, pos);
    }

    private static void enterCreated(Level level, int pos) {
        RegionState region = RegionState.current();
        if (region != null && Dungeon.level instanceof RegionAreaLevel) {
            region.captureExploration(Dungeon.level);
        }
        if (Dungeon.level != null) Level.beforeTransition();
        if (region != null && level instanceof RegionAreaLevel) {
            region.restoreExploration(level);
        }
        SequelTransitionScene.enter(level, pos);
    }
}
