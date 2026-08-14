/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Entry;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Kind;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;

/**
 * Central cost calculator for returning-hero reconstruction.
 *
 * These tables normalize the original tier/upgrade curves into Echoes build
 * points.  They are intentionally kept outside UI code so later balance passes
 * can replace numbers without changing save data or scene layout.
 */
public final class ReturningHeroBuildCost {

    // Index is upgrade level. T1 is career heritage and has no selectable table.
    private static final int[][] WEAPON_COST = new int[][]{
            null,
            null,
            {0, 1, 2, 3, 4, 5, 6, 7},       // T2
            {2, 3, 4, 6, 7, 8, 9, 11},      // T3
            {3, 5, 6, 8, 9, 11, 12, 14},    // T4
            {5, 7, 8, 10, 12, 14, 15, 17}   // T5
    };

    private static final int[][] ARMOR_COST = new int[][]{
            null,
            null,
            {0, 1, 2, 3, 4, 5, 6},          // T2
            {1, 2, 4, 5, 6, 8, 9},          // T3
            {2, 3, 5, 7, 8, 10, 12},        // T4
            {2, 4, 6, 8, 10, 12, 14}        // T5
    };

    private ReturningHeroBuildCost() {
        // Utility class.
    }

    public static int weaponCost(String itemId, int level) {
        Entry entry = ReturningHeroItemCatalog.byId(itemId);
        if (entry == null || entry.kind != Kind.MELEE_WEAPON || !entry.selectable) return -1;
        if (entry.tier < 2 || entry.tier >= WEAPON_COST.length) return -1;
        int[] table = WEAPON_COST[entry.tier];
        if (level < 0 || level >= table.length) return -1;
        return table[level] + weaponFeatureAdjustment(itemId, level);
    }

    /**
     * Reserved hook for the detailed per-weapon pass (reach, speed, accuracy,
     * blocking, etc.).  Stable IDs mean that pass can add small modifiers here
     * without touching presets or scenes.
     */
    private static int weaponFeatureAdjustment(String itemId, int level) {
        return 0;
    }

    public static int armorCost(String itemId, int level) {
        Entry entry = ReturningHeroItemCatalog.byId(itemId);
        if (entry == null || entry.kind != Kind.ARMOR || !entry.selectable) return -1;
        if (entry.tier < 2 || entry.tier >= ARMOR_COST.length) return -1;
        int[] table = ARMOR_COST[entry.tier];
        if (level < 0 || level >= table.length) return -1;
        return table[level];
    }

    public static int carriedWandCost(String itemId, int level) {
        if (!ReturningHeroItemCatalog.isSelectable(itemId, Kind.WAND)) return -1;
        if (level < 0 || level > ReturningHeroBuildRules.MAX_CARRIED_WAND_LEVEL) return -1;
        return ReturningHeroBuildRules.CARRIED_WAND_BASE_COST + level;
    }

    public static int ringCost(String itemId, int level) {
        if (!ReturningHeroItemCatalog.isSelectable(itemId, Kind.RING)) return -1;
        if (level < 0 || level > ReturningHeroBuildRules.MAX_RING_LEVEL) return -1;
        return level;
    }

    public static int equipmentSpent(HeroClass heroClass, ReturningHeroLoadout loadout) {
        if (loadout == null) return 0;
        int total = 0;

        if (loadout.primaryWeaponId != null) {
            int value = weaponCost(loadout.primaryWeaponId, loadout.primaryWeaponLevel);
            if (value < 0) return -1;
            total += value;
        }

        if (loadout.armorId != null) {
            int value = armorCost(loadout.armorId, loadout.armorLevel);
            if (value < 0) return -1;
            total += value;
        }

        if (loadout.carriedWandId != null) {
            int value = carriedWandCost(loadout.carriedWandId, loadout.carriedWandLevel);
            if (value < 0) return -1;
            total += value;
        }

        if (loadout.ringSlotId != null) {
            int value = ringCost(loadout.ringSlotId, loadout.ringSlotLevel);
            if (value < 0) return -1;
            total += value;
        }

        if (loadout.miscIsRing()) {
            int value = ringCost(loadout.miscSlotId, loadout.miscSlotLevel);
            if (value < 0) return -1;
            total += value;
        }

        // Career heritage (including the returning Mage's completed staff) is
        // intentionally not charged against the 15-point player budget.
        return total;
    }

    public static int equipmentRemaining(HeroClass heroClass, ReturningHeroLoadout loadout) {
        int spent = equipmentSpent(heroClass, loadout);
        if (spent < 0) return -1;
        return ReturningHeroBuildRules.EQUIPMENT_RECONSTRUCTION_BUDGET - spent;
    }

    public static int ordinaryArtifactGrowthSpent(HeroClass heroClass,
                                                   ReturningHeroLoadout loadout) {
        if (loadout == null) return 0;
        int total = 0;

        // A class artifact is derived and free. Classes without one may use the
        // dedicated artifact slot for an ordinary artifact.
        if (!ReturningHeroHeritage.hasClassArtifact(heroClass)
                && loadout.artifactSlotId != null) {
            total += Math.max(0, loadout.artifactSlotLevel);
        }

        if (loadout.miscIsArtifact()) {
            total += Math.max(0, loadout.miscSlotLevel);
        }
        return total;
    }
}
