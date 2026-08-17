/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.RegionPoi;
import com.shatteredpixel.shatteredpixeldungeon.RegionState;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SurfaceVillager;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.tiles.EchoesLandmarkTilemap;
import com.watabou.utils.Bundle;

/**
 * Large authored Morningcreek hub prototype.
 *
 * This is intentionally separate from MorningcreekMainStreetLevel, which remains part of
 * the legacy linear sequel demo. Geometry here is a navigation/performance prototype for
 * the formal semi-open region and uses placeholder landmark art where final assets do not
 * exist yet.
 */
public class MorningcreekTownPrototypeLevel extends Level implements RegionAreaLevel {

    public static final int WIDTH = 96;
    public static final int HEIGHT = 72;

    public static final int START_X = 48;
    public static final int START_Y = 68;
    public static final int INN_DOOR_X = 40;
    public static final int INN_DOOR_Y = 50;

    private static final String TOWN_TILES = "environment/tiles_town_v1.png";
    private static final String SURFACE_WATER = "environment/water_surface_v1.png";

    private static final RegionPoi[] POIS = new RegionPoi[]{
            poi(RegionState.Location.SOUTH_GATE, RegionPoi.Category.TRAVEL,
                    48, 68, 48, 66, 6, "晨溪南门",
                    "晨溪南侧的城门区域。", "晨溪通往旧王道方向的南门，也是主要旅行节点。"),
            poi(RegionState.Location.SOUTH_CARAVAN_APRON, RegionPoi.Category.AMBIENT,
                    48, 64, 48, 64, 5, "南门车马坪",
                    "南门附近似乎有供车马停靠的空地。", "商旅进镇前整理货物、饮马和等候检查的车马坪。"),
            poi(RegionState.Location.CENTRAL_MARKET, RegionPoi.Category.SERVICE,
                    50, 55, 50, 55, 7, "中心市场",
                    "主街中段有人群和摊贩聚集。", "晨溪最繁忙的公共空间，主街与多条支路在这里汇合。"),
            poi(RegionState.Location.MARKET_WELL, RegionPoi.Category.LANDMARK,
                    48, 56, 48, 56, 4, "市场水井",
                    "市场中央有一处常用的公共水源。", "市场中央的石砌水井，是镇民最常用的指路参照。"),
            poi(RegionState.Location.OLD_CROW_INN, RegionPoi.Category.TRAVEL,
                    30, 47, 36, 55, 7, "老鸦旅店",
                    "听说旅店在市场西侧、靠近南岸的一带。", "黑乌鸦招牌悬在宽阔的旅店正门前；建筑规模远大于旧 Demo。"),
            poi(RegionState.Location.OLD_CROW_REAR_YARD, RegionPoi.Category.SERVICE,
                    13, 46, 16, 48, 5, "老鸦后院",
                    "旅店后方有供货和牲口进出的院落。", "旅店后院预留了马厩、货物入口和未来的替代出入口。"),
            poi(RegionState.Location.STONE_BRIDGE, RegionPoi.Category.LANDMARK,
                    48, 36, 48, 36, 7, "晨溪大石桥",
                    "主街跨河处有一座大桥。", "宽阔石桥把南北城区连在一起，是整个晨溪最稳定的空间参照之一。"),
            poi(RegionState.Location.RAVENFEATHER_TOWER, RegionPoi.Category.LANDMARK,
                    12, 11, 18, 13, 7, "鸦羽塔",
                    "西北城区有一座高耸的旧塔。", "鸦羽区最醒目的垂直地标；最终美术尚未制作，当前用大型体量占位。"),
            poi(RegionState.Location.RAVENFEATHER_REGISTRY, RegionPoi.Category.TRAVEL,
                    28, 16, 20, 15, 7, "鸦羽登记处",
                    "听说登记处位于西北的鸦羽区，靠近一座高塔。", "晨溪的重要登记与档案机构；未来调查权限会在这里逐层开放。"),
            poi(RegionState.Location.ARCHIVE_COURT, RegionPoi.Category.INVESTIGATION,
                    29, 26, 23, 25, 5, "档案庭院",
                    "登记处附近似乎还有独立档案院落。", "连接登记处与档案区的安静庭院，适合未来承载调查与权限变化。"),
            poi(RegionState.Location.EAST_CLINIC, RegionPoi.Category.SERVICE,
                    70, 18, 70, 20, 7, "东城诊所",
                    "东城区有一处诊所。", "晨溪主要诊疗点，旁边直接连着药圃。"),
            poi(RegionState.Location.HERB_GARDEN, RegionPoi.Category.SERVICE,
                    86, 20, 84, 20, 6, "诊所药圃",
                    "诊所旁似乎种着药草。", "分畦种植的药圃与小水渠，让东城区具有明显的生活功能。"),
            poi(RegionState.Location.RIVER_WAREHOUSES, RegionPoi.Category.INVESTIGATION,
                    76, 49, 74, 48, 7, "河岸仓储区",
                    "南岸东侧分布着成片仓库。", "沿河排列的仓储建筑群，未来既可提供物资也可承载调查证据。"),
            poi(RegionState.Location.WAREHOUSE_LOADING_LANE, RegionPoi.Category.AMBIENT,
                    64, 47, 66, 47, 5, "仓库装卸巷",
                    "仓库之间有专门的装卸通道。", "连接市场、仓库和河岸的狭长运输巷，车流避开了主市场。"),
            poi(RegionState.Location.RIVER_LANDING, RegionPoi.Category.LANDMARK,
                    89, 41, 88, 42, 5, "东河埠头",
                    "东侧河岸有一处小型埠头。", "面向东部河谷的装卸埠头，也是未来区域路线的重要参照。"),
            poi(RegionState.Location.BLACKSMITH, RegionPoi.Category.SERVICE,
                    14, 56, 16, 56, 6, "铁匠铺",
                    "南门西侧能听见打铁声。", "靠近南侧运输路线的铁匠铺，方便车队和居民修理工具。"),
            poi(RegionState.Location.PUBLIC_STABLES, RegionPoi.Category.SERVICE,
                    18, 65, 18, 64, 6, "公共马厩",
                    "南门附近有公共马厩。", "面向商旅的公共马厩，为未来 Travel Skip 提供现实世界基础。"),
            poi(RegionState.Location.EAST_BACK_ALLEY, RegionPoi.Category.AMBIENT,
                    59, 53, 59, 53, 5, "东侧后巷",
                    "市场东侧有一条避开主街的窄巷。", "夹在住宅与仓储区之间的窄巷，可绕开市场最拥挤的路段。"),
            poi(RegionState.Location.NORTH_GATE, RegionPoi.Category.TRAVEL,
                    48, 3, 48, 5, 6, "晨溪北门",
                    "主街北端通向镇外。", "通往北部高地的北门，未来将成为正式旅行节点。"),
            poi(RegionState.Location.HUNTERS_ROAD_MOUTH, RegionPoi.Category.SHORTCUT,
                    3, 20, 8, 20, 5, "猎人路入口",
                    "西侧城缘似乎有猎人使用的小路。", "一条尚未开放的永久捷径入口；未来可连接晨溪与西侧林地。"),
            poi(RegionState.Location.RIVERSIDE_ROAD_MOUTH, RegionPoi.Category.SHORTCUT,
                    92, 41, 88, 41, 5, "河岸路入口",
                    "东河岸有沿水延伸的小路。", "尚未开放的河岸永久捷径，未来可减少穿过市场的往返。"),
            poi(RegionState.Location.NORTH_MILL_PATH_MOUTH, RegionPoi.Category.SHORTCUT,
                    67, 3, 65, 7, 5, "北磨坊小径",
                    "北门附近另有一条偏离主路的小径。", "通往旧磨坊方向的永久捷径入口，目前只完成空间预留。")
    };

    {
        color1 = 0x70775A;
        color2 = 0xBEAA78;
        viewDistance = 16;
    }

    @Override
    public String tilesTex() {
        return TOWN_TILES;
    }

    @Override
    public String waterTex() {
        return SURFACE_WATER;
    }

    @Override
    public void playLevelMusic() {
        // Formal prototype intentionally adds no new Chapter 1 audio.
        RegionState state = RegionState.get();
        if (state != null) state.syncHud();
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);
        rect(1, 1, WIDTH - 2, HEIGHT - 2, Terrain.GRASS);

        // The river remains the strongest east-west anchor. Everything else is authored around it.
        rect(1, 34, 94, 39, Terrain.WATER);

        buildDistrictMassing();
        carveRoadHierarchy();
        buildBridgeAndMicroLoops();
        buildClinicGarden();
        buildMarketDetails();

        int innDoor = cell(INN_DOOR_X, INN_DOOR_Y);
        map[innDoor] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, innDoor, LevelTransition.Type.REGULAR_EXIT));

        installVisualFoundation();
        return true;
    }

    /**
     * Breaks the old monolithic obstacle rectangles into authored building clusters.
     * Interiors are not implemented here; the one- and two-cell gaps are intentional alleys,
     * service lanes, yards and facade breaks that make the town read as many buildings.
     */
    private void buildDistrictMassing() {
        // Old Crow Inn: one large public-facing body plus a detached service wing.
        block(25, 44, 40, 51);
        block(16, 43, 23, 48);

        // South gate / transport quarter: workshops, stables and small street-front rows.
        block(6, 53, 14, 59);
        block(16, 54, 22, 60);
        block(7, 64, 15, 69);
        block(17, 63, 25, 69);
        block(28, 64, 34, 69);
        block(35, 64, 41, 69);

        // South-east residential edge. Small footprints keep the side streets legible.
        block(55, 63, 62, 69);
        block(65, 64, 72, 69);
        block(75, 63, 82, 69);
        block(85, 63, 91, 69);
        block(57, 55, 63, 60);
        block(66, 55, 72, 59);
        block(75, 55, 82, 60);
        block(85, 54, 91, 59);

        // River warehouses: narrow sheds with dedicated loading gaps rather than four giant blocks.
        block(63, 44, 70, 47);
        block(73, 44, 79, 48);
        block(82, 44, 88, 47);
        block(63, 52, 69, 56);
        block(72, 52, 79, 57);
        block(82, 51, 88, 56);
        block(89, 47, 92, 52);

        // Ravenfeather Tower: clipped corners make it read as a singular civic mass.
        block(8, 8, 17, 16);
        rect(8, 8, 9, 9, Terrain.GRASS);
        rect(16, 8, 17, 9, Terrain.GRASS);
        rect(8, 15, 9, 16, Terrain.GRASS);
        rect(16, 15, 17, 16, Terrain.GRASS);

        // Registry: a U-shaped civic footprint around a formal inner court.
        block(23, 10, 29, 19);
        block(35, 10, 41, 19);
        block(23, 8, 41, 10);
        block(5, 24, 11, 29);
        block(13, 24, 19, 29);
        block(24, 24, 30, 28);
        block(34, 24, 40, 29);
        block(31, 3, 40, 6);

        // East-side clinic/living quarter: lower, smaller masses around the garden.
        block(61, 11, 70, 18);
        block(72, 13, 77, 18);
        block(54, 4, 60, 9);
        block(71, 4, 77, 9);
        block(54, 25, 60, 30);
        block(63, 25, 69, 30);
        block(72, 25, 78, 30);
        block(82, 25, 88, 30);
    }

    /** Establishes a readable 7-cell spine, 3-4 cell district streets and 1-2 cell shortcuts. */
    private void carveRoadHierarchy() {
        // South of the river is a busier dirt/road language; north civic streets use stone paving.
        rect(45, 40, 51, 70, Terrain.EMPTY);
        rect(45, 1, 51, 33, Terrain.EMPTY_SP);

        // South gate traffic apron and transport cross streets.
        rect(38, 62, 58, 69, Terrain.EMPTY);
        rect(3, 60, 27, 62, Terrain.EMPTY);
        rect(27, 59, 44, 62, Terrain.EMPTY);
        rect(52, 59, 84, 62, Terrain.EMPTY);

        // Ravenfeather district: older, quieter, more formal stone network.
        rect(2, 18, 44, 21, Terrain.EMPTY_SP);
        rect(20, 27, 44, 30, Terrain.EMPTY_SP);
        rect(2, 31, 44, 33, Terrain.EMPTY_SP);
        rect(18, 7, 22, 20, Terrain.EMPTY_SP);
        rect(20, 21, 22, 29, Terrain.EMPTY_SP);
        rect(28, 2, 30, 17, Terrain.EMPTY_SP);

        // East-side life/clinic streets retain a softer ordinary road language.
        rect(52, 20, 93, 23, Terrain.EMPTY);
        rect(52, 27, 93, 30, Terrain.EMPTY);
        rect(52, 31, 93, 33, Terrain.EMPTY);
        rect(52, 10, 60, 12, Terrain.EMPTY);

        // Market approach and compact east-west circulation.
        rect(34, 40, 62, 43, Terrain.EMPTY_SP);
        rect(28, 52, 44, 55, Terrain.EMPTY);
        rect(52, 49, 93, 52, Terrain.EMPTY);
        rect(36, 51, 56, 60, Terrain.EMPTY_SP);
        rect(33, 54, 59, 58, Terrain.EMPTY_SP);

        // West river path and the east warehouse river path both advertise lateral exploration.
        rect(2, 40, 33, 42, Terrain.EMPTY);
        rect(52, 40, 93, 42, Terrain.EMPTY);

        // Warehouse service hierarchy: narrow loading lanes between distinct sheds.
        rect(60, 43, 62, 58, Terrain.EMPTY);
        rect(70, 43, 72, 58, Terrain.EMPTY);
        rect(79, 43, 81, 58, Terrain.EMPTY);
        rect(89, 42, 91, 58, Terrain.EMPTY);
        rect(79, 57, 91, 59, Terrain.EMPTY);
        rect(60, 57, 81, 59, Terrain.EMPTY);

        // Familiarity shortcuts around the market: deliberately only 1-2 cells wide.
        rect(56, 52, 58, 66, Terrain.EMPTY);
        rect(31, 55, 33, 65, Terrain.EMPTY);

        // Visible future route mouths. They remain spatial promises, not Travel Skip implementation.
        rect(1, 19, 5, 21, Terrain.EMPTY);
        rect(91, 40, 94, 42, Terrain.EMPTY);
        rect(64, 1, 70, 6, Terrain.EMPTY);
        rect(61, 6, 67, 8, Terrain.EMPTY);
    }

    private void buildBridgeAndMicroLoops() {
        // Morningcreek Stone Bridge: seven walkable cells wide, with solid parapets and bridgeheads.
        rect(45, 34, 51, 39, Terrain.EMPTY_SP);
        for (int y = 34; y <= 39; y++) {
            map[cell(44, y)] = Terrain.WALL;
            map[cell(52, y)] = Terrain.WALL;
        }
        rect(42, 31, 54, 33, Terrain.EMPTY_SP);
        rect(42, 40, 54, 42, Terrain.EMPTY_SP);
        map[cell(42, 33)] = Terrain.WALL;
        map[cell(54, 33)] = Terrain.WALL;
        map[cell(42, 40)] = Terrain.WALL;
        map[cell(54, 40)] = Terrain.WALL;

        // Micro-loop 1: market -> Inn side alley -> rear service yard -> west river road.
        rect(23, 49, 24, 58, Terrain.EMPTY);
        rect(8, 49, 24, 51, Terrain.EMPTY);
        rect(8, 44, 15, 48, Terrain.EMPTY_SP);
        rect(8, 43, 10, 49, Terrain.EMPTY);

        // Micro-loop 2: an east river walk wraps behind the warehouses before returning to market.
        // The lanes are already carved above; this short south return makes the loop obvious on foot.
        rect(84, 57, 91, 59, Terrain.EMPTY);

        // Micro-loop 3: a completely optional quiet Ravenfeather courtyard.
        rect(10, 21, 22, 23, Terrain.EMPTY_SP);
        rect(10, 23, 12, 29, Terrain.EMPTY_SP);
        rect(10, 29, 22, 31, Terrain.EMPTY_SP);

        // Formal registry forecourt and its inner U-shaped courtyard.
        rect(30, 11, 34, 19, Terrain.EMPTY_SP);
        rect(29, 17, 35, 22, Terrain.EMPTY_SP);

        // Tower court lets the civic silhouette be approached from more than one side.
        rect(5, 17, 18, 20, Terrain.EMPTY_SP);

        // Micro-loop 5: a deliberately unnecessary east-bank dead end near the future landing.
        rect(87, 31, 93, 33, Terrain.EMPTY);
    }

    private void buildClinicGarden() {
        rect(78, 18, 93, 20, Terrain.EMPTY);
        rect(78, 22, 80, 29, Terrain.EMPTY);
        rect(81, 11, 92, 12, Terrain.HIGH_GRASS);
        rect(81, 15, 92, 16, Terrain.HIGH_GRASS);
        rect(81, 24, 92, 25, Terrain.HIGH_GRASS);
        rect(81, 27, 92, 28, Terrain.HIGH_GRASS);
        rect(85, 10, 86, 28, Terrain.WATER);
        // A small foot crossing prevents the irrigation channel from becoming a hard wall.
        rect(84, 19, 87, 20, Terrain.EMPTY);
    }

    private void buildMarketDetails() {
        // The well is a compact obstruction inside the market rather than the center of a giant crossroad.
        rect(47, 55, 49, 57, Terrain.WATER);
        map[cell(48, 56)] = Terrain.EMPTY_DECO;

        // Sparse clutter anchors the edge of the square without creating collision-heavy final art.
        map[cell(36, 52)] = Terrain.EMPTY_DECO;
        map[cell(55, 52)] = Terrain.EMPTY_DECO;
        map[cell(35, 58)] = Terrain.EMPTY_DECO;
        map[cell(56, 58)] = Terrain.EMPTY_DECO;
        map[cell(42, 61)] = Terrain.EMPTY_DECO;
        map[cell(54, 61)] = Terrain.EMPTY_DECO;
        map[cell(63, 41)] = Terrain.EMPTY_DECO;
        map[cell(88, 41)] = Terrain.EMPTY_DECO;
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

    private void installVisualFoundation() {
        customTiles.removeIf(t -> t instanceof EchoesLandmarkTilemap);
        customWalls.removeIf(t -> t instanceof EchoesLandmarkTilemap);
        addWall(EchoesLandmarkTilemap.TOWN_GATE, 45, 67);
        addWall(EchoesLandmarkTilemap.TOWN_GATE, 45, 1);
        addWall(EchoesLandmarkTilemap.OLD_CROW_INN, 25, 44);
        addTile(EchoesLandmarkTilemap.OLD_CROW_SIGN, 38, 49);
        addWall(EchoesLandmarkTilemap.BLACKSMITH, 9, 54);
        addWall(EchoesLandmarkTilemap.FARMHOUSE, 11, 64);
        addWall(EchoesLandmarkTilemap.SHOP, 24, 13);
        addWall(EchoesLandmarkTilemap.SHOP, 67, 14);
        addWall(EchoesLandmarkTilemap.SHOP, 68, 45);
        addTile(EchoesLandmarkTilemap.WELL, 47, 55);
        addTile(EchoesLandmarkTilemap.NOTICE_BOARD, 22, 25);
        addTile(EchoesLandmarkTilemap.SIGNPOST, 3, 20);
        addTile(EchoesLandmarkTilemap.SIGNPOST, 92, 41);
        addTile(EchoesLandmarkTilemap.SIGNPOST, 67, 3);
    }

    private void addTile(int kind, int x, int y) {
        EchoesLandmarkTilemap art = new EchoesLandmarkTilemap(kind);
        art.pos(x, y);
        customTiles.add(art);
    }

    private void addWall(int kind, int x, int y) {
        EchoesLandmarkTilemap art = new EchoesLandmarkTilemap(kind);
        art.pos(x, y);
        customWalls.add(art);
    }

    private void block(int x1, int y1, int x2, int y2) {
        rect(x1, y1, x2, y2, Terrain.WALL);
    }

    private void rect(int x1, int y1, int x2, int y2, int terrain) {
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                if (x > 0 && y > 0 && x < WIDTH - 1 && y < HEIGHT - 1) map[cell(x, y)] = terrain;
            }
        }
    }

    public int cell(int x, int y) {
        return x + y * width();
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_EXIT) {
            SequelGame.enterOldCrowInnPrototype();
            return true;
        }
        return super.activateTransition(hero, transition);
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
        addVillager(48, 65, "南门守卫", "市场过桥就是北城区。第一次来晨溪的话，别只沿一条街走。", 0);
        addVillager(38, 55, "水果摊主", "河那边是鸦羽区和诊所；东边的仓库这会儿正忙。", 1);
        addVillager(55, 53, "跑腿少年", "我走后巷比主街快。只是下雨天那边会积水。", 2);
        addVillager(47, 45, "桥边老人", "这座桥比很多房子都老。镇子长大了，它倒一直在这里。", 3);
        addVillager(42, 50, "旅店伙计", "正门就在市场西边。后院现在只让送货车进。", 4);
        addVillager(29, 30, "抄写员", "登记处今天照常开门，不过档案院里还有几间房没整理。", 5);
        addVillager(67, 27, "药圃学徒", "别踩东边的药畦。那些不是杂草。", 6);
        addVillager(85, 22, "采药人", "晨溪外的草药更好，但镇里的药圃至少不用和野猪抢。", 7);
        addVillager(62, 51, "搬运工", "重车都走装卸巷，不然市场早被堵死了。", 0);
        addVillager(88, 42, "河工", "顺河往东还有路，只是现在那一段不好走。", 1);
        addVillager(20, 62, "马夫", "要跑远路先看马蹄。省下的时间都在这些小地方。", 2);
        addVillager(48, 8, "北门巡丁", "北面地势高，风也大。今天还算好走。", 3);
    }

    private void addVillager(int x, int y, String name, String line, int variant) {
        SurfaceVillager villager = new SurfaceVillager(name, line, variant);
        villager.pos = cell(x, y);
        mobs.add(villager);
    }

    @Override
    protected void createItems() {
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    public int randomRespawnCell(Char ch) {
        return cell(START_X, START_Y);
    }

    @Override
    public RegionState.Area regionArea() {
        return RegionState.Area.MORNINGCREEK_TOWN;
    }

    @Override
    public String regionAreaName() {
        return "晨溪镇";
    }

    @Override
    public RegionPoi[] regionPois() {
        return POIS;
    }

    private static RegionPoi poi(RegionState.Location location,
                                 RegionPoi.Category category,
                                 int x, int y,
                                 int heardX, int heardY,
                                 int radius,
                                 String name,
                                 String heard,
                                 String discovered) {
        return new RegionPoi(location, category, x, y, heardX, heardY, radius, name, heard, discovered);
    }
}
