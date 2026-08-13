/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.utils.GameSettings;

/** Small per-save metadata store used by the in-world ledger UI. */
public final class LedgerSettings extends GameSettings {

    private LedgerSettings() {
    }

    private static String key(String field, int slot) {
        return "echoes_ledger_" + field + "_" + slot;
    }

    public static void name(int slot, String value) {
        put(key("name", slot), value == null || value.trim().isEmpty() ? "无名者" : value.trim());
    }

    public static String name(int slot) {
        return getString(key("name", slot), "无名者", 24);
    }

    public static void heroClass(int slot, int value) {
        put(key("class", slot), value);
    }

    public static int heroClass(int slot) {
        return getInt(key("class", slot), 0);
    }

    public static void subclass(int slot, int value) {
        put(key("subclass", slot), value);
    }

    public static int subclass(int slot) {
        return getInt(key("subclass", slot), 0);
    }

    public static void ability(int slot, int value) {
        put(key("ability", slot), value);
    }

    public static int ability(int slot) {
        return getInt(key("ability", slot), 0);
    }

    public static void weapon(int slot, int value) {
        put(key("weapon", slot), value);
    }

    public static int weapon(int slot) {
        return getInt(key("weapon", slot), 0);
    }

    public static void growth(int slot, int value) {
        put(key("growth", slot), value);
    }

    public static int growth(int slot) {
        return getInt(key("growth", slot), 0);
    }
}
