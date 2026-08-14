/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.watabou.utils.GameSettings;

/** Persistent reusable build presets, separate from character save slots. */
public final class ReturningHeroPresetSettings extends GameSettings {

    public static final int MAX_PRESETS = 8;

    private ReturningHeroPresetSettings() {
    }

    private static String key(String field, int slot) {
        return "echoes_returning_preset_" + field + "_" + slot;
    }

    private static int clampSlot(int slot) {
        if (slot < 0 || slot >= MAX_PRESETS) {
            throw new IllegalArgumentException("Preset slot out of range: " + slot);
        }
        return slot;
    }

    public static boolean exists(int slot) {
        slot = clampSlot(slot);
        return getInt(key("exists", slot), 0) == 1;
    }

    public static void save(int slot, ReturningHeroPreset preset) {
        slot = clampSlot(slot);
        if (preset == null) throw new IllegalArgumentException("preset == null");

        put(key("exists", slot), 1);
        put(key("name", slot), ReturningHeroPreset.sanitizeName(preset.presetName));
        put(key("ruleset", slot), preset.rulesetVersion);
        put(key("class", slot), preset.heroClass.ordinal());
        put(key("subclass", slot), preset.subclassIndex);
        put(key("ability", slot), preset.abilityIndex);
        put(key("legacy_growth", slot), preset.legacyGrowthPreset == null
                ? ReturningHeroProfile.GrowthPreset.BALANCED.ordinal()
                : preset.legacyGrowthPreset.ordinal());
        put(key("loadout", slot), ReturningHeroLoadoutCodec.encode(preset.loadout));
    }

    public static ReturningHeroPreset load(int slot) {
        slot = clampSlot(slot);
        if (!exists(slot)) return null;

        ReturningHeroPreset preset = new ReturningHeroPreset();
        preset.presetName = getString(key("name", slot), "未命名预设", 24);
        preset.rulesetVersion = getInt(key("ruleset", slot), 0);

        HeroClass[] classes = HeroClass.values();
        int classIndex = getInt(key("class", slot), HeroClass.WARRIOR.ordinal());
        preset.heroClass = classes[Math.max(0, Math.min(classIndex, classes.length - 1))];
        preset.subclassIndex = getInt(key("subclass", slot), 0);
        preset.abilityIndex = getInt(key("ability", slot), 0);

        ReturningHeroProfile.GrowthPreset[] growth = ReturningHeroProfile.GrowthPreset.values();
        int growthIndex = getInt(key("legacy_growth", slot), 0);
        preset.legacyGrowthPreset = growth[Math.max(0, Math.min(growthIndex, growth.length - 1))];

        String encoded = getString(key("loadout", slot), "", 512);
        preset.loadout = ReturningHeroLoadoutCodec.decode(encoded, preset.heroClass);
        return preset;
    }

    public static void delete(int slot) {
        slot = clampSlot(slot);
        put(key("exists", slot), 0);
        put(key("name", slot), "");
        put(key("ruleset", slot), 0);
        put(key("loadout", slot), "");
    }
}
