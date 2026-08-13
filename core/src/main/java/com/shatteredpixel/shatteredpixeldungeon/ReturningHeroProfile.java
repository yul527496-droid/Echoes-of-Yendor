/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;

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
    public int weaponIndex = 0;
    public GrowthPreset growthPreset = GrowthPreset.BALANCED;

    public HeroSubClass subClass() {
        HeroSubClass[] values = heroClass.subClasses();
        return values[Math.max(0, Math.min(subclassIndex, values.length - 1))];
    }

    public ArmorAbility armorAbility() {
        ArmorAbility[] values = heroClass.armorAbilities();
        return values[Math.max(0, Math.min(abilityIndex, values.length - 1))];
    }

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
    }

    public void saveToSlot(int slot) {
        SPDSettings.ledgerName(slot, name);
        SPDSettings.ledgerClass(slot, heroClass.ordinal());
        SPDSettings.ledgerSubclass(slot, subclassIndex);
        SPDSettings.ledgerAbility(slot, abilityIndex);
        SPDSettings.ledgerWeapon(slot, weaponIndex);
        SPDSettings.ledgerGrowth(slot, growthPreset.ordinal());
    }

    public static ReturningHeroProfile loadFromSlot(int slot) {
        ReturningHeroProfile profile = new ReturningHeroProfile();
        profile.name = SPDSettings.ledgerName(slot);

        HeroClass[] classes = HeroClass.values();
        int classIndex = SPDSettings.ledgerClass(slot);
        profile.heroClass = classes[Math.max(0, Math.min(classIndex, classes.length - 1))];

        profile.subclassIndex = SPDSettings.ledgerSubclass(slot);
        profile.abilityIndex = SPDSettings.ledgerAbility(slot);
        profile.weaponIndex = SPDSettings.ledgerWeapon(slot);

        GrowthPreset[] presets = GrowthPreset.values();
        int growthIndex = SPDSettings.ledgerGrowth(slot);
        profile.growthPreset = presets[Math.max(0, Math.min(growthIndex, presets.length - 1))];
        return profile;
    }
}
