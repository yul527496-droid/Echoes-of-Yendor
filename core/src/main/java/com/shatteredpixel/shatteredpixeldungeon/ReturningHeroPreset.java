/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;

/**
 * A reusable returning-hero build template.  Presets intentionally exclude the
 * character's ledger name so one build can be reused for multiple save slots.
 */
public class ReturningHeroPreset {

    public String presetName = "未命名预设";
    public int rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;

    public HeroClass heroClass = HeroClass.WARRIOR;
    public int subclassIndex;
    public int abilityIndex;

    /** Kept only while the old three-choice growth UI still exists. */
    public ReturningHeroProfile.GrowthPreset legacyGrowthPreset =
            ReturningHeroProfile.GrowthPreset.BALANCED;

    public ReturningHeroLoadout loadout = new ReturningHeroLoadout();

    public ReturningHeroPreset() {
        loadout.resetForClass(heroClass);
    }

    public ReturningHeroPreset(ReturningHeroPreset other) {
        if (other == null) return;
        presetName = other.presetName;
        rulesetVersion = other.rulesetVersion;
        heroClass = other.heroClass;
        subclassIndex = other.subclassIndex;
        abilityIndex = other.abilityIndex;
        legacyGrowthPreset = other.legacyGrowthPreset;
        loadout = other.loadout == null ? new ReturningHeroLoadout() : other.loadout.copy();
    }

    public ReturningHeroPreset copy() {
        return new ReturningHeroPreset(this);
    }

    public static ReturningHeroPreset fromProfile(String presetName,
                                                   ReturningHeroProfile profile) {
        ReturningHeroPreset preset = new ReturningHeroPreset();
        preset.presetName = sanitizeName(presetName);
        preset.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
        preset.heroClass = profile.heroClass;
        preset.subclassIndex = profile.subclassIndex;
        preset.abilityIndex = profile.abilityIndex;
        preset.legacyGrowthPreset = profile.growthPreset;
        preset.loadout = profile.loadout == null
                ? new ReturningHeroLoadout()
                : profile.loadout.copy();
        return preset;
    }

    /** Applies build choices only. The character's ledger name is untouched. */
    public void applyTo(ReturningHeroProfile profile) {
        profile.heroClass = heroClass;
        profile.subclassIndex = subclassIndex;
        profile.abilityIndex = abilityIndex;
        profile.weaponIndex = 0; // legacy picker has no stable meaning across presets
        profile.growthPreset = legacyGrowthPreset == null
                ? ReturningHeroProfile.GrowthPreset.BALANCED
                : legacyGrowthPreset;
        profile.loadout = loadout == null ? new ReturningHeroLoadout() : loadout.copy();
    }

    public boolean needsRulesetReview() {
        return rulesetVersion != ReturningHeroBuildRules.RULESET_VERSION
                || loadout == null
                || loadout.rulesetVersion != ReturningHeroBuildRules.RULESET_VERSION;
    }

    public static String sanitizeName(String value) {
        if (value == null) return "未命名预设";
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return "未命名预设";
        return trimmed.length() > 24 ? trimmed.substring(0, 24) : trimmed;
    }
}
