package com.shatteredpixel.shatteredpixeldungeon.scenes;

/** Small paging helper for long ledger lists such as wands, rings and trinkets. */
final class LedgerChoicePages {

    static final int PAGE_SIZE = 6;

    private LedgerChoicePages() {}

    static int pageCount(int itemCount) {
        return Math.max(1, (itemCount + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    static int clampPage(int page, int itemCount) {
        return Math.max(0, Math.min(page, pageCount(itemCount) - 1));
    }

    static int from(int page, int itemCount) {
        return Math.min(itemCount, clampPage(page, itemCount) * PAGE_SIZE);
    }

    static int to(int page, int itemCount) {
        return Math.min(itemCount, from(page, itemCount) + PAGE_SIZE);
    }
}
