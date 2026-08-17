/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.RegionPoi;
import com.shatteredpixel.shatteredpixeldungeon.RegionState;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesReturnPropTilemap;
import com.watabou.utils.Bundle;

/**
 * v0.1 candidate dressing layer for the formal Return geometry.
 * Keeps the authored route/state in ActOneReturnLevel while giving each scene a readable
 * project-owned 16px prop language. This is deliberately candidate art, not final art.
 */
public class ActOneReturnCandidateLevel extends ActOneReturnLevel {

    @Override
    protected boolean build() {
        boolean ok = super.build();
        installReturnProps();
        return ok;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        customTiles.removeIf(t -> t instanceof EchoesReturnPropTilemap);
        customWalls.removeIf(t -> t instanceof EchoesReturnPropTilemap);
        installReturnProps();
    }

    @Override
    public RegionPoi[] regionPois() {
        RegionPoi[] base = super.regionPois();
        RegionPoi[] safe = base.clone();
        for (int i = 0; i < safe.length; i++) {
            RegionPoi poi = safe[i];
            if (poi != null && poi.location == RegionState.Location.MORNINGCREEK) {
                // The formal Scene 1 can only HEAR about Morningcreek. Put its exact discovery
                // anchor behind the impassable far-east border so proximity can never promote it.
                safe[i] = new RegionPoi(
                        RegionState.Location.MORNINGCREEK,
                        RegionPoi.Category.TRAVEL,
                        86, 1,
                        46, 4,
                        1,
                        "晨溪",
                        "老农说，沿北边大路可以到达晨溪。具体位置还没有确认。",
                        "晨溪方向。"
                );
            }
        }
        return safe;
    }

    private void installReturnProps() {
        // Camp: lived-in four-person arrangement, intentionally asymmetrical.
        prop(EchoesReturnPropTilemap.FADED_TENT, 19, 46);
        prop(EchoesReturnPropTilemap.CLOTH_STRIP, 33, 47);
        prop(EchoesReturnPropTilemap.DEAD_FIRE, 26, 51);
        prop(EchoesReturnPropTilemap.BEDROLL_A, 23, 48);
        prop(EchoesReturnPropTilemap.BEDROLL_B, 29, 47);
        prop(EchoesReturnPropTilemap.BEDROLL_C, 22, 53);
        prop(EchoesReturnPropTilemap.BEDROLL_D, 31, 53);
        prop(EchoesReturnPropTilemap.COOK_POT, 25, 49);
        prop(EchoesReturnPropTilemap.TRAVEL_BAG, 29, 51);
        prop(EchoesReturnPropTilemap.CRATE, 31, 50);
        prop(EchoesReturnPropTilemap.CHECKLIST, 28, 52);
        prop(EchoesReturnPropTilemap.MAINTENANCE_TOOLS, 21, 50);
        prop(EchoesReturnPropTilemap.ROOTS, 18, 45);
        prop(EchoesReturnPropTilemap.STUMP, 34, 55);

        // Road readability: the farther north, the more deliberate the human construction.
        prop(EchoesReturnPropTilemap.MILESTONE, 49, 27);
        prop(EchoesReturnPropTilemap.LOW_WALL, 43, 16);
        prop(EchoesReturnPropTilemap.LOW_WALL, 49, 13);

        // Farmer/cart: ordinary working objects, not a set-piece shrine to the plot.
        prop(EchoesReturnPropTilemap.SACK, 57, 39);
        prop(EchoesReturnPropTilemap.FARM_TOOL, 58, 40);
        prop(EchoesReturnPropTilemap.CART_WOOD, 54, 39);

        // Shrine: small human-scale offering architecture and an almost invisible four-fold wear.
        prop(EchoesReturnPropTilemap.OFFERING_BOWL, 70, 19);
        prop(EchoesReturnPropTilemap.FOUR_RECESSES, 68, 17);
        prop(EchoesReturnPropTilemap.ROOTS, 74, 21);

        // Civilization edge: field/fence rhythm becomes visible without revealing the town itself.
        for (int x = 34; x <= 40; x += 2) prop(EchoesReturnPropTilemap.FIELD_EDGE, x, 8);
        for (int x = 55; x <= 63; x += 2) prop(EchoesReturnPropTilemap.FIELD_EDGE, x, 7);
        prop(EchoesReturnPropTilemap.FARM_TOOL, 52, 8);
        prop(EchoesReturnPropTilemap.STUMP, 42, 10);
    }

    private void prop(int kind, int x, int y) {
        EchoesReturnPropTilemap art = new EchoesReturnPropTilemap(kind);
        art.pos(x, y);
        customTiles.add(art);
    }
}
