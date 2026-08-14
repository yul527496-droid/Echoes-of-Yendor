/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Derives artifact levels from the original Artifact visible-level conversion.
 * Different artifacts use different internal level caps, so Echoes must never
 * assume that every visible integer from +0 to +10 is representable.
 */
public final class ReturningHeroArtifactBalance {

    private ReturningHeroArtifactBalance() {}

    public static boolean isLegalVisibleLevel(String itemId, int visibleLevel,
                                              int maxVisibleLevel) {
        if (visibleLevel < 0 || visibleLevel > maxVisibleLevel) return false;
        Artifact artifact = artifact(itemId);
        if (artifact == null) return false;
        artifact.identify();
        artifact.resetForTrinity(visibleLevel);
        return artifact.visiblyUpgraded() == visibleLevel;
    }

    public static List<Integer> legalVisibleLevels(String itemId, int maxVisibleLevel) {
        Artifact artifact = artifact(itemId);
        if (artifact == null) return Collections.emptyList();

        List<Integer> result = new ArrayList<>();
        for (int visible = 0; visible <= maxVisibleLevel; visible++) {
            artifact = artifact(itemId);
            if (artifact == null) break;
            artifact.identify();
            artifact.resetForTrinity(visible);
            if (artifact.visiblyUpgraded() == visible) result.add(visible);
        }
        return result;
    }

    private static Artifact artifact(String itemId) {
        ReturningHeroItemCatalog.Entry entry = ReturningHeroItemCatalog.byId(itemId);
        if (entry == null || entry.kind != ReturningHeroItemCatalog.Kind.ARTIFACT
                || !entry.selectable) return null;
        Item item = ReturningHeroItemCatalog.newItem(itemId);
        return item instanceof Artifact ? (Artifact) item : null;
    }
}
