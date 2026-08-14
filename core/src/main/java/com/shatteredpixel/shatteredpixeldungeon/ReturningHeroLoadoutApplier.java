/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClericArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.DuelistArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.HuntressArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.RogueArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.WarriorArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;

/**
 * Turns validated returning-hero reconstruction data into real SPD item objects.
 *
 * Dungeon.init() has already created the original class start before this runs,
 * so class heritage is upgraded/reused rather than blindly duplicated.  Until
 * the expanded ledger UI replaces the old three-choice picker, missing modern
 * fields deliberately fall back to the previous representative +3 equipment.
 */
public final class ReturningHeroLoadoutApplier {

    private static final int LEGACY_GEAR_UPGRADE = 3;

    private ReturningHeroLoadoutApplier() {
        // Utility class.
    }

    public static void apply(Hero hero, ReturningHeroProfile profile) {
        if (hero == null || profile == null) return;

        ReturningHeroLoadout loadout = profile.loadout;
        boolean modern = loadout != null
                && loadout.rulesetVersion == ReturningHeroBuildRules.RULESET_VERSION
                && ReturningHeroBuildValidator.validate(profile.heroClass, loadout).isValid();

        // Career artifacts are already created by HeroClass.initHero(). They are
        // guaranteed to return fully grown, independent of the ordinary budgets.
        restoreClassArtifact(hero, profile.heroClass);

        if (profile.heroClass == HeroClass.MAGE) {
            applyMageStaff(hero, profile, modern ? loadout : null);
        } else {
            applyPrimaryWeapon(hero, profile, modern ? loadout : null);
        }

        applyArmor(hero, profile, modern ? loadout : null);

        if (modern) {
            applyAccessorySlots(hero, profile.heroClass, loadout);
            applyCarriedWand(loadout);
            applyTrinket(loadout);
        }
    }

    private static void applyMageStaff(Hero hero, ReturningHeroProfile profile,
                                       ReturningHeroLoadout loadout) {
        MagesStaff staff = hero.belongings.weapon instanceof MagesStaff
                ? (MagesStaff) hero.belongings.weapon
                : new MagesStaff();

        String imbuementId = mageImbuementId(profile, loadout);
        Wand imbued = item(imbuementId, Wand.class);
        if (imbued == null) {
            imbued = item("wand.magic_missile", Wand.class);
        }

        staff.imbueWand(imbued, hero);
        int targetLevel = ReturningHeroBuildRules.MAGES_STAFF_RETURN_LEVEL;
        if (staff.trueLevel() < targetLevel) {
            staff.upgrade(targetLevel - staff.trueLevel());
        }
        staff.identify();
        staff.activate(hero);
        Dungeon.quickslot.setSlot(0, staff);

        // A Mage may still reconstruct an ordinary preferred melee weapon.  In
        // that case the completed staff remains carried and usable, while the
        // selected ordinary weapon occupies the actual weapon slot.
        if (loadout != null && loadout.primaryWeaponId != null) {
            hero.belongings.weapon = null;
            staff.collect(hero.belongings.backpack);

            MeleeWeapon preferred = item(loadout.primaryWeaponId, MeleeWeapon.class);
            if (preferred != null) {
                preferred.level(loadout.primaryWeaponLevel);
                preferred.identify();
                hero.belongings.weapon = preferred;
                preferred.activate(hero);
            } else {
                hero.belongings.weapon = staff;
            }
        } else {
            hero.belongings.weapon = staff;
        }
    }

    private static String mageImbuementId(ReturningHeroProfile profile,
                                          ReturningHeroLoadout loadout) {
        if (loadout != null && loadout.mageStaffImbuementId != null) {
            boolean onlyDefaultHeritage = "wand.magic_missile".equals(loadout.mageStaffImbuementId)
                    && loadout.primaryWeaponId == null
                    && loadout.armorId == null
                    && loadout.carriedWandId == null
                    && loadout.artifactSlotId == null
                    && loadout.miscSlotId == null
                    && loadout.ringSlotId == null
                    && loadout.trinketId == null;

            // Compatibility with the old Mage three-choice page while the new
            // catalog UI is not yet exposed.
            if (onlyDefaultHeritage && profile.weaponIndex == 1) return "wand.lightning";
            if (onlyDefaultHeritage && profile.weaponIndex == 2) return "wand.frost";
            return loadout.mageStaffImbuementId;
        }

        if (profile.weaponIndex == 1) return "wand.lightning";
        if (profile.weaponIndex == 2) return "wand.frost";
        return "wand.magic_missile";
    }

    private static void applyPrimaryWeapon(Hero hero, ReturningHeroProfile profile,
                                           ReturningHeroLoadout loadout) {
        String id = loadout != null && loadout.primaryWeaponId != null
                ? loadout.primaryWeaponId
                : legacyWeaponId(profile);
        int level = loadout != null && loadout.primaryWeaponId != null
                ? loadout.primaryWeaponLevel
                : LEGACY_GEAR_UPGRADE;

        MeleeWeapon weapon = item(id, MeleeWeapon.class);
        if (weapon == null) return;
        weapon.level(level);
        weapon.identify();
        hero.belongings.weapon = weapon;
        weapon.activate(hero);

        if (hero.heroClass == HeroClass.DUELIST) {
            Dungeon.quickslot.setSlot(0, weapon);
        }
    }

    private static String legacyWeaponId(ReturningHeroProfile profile) {
        int choice = Math.max(0, Math.min(profile.weaponIndex, 2));
        switch (profile.heroClass) {
            case WARRIOR:
                if (choice == 1) return "weapon.t5.war_hammer";
                if (choice == 2) return "weapon.t4.runic_blade";
                return "weapon.t5.greatsword";
            case ROGUE:
                if (choice == 1) return "weapon.t4.runic_blade";
                if (choice == 2) return "weapon.t3.scimitar";
                return "weapon.t4.assassins_blade";
            case HUNTRESS:
                if (choice == 1) return "weapon.t4.runic_blade";
                if (choice == 2) return "weapon.t5.greatsword";
                return "weapon.t3.scimitar";
            case DUELIST:
                if (choice == 1) return "weapon.t3.scimitar";
                if (choice == 2) return "weapon.t4.assassins_blade";
                return "weapon.t4.runic_blade";
            case CLERIC:
            default:
                if (choice == 1) return "weapon.t4.runic_blade";
                if (choice == 2) return "weapon.t5.greatsword";
                return "weapon.t5.war_hammer";
        }
    }

    private static void applyArmor(Hero hero, ReturningHeroProfile profile,
                                   ReturningHeroLoadout loadout) {
        ClassArmor armor;

        if (loadout != null && loadout.armorId != null) {
            Armor base = item(loadout.armorId, Armor.class);
            if (base != null) {
                base.level(loadout.armorLevel);
                base.identify();
                if (hero.heroClass == HeroClass.WARRIOR) {
                    BrokenSeal seal = new BrokenSeal();
                    if (loadout.armorLevel > 0) seal.upgrade();
                    base.affixSeal(seal);
                }
                armor = ClassArmor.upgrade(hero, base);
            } else {
                armor = legacyClassArmor(hero.heroClass);
                armor.upgrade(LEGACY_GEAR_UPGRADE);
            }
        } else {
            armor = legacyClassArmor(hero.heroClass);
            if (hero.heroClass == HeroClass.WARRIOR) {
                armor.affixSeal(new BrokenSeal());
            }
            armor.upgrade(LEGACY_GEAR_UPGRADE);
        }

        armor.identify();
        armor.charge = 100f;
        hero.belongings.armor = armor;
        armor.activate(hero);
    }

    private static ClassArmor legacyClassArmor(HeroClass heroClass) {
        switch (heroClass) {
            case WARRIOR:
                return new WarriorArmor();
            case MAGE:
                return new MageArmor();
            case ROGUE:
                return new RogueArmor();
            case HUNTRESS:
                return new HuntressArmor();
            case DUELIST:
                return new DuelistArmor();
            case CLERIC:
            default:
                return new ClericArmor();
        }
    }

    private static void restoreClassArtifact(Hero hero, HeroClass heroClass) {
        if (!ReturningHeroHeritage.hasClassArtifact(heroClass)) return;

        Class<? extends Item> expected = ReturningHeroHeritage.classArtifact(heroClass);
        Artifact artifact = expected != null && expected.isInstance(hero.belongings.artifact)
                ? hero.belongings.artifact
                : null;
        if (artifact == null) {
            String id = ReturningHeroItemCatalog.idForClass(expected);
            artifact = item(id, Artifact.class);
            hero.belongings.artifact = artifact;
        }
        if (artifact == null) return;

        raiseArtifactToVisibleLevel(
                artifact, ReturningHeroBuildRules.CLASS_ARTIFACT_RETURN_LEVEL);
        artifact.identify();
        artifact.activate(hero);
    }

    private static void applyAccessorySlots(Hero hero, HeroClass heroClass,
                                            ReturningHeroLoadout loadout) {
        if (!ReturningHeroHeritage.hasClassArtifact(heroClass)
                && loadout.artifactSlotId != null) {
            Artifact artifact = item(loadout.artifactSlotId, Artifact.class);
            if (artifact != null) {
                raiseArtifactToVisibleLevel(artifact, loadout.artifactSlotLevel);
                artifact.identify();
                hero.belongings.artifact = artifact;
                artifact.activate(hero);
            }
        }

        hero.belongings.misc = null;
        if (loadout.miscIsRing()) {
            Ring ring = item(loadout.miscSlotId, Ring.class);
            if (ring != null) {
                ring.level(loadout.miscSlotLevel);
                ring.identify();
                hero.belongings.misc = ring;
                ring.activate(hero);
            }
        } else if (loadout.miscIsArtifact()) {
            Artifact artifact = item(loadout.miscSlotId, Artifact.class);
            if (artifact != null) {
                raiseArtifactToVisibleLevel(artifact, loadout.miscSlotLevel);
                artifact.identify();
                hero.belongings.misc = artifact;
                artifact.activate(hero);
            }
        }

        hero.belongings.ring = null;
        if (loadout.ringSlotId != null) {
            Ring ring = item(loadout.ringSlotId, Ring.class);
            if (ring != null) {
                ring.level(loadout.ringSlotLevel);
                ring.identify();
                hero.belongings.ring = ring;
                ring.activate(hero);
            }
        }
    }

    private static void applyCarriedWand(ReturningHeroLoadout loadout) {
        if (loadout.carriedWandId == null) return;
        Wand wand = item(loadout.carriedWandId, Wand.class);
        if (wand == null) return;
        wand.level(loadout.carriedWandLevel);
        wand.curCharges = wand.maxCharges;
        wand.identify();
        wand.collect();
    }

    private static void applyTrinket(ReturningHeroLoadout loadout) {
        if (loadout.trinketId == null) return;
        Trinket trinket = item(loadout.trinketId, Trinket.class);
        if (trinket == null) return;
        trinket.level(loadout.trinketLevel);
        trinket.identify();
        trinket.collect();
    }

    private static void raiseArtifactToVisibleLevel(Artifact artifact, int targetVisible) {
        if (artifact == null || targetVisible <= artifact.visiblyUpgraded()) return;
        artifact.transferUpgrade(targetVisible - artifact.visiblyUpgraded());
    }

    private static <T extends Item> T item(String id, Class<T> type) {
        Item item = ReturningHeroItemCatalog.newItem(id);
        return type.isInstance(item) ? type.cast(item) : null;
    }
}
