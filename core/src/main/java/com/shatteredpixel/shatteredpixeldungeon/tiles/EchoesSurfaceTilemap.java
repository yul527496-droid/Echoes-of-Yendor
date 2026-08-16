/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

/**
 * Project-owned surface overlays rendered through SPD's native 16px CustomTilemap layer.
 *
 * Kinds 0-6 are the legacy Art-v2 prototypes kept for compatibility with existing saves/maps.
 * Kinds 100+ are the Chapter 1 Surface Entrance vertical-slice production set generated from
 * tools/generate_surface_vertical_slice.py. Each production family lives in its own editable
 * PNG pack so material changes do not require rebuilding one opaque mega-atlas.
 */
public class EchoesSurfaceTilemap extends CustomTilemap {

    // Legacy prototype kinds; do not reuse for new art.
    public static final int TREE = 0;
    public static final int BUSH = 1;
    public static final int SIGNPOST = 2;
    public static final int OLD_CROW_SIGN = 3;
    public static final int FENCE = 4;
    public static final int BRIDGE = 5;
    public static final int FARMHOUSE = 6;

    // Surface Entrance production kinds.
    public static final int GRASS_TUFT_A = 100;
    public static final int GRASS_TUFT_B = 101;
    public static final int GRASS_TRAMPLED = 102;
    public static final int GRASS_DAMP = 103;
    public static final int GRASS_STONES = 104;
    public static final int GRASS_FLOWERS = 105;
    public static final int GRASS_BARE = 106;
    public static final int GRASS_MIX = 107;

    public static final int FOREST_EDGE_A = 120;
    public static final int FOREST_EDGE_B = 121;
    public static final int FOREST_DEEP = 122;
    public static final int FOREST_BUSH = 123;
    public static final int FOREST_STUMP = 124;
    public static final int FOREST_LOG = 125;
    public static final int FOREST_UNDERBRUSH = 126;
    public static final int FOREST_ROCKS = 127;

    public static final int ROAD_VERGE_L = 140;
    public static final int ROAD_VERGE_R = 141;
    public static final int ROAD_RUTS = 142;
    public static final int ROAD_STONES = 143;
    public static final int ROAD_MUD = 144;
    public static final int ROAD_TRAMPLE = 145;
    public static final int ROAD_SCAR = 146;
    public static final int ROAD_WEEDS = 147;

    public static final int RIVER_BANK_TOP = 160;
    public static final int RIVER_BANK_BOTTOM = 161;
    public static final int RIVER_REEDS = 162;
    public static final int RIVER_RIPPLE = 163;
    public static final int RIVER_STONES = 164;
    public static final int RIVER_FOAM = 165;
    public static final int RIVER_WET_GRASS = 166;
    public static final int RIVER_ROOTS = 167;

    public static final int BRIDGE_SCENE = 180;
    public static final int CAMP_SCENE = 181;
    public static final int DUNGEON_MOUTH = 182;

    private static final String LEGACY = "environment/custom_tiles/echoes_surface_art_v2.png";
    private static final String GRASS = "environment/echoes/ch1_surface/grass.png";
    private static final String FOREST = "environment/echoes/ch1_surface/forest.png";
    private static final String ROAD = "environment/echoes/ch1_surface/road.png";
    private static final String RIVER = "environment/echoes/ch1_surface/river.png";
    private static final String BRIDGE_PACK = "environment/echoes/ch1_surface/bridge.png";
    private static final String CAMP_RUIN = "environment/echoes/ch1_surface/camp_ruin.png";
    private static final String KIND = "kind";

    private int kind = TREE;
    private int textureX;
    private int textureY;
    private int textureWidth;

    public EchoesSurfaceTilemap() {
        configure();
    }

    public EchoesSurfaceTilemap(int kind) {
        this.kind = kind;
        configure();
    }

    private void set(String path, int width, int x, int y, int w, int h) {
        texture = path;
        textureWidth = width;
        textureX = x;
        textureY = y;
        tileW = w;
        tileH = h;
    }

    private void configure() {
        switch (kind) {
            // Legacy atlas, 8 tiles wide.
            case TREE:          set(LEGACY, 128, 0, 0, 2, 2); break;
            case BUSH:          set(LEGACY, 128, 2, 0, 1, 1); break;
            case SIGNPOST:      set(LEGACY, 128, 3, 0, 1, 1); break;
            case OLD_CROW_SIGN: set(LEGACY, 128, 4, 0, 1, 1); break;
            case FENCE:         set(LEGACY, 128, 5, 0, 1, 1); break;
            case BRIDGE:        set(LEGACY, 128, 6, 0, 1, 1); break;
            case FARMHOUSE:     set(LEGACY, 128, 0, 2, 4, 3); break;

            // Grass 8x2 atlas.
            case GRASS_TUFT_A:   set(GRASS, 128, 0, 0, 1, 1); break;
            case GRASS_TUFT_B:   set(GRASS, 128, 1, 0, 1, 1); break;
            case GRASS_TRAMPLED: set(GRASS, 128, 2, 0, 1, 1); break;
            case GRASS_DAMP:     set(GRASS, 128, 3, 0, 1, 1); break;
            case GRASS_STONES:   set(GRASS, 128, 4, 0, 1, 1); break;
            case GRASS_FLOWERS:  set(GRASS, 128, 5, 0, 1, 1); break;
            case GRASS_BARE:     set(GRASS, 128, 6, 0, 1, 1); break;
            case GRASS_MIX:      set(GRASS, 128, 7, 0, 1, 1); break;

            // Forest 8x4 atlas.
            case FOREST_EDGE_A:      set(FOREST, 128, 0, 0, 2, 2); break;
            case FOREST_EDGE_B:      set(FOREST, 128, 2, 0, 2, 2); break;
            case FOREST_DEEP:        set(FOREST, 128, 4, 0, 2, 2); break;
            case FOREST_BUSH:        set(FOREST, 128, 6, 0, 1, 1); break;
            case FOREST_STUMP:       set(FOREST, 128, 7, 0, 1, 1); break;
            case FOREST_LOG:         set(FOREST, 128, 6, 1, 2, 1); break;
            case FOREST_UNDERBRUSH:  set(FOREST, 128, 0, 2, 2, 1); break;
            case FOREST_ROCKS:       set(FOREST, 128, 6, 2, 2, 1); break;

            // Road 8x2 atlas.
            case ROAD_VERGE_L:  set(ROAD, 128, 0, 0, 1, 1); break;
            case ROAD_VERGE_R:  set(ROAD, 128, 1, 0, 1, 1); break;
            case ROAD_RUTS:     set(ROAD, 128, 2, 0, 1, 1); break;
            case ROAD_STONES:   set(ROAD, 128, 3, 0, 1, 1); break;
            case ROAD_MUD:      set(ROAD, 128, 4, 0, 1, 1); break;
            case ROAD_TRAMPLE:  set(ROAD, 128, 5, 0, 1, 1); break;
            case ROAD_SCAR:     set(ROAD, 128, 6, 0, 1, 1); break;
            case ROAD_WEEDS:    set(ROAD, 128, 7, 0, 1, 1); break;

            // River 8x2 atlas.
            case RIVER_BANK_TOP:    set(RIVER, 128, 0, 0, 1, 1); break;
            case RIVER_BANK_BOTTOM: set(RIVER, 128, 1, 0, 1, 1); break;
            case RIVER_REEDS:       set(RIVER, 128, 2, 0, 1, 1); break;
            case RIVER_RIPPLE:      set(RIVER, 128, 3, 0, 1, 1); break;
            case RIVER_STONES:      set(RIVER, 128, 4, 0, 1, 1); break;
            case RIVER_FOAM:        set(RIVER, 128, 5, 0, 1, 1); break;
            case RIVER_WET_GRASS:   set(RIVER, 128, 6, 0, 1, 1); break;
            case RIVER_ROOTS:       set(RIVER, 128, 7, 0, 1, 1); break;

            case BRIDGE_SCENE:  set(BRIDGE_PACK, 64, 0, 0, 4, 3); break;
            case CAMP_SCENE:    set(CAMP_RUIN, 192, 0, 0, 6, 4); break;
            case DUNGEON_MOUTH: set(CAMP_RUIN, 192, 6, 0, 6, 4); break;

            default:
                kind = GRASS_TUFT_A;
                set(GRASS, 128, 0, 0, 1, 1);
                break;
        }
    }

    @Override
    public Tilemap create() {
        configure();
        Tilemap result = super.create();
        result.map(mapSimpleImage(textureX, textureY, textureWidth), tileW);
        return result;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(KIND, kind);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        kind = bundle.getInt(KIND);
        configure();
    }
}
