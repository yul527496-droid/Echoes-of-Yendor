/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.levels.ActOneReturnLevel;
import com.watabou.utils.Bundle;

/**
 * Persistent, non-linear state for Act 1 Scene 1 "Return".
 *
 * This deliberately does not use SequelState.Phase. Stable facts are reconstructed on
 * level creation, so save/load around an event cannot duplicate actors or the Amulet.
 */
public class ActOneReturnState extends Buff {

    public enum FarmerOutcome { UNRESOLVED, RESCUED, IGNORED }
    public enum DonkeyOutcome { UNRESOLVED, SURVIVED, LOST }
    public enum CartOutcome { UNRESOLVED, USABLE, ABANDONED }
    public enum WolvesOutcome { UNRESOLVED, RESOLVED, ABANDONED }

    private static final String OPENING = "a1r_opening";
    private static final String JOURNEY_STARTED = "a1r_journey_started";
    private static final String CAMP_DISCOVERED = "a1r_camp_discovered";
    private static final String CAMP_BEDROLL = "a1r_camp_bedroll";
    private static final String CAMP_LIST = "a1r_camp_list";
    private static final String HERO_MARK = "a1r_hero_mark";
    private static final String FARMER_SEEN = "a1r_farmer_seen";
    private static final String FARMER_OUTCOME = "a1r_farmer_outcome";
    private static final String DONKEY_OUTCOME = "a1r_donkey_outcome";
    private static final String CART_OUTCOME = "a1r_cart_outcome";
    private static final String WOLVES_OUTCOME = "a1r_wolves_outcome";
    private static final String MORNINGCREEK_HEARD = "a1r_morningcreek_heard";
    private static final String YENDOR_ANOMALY = "a1r_yendor_anomaly";
    private static final String YENDOR_MISSING = "a1r_yendor_missing";
    private static final String CROW_ACTIVE = "a1r_crow_active";
    private static final String CROW_RESOLVED = "a1r_crow_resolved";
    private static final String CROW_THEFT_PRESENTED = "a1r_crow_theft_presented";
    private static final String SHRINE_DISCOVERED = "a1r_shrine_discovered";
    private static final String INSCRIPTION = "a1r_inscription";
    private static final String YENDOR_RECOVERED = "a1r_yendor_recovered";
    private static final String NORTH_EXIT = "a1r_north_exit";
    private static final String FARMER_DIALOGUE = "a1r_farmer_dialogue";
    private static final String YENDOR_NOTICE = "a1r_yendor_notice";
    private static final String NORTH_NOTICE = "a1r_north_notice";

    public boolean openingShown;
    public boolean journeyStarted;
    public boolean campDiscovered;
    public boolean campBedrollClueSeen;
    public boolean campListClueSeen;
    public boolean heroMarkSeen;
    public boolean farmerEventSeen;
    public FarmerOutcome farmerOutcome = FarmerOutcome.UNRESOLVED;
    public DonkeyOutcome donkeyOutcome = DonkeyOutcome.UNRESOLVED;
    public CartOutcome cartOutcome = CartOutcome.UNRESOLVED;
    public WolvesOutcome wolvesOutcome = WolvesOutcome.UNRESOLVED;
    public boolean morningcreekHeardOf;
    public boolean yendorAnomalyStarted;
    public boolean yendorTemporarilyMissing;
    public boolean crowChaseActive;
    public boolean crowChaseResolved;
    public boolean crowTheftPresented;
    public boolean shrineDiscovered;
    public boolean inscriptionRead;
    public boolean yendorRecovered;
    public boolean northExitReached;

    // Stable presentation checkpoints. These are kept with the scene state rather than
    // scattered through NPCs/Levels so a reload cannot replay a completed presentation.
    public boolean farmerDialogueCompleted;
    public boolean yendorAnomalyNoticeShown;
    public boolean northExitNoticeShown;

    public static ActOneReturnState get() {
        if (Dungeon.hero == null) return null;
        ActOneReturnState state = Dungeon.hero.buff(ActOneReturnState.class);
        if (state == null) state = Buff.affect(Dungeon.hero, ActOneReturnState.class);
        return state;
    }

    public static ActOneReturnState current() {
        return Dungeon.hero == null ? null : Dungeon.hero.buff(ActOneReturnState.class);
    }

    /** HUD text is intentionally sparse: only the player's immediate scene-level intent. */
    public String objectiveText() {
        if (crowChaseActive && !yendorRecovered) return "找回 Yendor";
        if (morningcreekHeardOf) return "前往晨溪";
        if (journeyStarted || heroHasLeftExitBasin()) return "沿旧路前行";
        return "离开地下城，返回地表";
    }

    /** Crossing the old bridge is the durable boundary between the exit basin and the journey. */
    public void markJourneyStarted() {
        if (journeyStarted) return;
        journeyStarted = true;
        syncHud();
    }

    /** Removes the one formal Amulet from inventory for the crow beat. Idempotent. */
    public void beginYendorChase() {
        if (yendorRecovered || yendorTemporarilyMissing) return;
        if (Dungeon.hero != null) {
            Amulet amulet = Dungeon.hero.belongings.getItem(Amulet.class);
            if (amulet != null) amulet.detachAll(Dungeon.hero.belongings.backpack);
        }
        yendorAnomalyStarted = true;
        yendorTemporarilyMissing = true;
        crowChaseActive = true;
        crowChaseResolved = false;
        syncHud();
    }

    public void markCrowTheftPresented() {
        crowTheftPresented = true;
    }

    /** Restores exactly one formal Amulet and closes the chase. Idempotent across reloads. */
    public void recoverYendor() {
        if (Dungeon.hero != null && Dungeon.hero.belongings.getItem(Amulet.class) == null) {
            new Amulet().collect();
        }
        yendorTemporarilyMissing = false;
        yendorRecovered = true;
        crowChaseActive = false;
        crowChaseResolved = true;
        syncHud();
    }

    /** Repairs inventory/story consistency after loading at any chase boundary. */
    private void ensureYendorConsistency() {
        if (Dungeon.hero == null) return;
        Amulet amulet = Dungeon.hero.belongings.getItem(Amulet.class);
        if (yendorTemporarilyMissing && !yendorRecovered) {
            if (amulet != null) amulet.detachAll(Dungeon.hero.belongings.backpack);
        } else if (amulet == null) {
            new Amulet().collect();
        }
    }

    private boolean heroHasLeftExitBasin() {
        if (!(Dungeon.level instanceof ActOneReturnLevel) || Dungeon.hero == null) return false;
        return Dungeon.hero.pos / Dungeon.level.width() <= 79;
    }

    public void syncHud() {
        RegionState region = RegionState.current();
        if (region != null) region.syncHud();
    }

    @Override
    public boolean act() {
        if (Dungeon.level instanceof ActOneReturnLevel) {
            if (!journeyStarted && heroHasLeftExitBasin()) markJourneyStarted();
            ensureYendorConsistency();
            ((ActOneReturnLevel) Dungeon.level).tickScene(this);
        }
        spend(TICK);
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(OPENING, openingShown);
        bundle.put(JOURNEY_STARTED, journeyStarted);
        bundle.put(CAMP_DISCOVERED, campDiscovered);
        bundle.put(CAMP_BEDROLL, campBedrollClueSeen);
        bundle.put(CAMP_LIST, campListClueSeen);
        bundle.put(HERO_MARK, heroMarkSeen);
        bundle.put(FARMER_SEEN, farmerEventSeen);
        bundle.put(FARMER_OUTCOME, farmerOutcome.ordinal());
        bundle.put(DONKEY_OUTCOME, donkeyOutcome.ordinal());
        bundle.put(CART_OUTCOME, cartOutcome.ordinal());
        bundle.put(WOLVES_OUTCOME, wolvesOutcome.ordinal());
        bundle.put(MORNINGCREEK_HEARD, morningcreekHeardOf);
        bundle.put(YENDOR_ANOMALY, yendorAnomalyStarted);
        bundle.put(YENDOR_MISSING, yendorTemporarilyMissing);
        bundle.put(CROW_ACTIVE, crowChaseActive);
        bundle.put(CROW_RESOLVED, crowChaseResolved);
        bundle.put(CROW_THEFT_PRESENTED, crowTheftPresented);
        bundle.put(SHRINE_DISCOVERED, shrineDiscovered);
        bundle.put(INSCRIPTION, inscriptionRead);
        bundle.put(YENDOR_RECOVERED, yendorRecovered);
        bundle.put(NORTH_EXIT, northExitReached);
        bundle.put(FARMER_DIALOGUE, farmerDialogueCompleted);
        bundle.put(YENDOR_NOTICE, yendorAnomalyNoticeShown);
        bundle.put(NORTH_NOTICE, northExitNoticeShown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        openingShown = bundle.getBoolean(OPENING);
        journeyStarted = bundle.getBoolean(JOURNEY_STARTED);
        campDiscovered = bundle.getBoolean(CAMP_DISCOVERED);
        campBedrollClueSeen = bundle.getBoolean(CAMP_BEDROLL);
        campListClueSeen = bundle.getBoolean(CAMP_LIST);
        heroMarkSeen = bundle.getBoolean(HERO_MARK);
        farmerEventSeen = bundle.getBoolean(FARMER_SEEN);
        farmerOutcome = enumAt(FarmerOutcome.values(), bundle.getInt(FARMER_OUTCOME), FarmerOutcome.UNRESOLVED);
        donkeyOutcome = enumAt(DonkeyOutcome.values(), bundle.getInt(DONKEY_OUTCOME), DonkeyOutcome.UNRESOLVED);
        cartOutcome = enumAt(CartOutcome.values(), bundle.getInt(CART_OUTCOME), CartOutcome.UNRESOLVED);
        wolvesOutcome = enumAt(WolvesOutcome.values(), bundle.getInt(WOLVES_OUTCOME), WolvesOutcome.UNRESOLVED);
        morningcreekHeardOf = bundle.getBoolean(MORNINGCREEK_HEARD);
        yendorAnomalyStarted = bundle.getBoolean(YENDOR_ANOMALY);
        yendorTemporarilyMissing = bundle.getBoolean(YENDOR_MISSING);
        crowChaseActive = bundle.getBoolean(CROW_ACTIVE);
        crowChaseResolved = bundle.getBoolean(CROW_RESOLVED);
        crowTheftPresented = bundle.getBoolean(CROW_THEFT_PRESENTED);
        shrineDiscovered = bundle.getBoolean(SHRINE_DISCOVERED);
        inscriptionRead = bundle.getBoolean(INSCRIPTION);
        yendorRecovered = bundle.getBoolean(YENDOR_RECOVERED);
        northExitReached = bundle.getBoolean(NORTH_EXIT);
        farmerDialogueCompleted = bundle.getBoolean(FARMER_DIALOGUE);
        yendorAnomalyNoticeShown = bundle.getBoolean(YENDOR_NOTICE);
        northExitNoticeShown = bundle.getBoolean(NORTH_NOTICE);
    }

    private static <T> T enumAt(T[] values, int ordinal, T fallback) {
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : fallback;
    }
}
