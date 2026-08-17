/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

/**
 * Modular Morningcreek building overlay.
 *
 * One instance covers one authored wall footprint. Gameplay collision still comes from the
 * Level's Terrain.WALL cells; this class only replaces the visually empty wall mass with a
 * deterministic roof/facade composition made from 16x16 modules.
 */
public class EchoesTownBuildingTilemap extends CustomTilemap {
    public static final int HOUSE_WARM = 0;
    public static final int HOUSE_STONE = 1;
    public static final int OLD_CROW_INN = 2;
    public static final int RAVENFEATHER_CIVIC = 3;
    public static final int RAVENFEATHER_TOWER = 4;
    public static final int WAREHOUSE = 5;
    public static final int CLINIC = 6;
    public static final int STABLE = 7;
    public static final int BLACKSMITH = 8;
    public static final int HOUSE_GREEN = 9;

    public static final int FRONT_SOUTH = 0;
    public static final int FRONT_EAST = 1;
    public static final int FRONT_WEST = 2;
    public static final int FRONT_NORTH = 3;
    public static final int FRONT_NONE = 4;

    private static final String TEXTURE = "environment/echoes/morningcreek/town_structures_v1.png";
    private static final String STYLE = "style";
    private static final String FRONT = "front";
    private static final String DOOR = "door";
    private static final int ROOF = 0, ROOF_NORTH = 1, ROOF_SOUTH = 2, ROOF_LEFT = 3, ROOF_RIGHT = 4;
    private static final int WALL = 5, WINDOW = 6, DOOR_TILE = 7;

    private int style = HOUSE_WARM;
    private int front = FRONT_SOUTH;
    private int doorOffset = -1;

    public EchoesTownBuildingTilemap() { texture = TEXTURE; }

    public EchoesTownBuildingTilemap(int style, int width, int height, int front, int doorOffset) {
        this.style = Math.max(HOUSE_WARM, Math.min(HOUSE_GREEN, style));
        this.front = front;
        this.doorOffset = doorOffset;
        tileW = Math.max(1, width);
        tileH = Math.max(1, height);
        texture = TEXTURE;
    }

    @Override
    public Tilemap create() {
        texture = TEXTURE;
        Tilemap result = super.create();
        int[] data = new int[tileW * tileH];
        for (int y = 0; y < tileH; y++) for (int x = 0; x < tileW; x++) data[x + y * tileW] = style * 8 + moduleAt(x, y);
        result.map(data, tileW);
        return result;
    }

    private int moduleAt(int x, int y) {
        if (isFacadeCell(x, y)) {
            int along = (front == FRONT_EAST || front == FRONT_WEST) ? y : x;
            if (doorOffset >= 0 && along == doorOffset) return DOOR_TILE;
            return along % 3 == 1 ? WINDOW : WALL;
        }
        if (y == 0) return ROOF_NORTH;
        if (y == tileH - 2 && tileH > 3) return ROOF_SOUTH;
        if (x == 0) return ROOF_LEFT;
        if (x == tileW - 1) return ROOF_RIGHT;
        return ROOF;
    }

    private boolean isFacadeCell(int x, int y) {
        switch (front) {
            case FRONT_SOUTH: return y == tileH - 1;
            case FRONT_EAST: return x == tileW - 1;
            case FRONT_WEST: return x == 0;
            case FRONT_NORTH: return y == 0;
            default: return false;
        }
    }

    @Override public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle); bundle.put(STYLE, style); bundle.put(FRONT, front); bundle.put(DOOR, doorOffset);
    }

    @Override public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle); style = Math.max(HOUSE_WARM, Math.min(HOUSE_GREEN, bundle.getInt(STYLE)));
        front = bundle.getInt(FRONT); doorOffset = bundle.getInt(DOOR); texture = TEXTURE;
    }
}
