/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.RegionState;

/**
 * Act 1 Return shrine landmark with a real terrain-inspect interaction.
 *
 * The inscription is architecture, not loot. SPD's WndInfoCell asks CustomTilemap for a
 * visible tile's name/description, so examining any visible part of this tiny shrine reads
 * the weathered inscription without creating a Heap, pickup action, backpack item, or
 * "cannot carry" failure message.
 */
public class ActOneReturnShrineTilemap extends EchoesLandmarkTilemap {

    private static final String INSCRIPTION =
            "石面上的字已经被风雨磨去大半，只剩一句仍然完整：\n\n「力量会使你迷失。」";

    public ActOneReturnShrineTilemap() {
        super(EchoesLandmarkTilemap.SHRINE);
    }

    @Override
    public String name(int tileX, int tileY) {
        return "古老神龛";
    }

    @Override
    public String desc(int tileX, int tileY) {
        ActOneReturnState state = ActOneReturnState.current();
        if (state != null && !state.inscriptionRead) {
            state.inscriptionRead = true;
            RegionState region = RegionState.current();
            if (region != null) region.syncHud();
        }
        return INSCRIPTION;
    }
}
