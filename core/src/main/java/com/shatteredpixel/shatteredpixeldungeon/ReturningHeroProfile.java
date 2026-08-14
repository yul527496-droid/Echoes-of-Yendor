/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

/**
 * The in-world ledger entry used to reconstruct the hero who returned from the dungeon.
 * This deliberately stores choices rather than inventing a second class system.
 */
public class ReturningHeroProfile {

    public enum GrowthPreset {
        BALANCED("均衡记录"),
        OFFENSE("进攻记录"),
        SURVIVAL("生存记录");

        public final String title;

        GrowthPreset(String title) {
            this.title = title;
        }
    }

    public String name = "无名者";
    public HeroClass heroClass = HeroClass.WARRIOR;
    public int subclassIndex = 0;
    public int abilityIndex = 0;

    /**
     * Legacy three-choice weapon index.  Kept temporarily so the current ledger
     * screens remain usable while the stable-ID equipment flow is introduced.
     */
    public int weaponIndex = 0;

    /** Temporary compatibility field until free talent allocation replaces it. */
    public GrowthPreset growthPreset = GrowthPreset.BALANCED;

    /** New stable-ID equipment build state. */
    public ReturningHeroLoadout loadout = new ReturningHeroLoadout();

    /** Free allocation of the original hero's four talent tiers. */
    public ReturningHeroTalentPlan talentPlan = new ReturningHeroTalentPlan();

    public HeroSubClass subClass() {
        HeroSubClass[] values = heroClass.subClasses();
        return values[Math.max(0, Math.min(subclassIndex, values.length - 1))];
    }

    public ArmorAbility armorAbility() {
        ArmorAbility[] values = heroClass.armorAbilities();
        return values[Math.max(0, Math.min(abilityIndex, values.length - 1))];
    }

    /** Original class loadout facts are owned by ReturningHeroHeritage. */
    public Class<? extends Item> initialWeaponClass() {
        return ReturningHeroHeritage.initialWeapon(heroClass);
    }

    public Class<? extends Item> signatureHeritageClass() {
        return ReturningHeroHeritage.signatureItem(heroClass);
    }

    public Class<? extends Item> initialThrownWeaponClass() {
        return ReturningHeroHeritage.initialThrownWeapon(heroClass);
    }

    public boolean hasClassArtifact() {
        return ReturningHeroHeritage.hasClassArtifact(heroClass);
    }

    public int classArtifactReturnLevel() {
        return ReturningHeroHeritage.classArtifactReturnLevel(heroClass);
    }

    public int mageStaffReturnLevel() {
        return ReturningHeroHeritage.mageStaffReturnLevel(heroClass);
    }

    /**
     * Legacy three-choice weapon UI.  This remains temporarily so the current
     * ledger flow keeps working while the full stable-ID loadout catalog is
     * introduced behind it.
     */
    public String[] weaponOptions() {
        switch (heroClass) {
            case WARRIOR:
                return new String[]{"巨剑", "战锤", "符文之刃"};
            case MAGE:
                return new String[]{"魔弹法杖", "雷霆法杖", "寒霜法杖"};
            case ROGUE:
                return new String[]{"刺客之刃", "符文之刃", "弯刀"};
            case HUNTRESS:
                return new String[]{"弯刀", "符文之刃", "巨剑"};
            case DUELIST:
                return new String[]{"符文之刃", "弯刀", "刺客之刃"};
            case CLERIC:
            default:
                return new String[]{"战锤", "符文之刃", "巨剑"};
        }
    }

    public String weaponName() {
        String[] options = weaponOptions();
        return options[Math.max(0, Math.min(weaponIndex, options.length - 1))];
    }

    public void resetDependentChoices() {
        subclassIndex = 0;
        abilityIndex = 0;
        weaponIndex = 0;
        growthPreset = GrowthPreset.BALANCED;
        if (loadout == null) loadout = new ReturningHeroLoadout();
        loadout.resetForClass(heroClass);
        if (talentPlan == null) talentPlan = new ReturningHeroTalentPlan();
        talentPlan.clear();
    }

    public void saveToSlot(int slot) {
        LedgerSettings.name(slot, name);
        LedgerSettings.heroClass(slot, heroClass.ordinal());
        LedgerSettings.subclass(slot, subclassIndex);
        LedgerSettings.ability(slot, abilityIndex);
        LedgerSettings.weapon(slot, weaponIndex);
        LedgerSettings.growth(slot, growthPreset.ordinal());
        LedgerSettings.loadout(slot, loadout);
        LedgerSettings.talentPlan(slot, talentPlan);
    }

    public static ReturningHeroProfile loadFromSlot(int slot) {
        ReturningHeroProfile profile = new ReturningHeroProfile();
        profile.name = LedgerSettings.name(slot);

        HeroClass[] classes = HeroClass.values();
        int classIndex = LedgerSettings.heroClass(slot);
        profile.heroClass = classes[Math.max(0, Math.min(classIndex, classes.length - 1))];

        profile.subclassIndex = LedgerSettings.subclass(slot);
        profile.abilityIndex = LedgerSettings.ability(slot);
        profile.weaponIndex = LedgerSettings.weapon(slot);

        GrowthPreset[] presets = GrowthPreset.values();
        int growthIndex = LedgerSettings.growth(slot);
        profile.growthPreset = presets[Math.max(0, Math.min(growthIndex, presets.length - 1))];
        profile.loadout = LedgerSettings.loadout(slot, profile.heroClass);
        profile.talentPlan = LedgerSettings.talentPlan(slot);
        return profile;
    }
}
