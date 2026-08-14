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

    // Index is upgrade level. T1 is only available as the matching class heritage.
    private static final int[][] WEAPON_COST = new int[][]{
            null,
            {0, 0, 0, 1, 2, 2, 3, 4},       // T1 class heritage only
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

    /** Ordinary freely selectable T2-T5 melee weapons. */
    public static int weaponCost(String itemId, int level) {
        Entry entry = ReturningHeroItemCatalog.byId(itemId);
        if (entry == null || entry.kind != Kind.MELEE_WEAPON || !entry.selectable) return -1;
        if (entry.tier < 2 || entry.tier >= WEAPON_COST.length) return -1;
        return tableWeaponCost(entry, itemId, level);
    }

    /**
     * Cost for the actual primary-weapon slot.  In addition to ordinary T2-T5
     * choices, a non-Mage may keep using only their own original T1 weapon.
     * Mage T1 is the completed staff heritage and is represented implicitly.
     */
    public static int primaryWeaponCost(HeroClass heroClass, String itemId, int level) {
        Entry entry = ReturningHeroItemCatalog.byId(itemId);
        if (entry == null || entry.kind != Kind.MELEE_WEAPON) return -1;

        if (entry.tier == 1) {
            if (heroClass == null || heroClass == HeroClass.MAGE) return -1;
            String heritageId = ReturningHeroItemCatalog.idForClass(
                    ReturningHeroHeritage.initialWeapon(heroClass));
            if (!itemId.equals(heritageId)) return -1;
            return tableWeaponCost(entry, itemId, level);
        }

        return weaponCost(itemId, level);
    }

    private static int tableWeaponCost(Entry entry, String itemId, int level) {
        if (entry.tier < 1 || entry.tier >= WEAPON_COST.length) return -1;
        int[] table = WEAPON_COST[entry.tier];
        if (table == null || level < 0 || level >= table.length) return -1;
        int cost = table[level] + ReturningHeroWeaponBalance.featureAdjustment(itemId, level);
        return Math.max(0, cost);
    }

    /** Exposed for ledger explanations and balance diagnostics. */
    public static int weaponFeatureAdjustment(String itemId, int level) {
        Entry entry = ReturningHeroItemCatalog.byId(itemId);
        if (entry == null || entry.kind != Kind.MELEE_WEAPON) return 0;
        return ReturningHeroWeaponBalance.featureAdjustment(itemId, level);
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

    public static int trinketAlchemyCost(String itemId, int level) {
        return ReturningHeroTrinketBalance.alchemyCost(itemId, level);
    }

    public static int trinketAlchemyRemaining(ReturningHeroLoadout loadout) {
        if (loadout == null || loadout.trinketId == null) {
            return ReturningHeroBuildRules.TRINKET_ALCHEMY_RECONSTRUCTION_BUDGET;
        }
        int spent = trinketAlchemyCost(loadout.trinketId, loadout.trinketLevel);
        if (spent < 0) return -1;
        return ReturningHeroBuildRules.TRINKET_ALCHEMY_RECONSTRUCTION_BUDGET - spent;
    }

    public static int equipmentSpent(HeroClass heroClass, ReturningHeroLoadout loadout) {
        if (loadout == null) return 0;
        int total = 0;

        if (loadout.primaryWeaponId != null) {
            int value = primaryWeaponCost(heroClass, loadout.primaryWeaponId,
                    loadout.primaryWeaponLevel);
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
