/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Kind;

/**
 * Original-SPD alchemy investment needed to raise a reconstructed trinket.
 *
 * The player is choosing which trinket survived the prior adventure, so owning
 * the +0 trinket itself is free here.  We only reconstruct the alchemy energy
 * invested into upgrades.  Most trinkets cost 6/8/10 energy for +1/+2/+3;
 * Parchment Scrap, Mossy Clump and Wondrous Resin use the expensive 10/15/20
 * curve.  With the Echoes budget of 25 this intentionally permits ordinary
 * trinkets at +3 (24 energy) but expensive trinkets only at +2 (25 energy).
 */
public final class ReturningHeroTrinketBalance {

    private static final int[] NORMAL_CUMULATIVE_COST = {0, 6, 14, 24};
    private static final int[] EXPENSIVE_CUMULATIVE_COST = {0, 10, 25, 45};

    private ReturningHeroTrinketBalance() {
        // Utility class.
    }

    public static int alchemyCost(String itemId, int level) {
        if (!ReturningHeroItemCatalog.isSelectable(itemId, Kind.TRINKET)) return -1;
        if (level < 0 || level > 3) return -1;
        return curve(itemId)[level];
    }

    public static boolean isExpensive(String itemId) {
        return "trinket.parchment_scrap".equals(itemId)
                || "trinket.mossy_clump".equals(itemId)
                || "trinket.wondrous_resin".equals(itemId);
    }

    private static int[] curve(String itemId) {
        return isExpensive(itemId) ? EXPENSIVE_CUMULATIVE_COST : NORMAL_CUMULATIVE_COST;
    }
}
