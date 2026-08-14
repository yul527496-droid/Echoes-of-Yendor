package com.shatteredpixel.shatteredpixeldungeon;

public final class LedgerFlow {
    private static ReturningHeroProfile draft = new ReturningHeroProfile();
    private static int weaponTier = 2;

    private LedgerFlow() {}

    public static ReturningHeroProfile draft() { return draft; }

    public static ReturningHeroProfile resetDraft() {
        draft = new ReturningHeroProfile();
        weaponTier = 2;
        return draft;
    }

    /** Transient picker state only; never serialized into a character build. */
    public static int weaponTier() {
        return weaponTier;
    }

    public static void weaponTier(int value) {
        weaponTier = Math.max(1, Math.min(value, 5));
    }
}
