/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Original-talent rules used by the returning-hero reconstruction flow.
 *
 * Legal talent lists are derived from SPD itself through Talent's class,
 * subclass and armor-ability initialization helpers. Echoes therefore does not
 * maintain a second hand-written copy of every hero's talent table.
 *
 * The baseline reconstruction intentionally excludes Metamorphosis replacements
 * and Potion of Divine Inspiration bonus points. Those represent optional past
 * events rather than the guaranteed level-30 talent economy.
 */
public final class ReturningHeroTalentRules {

    public enum Problem {
        RULESET_MISMATCH,
        UNKNOWN_TALENT,
        TALENT_NOT_AVAILABLE,
        TALENT_LEVEL_TOO_HIGH,
        TIER_BUDGET_EXCEEDED
    }

    public static final class Result {
        private final List<Problem> problems;

        private Result(List<Problem> problems) {
            this.problems = Collections.unmodifiableList(problems);
        }

        public boolean isValid() {
            return problems.isEmpty();
        }

        public List<Problem> problems() {
            return problems;
        }
    }

    private ReturningHeroTalentRules() {
        // Utility class.
    }

    /**
     * Natural level-30 point totals: 5 / 6 / 8 / 10 for tiers 1..4.
     * Deriving them from SPD's thresholds keeps the relationship explicit.
     */
    public static int tierBudget(int tier) {
        if (tier < 1 || tier > Talent.MAX_TALENT_TIERS) return 0;
        return Talent.tierLevelThresholds[tier + 1] - Talent.tierLevelThresholds[tier];
    }

    public static ArrayList<LinkedHashMap<Talent, Integer>> legalTiers(
            HeroClass heroClass, HeroSubClass subClass, ArmorAbility armorAbility) {
        ArrayList<LinkedHashMap<Talent, Integer>> result = new ArrayList<>();
        Talent.initClassTalents(heroClass, result);
        Talent.initSubclassTalents(subClass, result);
        Talent.initArmorTalents(armorAbility, result);
        return result;
    }

    /**
     * Reconcile one dependency tier after a choice changes. This deliberately
     * preserves every other tier: subclass changes only invalidate old T3
     * talents, while armor-ability changes only invalidate old T4 talents.
     */
    public static void reconcileChangedTier(ReturningHeroTalentPlan plan, int tier,
                                            HeroClass oldClass, HeroSubClass oldSubClass,
                                            ArmorAbility oldAbility,
                                            HeroClass newClass, HeroSubClass newSubClass,
                                            ArmorAbility newAbility) {
        if (plan == null || tier < 1 || tier > Talent.MAX_TALENT_TIERS) return;

        ArrayList<LinkedHashMap<Talent, Integer>> oldLegal =
                legalTiers(oldClass, oldSubClass, oldAbility);
        ArrayList<LinkedHashMap<Talent, Integer>> newLegal =
                legalTiers(newClass, newSubClass, newAbility);

        LinkedHashMap<Talent, Integer> oldTier = oldLegal.get(tier - 1);
        LinkedHashMap<Talent, Integer> newTier = newLegal.get(tier - 1);

        for (Talent talent : oldTier.keySet()) {
            if (!newTier.containsKey(talent)) {
                plan.set(talent, 0);
            }
        }
        for (Talent talent : newTier.keySet()) {
            int current = plan.pointsIn(talent);
            if (current > talent.maxPoints()) {
                plan.set(talent, talent.maxPoints());
            }
        }
        plan.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
    }

    public static int tierOf(Talent talent, HeroClass heroClass,
                             HeroSubClass subClass, ArmorAbility armorAbility) {
        if (talent == null) return -1;
        ArrayList<LinkedHashMap<Talent, Integer>> legal =
                legalTiers(heroClass, subClass, armorAbility);
        for (int i = 0; i < legal.size(); i++) {
            if (legal.get(i).containsKey(talent)) return i + 1;
        }
        return -1;
    }

    public static int spent(ReturningHeroTalentPlan plan, int tier,
                            HeroClass heroClass, HeroSubClass subClass,
                            ArmorAbility armorAbility) {
        if (plan == null || tier < 1 || tier > Talent.MAX_TALENT_TIERS) return 0;
        ArrayList<LinkedHashMap<Talent, Integer>> legal =
                legalTiers(heroClass, subClass, armorAbility);
        int total = 0;
        for (Talent talent : legal.get(tier - 1).keySet()) {
            total += plan.pointsIn(talent);
        }
        return total;
    }

    public static int remaining(ReturningHeroTalentPlan plan, int tier,
                                HeroClass heroClass, HeroSubClass subClass,
                                ArmorAbility armorAbility) {
        return tierBudget(tier) - spent(plan, tier, heroClass, subClass, armorAbility);
    }

    /** True only when the normal level-30 budget for every tier is fully assigned. */
    public static boolean isComplete(ReturningHeroTalentPlan plan,
                                     HeroClass heroClass, HeroSubClass subClass,
                                     ArmorAbility armorAbility) {
        Result result = validate(plan, heroClass, subClass, armorAbility);
        if (!result.isValid()) return false;
        for (int tier = 1; tier <= Talent.MAX_TALENT_TIERS; tier++) {
            if (spent(plan, tier, heroClass, subClass, armorAbility) != tierBudget(tier)) {
                return false;
            }
        }
        return true;
    }

    public static Result validate(ReturningHeroTalentPlan plan,
                                  HeroClass heroClass, HeroSubClass subClass,
                                  ArmorAbility armorAbility) {
        ArrayList<Problem> problems = new ArrayList<>();
        if (plan == null || plan.rulesetVersion != ReturningHeroBuildRules.RULESET_VERSION) {
            problems.add(Problem.RULESET_MISMATCH);
            return new Result(problems);
        }

        ArrayList<LinkedHashMap<Talent, Integer>> legal =
                legalTiers(heroClass, subClass, armorAbility);

        for (Map.Entry<String, Integer> entry : plan.entries().entrySet()) {
            Talent talent;
            try {
                talent = Talent.valueOf(entry.getKey());
            } catch (RuntimeException ignored) {
                problems.add(Problem.UNKNOWN_TALENT);
                continue;
            }

            int tier = -1;
            for (int i = 0; i < legal.size(); i++) {
                if (legal.get(i).containsKey(talent)) {
                    tier = i + 1;
                    break;
                }
            }
            if (tier == -1) {
                problems.add(Problem.TALENT_NOT_AVAILABLE);
                continue;
            }

            int value = entry.getValue() == null ? 0 : entry.getValue();
            if (value < 0 || value > talent.maxPoints()) {
                problems.add(Problem.TALENT_LEVEL_TOO_HIGH);
            }
        }

        for (int tier = 1; tier <= Talent.MAX_TALENT_TIERS; tier++) {
            if (spent(plan, tier, heroClass, subClass, armorAbility) > tierBudget(tier)) {
                problems.add(Problem.TIER_BUDGET_EXCEEDED);
            }
        }

        return new Result(problems);
    }
}
