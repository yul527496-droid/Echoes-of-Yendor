/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.RegionPoi;
import com.shatteredpixel.shatteredpixeldungeon.RegionState;
import com.shatteredpixel.shatteredpixeldungeon.items.ActOneShrineInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.tiles.ActOneReturnShrineTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesLandmarkTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesReturnPropTilemap;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

/**
 * v0.3 Event Readability & Signature Audio candidate dressing/presentation layer.
 * The accepted 124x92 v0.2 journey geometry remains frozen; this class only repairs
 * presentation semantics and the already-authored river/crow/shrine staging.
 */
public class ActOneReturnCandidateLevel extends ActOneReturnLevel {

    private static final int[][] EARLY_RIVER = {
            {7,78},{18,79},{30,80},{43,80},{55,78},{67,77}
    };

    @Override
    protected boolean build() {
        boolean ok = super.build();
        // Super builds the river before a decorative south-bank exploration spur. Reassert the
        // river here, then reopen only the authored old bridge. This preserves the accepted v0.2 route.
        sealUnintendedEarlyFord();
        installReturnShrineInteraction();
        installReturnProps();
        return ok;
    }

    @Override
    protected void createItems() {
        super.createItems();
        // v0.3: architecture is no longer represented by a non-pickable Item/Heap.
        removeLegacyShrineInscriptionHeaps();
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        removeLegacyShrineInscriptionHeaps();
        customTiles.removeIf(t -> t instanceof EchoesReturnPropTilemap);
        customWalls.removeIf(t -> t instanceof EchoesReturnPropTilemap);
        installReturnShrineInteraction();
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

    private void sealUnintendedEarlyFord() {
        for (int i = 0; i < EARLY_RIVER.length - 1; i++) {
            paintTerrainLine(EARLY_RIVER[i][0], EARLY_RIVER[i][1],
                    EARLY_RIVER[i+1][0], EARLY_RIVER[i+1][1], 2, Terrain.WATER);
        }
        // The old bridge is the one deliberate crossing. Its broad deck also keeps diagonal
        // pathfinding from being snagged on a water corner.
        fillTerrainRect(44,79,48,81,Terrain.EMPTY);
        paintTerrainLine(45,82,47,78,1,Terrain.EMPTY);
    }

    private void installReturnShrineInteraction() {
        // Replace only the generic 2x2 Return shrine visual at this authored location. Other
        // Echoes landmarks are untouched. The replacement uses the same art but owns inspect text.
        customTiles.removeIf(t -> t instanceof EchoesLandmarkTilemap
                && t.tileX == 40 && t.tileY == 20 && t.tileW == 2 && t.tileH == 2);
        ActOneReturnShrineTilemap shrine = new ActOneReturnShrineTilemap();
        shrine.pos(40,20);
        customTiles.add(shrine);
    }

    /**
     * v0.2 saves may deserialize an ActOneShrineInscription Heap before this candidate restores.
     * Remove only that obsolete architecture-as-item representation; leave every other Heap intact.
     */
    private void removeLegacyShrineInscriptionHeaps() {
        ArrayList<Heap> emptyHeaps = new ArrayList<>();
        for (Heap heap : heaps.valueList()) {
            for (Item item : heap.items.toArray(new Item[0])) {
                if (item instanceof ActOneShrineInscription) heap.items.remove(item);
            }
            if (heap.items.isEmpty()) emptyHeaps.add(heap);
        }
        for (Heap heap : emptyHeaps) heap.destroy();
    }

    private void paintTerrainLine(int x1, int y1, int x2, int y2, int radius, int terrain) {
        int steps = Math.max(Math.abs(x2-x1), Math.abs(y2-y1));
        for (int i=0; i<=steps; i++) {
            float t = steps == 0 ? 0 : i/(float)steps;
            int x = Math.round(x1+(x2-x1)*t);
            int y = Math.round(y1+(y2-y1)*t);
            for (int dy=-radius; dy<=radius; dy++) {
                for (int dx=-radius; dx<=radius; dx++) {
                    if (Math.abs(dx)+Math.abs(dy) <= radius+1
                            && x+dx > 0 && y+dy > 0 && x+dx < WIDTH-1 && y+dy < HEIGHT-1) {
                        map[cell(x+dx,y+dy)] = terrain;
                    }
                }
            }
        }
    }

    private void fillTerrainRect(int x1, int y1, int x2, int y2, int terrain) {
        for (int y=y1; y<=y2; y++) for (int x=x1; x<=x2; x++) map[cell(x,y)] = terrain;
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

        // Crow stops deliberately sit beside distinct anchors: wall -> stump -> roots -> shrine.
        prop(EchoesReturnPropTilemap.LOW_WALL, 61, 33);
        prop(EchoesReturnPropTilemap.STUMP, 54, 30);
        prop(EchoesReturnPropTilemap.ROOTS, 47, 26);

        // Shrine: compact visual chain from final crow perch to Yendor altar to readable stonework.
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
