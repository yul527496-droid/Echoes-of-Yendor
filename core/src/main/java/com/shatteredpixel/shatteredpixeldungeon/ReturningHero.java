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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;

import java.util.LinkedHashMap;

/** Builds a stable, representative post-Yog hero without pretending to import a real old save. */
public final class ReturningHero {

    private static final int RETURNING_STRENGTH = 20;
    private static final int GEAR_UPGRADE = 3;

    private ReturningHero() {
    }

    public static void apply(Hero hero) {
        if (hero == null) return;

        hero.lvl = Hero.MAX_LEVEL;
        hero.exp = 0;
        hero.STR = RETURNING_STRENGTH;

        // Until legacy choices are exposed in the sequel character screen, use the
        // first subclass as a deterministic development baseline.
        if (hero.subClass == null || hero.subClass == com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass.NONE) {
            hero.subClass = hero.heroClass.subClasses()[0];
            Talent.initSubclassTalents(hero);
        }

        equipRepresentativeGear(hero);
        spendAvailableTalents(hero);

        hero.updateHT(true);
        hero.HP = hero.HT;

        PotionOfHealing healing = new PotionOfHealing();
        healing.quantity(3);
        healing.identify();
        healing.collect();
    }

    private static void equipRepresentativeGear(Hero hero) {
        MeleeWeapon weapon;
        Armor armor;

        switch (hero.heroClass) {
            case WARRIOR:
                weapon = new Greatsword();
                armor = new PlateArmor();
                armor.affixSeal(new BrokenSeal());
                break;
            case MAGE:
                weapon = new MagesStaff(new WandOfMagicMissile());
                armor = new ScaleArmor();
                break;
            case ROGUE:
                weapon = new AssassinsBlade();
                armor = new ScaleArmor();
                break;
            case HUNTRESS:
                weapon = new Scimitar();
                armor = new ScaleArmor();
                break;
            case DUELIST:
                weapon = new RunicBlade();
                armor = new PlateArmor();
                break;
            case CLERIC:
            default:
                weapon = new WarHammer();
                armor = new PlateArmor();
                break;
        }

        weapon.upgrade(GEAR_UPGRADE);
        weapon.identify();
        armor.upgrade(GEAR_UPGRADE);
        armor.identify();

        hero.belongings.weapon = weapon;
        hero.belongings.armor = armor;

        // Mage and Duelist weapon mechanics need their equipped weapon activated.
        if (hero.heroClass == com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass.MAGE
                || hero.heroClass == com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass.DUELIST) {
            weapon.activate(hero);
            Dungeon.quickslot.setSlot(0, weapon);
        }
    }

    private static void spendAvailableTalents(Hero hero) {
        // Fill each unlocked tier round-robin so a level-30 returning hero does not
        // open with a screen full of unspent tutorial-era talent points.
        for (int tier = 1; tier <= hero.talents.size(); tier++) {
            int safety = 64;
            while (hero.talentPointsAvailable(tier) > 0 && safety-- > 0) {
                boolean spent = false;
                LinkedHashMap<Talent, Integer> talents = hero.talents.get(tier - 1);
                for (Talent talent : talents.keySet()) {
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
