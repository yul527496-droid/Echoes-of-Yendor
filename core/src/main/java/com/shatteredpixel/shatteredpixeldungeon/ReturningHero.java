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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClericArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.DuelistArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.HuntressArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.RogueArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.WarriorArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/** Builds a stable, representative post-Yog hero from the player's ledger entry. */
public final class ReturningHero {

    private static final int RETURNING_STRENGTH = 20;
    private static final int GEAR_UPGRADE = 3;

    private ReturningHero() {
    }

    public static void apply(Hero hero) {
        ReturningHeroProfile profile = new ReturningHeroProfile();
        if (hero != null && hero.heroClass != null) profile.heroClass = hero.heroClass;
        apply(hero, profile);
    }

    public static void apply(Hero hero, ReturningHeroProfile profile) {
        if (hero == null) return;
        if (profile == null) profile = new ReturningHeroProfile();

        hero.lvl = Hero.MAX_LEVEL;
        hero.exp = 0;
        hero.STR = RETURNING_STRENGTH;

        // The ledger now owns these choices; no more silently picking the first option.
        hero.subClass = profile.subClass();
        Talent.initSubclassTalents(hero);
        hero.armorAbility = profile.armorAbility();
        Talent.initArmorTalents(hero);

        equipRepresentativeGear(hero, profile);
        spendAvailableTalents(hero, profile.growthPreset);

        hero.updateHT(true);
        hero.HP = hero.HT;

        PotionOfHealing healing = new PotionOfHealing();
        healing.quantity(3);
        healing.identify();
        healing.collect();
    }

    private static void equipRepresentativeGear(Hero hero, ReturningHeroProfile profile) {
        MeleeWeapon weapon = selectedWeapon(profile);
        ClassArmor armor;

        switch (hero.heroClass) {
            case WARRIOR:
                armor = new WarriorArmor();
                armor.affixSeal(new BrokenSeal());
                break;
            case MAGE:
                armor = new MageArmor();
                break;
            case ROGUE:
                armor = new RogueArmor();
                break;
            case HUNTRESS:
                armor = new HuntressArmor();
                break;
            case DUELIST:
                armor = new DuelistArmor();
                break;
            case CLERIC:
            default:
                armor = new ClericArmor();
                break;
        }

        weapon.upgrade(GEAR_UPGRADE);
        weapon.identify();
        armor.upgrade(GEAR_UPGRADE);
        armor.identify();
        armor.charge = 100f;

        hero.belongings.weapon = weapon;
        hero.belongings.armor = armor;
        armor.activate(hero);

        if (hero.heroClass == HeroClass.MAGE || hero.heroClass == HeroClass.DUELIST) {
            weapon.activate(hero);
            Dungeon.quickslot.setSlot(0, weapon);
        }
    }

    private static MeleeWeapon selectedWeapon(ReturningHeroProfile profile) {
        int choice = Math.max(0, Math.min(profile.weaponIndex, 2));
        switch (profile.heroClass) {
            case WARRIOR:
                if (choice == 1) return new WarHammer();
                if (choice == 2) return new RunicBlade();
                return new Greatsword();
            case MAGE:
                if (choice == 1) return new MagesStaff(new WandOfLightning());
                if (choice == 2) return new MagesStaff(new WandOfFrost());
                return new MagesStaff(new WandOfMagicMissile());
            case ROGUE:
                if (choice == 1) return new RunicBlade();
                if (choice == 2) return new Scimitar();
                return new AssassinsBlade();
            case HUNTRESS:
                if (choice == 1) return new RunicBlade();
                if (choice == 2) return new Greatsword();
                return new Scimitar();
            case DUELIST:
                if (choice == 1) return new Scimitar();
                if (choice == 2) return new AssassinsBlade();
                return new RunicBlade();
            case CLERIC:
            default:
                if (choice == 1) return new RunicBlade();
                if (choice == 2) return new Greatsword();
                return new WarHammer();
        }
    }

    private static void spendAvailableTalents(Hero hero, ReturningHeroProfile.GrowthPreset preset) {
        // The underlying budgets remain Shattered's tier budgets. These three presets are intentionally
        // conservative first-pass starting points; the ledger's detailed +/- editor will manipulate
        // the same maps directly once the new front-end has survived playtesting.
        for (int tier = 1; tier <= hero.talents.size(); tier++) {
            int safety = 64;
            while (hero.talentPointsAvailable(tier) > 0 && safety-- > 0) {
                LinkedHashMap<Talent, Integer> talents = hero.talents.get(tier - 1);
                List<Talent> order = new ArrayList<>(talents.keySet());
                if (preset == ReturningHeroProfile.GrowthPreset.OFFENSE) {
                    Collections.reverse(order);
                } else if (preset == ReturningHeroProfile.GrowthPreset.BALANCED && order.size() > 2) {
                    Collections.rotate(order, tier % order.size());
                }

                boolean spent = false;
                for (Talent talent : order) {
                    if (talents.get(talent) < talent.maxPoints() && hero.talentPointsAvailable(tier) > 0) {
                        hero.upgradeTalent(talent);
                        spent = true;
                    }
                }
                if (!spent) break;
            }
        }
    }
}
