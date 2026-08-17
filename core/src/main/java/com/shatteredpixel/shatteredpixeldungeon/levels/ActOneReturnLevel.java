/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.RegionPoi;
import com.shatteredpixel.shatteredpixeldungeon.RegionState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ActOneReturnWolf;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ActOneReturnCrow;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ActOneReturnDonkey;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ActOneReturnFarmer;
import com.shatteredpixel.shatteredpixeldungeon.items.ActOneCampChecklist;
import com.shatteredpixel.shatteredpixeldungeon.items.ActOneHeroMark;
import com.shatteredpixel.shatteredpixeldungeon.items.ActOneShrineInscription;
import com.shatteredpixel.shatteredpixeldungeon.items.ActOneYendorAtShrine;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesLandmarkTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesSurfaceTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

/** Formal Act 1 Scene 1 surface: Return / 归来. v0.2 Journey & Audio Pass geometry. */
public class ActOneReturnLevel extends Level implements RegionAreaLevel {

    public static final int WIDTH = 124, HEIGHT = 92;
    public static final int START_X = 22, START_Y = 86;
    public static final int NORTH_X = 84, NORTH_Y = 3;

    private static final int CAMP_X = 30, CAMP_Y = 69;
    private static final int FARMER_X = 102, FARMER_Y = 59;
    private static final int SHRINE_X = 41, SHRINE_Y = 22;
    private static final int SHRINE_ALTAR_X = 42, SHRINE_ALTAR_Y = 21;

    /** Authored route is deliberately lateral and folded; it is not a vertical story pipe. */
    private static final int[][] ROAD = {
            {22,86},{31,82},{43,82},{50,77},{63,77},{68,72},{79,71},{87,65},
            {98,64},{103,59},{106,53},{101,47},{92,46},{85,42},{77,40},{71,35},
            {76,30},{86,29},{94,24},{90,18},{83,15},{84,9},{84,3}
    };

    private static final int[][] CAMP_SPUR = {
            {56,76},{48,74},{41,72},{35,70},{30,69}
    };

    private static final int[][] CROW_SPUR = {
            {71,35},{64,34},{58,31},{50,29},{43,25},{41,22}
    };

    private static final int[][] SHRINE_RETURN = {
            {41,22},{42,18},{50,15},{59,17},{68,20},{78,25},{86,29}
    };

    private static final int[][] RIVER = {
            {7,78},{18,79},{30,80},{43,80},{55,78},{67,77}
    };

    private static final String SURFACE_TILES = "environment/tiles_surface_v1.png";
    private static final String SURFACE_WATER = "environment/water_surface_v1.png";

    private static final RegionPoi[] POIS = {
            poi(RegionState.Location.SURFACE_ENTRANCE, RegionPoi.Category.TRAVEL,
                    START_X, START_Y, START_X, START_Y, 7,
                    "Yendor 地牢旧址",
                    "南方林地里有一处古老地下遗迹的出口。",
                    "树根和苔藓正慢慢吞没破损石阶；这就是你重返地表的地方。"),
            poi(RegionState.Location.ABANDONED_EXPEDITION_CAMP, RegionPoi.Category.AMBIENT,
                    CAMP_X, CAMP_Y, CAMP_X, CAMP_Y, 8,
                    "废弃远征营地",
                    "旧路旁的林子里似乎有废弃帐布。",
                    "褪色帐布、熄灭营火和几处行军卧具留在林缘，像一支队伍仓促离开后的营地。"),
            poi(RegionState.Location.OLD_KINGS_ROAD_SHRINE, RegionPoi.Category.LANDMARK,
                    SHRINE_X, SHRINE_Y, SHRINE_X, SHRINE_Y, 6,
                    "古道旧神龛",
                    "旧王道旁的林中似乎有一处旧石龛。",
                    "一座小而陈旧的路边神龛，石台、供奉槽和苔痕都比这次远征古老得多。"),
            poi(RegionState.Location.MORNINGCREEK, RegionPoi.Category.TRAVEL,
                    NORTH_X, NORTH_Y, 84, 6, 4,
                    "晨溪",
                    "老农说，沿北边大路可以到达晨溪。具体位置还没有确认。",
                    "晨溪方向。")
    };

    {
        color1 = 0x627D4A;
        color2 = 0xA99B67;
        viewDistance = 15;
    }

    @Override public String tilesTex(){ return SURFACE_TILES; }
    @Override public String waterTex(){ return SURFACE_WATER; }

    @Override
    public RegionState.Area regionArea() {
        return RegionState.Area.ACT_ONE_RETURN;
    }

    @Override
    public String regionAreaName() {
        return "归来 · 地表旧道";
    }

    @Override
    public RegionPoi[] regionPois() {
        return POIS;
    }

    @Override
    public void playLevelMusic() {
        ChapterOneAudio.surfaceAmbience();
        ActOneReturnState.get();
        RegionState region = RegionState.current();
        if (region != null) region.syncHud();
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT); // Level.setSize starts as WALL: dense forest is the authored boundary.

        buildExitBasin();
        buildMainJourneyRibbon();
        buildRiverValley();
        buildCampSideArea();
        buildFarmerArea();
        buildShrineLoop();
        buildCivilizationEdge();
        buildExplorationPockets();
        installVisualFoundation();

        int start = cell(START_X, START_Y);
        map[start] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, start, LevelTransition.Type.REGULAR_ENTRANCE));
        int north = cell(NORTH_X, NORTH_Y);
        map[north] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, north, LevelTransition.Type.REGULAR_EXIT));
        return true;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (width() != WIDTH || height() != HEIGHT) {
            // v0.1 -> v0.2 geometry migration: preserve scene state, rebuild authored terrain,
            // and place the hero safely at the old dungeon basin rather than leaving a stale cell.
            create();
            if (Dungeon.hero != null) Dungeon.hero.pos = cell(START_X, START_Y);
            return;
        }
        installVisualFoundation();
    }

    @Override
    public void buildFlagMaps() {
        super.buildFlagMaps();
        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.WATER) {
                passable[i] = false;
                avoid[i] = true;
            }
        }
    }

    private void buildExitBasin() {
        // A real breathing-space clearing: old stone mouth, damp ground, river edge and two nooks.
        carveOval(22,84,15,7,Terrain.GRASS);
        carveOval(14,84,7,4,Terrain.GRASS);
        carveOval(34,87,8,3,Terrain.GRASS);
        rect(16,82,30,89,Terrain.EMPTY_SP);
        carveOval(22,85,8,4,Terrain.EMPTY);
        rect(18,84,26,89,Terrain.EMPTY_DECO);

        // Collapsed carving / half-lost travel trace are environmental, not plot clues.
        paintLine(29,86,36,88,1,Terrain.EMPTY_DECO);
        paintLine(13,86,9,83,0,Terrain.EMPTY_DECO);
    }

    private void buildMainJourneyRibbon() {
        // First carve a broad walkable forest verge, then the narrower human route inside it.
        for (int i = 0; i < ROAD.length - 1; i++) {
            int verge = i < 4 ? 5 : (i >= 17 ? 6 : 4);
            paintLine(ROAD[i][0], ROAD[i][1], ROAD[i+1][0], ROAD[i+1][1], verge, Terrain.GRASS);
        }
        for (int i = 0; i < ROAD.length - 1; i++) {
            int roadWidth = i >= 17 ? 2 : 1;
            int material = i < 4 ? Terrain.EMPTY_SP : Terrain.EMPTY;
            paintLine(ROAD[i][0], ROAD[i][1], ROAD[i+1][0], ROAD[i+1][1], roadWidth, material);
        }

        // Small readable changes every ~15-30 movement cells without turning them into rewards.
        carveOval(57,76,7,4,Terrain.GRASS);   // camp junction / broken trail mouth
        carveOval(74,72,7,4,Terrain.GRASS);   // narrow woods opening
        carveOval(89,66,8,5,Terrain.GRASS);   // road opens before the farmer
        carveOval(98,48,7,5,Terrain.GRASS);   // post-event quiet bend
        carveOval(85,43,6,4,Terrain.GRASS);   // old stone verge
        carveOval(77,39,6,4,Terrain.GRASS);   // Yendor anomaly clearing
        carveOval(91,24,7,4,Terrain.GRASS);   // old King's Road remains
    }

    private void buildRiverValley() {
        // The river cuts across the early journey. Only the old narrow bridge is passable.
        for (int i = 0; i < RIVER.length - 1; i++) {
            paintLine(RIVER[i][0],RIVER[i][1],RIVER[i+1][0],RIVER[i+1][1],2,Terrain.WATER);
        }
        rect(44,79,48,81,Terrain.EMPTY); // bridge deck / banks, aligned to BRIDGE_SCENE overlay
        paintLine(45,82,47,78,1,Terrain.EMPTY);

        // Riverbank lookout and wet-grass pocket: optional exploration, no clue/reward dependency.
        carveOval(22,75,8,4,Terrain.GRASS);
        paintLine(30,82,26,78,2,Terrain.GRASS);
        paintLine(26,78,22,75,1,Terrain.EMPTY_DECO);
    }

    private void buildCampSideArea() {
        // A branch that must be chosen: the camp cannot be read from the main road in one glance.
        for (int i=0;i<CAMP_SPUR.length-1;i++) {
            paintLine(CAMP_SPUR[i][0],CAMP_SPUR[i][1],CAMP_SPUR[i+1][0],CAMP_SPUR[i+1][1],3,Terrain.GRASS);
            paintLine(CAMP_SPUR[i][0],CAMP_SPUR[i][1],CAMP_SPUR[i+1][0],CAMP_SPUR[i+1][1],1,Terrain.EMPTY_DECO);
        }
        carveOval(CAMP_X,CAMP_Y,13,9,Terrain.GRASS);
        carveOval(CAMP_X,CAMP_Y,10,7,Terrain.EMPTY_SP);
        carveOval(20,66,6,4,Terrain.GRASS); // grassed-over rear trace
        paintLine(25,68,20,66,1,Terrain.EMPTY_DECO);

        map[cell(29,69)] = Terrain.EMBERS;
        int[][] livedIn = {{25,66},{33,65},{24,72},{35,72},{27,75},{38,68}};
        for (int[] p : livedIn) map[cell(p[0],p[1])] = Terrain.EMPTY_DECO;
    }

    private void buildFarmerArea() {
        // A recognisable old pasture pinch rather than combat staged in the middle of a hallway.
        carveOval(FARMER_X,FARMER_Y,12,8,Terrain.GRASS);
        carveOval(FARMER_X,FARMER_Y,9,6,Terrain.EMPTY_SP);
        paintLine(96,64,103,59,2,Terrain.EMPTY);
        paintLine(103,59,106,53,2,Terrain.EMPTY);
        rect(111,55,114,64,Terrain.GRASS); // collapsed fence-side slope
        map[cell(100,59)] = Terrain.EMPTY_DECO;
        map[cell(105,60)] = Terrain.EMPTY_DECO;
        map[cell(111,61)] = Terrain.HIGH_GRASS;
        map[cell(94,56)] = Terrain.HIGH_GRASS;
    }

    private void buildShrineLoop() {
        // The crow branch is a distinct enclosed woodland route, not a GPS arrow beside the road.
        for (int i=0;i<CROW_SPUR.length-1;i++) {
            paintLine(CROW_SPUR[i][0],CROW_SPUR[i][1],CROW_SPUR[i+1][0],CROW_SPUR[i+1][1],3,Terrain.GRASS);
            paintLine(CROW_SPUR[i][0],CROW_SPUR[i][1],CROW_SPUR[i+1][0],CROW_SPUR[i+1][1],1,Terrain.EMPTY_SP);
        }
        carveOval(SHRINE_X,SHRINE_Y,10,7,Terrain.GRASS);
        carveOval(SHRINE_X,SHRINE_Y,7,5,Terrain.EMPTY_SP);
        carveOval(SHRINE_X,SHRINE_Y,4,3,Terrain.EMPTY);
        map[cell(SHRINE_ALTAR_X,SHRINE_ALTAR_Y)] = Terrain.EMPTY_DECO;

        // A second short path returns farther north: Scene 1's one explicit exploration loop.
        for (int i=0;i<SHRINE_RETURN.length-1;i++) {
            paintLine(SHRINE_RETURN[i][0],SHRINE_RETURN[i][1],SHRINE_RETURN[i+1][0],SHRINE_RETURN[i+1][1],3,Terrain.GRASS);
            paintLine(SHRINE_RETURN[i][0],SHRINE_RETURN[i][1],SHRINE_RETURN[i+1][0],SHRINE_RETURN[i+1][1],1,Terrain.EMPTY_DECO);
        }
        carveOval(56,16,7,4,Terrain.GRASS);
        carveOval(70,21,6,4,Terrain.GRASS);
    }

    private void buildCivilizationEdge() {
        // The last 1-2 minutes are only environmental resolution: wider road, walls, field edges.
        carveOval(90,18,9,5,Terrain.GRASS);
        carveOval(84,10,10,6,Terrain.GRASS);
        paintLine(94,24,90,18,6,Terrain.GRASS);
        paintLine(90,18,83,15,6,Terrain.GRASS);
        paintLine(83,15,84,3,7,Terrain.GRASS);
        paintLine(94,24,90,18,2,Terrain.EMPTY);
        paintLine(90,18,83,15,2,Terrain.EMPTY);
        paintLine(83,15,84,3,2,Terrain.EMPTY);

        rect(70,5,76,12,Terrain.EMPTY_SP);
        rect(94,6,103,13,Terrain.EMPTY_SP);
        for (int x=71;x<=76;x+=2) map[cell(x,10)] = Terrain.EMPTY_DECO;
        for (int x=95;x<=102;x+=2) map[cell(x,9)] = Terrain.EMPTY_DECO;
    }

    private void buildExplorationPockets() {
        // Side spaces prevent the walk from feeling like a pipe while still keeping orientation clear.
        carveOval(62,70,5,4,Terrain.GRASS);
        carveOval(82,63,6,4,Terrain.GRASS);
        carveOval(111,51,5,4,Terrain.GRASS);
        carveOval(88,38,5,3,Terrain.GRASS);
        carveOval(62,27,5,4,Terrain.GRASS);
        carveOval(98,20,5,3,Terrain.GRASS);

        // A few short visual dead-ends deliberately use grass/old trace, never full road language.
        paintLine(81,70,84,75,1,Terrain.GRASS);
        paintLine(93,45,98,41,1,Terrain.GRASS);
        paintLine(59,18,56,13,1,Terrain.GRASS);
    }

    private void installVisualFoundation() {
        customTiles.removeIf(t -> t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);
        customWalls.removeIf(t -> t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);

        addSurfaceTile(EchoesSurfaceTilemap.DUNGEON_MOUTH, 18, 83);
        addSurfaceTile(EchoesSurfaceTilemap.BRIDGE_SCENE, 44, 79);
        addLandmarkTile(EchoesLandmarkTilemap.CAMP, 28, 67);
        addSurfaceTile(EchoesSurfaceTilemap.CAMP_SCENE, 25, 66);
        addLandmarkTile(EchoesLandmarkTilemap.WAGON, 100, 57);
        addLandmarkTile(EchoesLandmarkTilemap.SHRINE, 40, 20);
        addLandmarkTile(EchoesLandmarkTilemap.SIGNPOST, 83, 10);

        int[][] forest = {
                {5,84},{11,76},{18,72},{35,76},{58,83},{72,81},{90,75},{113,70},
                {116,57},{109,45},{96,39},{67,43},{55,37},{34,30},{29,19},{49,12},
                {72,13},{103,16},{111,30},{17,57},{8,44},{20,34}
        };
        for (int i=0;i<forest.length;i++) {
            addSurfaceWall(i%3==0?EchoesSurfaceTilemap.FOREST_DEEP:
                    i%2==0?EchoesSurfaceTilemap.FOREST_EDGE_B:EchoesSurfaceTilemap.FOREST_EDGE_A,
                    forest[i][0], forest[i][1]);
        }

        int[][] features = {
                {34,87},{27,77},{52,80},{61,74},{73,70},{86,67},{96,63},{111,59},
                {101,50},{91,45},{82,42},{68,35},{61,32},{52,28},{46,24},{37,18},
                {55,16},{70,21},{82,27},{92,23},{87,16},{80,8}
        };
        for (int i=0;i<features.length;i++) {
            addSurfaceTile(i%4==0?EchoesSurfaceTilemap.FOREST_LOG:
                    i%4==1?EchoesSurfaceTilemap.FOREST_ROCKS:
                            i%4==2?EchoesSurfaceTilemap.FOREST_STUMP:EchoesSurfaceTilemap.FOREST_BUSH,
                    features[i][0],features[i][1]);
        }

        int[][] road = {
                {28,83},{39,82},{52,77},{64,76},{71,72},{82,69},{91,65},{99,63},
                {104,55},{101,48},{92,46},{84,42},{76,39},{72,35},{77,30},{87,28},
                {93,23},{89,18},{84,15},{84,11},{84,7},{84,4}
        };
        int[] kinds = {EchoesSurfaceTilemap.ROAD_WEEDS,EchoesSurfaceTilemap.ROAD_TRAMPLE,
                EchoesSurfaceTilemap.ROAD_MUD,EchoesSurfaceTilemap.ROAD_RUTS,
                EchoesSurfaceTilemap.ROAD_STONES,EchoesSurfaceTilemap.ROAD_SCAR,
                EchoesSurfaceTilemap.ROAD_RUTS,EchoesSurfaceTilemap.ROAD_STONES};
        for (int i=0;i<road.length;i++) addSurfaceTile(kinds[i%kinds.length],road[i][0],road[i][1]);

        int[][] creek = {{10,77},{20,78},{31,79},{40,79},{52,77},{63,76}};
        int[] waterKinds = {EchoesSurfaceTilemap.RIVER_REEDS,EchoesSurfaceTilemap.RIVER_BANK_TOP,
                EchoesSurfaceTilemap.RIVER_STONES,EchoesSurfaceTilemap.RIVER_RIPPLE,
                EchoesSurfaceTilemap.RIVER_WET_GRASS,EchoesSurfaceTilemap.RIVER_ROOTS};
        for (int i=0;i<creek.length;i++) addSurfaceTile(waterKinds[i],creek[i][0],creek[i][1]);

        // Curated variation follows readable beats rather than random confetti.
        grassCluster(12,86); grassCluster(33,84); grassCluster(23,74); grassCluster(46,76);
        grassCluster(61,78); grassCluster(75,68); grassCluster(90,63); grassCluster(110,55);
        grassCluster(96,49); grassCluster(86,44); grassCluster(72,39); grassCluster(60,30);
        grassCluster(47,26); grassCluster(35,22); grassCluster(54,16); grassCluster(72,22);
        grassCluster(97,22); grassCluster(74,9); grassCluster(96,10);

        for (int x=71;x<=77;x+=2) addSurfaceTile(EchoesSurfaceTilemap.FENCE,x,10);
        for (int x=95;x<=103;x+=2) addSurfaceTile(EchoesSurfaceTilemap.FENCE,x,9);
    }

    private void grassCluster(int x, int y) {
        addSurfaceTile(EchoesSurfaceTilemap.GRASS_TUFT_A,x,y);
        addSurfaceTile(EchoesSurfaceTilemap.GRASS_MIX,x+1,y);
        addSurfaceTile(EchoesSurfaceTilemap.GRASS_STONES,x,y+1);
        addSurfaceTile(EchoesSurfaceTilemap.GRASS_TUFT_B,x+1,y+1);
    }

    private void addSurfaceTile(int kind,int x,int y){
        EchoesSurfaceTilemap art=new EchoesSurfaceTilemap(kind); art.pos(x,y); customTiles.add(art);
    }
    private void addSurfaceWall(int kind,int x,int y){
        EchoesSurfaceTilemap art=new EchoesSurfaceTilemap(kind); art.pos(x,y); customWalls.add(art);
    }
    private void addLandmarkTile(int kind,int x,int y){
        EchoesLandmarkTilemap art=new EchoesLandmarkTilemap(kind); art.pos(x,y); customTiles.add(art);
    }

    @Override
    protected void createMobs() {
        ActOneReturnState state = ActOneReturnState.get();
        if (state == null) return;

        if (state.farmerOutcome == ActOneReturnState.FarmerOutcome.UNRESOLVED
                || (state.farmerOutcome == ActOneReturnState.FarmerOutcome.RESCUED && !state.farmerDialogueCompleted)) {
            ActOneReturnFarmer farmer = new ActOneReturnFarmer();
            farmer.pos = cell(FARMER_X, FARMER_Y); mobs.add(farmer);
            ActOneReturnDonkey donkey = new ActOneReturnDonkey();
            donkey.pos = cell(FARMER_X+2, FARMER_Y); mobs.add(donkey);
        }
        if (state.wolvesOutcome == ActOneReturnState.WolvesOutcome.UNRESOLVED) {
            int[][] wolves = {{99,57},{106,56},{107,62}};
            for (int[] p : wolves) { ActOneReturnWolf wolf=new ActOneReturnWolf(); wolf.pos=cell(p[0],p[1]); mobs.add(wolf); }
        }
        if (state.crowChaseActive && !state.yendorRecovered) ensureCrow();
    }

    @Override
    protected void createItems() {
        ActOneReturnState state = ActOneReturnState.get();
        if (state == null) return;
        if (!state.campListClueSeen) drop(new ActOneCampChecklist(), cell(31,69));
        drop(new ActOneHeroMark(), cell(35,67));
        drop(new ActOneShrineInscription(), cell(42,22));
        if (state.yendorTemporarilyMissing && !state.yendorRecovered) ensureShrineYendor();
    }

    /** Called by ActOneReturnState once per actor tick while this authored scene is active. */
    public void tickScene(ActOneReturnState state) {
        if (Dungeon.hero == null || state == null) return;
        int hx = Dungeon.hero.pos % width();
        int hy = Dungeon.hero.pos / width();

        ChapterOneAudio.tickTransientReturnAudio();
        ChapterOneAudio.updateStreamDistance(nearestPolylineDistance(hx,hy,RIVER));

        RegionState region = RegionState.current();
        if (region != null) region.discover(RegionState.Location.SURFACE_ENTRANCE);

        if (!state.campDiscovered && distanceTo(hx,hy,CAMP_X,CAMP_Y) <= 8) {
            state.campDiscovered = true;
            if (region != null) region.discover(RegionState.Location.ABANDONED_EXPEDITION_CAMP);
        }
        if (state.campDiscovered && !state.campBedrollClueSeen
                && distanceTo(hx,hy,29,69) <= 4) {
            state.campBedrollClueSeen = true;
            Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                    "熄灭的营火周围散着四处行军卧具。摆法和磨损都不一样。\n\n另一支远征队，大概在这里住过一阵。")));
        }

        int farmerDistance = distanceTo(hx,hy,FARMER_X,FARMER_Y);
        if (!state.farmerEventSeen && farmerDistance <= 13) {
            state.farmerEventSeen = true;
            ChapterOneAudio.playWolfWarning();
        }
        if (state.farmerEventSeen && state.wolvesOutcome == ActOneReturnState.WolvesOutcome.UNRESOLVED
                && !hasReturnWolf()) {
            state.wolvesOutcome = ActOneReturnState.WolvesOutcome.RESOLVED;
            state.farmerOutcome = ActOneReturnState.FarmerOutcome.RESCUED;
            state.donkeyOutcome = ActOneReturnState.DonkeyOutcome.SURVIVED;
            state.cartOutcome = ActOneReturnState.CartOutcome.USABLE;
        }
        if (state.wolvesOutcome == ActOneReturnState.WolvesOutcome.UNRESOLVED
                && hy <= 50 && farmerDistance > 18) {
            // Walking past the pasture is a valid non-intervention outcome, never a soft lock.
            state.farmerEventSeen = true;
            state.farmerOutcome = ActOneReturnState.FarmerOutcome.IGNORED;
            state.donkeyOutcome = ActOneReturnState.DonkeyOutcome.SURVIVED;
            state.cartOutcome = ActOneReturnState.CartOutcome.ABANDONED;
            state.wolvesOutcome = ActOneReturnState.WolvesOutcome.ABANDONED;
        }

        boolean farmerResolved = state.wolvesOutcome != ActOneReturnState.WolvesOutcome.UNRESOLVED;
        if (farmerResolved && !state.yendorAnomalyStarted
                && hx >= 74 && hx <= 84 && hy >= 36 && hy <= 43) {
            state.yendorAnomalyStarted = true;
            state.yendorAnomalyNoticeShown = true;
            ChapterOneAudio.playYendorPulse();
            Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                    "Yendor 贴着掌心，忽然热了一瞬。\n\n很轻。轻得几乎像错觉。\n\n你把护符拿出来看了看。它又安静下来。")));
        }
        if (state.yendorAnomalyStarted && !state.yendorTemporarilyMissing && !state.yendorRecovered
                && hx >= 67 && hx <= 73 && hy >= 31 && hy <= 36) {
            ChapterOneAudio.playYendorPulse();
            state.beginYendorChase();
            ensureCrow();
            ensureShrineYendor();
            Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                    "护符又短促地震了一下。\n\n你手指一松，一道黑影从树枝间扑近。吊链擦过指节，被乌鸦一把带了起来。\n\n它没有飞高，只钻进了左侧更密的林子。")));
        }

        if (!state.shrineDiscovered && distanceTo(hx,hy,SHRINE_X,SHRINE_Y) <= 6) {
            state.shrineDiscovered = true;
            if (region != null) region.discover(RegionState.Location.OLD_KINGS_ROAD_SHRINE);
            ChapterOneAudio.returnShrineAmbience();
        }
        if (state.shrineDiscovered && distanceTo(hx,hy,SHRINE_X,SHRINE_Y) > 10) {
            ChapterOneAudio.surfaceAmbience();
        }

        if (state.yendorRecovered && !state.crowChaseResolved) {
            state.crowChaseResolved = true;
            state.crowChaseActive = false;
        }
    }

    private boolean hasReturnWolf() {
        for (Mob mob : mobs) if (mob instanceof ActOneReturnWolf) return true;
        return false;
    }

    private void ensureCrow() {
        for (Mob mob : mobs) if (mob instanceof ActOneReturnCrow) return;
        ActOneReturnCrow crow = new ActOneReturnCrow();
        crow.pos = crowStops()[0];
        mobs.add(crow);
        GameScene.add(crow);
    }

    private void ensureShrineYendor() {
        for (Heap heap : heaps.valueList()) {
            for (Item item : heap.items) if (item instanceof ActOneYendorAtShrine) return;
        }
        drop(new ActOneYendorAtShrine(), cell(SHRINE_ALTAR_X,SHRINE_ALTAR_Y));
    }

    /** Four sparse stops produce three actual direction changes before the shrine. */
    public int[] crowStops() {
        return new int[]{cell(62,33),cell(54,31),cell(47,27),cell(42,22)};
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) {
            GLog.p("潮湿的石阶重新没入地下。你已经把要带出来的东西带出来了。");
            return false;
        }
        if (transition.type == LevelTransition.Type.REGULAR_EXIT) {
            ActOneReturnState state = ActOneReturnState.get();
            if (state != null && state.yendorTemporarilyMissing && !state.yendorRecovered) {
                GLog.w("Yendor 还在林子里的神龛附近。");
                return false;
            }
            if (state != null) {
                state.northExitReached = true;
                if (!state.northExitNoticeShown) {
                    state.northExitNoticeShown = true;
                    Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                            "开发占位 · 晨溪方向\n\nAct 1 Scene 1 — Return / 归来 v0.2 Journey & Audio Pass Candidate 到此结束。\n下一正式场景尚未接入；封存的 Morningcreek Town prototype 不会在这里被冒充为正式后续。")));
                }
            }
            return false;
        }
        return super.activateTransition(hero, transition);
    }

    @Override public Mob createMob(){ return null; }
    @Override public Actor addRespawner(){ return null; }
    @Override public int randomRespawnCell(Char ch){ return cell(START_X,START_Y-1); }

    private static RegionPoi poi(RegionState.Location location, RegionPoi.Category category,
                                 int x,int y,int heardX,int heardY,int radius,
                                 String name,String heard,String discovered){
        return new RegionPoi(location,category,x,y,heardX,heardY,radius,name,heard,discovered);
    }

    private void paintLine(int x1,int y1,int x2,int y2,int radius,int terrain){
        int steps=Math.max(Math.abs(x2-x1),Math.abs(y2-y1));
        for(int i=0;i<=steps;i++){
            float t=steps==0?0:i/(float)steps;
            int x=Math.round(x1+(x2-x1)*t), y=Math.round(y1+(y2-y1)*t);
            for(int dy=-radius;dy<=radius;dy++) for(int dx=-radius;dx<=radius;dx++)
                if(Math.abs(dx)+Math.abs(dy)<=radius+1&&inside(x+dx,y+dy)) map[cell(x+dx,y+dy)]=terrain;
        }
    }

    private void rect(int x1,int y1,int x2,int y2,int terrain){
        for(int y=Math.max(1,y1);y<=Math.min(HEIGHT-2,y2);y++)
            for(int x=Math.max(1,x1);x<=Math.min(WIDTH-2,x2);x++) map[cell(x,y)]=terrain;
    }

    private void carveOval(int cx,int cy,int rx,int ry,int terrain){
        for(int y=Math.max(1,cy-ry);y<=Math.min(HEIGHT-2,cy+ry);y++) {
            for(int x=Math.max(1,cx-rx);x<=Math.min(WIDTH-2,cx+rx);x++) {
                float nx=(x-cx)/(float)Math.max(1,rx);
                float ny=(y-cy)/(float)Math.max(1,ry);
                if(nx*nx+ny*ny<=1f) map[cell(x,y)]=terrain;
            }
        }
    }

    private boolean inside(int x,int y){ return x>0&&y>0&&x<WIDTH-1&&y<HEIGHT-1; }
    public int cell(int x,int y){ return x+y*width(); }
    private static int distanceTo(int x1,int y1,int x2,int y2){ return Math.abs(x1-x2)+Math.abs(y1-y2); }

    private static int nearestPolylineDistance(int x,int y,int[][] points){
        int best=Integer.MAX_VALUE;
        for(int[] p:points) best=Math.min(best,distanceTo(x,y,p[0],p[1]));
        return best;
    }
}
