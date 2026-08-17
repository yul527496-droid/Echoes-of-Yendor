/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.RegionPoi;
import com.shatteredpixel.shatteredpixeldungeon.RegionState;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesReturnPropTilemap;
import com.watabou.utils.Bundle;

/**
 * v0.2 Journey & Audio Pass candidate dressing layer for the formal Return geometry.
 * Keeps the authored route/state in ActOneReturnLevel while giving each travel beat a readable
 * project-owned 16px prop language. This remains candidate art, not final art.
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
                // Scene 1 can only HEAR about Morningcreek. Exact discovery remains unreachable.
                safe[i] = new RegionPoi(
                        RegionState.Location.MORNINGCREEK,
                        RegionPoi.Category.TRAVEL,
                        123, 91,
                        84, 6,
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
        // Exit basin / early river valley: old stone and natural obstacles establish travel scale.
        prop(EchoesReturnPropTilemap.ROOTS, 12, 85);
        prop(EchoesReturnPropTilemap.STUMP, 35, 87);
        prop(EchoesReturnPropTilemap.LOW_WALL, 31, 86);
        prop(EchoesReturnPropTilemap.STUMP, 27, 76);
        prop(EchoesReturnPropTilemap.ROOTS, 52, 80);

        // Camp approach: traces appear before the actual lived-in clearing.
        prop(EchoesReturnPropTilemap.CART_WOOD, 45, 73);
        prop(EchoesReturnPropTilemap.CLOTH_STRIP, 39, 71);
        prop(EchoesReturnPropTilemap.STUMP, 35, 70);

        // Camp: lived-in four-person arrangement, intentionally asymmetrical.
        prop(EchoesReturnPropTilemap.FADED_TENT, 23, 65);
        prop(EchoesReturnPropTilemap.CLOTH_STRIP, 38, 67);
        prop(EchoesReturnPropTilemap.DEAD_FIRE, 29, 69);
        prop(EchoesReturnPropTilemap.BEDROLL_A, 25, 66);
        prop(EchoesReturnPropTilemap.BEDROLL_B, 33, 65);
        prop(EchoesReturnPropTilemap.BEDROLL_C, 24, 72);
        prop(EchoesReturnPropTilemap.BEDROLL_D, 35, 72);
        prop(EchoesReturnPropTilemap.COOK_POT, 28, 67);
        prop(EchoesReturnPropTilemap.TRAVEL_BAG, 33, 69);
        prop(EchoesReturnPropTilemap.CRATE, 35, 67);
        prop(EchoesReturnPropTilemap.CHECKLIST, 31, 69);
        prop(EchoesReturnPropTilemap.MAINTENANCE_TOOLS, 22, 69);
        prop(EchoesReturnPropTilemap.ROOTS, 20, 64);
        prop(EchoesReturnPropTilemap.STUMP, 39, 74);

        // Camp -> farmer journey: sparse ordinary road markers create rhythm without rewards.
        prop(EchoesReturnPropTilemap.STUMP, 67, 74);
        prop(EchoesReturnPropTilemap.LOW_WALL, 81, 69);
        prop(EchoesReturnPropTilemap.MILESTONE, 92, 64);
        prop(EchoesReturnPropTilemap.ROOTS, 98, 62);

        // Farmer/cart: ordinary working objects, not a plot shrine.
        prop(EchoesReturnPropTilemap.SACK, 104, 60);
        prop(EchoesReturnPropTilemap.FARM_TOOL, 106, 61);
        prop(EchoesReturnPropTilemap.CART_WOOD, 100, 60);
        prop(EchoesReturnPropTilemap.LOW_WALL, 111, 58);
        prop(EchoesReturnPropTilemap.LOW_WALL, 112, 61);

        // Pacing valley after the farmer: old road construction slowly returns.
        prop(EchoesReturnPropTilemap.STUMP, 103, 49);
        prop(EchoesReturnPropTilemap.MILESTONE, 93, 45);
        prop(EchoesReturnPropTilemap.LOW_WALL, 87, 42);
        prop(EchoesReturnPropTilemap.ROOTS, 78, 39);

        // Crow spur: a few readable anchors, never a breadcrumb every tile.
        prop(EchoesReturnPropTilemap.LOW_WALL, 61, 33);
        prop(EchoesReturnPropTilemap.STUMP, 54, 30);
        prop(EchoesReturnPropTilemap.ROOTS, 47, 26);

        // Shrine: small human-scale offering architecture and almost invisible four-fold wear.
        prop(EchoesReturnPropTilemap.OFFERING_BOWL, 41, 22);
        prop(EchoesReturnPropTilemap.FOUR_RECESSES, 38, 20);
        prop(EchoesReturnPropTilemap.ROOTS, 46, 24);
        prop(EchoesReturnPropTilemap.LOW_WALL, 44, 18);

        // Return loop / northern road: construction becomes progressively more regular.
        prop(EchoesReturnPropTilemap.LOW_WALL, 51, 15);
        prop(EchoesReturnPropTilemap.MILESTONE, 68, 20);
        prop(EchoesReturnPropTilemap.LOW_WALL, 79, 25);
        prop(EchoesReturnPropTilemap.MILESTONE, 90, 18);
        prop(EchoesReturnPropTilemap.LOW_WALL, 82, 14);

        // Civilization edge: field/fence rhythm becomes visible without revealing the town.
        for (int x = 71; x <= 77; x += 2) prop(EchoesReturnPropTilemap.FIELD_EDGE, x, 10);
        for (int x = 95; x <= 103; x += 2) prop(EchoesReturnPropTilemap.FIELD_EDGE, x, 9);
        prop(EchoesReturnPropTilemap.FARM_TOOL, 97, 11);
        prop(EchoesReturnPropTilemap.STUMP, 76, 12);
    }

    private void prop(int kind, int x, int y) {
        EchoesReturnPropTilemap art = new EchoesReturnPropTilemap(kind);
        art.pos(x, y);
        customTiles.add(art);
    }
}
