/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

/**
 * Large, authored Chapter 1 landmarks. These use SPD's native CustomTilemap layer:
 * terrain atlases own the common ground/walls while this class owns readable
 * landmarks such as the camp, shrine, wagon, town gate and Old Crow Inn.
 */
public class EchoesLandmarkTilemap extends CustomTilemap {

    public static final int CAMP = 0;
    public static final int SHRINE = 1;
    public static final int WAGON = 2;
    public static final int SIGNPOST = 3;
    public static final int FARMHOUSE = 4;
    public static final int WELL = 5;
    public static final int TOWN_GATE = 6;
    public static final int BLACKSMITH = 7;
    public static final int SHOP = 8;
    public static final int OLD_CROW_INN = 9;
    public static final int OLD_CROW_SIGN = 10;
    public static final int BAR = 11;
    public static final int FIREPLACE = 12;
    public static final int LEDGER_TABLE = 13;
    public static final int TAVERN_TABLE = 14;
    public static final int NOTICE_BOARD = 15;
    public static final int DUNGEON_MOUTH = 16;

    private static final int TEX_WIDTH = 256;
    private static final String KIND = "kind";

    private int kind;
    private int textureX;
    private int textureY;

    public EchoesLandmarkTilemap() {
        this(CAMP);
    }

    public EchoesLandmarkTilemap(int kind) {
        this.kind = kind;
        configure();
    }

    private void configure() {
        texture = Assets.Environment.ECHOES_LANDMARKS_V1;
        switch (kind) {
            case CAMP:            setTextureRect(0, 0, 4, 3); break;
            case SHRINE:          setTextureRect(4, 0, 2, 2); break;
            case WAGON:           setTextureRect(6, 0, 3, 2); break;
            case SIGNPOST:        setTextureRect(9, 0, 1, 1); break;
            case FARMHOUSE:       setTextureRect(10, 0, 4, 3); break;
            case WELL:            setTextureRect(14, 0, 2, 2); break;
            case TOWN_GATE:       setTextureRect(0, 3, 6, 3); break;
            case BLACKSMITH:      setTextureRect(6, 3, 4, 3); break;
            case SHOP:            setTextureRect(10, 3, 4, 3); break;
            case OLD_CROW_INN:    setTextureRect(0, 6, 8, 5); break;
            case OLD_CROW_SIGN:   setTextureRect(8, 6, 2, 2); break;
            case BAR:             setTextureRect(10, 6, 6, 2); break;
            case FIREPLACE:       setTextureRect(8, 8, 2, 2); break;
            case LEDGER_TABLE:    setTextureRect(10, 8, 2, 1); break;
            case TAVERN_TABLE:    setTextureRect(12, 8, 2, 1); break;
            case NOTICE_BOARD:    setTextureRect(14, 8, 2, 2); break;
            case DUNGEON_MOUTH:   setTextureRect(0, 10, 4, 2); break;
            default:
                kind = CAMP;
                setTextureRect(0, 0, 4, 3);
                break;
        }
    }

    private void setTextureRect(int x, int y, int w, int h) {
        textureX = x;
        textureY = y;
        tileW = w;
        tileH = h;
    }

    @Override
    public Tilemap create() {
        configure();
        Tilemap visual = super.create();
        visual.map(mapSimpleImage(textureX, textureY, TEX_WIDTH), tileW);
        return visual;
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
