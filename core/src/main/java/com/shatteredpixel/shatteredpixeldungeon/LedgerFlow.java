package com.shatteredpixel.shatteredpixeldungeon;

public final class LedgerFlow {
    private static ReturningHeroProfile draft = new ReturningHeroProfile();
    private LedgerFlow() {}
    public static ReturningHeroProfile draft() { return draft; }
    public static ReturningHeroProfile resetDraft() {
        draft = new ReturningHeroProfile();
        return draft;
    }
}
