/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;

/**
 * Stable-ID equipment choices for a reconstructed returning hero.
 *
 * Career heritage is not duplicated here.  For example the Rogue cloak, Cleric
 * tome, Mage staff level, and the class's original T1 weapon are derived from
 * ReturningHeroHeritage.  This object only stores player-controlled build choices.
 */
public class ReturningHeroLoadout {

    public enum MiscSlotKind {
        EMPTY,
        RING,
        ARTIFACT
    }

    public int rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;

    /** Optional final preferred ordinary melee weapon. */
    public String primaryWeaponId;
    public int primaryWeaponLevel;

    /** Final ordinary armor base before class-armor conversion. */
    public String armorId;
    public int armorLevel;

    /** Mage-only final staff imbuement. Staff level itself is career heritage. */
    public String mageStaffImbuementId;

    /** Optional extra wand carried by any class. */
    public String carriedWandId;
    public int carriedWandLevel;

    /**
     * Ordinary artifact in the artifact slot.  Classes with a fixed class
     * artifact derive that slot from ReturningHeroHeritage instead.
     */
    public String artifactSlotId;
    public int artifactSlotLevel;

    /** Original misc slot: may contain either a ring or an ordinary artifact. */
    public MiscSlotKind miscSlotKind = MiscSlotKind.EMPTY;
    public String miscSlotId;
    public int miscSlotLevel;

    /** Dedicated ring slot. */
    public String ringSlotId;
    public int ringSlotLevel;

    /** One independent trinket, which does not occupy the three misc slots. */
    public String trinketId;
    public int trinketLevel;

    public ReturningHeroLoadout() {
    }

    public ReturningHeroLoadout(ReturningHeroLoadout other) {
        if (other == null) return;
        rulesetVersion = other.rulesetVersion;
        primaryWeaponId = other.primaryWeaponId;
        primaryWeaponLevel = other.primaryWeaponLevel;
        armorId = other.armorId;
        armorLevel = other.armorLevel;
        mageStaffImbuementId = other.mageStaffImbuementId;
        carriedWandId = other.carriedWandId;
        carriedWandLevel = other.carriedWandLevel;
        artifactSlotId = other.artifactSlotId;
        artifactSlotLevel = other.artifactSlotLevel;
        miscSlotKind = other.miscSlotKind;
        miscSlotId = other.miscSlotId;
        miscSlotLevel = other.miscSlotLevel;
        ringSlotId = other.ringSlotId;
        ringSlotLevel = other.ringSlotLevel;
        trinketId = other.trinketId;
        trinketLevel = other.trinketLevel;
    }

    public ReturningHeroLoadout copy() {
        return new ReturningHeroLoadout(this);
    }

    /**
     * Clears player choices after the hero class changes.  Heritage itself is
     * derived, so the only class-specific selectable default currently needed
     * here is the Mage's original Magic Missile imbuement.
     */
    public void resetForClass(HeroClass heroClass) {
        rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
        primaryWeaponId = null;
        primaryWeaponLevel = 0;
        armorId = null;
        armorLevel = 0;
        carriedWandId = null;
        carriedWandLevel = 0;
        artifactSlotId = null;
        artifactSlotLevel = 0;
        miscSlotKind = MiscSlotKind.EMPTY;
        miscSlotId = null;
        miscSlotLevel = 0;
        ringSlotId = null;
        ringSlotLevel = 0;
        trinketId = null;
        trinketLevel = 0;

        if (heroClass == HeroClass.MAGE) {
            mageStaffImbuementId = ReturningHeroItemCatalog.idForClass(
                    ReturningHeroHeritage.initialMageImbuement());
        } else {
            mageStaffImbuementId = null;
        }
    }

    public void clearMiscSlot() {
        miscSlotKind = MiscSlotKind.EMPTY;
        miscSlotId = null;
        miscSlotLevel = 0;
    }

    public boolean hasCarriedWand() {
        return carriedWandId != null;
    }

    public boolean miscIsRing() {
        return miscSlotKind == MiscSlotKind.RING && miscSlotId != null;
    }

    public boolean miscIsArtifact() {
        return miscSlotKind == MiscSlotKind.ARTIFACT && miscSlotId != null;
    }
}
