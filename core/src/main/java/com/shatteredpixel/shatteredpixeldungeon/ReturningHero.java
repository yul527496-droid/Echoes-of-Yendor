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
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/** Builds the post-Yog hero described by the player's ledger entry. */
public final class ReturningHero {

    private static final int RETURNING_STRENGTH = 20;

    private ReturningHero() {
    }

    public static void apply(Hero hero) {
        ReturningHeroProfile profile = new ReturningHeroProfile();
        if (hero != null && hero.heroClass != null) profile.heroClass = hero.heroClass;
        profile.resetDependentChoices();
        apply(hero, profile);
    }

    public static void apply(Hero hero, ReturningHeroProfile profile) {
        if (hero == null) return;
        if (profile == null) profile = new ReturningHeroProfile();

        hero.lvl = Hero.MAX_LEVEL;
        hero.exp = 0;
        hero.STR = RETURNING_STRENGTH;

        // The ledger owns these choices. Original SPD still owns the actual
        // subclass/talent/armor-ability implementations.
        hero.subClass = profile.subClass();
        Talent.initSubclassTalents(hero);
        hero.armorAbility = profile.armorAbility();
        Talent.initArmorTalents(hero);

        // Apply equipment before talent points. In particular this prevents the
        // Mage's reconstruction-time staff imbuement from accidentally triggering
        // Wand Preservation as though the player had just performed a live imbue.
        ReturningHeroLoadoutApplier.apply(hero, profile);
        applyConfiguredTalents(hero, profile);

        hero.updateHT(true);
        hero.HP = hero.HT;

        PotionOfHealing healing = new PotionOfHealing();
        healing.quantity(3);
        healing.identify();
        healing.collect();
    }

    private static void applyConfiguredTalents(Hero hero, ReturningHeroProfile profile) {
        ReturningHeroTalentPlan plan = profile.talentPlan;
        boolean modernPlan = plan != null
                && !plan.isEmpty()
                && ReturningHeroTalentRules.validate(
                plan, profile.heroClass, profile.subClass(), profile.armorAbility()).isValid();

        if (!modernPlan) {
            // Compatibility path for the currently shipped ledger pages. It can
            // be removed once the free talent editor is the only entry flow.
            spendAvailableTalents(hero, profile.growthPreset);
            return;
        }

        for (int tier = 1; tier <= hero.talents.size(); tier++) {
            LinkedHashMap<Talent, Integer> talents = hero.talents.get(tier - 1);
            for (Talent talent : talents.keySet()) {
                int target = plan.pointsIn(talent);
                while (talents.get(talent) < target) {
                    upgradeReturningTalent(hero, talent, talents);
                }
            }
        }
    }

    private static void spendAvailableTalents(Hero hero, ReturningHeroProfile.GrowthPreset preset) {
        // Legacy compatibility only. The detailed talent plan uses the exact same
        // original tier maps and supersedes this once the new ledger editor lands.
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
                    if (talents.get(talent) < talent.maxPoints()
                            && hero.talentPointsAvailable(tier) > 0) {
                        upgradeReturningTalent(hero, talent, talents);
                        spent = true;
                    }
                }
                if (!spent) break;
            }
        }
    }

    /**
     * Returning heroes are configured before SequelTransitionScene places them
     * on FinalStairLevel. Sensory talent upgrade callbacks immediately observe
     * the map, so record those points directly until the hero has a legal cell.
     */
    private static void upgradeReturningTalent(Hero hero, Talent talent,
                                                LinkedHashMap<Talent, Integer> talents) {
        boolean sensoryTalent = talent == Talent.HEIGHTENED_SENSES
                || talent == Talent.FARSIGHT
                || talent == Talent.DIVINE_SENSE;
        boolean heroNotPlaced = Dungeon.level == null
                || hero.pos < 0
                || hero.pos >= Dungeon.level.length();

        if (sensoryTalent && heroNotPlaced) {
            talents.put(talent, talents.get(talent) + 1);
        } else {
            hero.upgradeTalent(talent);
        }
    }
}
