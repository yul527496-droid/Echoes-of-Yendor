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
 * Old Crow Inn v0.1 interior shell.
 *
 * Only the public ground floor is playable in this pass. The stair/cellar/service zones are
 * spatial reservations for later multi-floor implementation and carry no formal Act 1 plot.
 */
public class OldCrowInnPrototypeLevel extends Level implements RegionAreaLevel {

    public static final int WIDTH = 48;
    public static final int HEIGHT = 34;
    public static final int DOOR_X = 24;
    public static final int DOOR_Y = 32;

    private static final String INN_TILES = "environment/tiles_inn_v1.png";
    private static final String SURFACE_WATER = "environment/water_surface_v1.png";
    private static final RegionPoi[] NO_POIS = new RegionPoi[0];

    {
        color1 = 0x6A5038;
        color2 = 0xB98A5D;
        viewDistance = 14;
    }

    @Override
    public String tilesTex() {
        return INN_TILES;
    }

    @Override
    public String waterTex() {
        return SURFACE_WATER;
    }

    @Override
    public void playLevelMusic() {
        // Spatial prototype intentionally does not add or bind new chapter audio.
        RegionState state = RegionState.get();
        if (state != null) state.syncHud();
    }

    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);
        for (int i = 0; i < length(); i++) map[i] = Terrain.WALL;

        // Main public taproom: broad circulation loop instead of the old single-room slice.
        rect(3, 4, 44, 31, Terrain.EMPTY_SP);
        rect(18, 25, 30, 32, Terrain.EMPTY);

        // Kitchen/service block in the west.
        rect(4, 5, 15, 15, Terrain.WALL);
        rect(6, 7, 13, 13, Terrain.EMPTY);
        map[cell(15, 11)] = Terrain.DOOR;

        // Bar and staff lane across the north-middle.
        rect(17, 5, 34, 10, Terrain.EMPTY);
        rect(17, 11, 34, 12, Terrain.EMPTY_SP);

        // Quiet side room / future private dining room.
        rect(36, 5, 43, 14, Terrain.WALL);
        rect(38, 7, 41, 12, Terrain.EMPTY_SP);
        map[cell(36, 11)] = Terrain.DOOR;

        // Future stair to second-floor guest rooms: readable but not active in v0.1.
        rect(36, 17, 43, 22, Terrain.WALL);
        rect(38, 18, 41, 20, Terrain.EMPTY_SP);
        map[cell(36, 20)] = Terrain.DOOR;
        map[cell(40, 19)] = Terrain.EMPTY_DECO;

        // Cellar/service reservation in the west-south corner.
        rect(4, 19, 13, 27, Terrain.WALL);
        rect(6, 21, 11, 25, Terrain.EMPTY_SP);
        map[cell(13, 23)] = Terrain.DOOR;
        map[cell(8, 23)] = Terrain.EMPTY_DECO;

        // Rear service-door direction. It is intentionally sealed until the stable yard is built.
        rect(42, 24, 44, 28, Terrain.EMPTY);
        map[cell(44, 26)] = Terrain.EMPTY_DECO;

        // Fireplace and table footprints break up the large public room.
        map[cell(8, 16)] = Terrain.EMPTY_DECO;
        map[cell(20, 17)] = Terrain.EMPTY_DECO;
        map[cell(27, 17)] = Terrain.EMPTY_DECO;
        map[cell(34, 16)] = Terrain.EMPTY_DECO;
        map[cell(18, 22)] = Terrain.EMPTY_DECO;
        map[cell(29, 22)] = Terrain.EMPTY_DECO;
        map[cell(35, 27)] = Terrain.EMPTY_DECO;

        installVisualFoundation();

        int door = cell(DOOR_X, DOOR_Y);
        map[door] = Terrain.ENTRANCE;
        transitions.add(new LevelTransition(this, door, LevelTransition.Type.REGULAR_ENTRANCE));
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
        addWall(EchoesLandmarkTilemap.BAR, 20, 7);
        addWall(EchoesLandmarkTilemap.FIREPLACE, 7, 15);
        addTile(EchoesLandmarkTilemap.TAVERN_TABLE, 19, 17);
        addTile(EchoesLandmarkTilemap.TAVERN_TABLE, 27, 17);
        addTile(EchoesLandmarkTilemap.TAVERN_TABLE, 18, 22);
        addTile(EchoesLandmarkTilemap.TAVERN_TABLE, 29, 22);
        addTile(EchoesLandmarkTilemap.TAVERN_TABLE, 34, 27);
        addTile(EchoesLandmarkTilemap.SIGNPOST, 40, 19); // future guest-room stair placeholder
        addTile(EchoesLandmarkTilemap.SIGNPOST, 8, 23);  // future cellar placeholder
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
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE) {
            SequelGame.enterTownPrototypeFromInn();
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
        addVillager(23, 13, "值班酒保", "楼上的客房和酒窖还没整理好。公共层先照常营业。", 4);
        addVillager(17, 19, "本地木匠", "这地方后院比你想的还大，送酒桶的车都从后面进。", 5);
        addVillager(31, 19, "旅行商人", "晨溪不算大城，不过该有的路、桥、马厩都不缺。", 6);
        addVillager(24, 27, "歇脚的车夫", "我把马留在南边公共马厩。走过来不算远。", 7);
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
        return cell(DOOR_X, DOOR_Y - 1);
    }

    @Override
    public RegionState.Area regionArea() {
        return RegionState.Area.OLD_CROW_INN;
    }

    @Override
    public String regionAreaName() {
        return "老鸦旅店 · 公共层";
    }

    @Override
    public RegionPoi[] regionPois() {
        return NO_POIS;
    }
}
