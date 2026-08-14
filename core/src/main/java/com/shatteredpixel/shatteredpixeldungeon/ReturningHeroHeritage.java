/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cudgel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;

/**
 * Canonical class heritage for the returning-hero reconstruction flow.
 *
 * This mirrors the original HeroClass starting loadouts, but keeps the facts in
 * one Echoes-owned place so the sequel UI does not need to duplicate switch
 * statements or infer class identity from localized item names.
 */
public final class ReturningHeroHeritage {

    private ReturningHeroHeritage() {
        // Utility class.
    }

    /** The weapon the class originally entered the dungeon with. */
    public static Class<? extends Item> initialWeapon(HeroClass heroClass) {
        switch (heroClass) {
            case WARRIOR:
                return WornShortsword.class;
            case MAGE:
                return MagesStaff.class;
            case ROGUE:
                return Dagger.class;
            case HUNTRESS:
                return Gloves.class;
            case DUELIST:
                return Rapier.class;
            case CLERIC:
            default:
                return Cudgel.class;
        }
    }

    /**
     * A signature class item that is not simply the starting melee weapon.
     * Null means that class has no extra signature item in the original start.
     */
    public static Class<? extends Item> signatureItem(HeroClass heroClass) {
        switch (heroClass) {
            case WARRIOR:
                return BrokenSeal.class;
            case ROGUE:
                return CloakOfShadows.class;
            case HUNTRESS:
                return SpiritBow.class;
            case CLERIC:
                return HolyTome.class;
            case MAGE:
            case DUELIST:
            default:
                return null;
        }
    }

    /** Optional starting thrown weapon recorded as history, not a guaranteed return item. */
    public static Class<? extends Item> initialThrownWeapon(HeroClass heroClass) {
        switch (heroClass) {
            case WARRIOR:
                return ThrowingStone.class;
            case ROGUE:
                return ThrowingKnife.class;
            case DUELIST:
                return ThrowingSpike.class;
            case MAGE:
            case HUNTRESS:
            case CLERIC:
            default:
                return null;
        }
    }

    /** The Mage's original staff begins imbued with Magic Missile. */
    public static Class<? extends Item> initialMageImbuement() {
        return WandOfMagicMissile.class;
    }

    public static boolean hasClassArtifact(HeroClass heroClass) {
        return heroClass == HeroClass.ROGUE || heroClass == HeroClass.CLERIC;
    }

    public static Class<? extends Item> classArtifact(HeroClass heroClass) {
        if (heroClass == HeroClass.ROGUE) return CloakOfShadows.class;
        if (heroClass == HeroClass.CLERIC) return HolyTome.class;
        return null;
    }

    /** Class artifacts return at their original full +10 state. */
    public static int classArtifactReturnLevel(HeroClass heroClass) {
        return hasClassArtifact(heroClass)
                ? ReturningHeroBuildRules.CLASS_ARTIFACT_RETURN_LEVEL
                : -1;
    }

    /** The returning Mage's staff is a fixed full-strength class heritage item. */
    public static int mageStaffReturnLevel(HeroClass heroClass) {
        return heroClass == HeroClass.MAGE
                ? ReturningHeroBuildRules.MAGES_STAFF_RETURN_LEVEL
                : -1;
    }
}
