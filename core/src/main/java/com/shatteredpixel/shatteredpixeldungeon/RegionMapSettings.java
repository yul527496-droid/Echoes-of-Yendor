/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.utils.GameSettings;

/** Persistent settings bucket for Chapter 1 region-map and HUD-map presentation. */
public final class RegionMapSettings extends GameSettings {

    private static final String KEY_ZOOM = "echoes_region_map_zoom";
    private static final String KEY_HUD_ZOOM = "echoes_surface_hud_map_zoom";

    private RegionMapSettings() {
    }

    public static int zoom() {
        return getInt(KEY_ZOOM, 2, 1, 4);
    }

    public static void zoom(int value) {
        put(KEY_ZOOM, Math.max(1, Math.min(4, value)));
    }

    /** 1 = compact, 2 = standard/1.5x, 3 = large/2x. */
    public static int hudZoom() {
        return getInt(KEY_HUD_ZOOM, 2, 1, 3);
    }

    public static void hudZoom(int value) {
        put(KEY_HUD_ZOOM, Math.max(1, Math.min(3, value)));
    }
}
