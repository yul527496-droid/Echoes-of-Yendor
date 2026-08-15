/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekMainStreetLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.TaskGuidanceToast;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDialogueStage;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

/** Persistent story state and lightweight proximity director for the sequel surface chapter. */
public class SequelState extends Buff {

    public enum Phase {
        FINAL_STAIR,
        SURFACE_REACHED,
        FARMER_APPROACHING,
        FARMER_NORMAL_TALK_DONE,
        AMULET_FLARE_DONE,
        FARMER_RECOVERED,
        FARMER_DEPARTED,
        OLD_ROAD_REACHED,
        WOLVES_DEFEATED,
        OUTSKIRTS_REACHED,
        INVESTIGATION_UNLOCKED,
        CH1_SLICE_COMPLETE, // legacy RC1 save marker; no longer treated as the real chapter ending
        MAIN_STREET_REACHED,
        INN_REACHED,
        LEDGER_READ,
        CH1_COMPLETE
    }

    private static final String PHASE = "phase";
    private static final String FARMER_MET = "farmer_met";
    private static final String WOLVES_DEFEATED = "wolves_defeated";
    private static final String CAMP_READ = "camp_read";
    private static final String BIRD_GONE = "bird_gone";
    private static final String BIRD_SEEN = "bird_seen";
    private static final String CAMP_VISITED = "camp_visited";
    private static final String CAMP_NOTE_TAKEN = "camp_note_taken";
    private static final String SHRINE_READ = "shrine_read";
    private static final String WAGON_INSPECTED = "wagon_inspected";
    private static final String MISSING_NOTICE_READ = "missing_notice_read";
    private static final String MISSING_NAME_RECOGNIZED = "missing_name_recognized";
    private static final String INVESTIGATION_KNOWN = "investigation_known";
    private static final String SURFACE_INTRO_SEEN = "surface_intro_seen";
    private static final String OUTSKIRTS_INTRO_SEEN = "outskirts_intro_seen";
    private static final String LEDGER_SCENE_SEEN = "ledger_scene_seen";

    private Phase phase = Phase.FINAL_STAIR;

    // Legacy aliases retained so RC1 saves migrate without losing progress.
    public boolean farmerMet;
    public boolean wolvesDefeated;
    public boolean campRead;
    public boolean birdGone;

    public boolean birdSeen;
    public boolean campVisited;
    public boolean campNoteTaken;
    public boolean shrineRead;
    public boolean wagonInspected;
    public boolean missingNoticeRead;
    public boolean missingNameRecognized;
    public boolean investigationKnown;
    public boolean surfaceIntroSeen;
    public boolean outskirtsIntroSeen;
    public boolean ledgerSceneSeen;

    public static SequelState get() {
        if (Dungeon.hero == null) return null;
        SequelState state = Dungeon.hero.buff(SequelState.class);
        return state != null ? state : Buff.affect(Dungeon.hero, SequelState.class);
    }

    /** Hunger and starvation are suspended only for saves that are actually in the sequel campaign. */
    public static boolean surfaceSafePhase() {
        if (Dungeon.hero == null) return false;
        SequelState state = Dungeon.hero.buff(SequelState.class);
        return state != null && !state.isAtLeast(Phase.CH1_COMPLETE);
    }

    public Phase phase() { return phase; }

    public boolean isAtLeast(Phase value) { return phase.ordinal() >= value.ordinal(); }

    public void advanceTo(Phase value) {
        if (value != null && value.ordinal() > phase.ordinal()) phase = value;
        syncLegacyFlags();
        syncObjective();
    }

    public void markInvestigationKnown() {
        investigationKnown = true;
        syncObjective();
    }

    public void markCampRead(boolean taken) {
        campVisited = true;
        campRead = true;
        if (taken) campNoteTaken = true;
    }

    /** One source of truth for persistent HUD task guidance. */
    public String objectiveText() {
        if (isAtLeast(Phase.CH1_COMPLETE)) return "第一章完成 · 线索：莱斯·赫恩与下行者名册";
        if (Dungeon.level instanceof OldCrowInnLevel) return "当前任务：调查下行者名册  ◇ 向吧台前进";
        if (Dungeon.level instanceof MorningcreekMainStreetLevel) return "当前任务：前往老鸦旅店  ◇ 沿主街向北寻找乌鸦招牌";
        if (Dungeon.level instanceof MorningcreekOutskirtsLevel) return "当前任务：进入晨溪镇  ◇ 沿大路向北";
        if (Dungeon.level instanceof OldKingsRoadLevel) {
            return wolvesDefeated ? "当前任务：前往晨溪  ◇ 沿旧王道向北" : "当前任务：沿旧王道前进  ◇ 注意狼群";
        }
        if (Dungeon.level instanceof SurfaceEntranceLevel) {
            if (!farmerMet) return "当前任务：返回文明世界  ◇ 沿道路向北";
            return "当前任务：前往晨溪  ◇ 沿道路进入旧王道";
        }
        return "当前任务：离开地下城，返回地表";
    }

    public void syncObjective() {
        Game.runOnRenderThread(() -> TaskGuidanceToast.showObjective(objectiveText()));
    }

    @Override
    public boolean act() {
        ChapterOneAudio.syncSettings();
        if (Dungeon.hero != null && Dungeon.level != null) {
            if (Dungeon.level instanceof SurfaceEntranceLevel) {
                handleSurface();
            } else if (Dungeon.level instanceof OldKingsRoadLevel) {
                handleOldRoad();
            } else if (Dungeon.level instanceof MorningcreekOutskirtsLevel) {
                handleOutskirts();
            } else if (Dungeon.level instanceof MorningcreekMainStreetLevel) {
                handleMainStreet();
            } else if (Dungeon.level instanceof OldCrowInnLevel) {
                handleInn();
            }
            syncObjective();
        }
        spend(TICK);
        return true;
    }

    private void handleSurface() {
        if (!surfaceIntroSeen) {
            int y = Dungeon.hero.pos / Dungeon.level.width();
            if (y <= 53) {
                surfaceIntroSeen = true;
                GLog.p("风从草地上吹过来。");
                GLog.p("这一次，头顶没有石头。");
            }
        }

        int x = Dungeon.hero.pos % Dungeon.level.width();
        int y = Dungeon.hero.pos / Dungeon.level.width();
        ChapterOneAudio.updateStreamDistance(Math.abs(y - 29));
        if (!campVisited && x >= 7 && x <= 23 && y >= 30 && y <= 42) {
            campVisited = true;
            GLog.p("林间藏着一处废弃营地。火塘早已冷透。");
        }
    }

    private void handleOldRoad() {
        OldKingsRoadLevel level = (OldKingsRoadLevel)Dungeon.level;
        if (!shrineRead && level.distance(Dungeon.hero.pos, level.cell(16, 29)) <= 1) {
            shrineRead = true;
            GLog.p("一座被苔藓盖住的旧路神龛。");
            GLog.p("石座上的字已经磨掉大半：「愿归路短于去路。」");
            ChapterOneAudio.playRaven();
        }

        if (!wagonInspected && level.distance(Dungeon.hero.pos, level.cell(42, 19)) <= 2) {
            wagonInspected = true;
            GLog.p("坏车的轮轴早已晒得发白。没有血迹，也没有值得拿走的东西。");
        }
    }

    private void handleOutskirts() {
        MorningcreekOutskirtsLevel level = (MorningcreekOutskirtsLevel)Dungeon.level;

        if (!missingNoticeRead && level.distance(Dungeon.hero.pos, level.cell(51, 13)) <= 1) {
            missingNoticeRead = true;
            if (campRead) {
                missingNameRecognized = true;
                GLog.p("寻人：莱斯·赫恩。数日前沿旧王道向南离镇，至今未归。");
                GLog.p("这个名字，你见过。");
            } else {
                GLog.p("寻人：莱斯·赫恩。数日前沿旧王道向南离镇，至今未归。");
            }
            GLog.p("路牌：↑ 晨溪镇　→ 老鸦旅店");
            investigationKnown = true;
            advanceTo(Phase.INVESTIGATION_UNLOCKED);
        }

        int y = Dungeon.hero.pos / Dungeon.level.width();
        if (!outskirtsIntroSeen && y <= 12) {
            outskirtsIntroSeen = true;
            investigationKnown = true;
            advanceTo(Phase.INVESTIGATION_UNLOCKED);
            GLog.p("屋顶和炊烟已经近在眼前。晨溪镇就在北面。");
        }
    }

    private void handleMainStreet() {
        advanceTo(Phase.MAIN_STREET_REACHED);
    }

    private void handleInn() {
        advanceTo(Phase.INN_REACHED);
        OldCrowInnLevel level = (OldCrowInnLevel)Dungeon.level;
        int ledger = level.cell(OldCrowInnLevel.LEDGER_X, OldCrowInnLevel.LEDGER_Y);
        if (!ledgerSceneSeen && level.distance(Dungeon.hero.pos, ledger) <= 2) {
            ledgerSceneSeen = true;
            advanceTo(Phase.LEDGER_READ);
            Game.runOnRenderThread(() -> GameScene.show(new WndDialogueStage(
                    "老鸦旅店老板娘",
                    "老板娘把一册厚重、磨损严重的名册推到你面前。\n\n"
                            + "『下行者名册。几十年来，往南去地下城的人都在这里留下过名字。』\n\n"
                            + "你的手指停在一个熟悉的名字上：莱斯·赫恩。\n"
                            + "他的登记日期，比林中营地留下的纸条早不了几天。\n\n"
                            + "Chapter 1 — Surface Return 完",
                    WndDialogueStage.Portrait.NONE,
                    () -> {
                        advanceTo(Phase.CH1_COMPLETE);
                        GLog.p("第一章完成：下行者名册把莱斯·赫恩与地下城重新连在了一起。");
                    }
            )));
        }
    }

    private void syncLegacyFlags() {
        farmerMet = farmerMet || phase.ordinal() >= Phase.FARMER_RECOVERED.ordinal();
        wolvesDefeated = wolvesDefeated || phase.ordinal() >= Phase.WOLVES_DEFEATED.ordinal();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        syncLegacyFlags();
        bundle.put(PHASE, phase.ordinal());
        bundle.put(FARMER_MET, farmerMet);
        bundle.put(WOLVES_DEFEATED, wolvesDefeated);
        bundle.put(CAMP_READ, campRead);
        bundle.put(BIRD_GONE, birdGone);
        bundle.put(BIRD_SEEN, birdSeen);
        bundle.put(CAMP_VISITED, campVisited);
        bundle.put(CAMP_NOTE_TAKEN, campNoteTaken);
        bundle.put(SHRINE_READ, shrineRead);
        bundle.put(WAGON_INSPECTED, wagonInspected);
        bundle.put(MISSING_NOTICE_READ, missingNoticeRead);
        bundle.put(MISSING_NAME_RECOGNIZED, missingNameRecognized);
        bundle.put(INVESTIGATION_KNOWN, investigationKnown);
        bundle.put(SURFACE_INTRO_SEEN, surfaceIntroSeen);
        bundle.put(OUTSKIRTS_INTRO_SEEN, outskirtsIntroSeen);
        bundle.put(LEDGER_SCENE_SEEN, ledgerSceneSeen);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        farmerMet = bundle.getBoolean(FARMER_MET);
        wolvesDefeated = bundle.getBoolean(WOLVES_DEFEATED);
        campRead = bundle.getBoolean(CAMP_READ);
        birdGone = bundle.getBoolean(BIRD_GONE);

        boolean hasPhase = bundle.contains(PHASE);
        int storedPhase = bundle.getInt(PHASE);
        if (hasPhase && storedPhase >= 0 && storedPhase < Phase.values().length) {
            phase = Phase.values()[storedPhase];
            // RC1 saves used CH1_SLICE_COMPLETE for the blocked town edge. Re-open the real Chapter 1 ending.
            if (phase == Phase.CH1_SLICE_COMPLETE) phase = Phase.INVESTIGATION_UNLOCKED;
        } else {
            phase = farmerMet ? Phase.FARMER_NORMAL_TALK_DONE : Phase.FINAL_STAIR;
            wolvesDefeated = false;
        }

        birdSeen = bundle.getBoolean(BIRD_SEEN);
        campVisited = bundle.getBoolean(CAMP_VISITED);
        campNoteTaken = bundle.getBoolean(CAMP_NOTE_TAKEN);
        shrineRead = bundle.getBoolean(SHRINE_READ);
        wagonInspected = bundle.getBoolean(WAGON_INSPECTED);
        missingNoticeRead = bundle.getBoolean(MISSING_NOTICE_READ);
        missingNameRecognized = bundle.getBoolean(MISSING_NAME_RECOGNIZED);
        investigationKnown = bundle.getBoolean(INVESTIGATION_KNOWN);
        surfaceIntroSeen = bundle.getBoolean(SURFACE_INTRO_SEEN);
        outskirtsIntroSeen = bundle.getBoolean(OUTSKIRTS_INTRO_SEEN);
        ledgerSceneSeen = bundle.getBoolean(LEDGER_SCENE_SEEN);

        syncLegacyFlags();
    }
}
