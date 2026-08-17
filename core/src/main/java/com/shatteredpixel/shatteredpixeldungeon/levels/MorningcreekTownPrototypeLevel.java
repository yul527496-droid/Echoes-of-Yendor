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
    public static final int INN_DOOR_X = 28;
    public static final int INN_DOOR_Y = 32;

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
                    48, 54, 48, 54, 7, "中心市场",
                    "主街中段有人群和摊贩聚集。", "晨溪最繁忙的公共空间，主街与多条支路在这里汇合。"),
            poi(RegionState.Location.MARKET_WELL, RegionPoi.Category.LANDMARK,
                    48, 56, 48, 56, 4, "市场水井",
                    "市场中央有一处常用的公共水源。", "市场中央的石砌水井，是镇民最常用的指路参照。"),
            poi(RegionState.Location.OLD_CROW_INN, RegionPoi.Category.TRAVEL,
                    28, 30, 29, 43, 7, "老鸦旅店",
                    "听说旅店在市场西北、靠近河岸的一带。", "黑乌鸦招牌悬在宽阔的旅店正门前；建筑规模远大于旧 Demo。"),
            poi(RegionState.Location.OLD_CROW_REAR_YARD, RegionPoi.Category.SERVICE,
                    13, 27, 16, 30, 5, "老鸦后院",
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
                    14, 54, 16, 54, 6, "铁匠铺",
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

        // Base urban ground. The edge remains solid to make future regional exits explicit.
        rect(1, 1, WIDTH - 2, HEIGHT - 2, Terrain.GRASS);

        // Primary navigation spine and the two riverbank lanes.
        rect(44, 1, 52, 70, Terrain.EMPTY);
        rect(2, 31, 93, 33, Terrain.EMPTY);
        rect(2, 40, 93, 42, Terrain.EMPTY);

        // Morningcreek River: broad enough to read as a real boundary, not a decorative ditch.
        rect(1, 34, 94, 39, Terrain.WATER);
        // Stone bridge carries the main street across the full river band.
        rect(44, 33, 52, 40, Terrain.EMPTY_SP);

        // Central market and approach streets.
        rect(32, 47, 63, 62, Terrain.EMPTY_SP);
        rect(4, 52, 32, 56, Terrain.EMPTY);
        rect(63, 50, 92, 54, Terrain.EMPTY);
        rect(57, 42, 60, 67, Terrain.EMPTY); // east back alley / service connector
        rect(24, 61, 43, 65, Terrain.EMPTY);
        rect(53, 61, 78, 65, Terrain.EMPTY);

        // North civic cross-streets and east clinic access.
        rect(2, 18, 43, 21, Terrain.EMPTY);
        rect(19, 8, 43, 11, Terrain.EMPTY);
        rect(19, 23, 43, 29, Terrain.EMPTY_SP);
        rect(53, 20, 93, 23, Terrain.EMPTY);
        rect(53, 26, 93, 29, Terrain.EMPTY);

        // Ravenfeather district. Large solid footprints intentionally establish massing first.
        block(6, 5, 18, 16);       // Ravenfeather Tower mass
        block(21, 10, 37, 21);     // Registry facade
        rect(21, 23, 38, 29, Terrain.EMPTY_SP); // Archive Court
        block(4, 24, 16, 30);      // west residential/service block
        block(34, 3, 42, 15);      // north-west housing

        // Old Crow Inn parcel and rear service yard.
        block(18, 23, 38, 32);
        rect(7, 23, 17, 31, Terrain.EMPTY_SP);
        rect(7, 29, 18, 32, Terrain.EMPTY);
        int innDoor = cell(INN_DOOR_X, INN_DOOR_Y);
        map[innDoor] = Terrain.EXIT;
        transitions.add(new LevelTransition(this, innDoor, LevelTransition.Type.REGULAR_EXIT));

        // East clinic, garden and residential blocks.
        block(62, 11, 79, 24);
        rect(81, 10, 92, 28, Terrain.GRASS);
        for (int y = 12; y <= 26; y += 4) {
            rect(82, y, 91, y + 1, Terrain.HIGH_GRASS);
        }
        rect(85, 10, 86, 28, Terrain.WATER);
        block(54, 4, 60, 15);
        block(54, 25, 61, 30);

        // South-west services.
        block(6, 48, 20, 59);      // blacksmith
        block(6, 63, 22, 69);      // public stables
        block(25, 64, 31, 69);     // south-west housing

        // South/east residential and warehouse fabric.
        block(64, 63, 75, 69);
        block(79, 62, 90, 69);
        block(64, 44, 75, 49);
        block(64, 55, 75, 59);
        block(79, 44, 91, 52);
        block(79, 55, 91, 59);
        rect(61, 43, 63, 60, Terrain.EMPTY); // loading lane
        rect(76, 43, 78, 60, Terrain.EMPTY); // warehouse lane
        rect(92, 40, 94, 43, Terrain.EMPTY); // river landing / east route mouth

        // Smaller residential blocks ensure side streets stay spatially dense.
        block(23, 43, 30, 49);
        block(23, 56, 30, 60);
        block(33, 64, 41, 69);
        block(54, 64, 61, 69);

        // Future permanent shortcut mouths. They are walkable stubs, not active transitions yet.
        rect(1, 19, 5, 21, Terrain.EMPTY);
        rect(91, 40, 94, 42, Terrain.EMPTY);
        rect(64, 1, 70, 5, Terrain.EMPTY);

        // Market well/fountain footprint.
        rect(46, 54, 50, 58, Terrain.WATER);
        map[cell(48, 56)] = Terrain.EMPTY_DECO;

        // Street furniture / breaks in long straight runs.
        for (int y = 44; y <= 66; y += 7) {
            map[cell(42, y)] = Terrain.EMPTY_DECO;
            map[cell(54, y)] = Terrain.EMPTY_DECO;
        }
        map[cell(39, 20)] = Terrain.EMPTY_DECO;
        map[cell(56, 22)] = Terrain.EMPTY_DECO;
        map[cell(63, 41)] = Terrain.EMPTY_DECO;
        map[cell(88, 41)] = Terrain.EMPTY_DECO;

        installVisualFoundation();
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

    private void installVisualFoundation() {
        customTiles.removeIf(t -> t instanceof EchoesLandmarkTilemap);
        customWalls.removeIf(t -> t instanceof EchoesLandmarkTilemap);

        addWall(EchoesLandmarkTilemap.TOWN_GATE, 45, 67);
        addWall(EchoesLandmarkTilemap.TOWN_GATE, 45, 1);
        addWall(EchoesLandmarkTilemap.OLD_CROW_INN, 22, 25);
        addTile(EchoesLandmarkTilemap.OLD_CROW_SIGN, 29, 30);
        addWall(EchoesLandmarkTilemap.BLACKSMITH, 9, 51);
        addWall(EchoesLandmarkTilemap.FARMHOUSE, 11, 64);
        addWall(EchoesLandmarkTilemap.SHOP, 24, 13); // registry placeholder facade
        addWall(EchoesLandmarkTilemap.SHOP, 67, 14); // clinic placeholder facade
        addWall(EchoesLandmarkTilemap.SHOP, 68, 45); // warehouse placeholder facade
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
        addVillager(25, 28, "旅店伙计", "正门在河岸路这边。后院现在只让送货车进。", 4);
        addVillager(29, 22, "抄写员", "登记处今天照常开门，不过档案院里还有几间房没整理。", 5);
        addVillager(67, 27, "药圃学徒", "别踩东边的药畦。那些不是杂草。", 6);
        addVillager(85, 22, "采药人", "晨溪外的草药更好，但镇里的药圃至少不用和野猪抢。", 7);
        addVillager(65, 51, "搬运工", "重车都走装卸巷，不然市场早被堵死了。", 0);
        addVillager(88, 42, "河工", "顺河往东还有路，只是现在那一段不好走。", 1);
        addVillager(17, 61, "马夫", "要跑远路先看马蹄。省下的时间都在这些小地方。", 2);
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
