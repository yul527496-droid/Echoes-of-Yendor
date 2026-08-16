/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

/**
 * Small, save-safe Chapter 1 art overlays used for the real in-game Art v2 pass.
 *
 * The texture is generated deterministically by tools/generate_echoes_art_v2.py.
 * Keeping these as CustomTilemaps lets Echoes extend SPD's native render layers
 * without replacing the base terrain atlas while the new surface style is still
 * being evaluated in actual gameplay.
 */
public class EchoesSurfaceTilemap extends CustomTilemap {

    public static final int TREE = 0;
    public static final int BUSH = 1;
    public static final int SIGNPOST = 2;
    public static final int OLD_CROW_SIGN = 3;
    public static final int FENCE = 4;
    public static final int BRIDGE = 5;
    public static final int FARMHOUSE = 6;

    private static final String TEXTURE = "environment/custom_tiles/echoes_surface_art_v2.png";
    private static final int TEXTURE_WIDTH = 128;
    private static final String KIND = "kind";

    private int kind = TREE;
    private int textureX;
    private int textureY;

    public EchoesSurfaceTilemap() {
        configure();
    }

    public EchoesSurfaceTilemap(int kind) {
        this.kind = kind;
        configure();
    }

    private void configure() {
        texture = TEXTURE;
        switch (kind) {
            case TREE:
                textureX = 0; textureY = 0; tileW = 2; tileH = 2;
                break;
            case BUSH:
                textureX = 2; textureY = 0; tileW = 1; tileH = 1;
                break;
            case SIGNPOST:
                textureX = 3; textureY = 0; tileW = 1; tileH = 1;
                break;
            case OLD_CROW_SIGN:
                textureX = 4; textureY = 0; tileW = 1; tileH = 1;
                break;
            case FENCE:
                textureX = 5; textureY = 0; tileW = 1; tileH = 1;
                break;
            case BRIDGE:
                textureX = 6; textureY = 0; tileW = 1; tileH = 1;
                break;
            case FARMHOUSE:
                textureX = 0; textureY = 2; tileW = 4; tileH = 3;
                break;
            default:
                kind = TREE;
                textureX = 0; textureY = 0; tileW = 2; tileH = 2;
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
