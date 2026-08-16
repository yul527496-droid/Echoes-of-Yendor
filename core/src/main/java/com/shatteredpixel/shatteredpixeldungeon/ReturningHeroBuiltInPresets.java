/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Built-in level-30 reconstruction templates shown before player-saved presets.
 *
 * These are real build drafts, not labels: each template selects a specialization,
 * equipment within the reconstruction budget, and a complete legal four-tier talent plan.
 * Players still edit every field on the following ledger pages before final confirmation.
 */
public final class ReturningHeroBuiltInPresets {

    public enum Style {
        STANDARD("标准归来"),
        OFFENSE("偏进攻"),
        SPECIALTY("职业特色");

        public final String title;

        Style(String title) {
            this.title = title;
        }
    }

    private ReturningHeroBuiltInPresets() {
    }

    public static ReturningHeroPreset create(HeroClass heroClass, Style style) {
        ReturningHeroPreset preset = new ReturningHeroPreset();
        preset.presetName = style.title;
        preset.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
        preset.heroClass = heroClass;
        preset.subclassIndex = style == Style.SPECIALTY
                ? Math.min(1, Math.max(0, heroClass.subClasses().length - 1))
                : 0;
        preset.abilityIndex = style == Style.SPECIALTY
                ? Math.min(1, Math.max(0, heroClass.armorAbilities().length - 1))
                : 0;
        preset.legacyGrowthPreset = style == Style.OFFENSE
                ? ReturningHeroProfile.GrowthPreset.OFFENSE
                : style == Style.SPECIALTY
                ? ReturningHeroProfile.GrowthPreset.SURVIVAL
                : ReturningHeroProfile.GrowthPreset.BALANCED;

        preset.loadout = new ReturningHeroLoadout();
        preset.loadout.resetForClass(heroClass);
        applyEquipment(preset.loadout, heroClass, style);

        preset.talentPlan = new ReturningHeroTalentPlan();
        fillTalents(preset, style);
        return preset;
    }

    public static List<ReturningHeroPreset> forClass(HeroClass heroClass) {
        ArrayList<ReturningHeroPreset> result = new ArrayList<>();
        for (Style style : Style.values()) {
            ReturningHeroPreset preset = create(heroClass, style);
            if (ReturningHeroBuildValidator.validate(preset).isValid()
                    && ReturningHeroTalentRules.isComplete(
                    preset.talentPlan,
                    preset.heroClass,
                    subClass(preset),
                    armorAbility(preset))) {
                result.add(preset);
            }
        }
        return result;
    }

    private static void applyEquipment(ReturningHeroLoadout loadout,
                                       HeroClass heroClass, Style style) {
        // Every preset leaves a few points of headroom unless its defensive package
        // deliberately spends the full 15-point equipment reconstruction budget.
        switch (style) {
            case OFFENSE:
                loadout.armorId = "armor.t3.mail";
                loadout.armorLevel = 2;
                loadout.ringSlotId = offensiveRing(heroClass);
                loadout.ringSlotLevel = 2;
                if (heroClass == HeroClass.MAGE) {
                    loadout.carriedWandId = "wand.fireblast";
                    loadout.carriedWandLevel = 3;
                } else {
                    loadout.primaryWeaponId = offensiveWeapon(heroClass);
                    loadout.primaryWeaponLevel = 2;
                }
                addOrdinaryArtifact(loadout, heroClass, "artifact.chalice_of_blood", 4);
                break;

            case SPECIALTY:
                loadout.armorId = "armor.t5.plate";
                loadout.armorLevel = 2;
                loadout.ringSlotId = defensiveRing(heroClass);
                loadout.ringSlotLevel = 2;
                if (heroClass == HeroClass.MAGE) {
                    loadout.carriedWandId = "wand.living_earth";
                    loadout.carriedWandLevel = 2;
                } else {
                    loadout.primaryWeaponId = specialtyWeapon(heroClass);
                    loadout.primaryWeaponLevel = specialtyWeaponLevel(heroClass);
                }
                addOrdinaryArtifact(loadout, heroClass, "artifact.talisman_of_foresight", 4);
                break;

            case STANDARD:
            default:
                loadout.armorId = "armor.t4.scale";
                loadout.armorLevel = 2;
                loadout.ringSlotId = standardRing(heroClass);
                loadout.ringSlotLevel = 1;
                if (heroClass == HeroClass.MAGE) {
                    loadout.carriedWandId = "wand.lightning";
                    loadout.carriedWandLevel = 2;
                } else {
                    loadout.primaryWeaponId = standardWeapon(heroClass);
                    loadout.primaryWeaponLevel = 2;
                }
                addOrdinaryArtifact(loadout, heroClass, "artifact.ethereal_chains", 4);
                break;
        }
    }

    private static void addOrdinaryArtifact(ReturningHeroLoadout loadout, HeroClass heroClass,
                                            String id, int level) {
        if (!ReturningHeroHeritage.hasClassArtifact(heroClass)) {
            loadout.artifactSlotId = id;
            loadout.artifactSlotLevel = level;
        }
    }

    private static String standardWeapon(HeroClass heroClass) {
        switch (heroClass) {
            case ROGUE: return "weapon.t4.assassins_blade";
            case HUNTRESS: return "weapon.t3.scimitar";
            case DUELIST: return "weapon.t4.runic_blade";
            case CLERIC: return "weapon.t4.runic_blade";
            case WARRIOR:
            default: return "weapon.t4.runic_blade";
        }
    }

    private static String offensiveWeapon(HeroClass heroClass) {
        switch (heroClass) {
            case ROGUE: return "weapon.t4.assassins_blade";
            case HUNTRESS: return "weapon.t5.glaive";
            case DUELIST: return "weapon.t5.war_scythe";
            case CLERIC: return "weapon.t5.war_hammer";
            case WARRIOR:
            default: return "weapon.t5.greatsword";
        }
    }

    private static String specialtyWeapon(HeroClass heroClass) {
        switch (heroClass) {
            case ROGUE: return "weapon.t3.sai";
            case HUNTRESS: return "weapon.t3.round_shield";
            case DUELIST: return "weapon.t3.sai";
            case CLERIC: return "weapon.t3.round_shield";
            case WARRIOR:
            default: return "weapon.t5.greatshield";
        }
    }

    private static int specialtyWeaponLevel(HeroClass heroClass) {
        return heroClass == HeroClass.WARRIOR ? 1 : 2;
    }

    private static String standardRing(HeroClass heroClass) {
        if (heroClass == HeroClass.MAGE) return "ring.energy";
        if (heroClass == HeroClass.HUNTRESS) return "ring.sharpshooting";
        return "ring.might";
    }

    private static String offensiveRing(HeroClass heroClass) {
        if (heroClass == HeroClass.MAGE) return "ring.energy";
        if (heroClass == HeroClass.HUNTRESS) return "ring.sharpshooting";
        return "ring.furor";
    }

    private static String defensiveRing(HeroClass heroClass) {
        if (heroClass == HeroClass.ROGUE || heroClass == HeroClass.DUELIST) return "ring.evasion";
        return "ring.tenacity";
    }

    private static void fillTalents(ReturningHeroPreset preset, Style style) {
        HeroSubClass subClass = subClass(preset);
        ArmorAbility ability = armorAbility(preset);
        ArrayList<LinkedHashMap<Talent, Integer>> legal = ReturningHeroTalentRules.legalTiers(
                preset.heroClass, subClass, ability);

        for (int tier = 1; tier <= Talent.MAX_TALENT_TIERS; tier++) {
            ArrayList<Talent> choices = new ArrayList<>(legal.get(tier - 1).keySet());
            if (style != Style.STANDARD) {
                final Style rankingStyle = style;
                Collections.sort(choices, new Comparator<Talent>() {
                    @Override public int compare(Talent a, Talent b) {
                        int score = score(b, rankingStyle) - score(a, rankingStyle);
                        return score != 0 ? score : a.ordinal() - b.ordinal();
                    }
                });
            }
            allocateTier(preset.talentPlan, choices,
                    ReturningHeroTalentRules.tierBudget(tier), style == Style.STANDARD);
        }
    }

    private static void allocateTier(ReturningHeroTalentPlan plan, List<Talent> choices,
                                     int budget, boolean evenSpread) {
        if (choices.isEmpty() || budget <= 0) return;
        int remaining = budget;
        if (evenSpread) {
            while (remaining > 0) {
                boolean changed = false;
                for (Talent talent : choices) {
                    if (remaining <= 0) break;
                    int current = plan.pointsIn(talent);
                    if (current < talent.maxPoints()) {
                        plan.set(talent, current + 1);
                        remaining--;
                        changed = true;
                    }
                }
                if (!changed) break;
            }
        } else {
            for (Talent talent : choices) {
                if (remaining <= 0) break;
                int give = Math.min(talent.maxPoints(), remaining);
                plan.set(talent, give);
                remaining -= give;
            }
        }
    }

    private static int score(Talent talent, Style style) {
        String id = talent.name();
        if (style == Style.OFFENSE) {
            return matches(id,
                    "LETHAL", "STRIKE", "ANGER", "FURY", "POWER", "FORCE", "CLEAVE",
                    "SLAM", "IMPACT", "BLAST", "FIRE", "SOUL_EATER", "PUNCH", "ASSASSIN",
                    "REAPER", "BLADE", "WRATH", "MOMENTUM", "ASSAULT", "DEADLY", "SEARING",
                    "LANCE", "JUDGEMENT", "RAY", "ELIMINATION", "SHARPSHOOT", "POINT_BLANK");
        }
        return matches(id,
                "WILL", "BARRIER", "DEFENSE", "DURABILITY", "ARMOR", "EVASIVE", "BARKSKIN",
                "SHIELD", "PROTECTION", "HOLD_FAST", "STOMACH", "PRESERVATION", "VISION",
                "STEPS", "SENSES", "AGILITY", "RETREAT", "CLEANSE", "BLESS", "HANDS", "AURA",
                "WALL", "LIFE", "STASIS", "STEALTH", "FORESIGHT", "NATURE", "ENDURE", "DODGE");
    }

    private static int matches(String id, String... keywords) {
        int score = 0;
        for (String keyword : keywords) {
            if (id.contains(keyword)) score += 10;
        }
        return score;
    }

    private static HeroSubClass subClass(ReturningHeroPreset preset) {
        HeroSubClass[] values = preset.heroClass.subClasses();
        return values[Math.max(0, Math.min(preset.subclassIndex, values.length - 1))];
    }

    private static ArmorAbility armorAbility(ReturningHeroPreset preset) {
        ArmorAbility[] values = preset.heroClass.armorAbilities();
        return values[Math.max(0, Math.min(preset.abilityIndex, values.length - 1))];
    }
}
