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

/** Formal Act 1 Scene 1 surface: Return / 归来. */
public class ActOneReturnLevel extends Level implements RegionAreaLevel {

    public static final int WIDTH = 88, HEIGHT = 68;
    public static final int START_X = 44, START_Y = 64;
    public static final int NORTH_X = 46, NORTH_Y = 2;

    private static final int CAMP_X = 27, CAMP_Y = 51;
    private static final int FARMER_X = 55, FARMER_Y = 38;
    private static final int SHRINE_X = 70, SHRINE_Y = 19;
    private static final int SHRINE_ALTAR_X = 71, SHRINE_ALTAR_Y = 18;

    private static final String SURFACE_TILES = "environment/tiles_surface_v1.png";
    private static final String SURFACE_WATER = "environment/water_surface_v1.png";

    private static final RegionPoi[] POIS = {
            poi(RegionState.Location.SURFACE_ENTRANCE, RegionPoi.Category.TRAVEL,
                    START_X, START_Y, START_X, START_Y, 5,
                    "Yendor 地牢旧址",
                    "南方林地里有一处古老地下遗迹的出口。",
                    "树根和苔藓正慢慢吞没破损石阶；这就是你重返地表的地方。"),
            poi(RegionState.Location.ABANDONED_EXPEDITION_CAMP, RegionPoi.Category.AMBIENT,
                    CAMP_X, CAMP_Y, CAMP_X, CAMP_Y, 6,
                    "废弃远征营地",
                    "旧路旁的林子里似乎有废弃帐布。",
                    "褪色帐布、熄灭营火和几处行军卧具留在林缘，像一支队伍仓促离开后的营地。"),
            poi(RegionState.Location.OLD_KINGS_ROAD_SHRINE, RegionPoi.Category.LANDMARK,
                    SHRINE_X, SHRINE_Y, SHRINE_X, SHRINE_Y, 5,
                    "古道旧神龛",
                    "旧王道旁的林中似乎有一处旧石龛。",
                    "一座小而陈旧的路边神龛，石台、供奉槽和苔痕都比这次远征古老得多。"),
            poi(RegionState.Location.MORNINGCREEK, RegionPoi.Category.TRAVEL,
                    NORTH_X, NORTH_Y, 46, 4, 4,
                    "晨溪",
                    "老农说，沿北边大路可以到达晨溪。具体位置还没有确认。",
                    "晨溪方向。")
    };

    private static final int[][] ROAD = {
            {44,64},{44,60},{46,56},{47,52},{50,48},{50,44},{53,40},{53,36},
            {51,32},{50,28},{48,24},{48,20},{47,16},{46,12},{46,7},{46,2}
    };

    {
        color1 = 0x627D4A;
        color2 = 0xA99B67;
        viewDistance = 16;
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
        setSize(WIDTH, HEIGHT);
        rect(1, 1, WIDTH - 2, HEIGHT - 2, Terrain.GRASS);
        buildForestMasses();
        buildExitBasin();
        buildCampSideArea();
        buildRoad();
        buildFarmerArea();
        buildShrineLoop();
        buildCivilizationEdge();
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
            create();
            if (Dungeon.hero != null) Dungeon.hero.pos = -1;
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
        // Sunken mossy clearing around the ancient stair mouth.
        rect(34, 58, 54, 66, Terrain.EMPTY_SP);
        rect(39, 60, 50, 66, Terrain.EMPTY);
        rect(41, 62, 48, 66, Terrain.EMPTY_DECO);

        // Small creek/water-edge exploration point; it does not block the north route.
        for (int x = 8; x <= 36; x++) {
            int y = 60 + (x < 18 ? 1 : x < 28 ? 0 : -1);
            map[cell(x, y)] = Terrain.WATER;
            if (x % 6 == 2 && y > 1) map[cell(x, y - 1)] = Terrain.HIGH_GRASS;
        }
        // Collapsed carving and overgrown old travel trace.
        rect(55, 60, 61, 63, Terrain.EMPTY_DECO);
        paintLine(34, 63, 27, 59, 0, Terrain.EMPTY_DECO);
    }

    private void buildCampSideArea() {
        // Natural, irregular clearing roughly 18x14, reached by a short west branch.
        rect(18, 44, 35, 56, Terrain.EMPTY);
        rect(20, 45, 33, 55, Terrain.EMPTY_SP);
        paintLine(46, 53, 36, 52, 1, Terrain.EMPTY);
        paintLine(36, 52, 32, 51, 1, Terrain.EMPTY);
        map[cell(26, 51)] = Terrain.EMBERS;
        int[][] fringe = {{18,44},{19,48},{18,54},{34,45},{35,49},{34,55},{22,44},{30,56}};
        for (int[] p : fringe) map[cell(p[0],p[1])] = Terrain.HIGH_GRASS;
        // Four intentionally non-identical bedroll/gear positions.
        map[cell(23,48)] = Terrain.EMPTY_DECO;
        map[cell(29,47)] = Terrain.EMPTY_DECO;
        map[cell(22,53)] = Terrain.EMPTY_DECO;
        map[cell(31,53)] = Terrain.EMPTY_DECO;
    }

    private void buildRoad() {
        for (int i = 0; i < ROAD.length - 1; i++) {
            int radius = ROAD[i][1] <= 15 ? 2 : 1;
            paintLine(ROAD[i][0], ROAD[i][1], ROAD[i+1][0], ROAD[i+1][1], radius, Terrain.EMPTY);
        }
        // Old paving becomes more frequent and more intact toward civilization.
        int[][] wear = {{45,59},{47,54},{49,49},{51,45},{53,41},{52,36},{51,32},{50,28},
                {48,24},{48,20},{47,16},{46,13},{45,10},{47,8},{45,6},{47,4}};
        for (int i = 0; i < wear.length; i++) {
            int[] p = wear[i];
            if (inside(p[0],p[1])) map[cell(p[0],p[1])] = Terrain.EMPTY_DECO;
            if (i >= 10 && inside(p[0]+1,p[1])) map[cell(p[0]+1,p[1])] = Terrain.EMPTY_DECO;
        }
    }

    private void buildFarmerArea() {
        rect(48, 34, 63, 43, Terrain.GRASS);
        paintLine(50,44,53,40,2,Terrain.EMPTY);
        paintLine(53,40,52,35,2,Terrain.EMPTY);
        map[cell(55,38)] = Terrain.EMPTY_DECO;
        map[cell(57,38)] = Terrain.EMPTY_DECO;
        map[cell(61,40)] = Terrain.HIGH_GRASS;
        map[cell(48,37)] = Terrain.HIGH_GRASS;
    }

    private void buildShrineLoop() {
        // East spur starts after the first Yendor anomaly and forms the scene's one clear loop.
        paintLine(48,24,59,23,1,Terrain.EMPTY);
        paintLine(59,23,66,20,1,Terrain.EMPTY);
        rect(64, 14, 78, 25, Terrain.GRASS);
        rect(66, 16, 76, 23, Terrain.EMPTY_SP);
        rect(68, 17, 74, 21, Terrain.EMPTY);
        map[cell(SHRINE_ALTAR_X, SHRINE_ALTAR_Y)] = Terrain.EMPTY_DECO;
        // Alternate short exit rejoins the old road substantially farther north.
        paintLine(70,16,64,12,1,Terrain.EMPTY);
        paintLine(64,12,53,11,1,Terrain.EMPTY);
        paintLine(53,11,46,12,1,Terrain.EMPTY);
    }

    private void buildCivilizationEdge() {
        // Northward human traces: low-wall/fence rhythm, stumps and field-edge grass.
        rect(33, 3, 40, 10, Terrain.EMPTY_SP);
        rect(54, 3, 64, 10, Terrain.EMPTY_SP);
        for (int x = 34; x <= 39; x += 2) map[cell(x,8)] = Terrain.EMPTY_DECO;
        for (int x = 55; x <= 63; x += 2) map[cell(x,7)] = Terrain.EMPTY_DECO;
        map[cell(42,10)] = Terrain.HIGH_GRASS;
        map[cell(51,9)] = Terrain.HIGH_GRASS;
    }

    private void buildForestMasses() {
        // Border first, then authored interior masses. Road/clearings are carved afterwards.
        rect(0,0,WIDTH-1,0,Terrain.WALL);
        rect(0,HEIGHT-1,WIDTH-1,HEIGHT-1,Terrain.WALL);
        rect(0,0,0,HEIGHT-1,Terrain.WALL);
        rect(WIDTH-1,0,WIDTH-1,HEIGHT-1,Terrain.WALL);

        rect(2,3,29,18,Terrain.WALL);
        rect(4,21,23,40,Terrain.WALL);
        rect(3,43,14,57,Terrain.WALL);
        rect(2,63,28,66,Terrain.WALL);
        rect(59,3,84,12,Terrain.WALL);
        rect(69,27,84,45,Terrain.WALL);
        rect(65,47,85,65,Terrain.WALL);
        rect(55,51,63,59,Terrain.WALL);
        rect(25,28,41,39,Terrain.WALL);
        rect(12,20,31,26,Terrain.WALL);

        // Break silhouettes so forest reads as natural masses rather than perfect rectangles.
        int[][] notches = {{29,8},{28,15},{23,31},{22,38},{14,48},{59,8},{69,32},{70,43},
                {65,53},{57,56},{41,33},{31,24},{12,23},{26,29}};
        for (int[] p : notches) if (inside(p[0],p[1])) map[cell(p[0],p[1])] = Terrain.GRASS;
    }

    private void installVisualFoundation() {
        customTiles.removeIf(t -> t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);
        customWalls.removeIf(t -> t instanceof EchoesSurfaceTilemap || t instanceof EchoesLandmarkTilemap);

        addSurfaceTile(EchoesSurfaceTilemap.DUNGEON_MOUTH, 40, 61);
        addLandmarkTile(EchoesLandmarkTilemap.CAMP, 24, 49);
        addSurfaceTile(EchoesSurfaceTilemap.CAMP_SCENE, 20, 46);
        addLandmarkTile(EchoesLandmarkTilemap.WAGON, 54, 37);
        addLandmarkTile(EchoesLandmarkTilemap.SHRINE, 70, 18);
        addLandmarkTile(EchoesLandmarkTilemap.SIGNPOST, 45, 9);

        int[][] forest = {{5,5},{13,10},{22,4},{6,24},{16,32},{5,45},{61,4},{75,6},
                {74,31},{78,39},{70,50},{76,57},{28,31},{58,53},{17,21}};
        for (int i=0;i<forest.length;i++) {
            addSurfaceWall(i%3==0?EchoesSurfaceTilemap.FOREST_DEEP:
                    i%2==0?EchoesSurfaceTilemap.FOREST_EDGE_B:EchoesSurfaceTilemap.FOREST_EDGE_A,
                    forest[i][0], forest[i][1]);
        }
        int[][] bushes = {{34,59},{57,61},{35,54},{18,50},{33,46},{63,24},{78,22},{54,13},
                {41,17},{62,11},{39,7}};
        for (int i=0;i<bushes.length;i++) addSurfaceTile(
                i%3==0?EchoesSurfaceTilemap.FOREST_ROCKS:
                        i%2==0?EchoesSurfaceTilemap.FOREST_STUMP:EchoesSurfaceTilemap.FOREST_BUSH,
                bushes[i][0],bushes[i][1]);

        int[][] road = {{45,59},{47,54},{49,49},{51,45},{53,41},{52,36},{51,32},{50,28},
                {48,24},{48,20},{47,16},{46,13},{46,10},{46,7},{46,4}};
        int[] kinds = {EchoesSurfaceTilemap.ROAD_WEEDS,EchoesSurfaceTilemap.ROAD_TRAMPLE,
                EchoesSurfaceTilemap.ROAD_MUD,EchoesSurfaceTilemap.ROAD_RUTS,
                EchoesSurfaceTilemap.ROAD_STONES,EchoesSurfaceTilemap.ROAD_SCAR,
                EchoesSurfaceTilemap.ROAD_RUTS,EchoesSurfaceTilemap.ROAD_STONES};
        for (int i=0;i<road.length;i++) addSurfaceTile(kinds[i%kinds.length],road[i][0],road[i][1]);

        int[][] creek = {{10,60},{15,61},{20,60},{25,60},{30,59},{35,59}};
        int[] waterKinds = {EchoesSurfaceTilemap.RIVER_REEDS,EchoesSurfaceTilemap.RIVER_BANK_TOP,
                EchoesSurfaceTilemap.RIVER_STONES,EchoesSurfaceTilemap.RIVER_RIPPLE,
                EchoesSurfaceTilemap.RIVER_WET_GRASS,EchoesSurfaceTilemap.RIVER_ROOTS};
        for (int i=0;i<creek.length;i++) addSurfaceTile(waterKinds[i],creek[i][0],creek[i][1]-1);

        // Curated surface variation around each readable beat, never uniform random confetti.
        grassCluster(31,62); grassCluster(54,60); grassCluster(36,55); grassCluster(16,42);
        grassCluster(37,44); grassCluster(62,42); grassCluster(43,29); grassCluster(58,26);
        grassCluster(62,15); grassCluster(78,24); grassCluster(40,12); grassCluster(52,6);

        // Existing project-owned fence tile is used sparingly only at the civilization edge.
        for (int x=34;x<=40;x+=2) addSurfaceTile(EchoesSurfaceTilemap.FENCE,x,8);
        for (int x=55;x<=63;x+=2) addSurfaceTile(EchoesSurfaceTilemap.FENCE,x,7);
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
            int[][] wolves = {{52,37},{58,36},{59,41}};
            for (int[] p : wolves) { ActOneReturnWolf wolf=new ActOneReturnWolf(); wolf.pos=cell(p[0],p[1]); mobs.add(wolf); }
        }
        if (state.crowChaseActive && !state.yendorRecovered) ensureCrow();
    }

    @Override
    protected void createItems() {
        ActOneReturnState state = ActOneReturnState.get();
        if (state == null) return;
        if (!state.campListClueSeen) drop(new ActOneCampChecklist(), cell(28,52));
        drop(new ActOneHeroMark(), cell(31,50));
        drop(new ActOneShrineInscription(), cell(72,19));
        if (state.yendorTemporarilyMissing && !state.yendorRecovered) ensureShrineYendor();
    }

    /** Called by ActOneReturnState once per actor tick while this authored scene is active. */
    public void tickScene(ActOneReturnState state) {
        if (Dungeon.hero == null || state == null) return;
        int hx = Dungeon.hero.pos % width();
        int hy = Dungeon.hero.pos / width();

        // Stream is proximity-only and always falls back to the clean forest bed.
        int streamX = Math.max(8, Math.min(36, hx));
        int streamY = 60 + (streamX < 18 ? 1 : streamX < 28 ? 0 : -1);
        ChapterOneAudio.updateStreamDistance(Math.abs(hx-streamX)+Math.abs(hy-streamY));

        RegionState region = RegionState.current();
        if (region != null) {
            region.discover(RegionState.Location.SURFACE_ENTRANCE);
        }

        if (!state.campDiscovered && distanceTo(hx,hy,CAMP_X,CAMP_Y) <= 7) {
            state.campDiscovered = true;
            if (region != null) region.discover(RegionState.Location.ABANDONED_EXPEDITION_CAMP);
        }
        if (state.campDiscovered && !state.campBedrollClueSeen
                && distanceTo(hx,hy,27,50) <= 3) {
            state.campBedrollClueSeen = true;
            Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                    "熄灭的营火周围散着四处行军卧具。摆法和磨损都不一样。\n\n另一支远征队，大概在这里住过一阵。")));
        }

        int farmerDistance = distanceTo(hx,hy,FARMER_X,FARMER_Y);
        if (!state.farmerEventSeen && farmerDistance <= 11) {
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
                && hy < 31 && farmerDistance > 12) {
            // Bypassing/withdrawing is a valid scene outcome, never a soft lock.
            state.farmerEventSeen = true;
            state.farmerOutcome = ActOneReturnState.FarmerOutcome.IGNORED;
            state.donkeyOutcome = ActOneReturnState.DonkeyOutcome.SURVIVED;
            state.cartOutcome = ActOneReturnState.CartOutcome.ABANDONED;
            state.wolvesOutcome = ActOneReturnState.WolvesOutcome.ABANDONED;
        }

        boolean farmerResolved = state.wolvesOutcome != ActOneReturnState.WolvesOutcome.UNRESOLVED;
        if (farmerResolved && !state.yendorAnomalyStarted && hy <= 29 && hx >= 43 && hx <= 57) {
            state.yendorAnomalyStarted = true;
            state.yendorAnomalyNoticeShown = true;
            ChapterOneAudio.playYendorPulse();
            Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                    "Yendor 贴着掌心，忽然热了一瞬。\n\n很轻。轻得几乎像错觉。\n\n你把护符拿出来看了看。它又安静下来。")));
        }
        if (state.yendorAnomalyStarted && !state.yendorTemporarilyMissing && !state.yendorRecovered
                && hy <= 25 && hx >= 44 && hx <= 60) {
            ChapterOneAudio.playYendorPulse();
            state.beginYendorChase();
            ensureCrow();
            ensureShrineYendor();
            Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                    "护符又短促地震了一下。\n\n你手指一松，一道黑影从树枝间扑近。吊链擦过指节，被乌鸦一把带了起来。\n\n它没有飞高，只钻进了右侧更密的林子。")));
        }

        if (!state.shrineDiscovered && distanceTo(hx,hy,SHRINE_X,SHRINE_Y) <= 5) {
            state.shrineDiscovered = true;
            if (region != null) region.discover(RegionState.Location.OLD_KINGS_ROAD_SHRINE);
            ChapterOneAudio.returnShrineAmbience();
        }
        if (state.shrineDiscovered && distanceTo(hx,hy,SHRINE_X,SHRINE_Y) > 8) {
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

    public int[] crowStops() {
        return new int[]{cell(58,24),cell(64,22),cell(68,20),cell(72,18)};
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
                            "开发占位 · 晨溪方向\n\nAct 1 Scene 1 — Return / 归来 v0.1 candidate 到此结束。\n下一正式场景尚未接入；封存的 Morningcreek Town prototype 不会在这里被冒充为正式后续。")));
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
        for(int y=Math.max(0,y1);y<=Math.min(HEIGHT-1,y2);y++)
            for(int x=Math.max(0,x1);x<=Math.min(WIDTH-1,x2);x++) map[cell(x,y)]=terrain;
    }
    private boolean inside(int x,int y){ return x>0&&y>0&&x<WIDTH-1&&y<HEIGHT-1; }
    public int cell(int x,int y){ return x+y*width(); }
    private static int distanceTo(int x1,int y1,int x2,int y2){ return Math.abs(x1-x2)+Math.abs(y1-y2); }
}
