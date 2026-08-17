/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

/** Small reusable Morningcreek public-space and service props on the native 16px grid. */
public class EchoesTownPropTilemap extends CustomTilemap {

    public static final int BRIDGE_DECK = 0;
    public static final int BRIDGE_PARAPET = 1;
    public static final int BRIDGE_PILLAR = 2;
    public static final int LOW_WALL = 3;
    public static final int FENCE = 4;
    public static final int SIGNPOST = 5;
    public static final int BRAZIER = 6;
    public static final int BENCH = 7;
    public static final int CRATE = 8;
    public static final int BARREL = 9;
    public static final int SACK = 10;
    public static final int ROPE = 11;
    public static final int BOLLARD = 12;
    public static final int HERB_BED = 13;
    public static final int FLOWER_BED = 14;
    public static final int WOODPILE = 15;

    public static final int WELL = 100;
    public static final int NOTICE_BOARD = 101;
    public static final int MARKET_STALL_RED = 102;
    public static final int MARKET_STALL_BLUE = 103;
    public static final int CART = 104;
    public static final int LOADING_FRAME = 105;
    public static final int BOAT = 106;
    public static final int CHICKEN_COOP = 107;

    private static final String TEXTURE = "environment/echoes/morningcreek/town_props_v1.png";
    private static final int TEXTURE_WIDTH = 256;
    private static final String KIND = "kind";

    private int kind = CRATE;
    private int textureX;
    private int textureY;

    public EchoesTownPropTilemap() { configure(); }

    public EchoesTownPropTilemap(int kind) {
        this.kind = kind;
        configure();
    }

    private void set(int x, int y, int w, int h) {
        texture = TEXTURE;
        textureX = x;
        textureY = y;
        tileW = w;
        tileH = h;
    }

    private void configure() {
        if (kind >= 0 && kind <= 15) {
            set(kind, 0, 1, 1);
            return;
        }
        switch (kind) {
            case WELL: set(0, 2, 2, 2); break;
            case NOTICE_BOARD: set(2, 2, 2, 2); break;
            case MARKET_STALL_RED: set(4, 2, 2, 2); break;
            case MARKET_STALL_BLUE: set(6, 2, 2, 2); break;
            case CART: set(8, 2, 2, 2); break;
            case LOADING_FRAME: set(10, 2, 2, 2); break;
            case BOAT: set(12, 2, 3, 1); break;
            case CHICKEN_COOP: set(15, 2, 1, 2); break;
            default:
                kind = CRATE;
                set(CRATE, 0, 1, 1);
                break;
        }
    }

    @Override
    public Tilemap create() {
        configure();
        Tilemap result = super.create();
        result.map(mapSimpleImage(textureX, textureY, TEXTURE_WIDTH), tileW);
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
