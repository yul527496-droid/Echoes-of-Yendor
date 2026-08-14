package com.shatteredpixel.shatteredpixeldungeon;

public final class LedgerFlow {
    private static ReturningHeroProfile draft = new ReturningHeroProfile();
    private static int weaponTier = 2;
    private static int weaponPickerStage = 0;
    private static String weaponCandidateId;
    private static int weaponCandidateLevel;

    private LedgerFlow() {}

    public static ReturningHeroProfile draft() { return draft; }

    public static ReturningHeroProfile resetDraft() {
        draft = new ReturningHeroProfile();
        resetWeaponPicker();
        return draft;
    }

    /** Transient picker state only; never serialized into a character build. */
    public static int weaponTier() {
        return weaponTier;
    }

    public static void weaponTier(int value) {
        weaponTier = Math.max(1, Math.min(value, 5));
    }

    /** 0=tier list, 1=weapon list, 2=upgrade level. */
    public static int weaponPickerStage() {
        return weaponPickerStage;
    }

    public static void weaponPickerStage(int value) {
        weaponPickerStage = Math.max(0, Math.min(value, 2));
    }

    public static String weaponCandidateId() {
        return weaponCandidateId;
    }

    public static void weaponCandidateId(String value) {
        weaponCandidateId = value;
    }

    public static int weaponCandidateLevel() {
        return weaponCandidateLevel;
    }

    public static void weaponCandidateLevel(int value) {
        weaponCandidateLevel = Math.max(0, Math.min(value, 7));
    }

    public static void resetWeaponPicker() {
        weaponTier = 2;
        weaponPickerStage = 0;
        weaponCandidateId = null;
        weaponCandidateLevel = 0;
    }
}
