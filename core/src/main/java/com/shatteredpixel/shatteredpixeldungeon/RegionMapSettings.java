/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.utils.GameSettings;

/** Small persistent settings bucket for the Chapter 1 region map. */
public final class RegionMapSettings extends GameSettings {

    private static final String KEY_ZOOM = "echoes_region_map_zoom";

    private RegionMapSettings() {
    }

    public static int zoom() {
        return getInt(KEY_ZOOM, 2, 1, 4);
    }

    public static void zoom(int value) {
        put(KEY_ZOOM, Math.max(1, Math.min(4, value)));
    }
}
