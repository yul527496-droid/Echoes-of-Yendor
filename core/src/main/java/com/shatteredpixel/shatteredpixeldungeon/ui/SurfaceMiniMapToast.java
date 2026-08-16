/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.RegionMapSettings;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekMainStreetLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndRegionMap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Point;

import java.util.ArrayList;

/**
 * Fixed-north graphical knowledge-only minimap.
 * Unknown cells are never rendered. Unknown/off-screen objectives are direction hints,
 * never precise GPS markers. Clicking/tapping the map opens the expanded region map.
 * HUD scale is independent from Region Map zoom and is persisted between sessions.
 *
 * The HUD panel itself keeps a stable footprint. Scale changes alter how much world space
 * is visible inside that footprint instead of physically growing/shrinking the widget.
 */
public class SurfaceMiniMapToast extends Toast {

    private static final String PIXEL = "interfaces/echoes/minimap_pixel.png";
    private static final float DESKTOP_MAP_W = 66f;
    private static final float DESKTOP_MAP_H = 42f;
    private static final int HERO = 0xF2D36B, WATER = 0x4B93A0, WALL = 0x3E5940,
            ROAD = 0xA78B62, GRASS = 0x6F9954, EXIT = 0xE7D9A0, TARGET = 0xD64D9C;

    private static SurfaceMiniMapToast instance;
    private static String lastSignature;

    private final ArrayList<Image> pixels = new ArrayList<>();
    private final ArrayList<Point> offsets = new ArrayList<>();
    private final int hudZoom;
    private final int viewRX;
    private final int viewRY;
    private final float mapWidth;
    private final float mapHeight;
    private final float cell;
    private Button expand;
    private RedButton zoomButton;

    private SurfaceMiniMapToast() {
        super(headerText());
        hudZoom = RegionMapSettings.hudZoom();
        viewRX = radiusX(hudZoom);
        viewRY = radiusY(hudZoom);

        // Stable per-device footprint. Zoom only changes the world area represented inside it.
        mapWidth = Math.min(DESKTOP_MAP_W, Math.max(44f, PixelScene.uiCamera.width - 16f));
        mapHeight = mapWidth * DESKTOP_MAP_H / DESKTOP_MAP_W;
        cell = Math.min(mapWidth / (viewRX * 2 + 1), mapHeight / (viewRY * 2 + 1));

        close.visible = false;
        close.active = false;
        width = mapWidth + 8;
        height = mapHeight + 22;
        buildPixels();

        expand = new Button() {
            @Override protected void onClick() {
                GameScene.show(new WndRegionMap());
            }

            @Override protected String hoverText() {
                return "打开区域地图";
            }
        };
        add(expand);

        zoomButton = new RedButton(zoomLabel(hudZoom), 6) {
            @Override protected void onClick() {
                super.onClick();
                RegionMapSettings.hudZoom(hudZoom % 3 + 1);
                lastSignature = null;
                if (instance != null) {
                    instance.killAndErase();
                    instance = null;
                }
                sync();
            }
        };
        add(zoomButton);
    }

    public static void sync() {
        if (!(Game.scene() instanceof GameScene) || Dungeon.level == null || Dungeon.hero == null) return;
        String signature = signature();
        if (signature.equals(lastSignature) && instance != null && instance.exists && instance.parent == Game.scene()) return;
        lastSignature = signature;
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
        instance = new SurfaceMiniMapToast();
        instance.camera = PixelScene.uiCamera;
        instance.setPos(Math.max(1, PixelScene.uiCamera.width - instance.width() - 2), 38);
        Game.scene().addToFront(instance);
    }

    public static void dismissMiniMap() {
        lastSignature = null;
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
    }

    /**
     * 1x is the widest contextual view, 1.5x is the default, and 2x is the closest detail view.
     * All three occupy the same HUD footprint.
     */
    private static int radiusX(int level) {
        if (level <= 1) return 11; // 23 cells wide
        if (level == 2) return 7;  // 15 cells wide
        return 5;                  // 11 cells wide
    }

    private static int radiusY(int level) {
        if (level <= 1) return 7; // 15 cells tall
        if (level == 2) return 4; // 9 cells tall
        return 3;                 // 7 cells tall
    }

    private static String zoomLabel(int level) {
        if (level <= 1) return "1×";
        if (level == 2) return "1.5×";
        return "2×";
    }

    private void buildPixels() {
        Level level = Dungeon.level;
        int w = level.width(), h = level.height();
        int hx = Dungeon.hero.pos % w, hy = Dungeon.hero.pos / w;
        int objective = objectiveCell(level);
        boolean objectiveKnown = exactLocationKnown(level, objective);

        for (int dy = -viewRY; dy <= viewRY; dy++) {
            int y = hy + dy;
            for (int dx = -viewRX; dx <= viewRX; dx++) {
                int x = hx + dx;
                if (x < 0 || y < 0 || x >= w || y >= h) continue;
                int cellIndex = x + y * w;
                boolean fov = known(level.heroFOV, cellIndex);
                boolean visited = known(level.visited, cellIndex);
                boolean mapped = known(level.mapped, cellIndex);
                if (cellIndex != Dungeon.hero.pos && !fov && !visited && !mapped) continue;

                int color;
                if (cellIndex == Dungeon.hero.pos) color = HERO;
                else if (objectiveKnown && cellIndex == objective) color = TARGET;
                else if (level.map[cellIndex] == Terrain.WATER) color = WATER;
                else if (level.map[cellIndex] == Terrain.WALL) color = WALL;
                else if (level.map[cellIndex] == Terrain.ENTRANCE || level.map[cellIndex] == Terrain.EXIT) color = EXIT;
                else if (level.map[cellIndex] == Terrain.EMPTY || level.map[cellIndex] == Terrain.EMPTY_SP
                        || level.map[cellIndex] == Terrain.EMPTY_DECO) color = ROAD;
                else color = GRASS;

                Image px = new Image(PIXEL);
                px.hardlight(color);
                if (!fov && cellIndex != Dungeon.hero.pos) px.alpha(visited ? 0.68f : 0.42f);
                px.scale.set(cell, cell);
                pixels.add(px);
                offsets.add(new Point(dx, dy));
                add(px);
            }
        }
    }

    private static String headerText() {
        if (Dungeon.level == null || Dungeon.hero == null) return "N↑";
        Level level = Dungeon.level;
        int objective = objectiveCell(level);
        if (objective < 0) return "N↑";

        int w = level.width();
        int hx = Dungeon.hero.pos % w, hy = Dungeon.hero.pos / w;
        int tx = objective % w, ty = objective / w;
        int rx = radiusX(RegionMapSettings.hudZoom());
        int ry = radiusY(RegionMapSettings.hudZoom());
        boolean inLocalWindow = Math.abs(tx - hx) <= rx && Math.abs(ty - hy) <= ry;
        if (inLocalWindow && exactLocationKnown(level, objective)) return "N↑";

        String arrow = directionArrow(hx, hy, tx, ty);
        return arrow.isEmpty() ? "N↑" : "N↑  ◇" + arrow;
    }

    private static String directionArrow(int hx, int hy, int tx, int ty) {
        int dx = tx - hx, dy = ty - hy;
        if (Math.abs(dx) > Math.abs(dy) * 1.4f) return dx > 0 ? "→" : "←";
        if (Math.abs(dy) > Math.abs(dx) * 1.4f) return dy > 0 ? "↓" : "↑";
        if (dx > 0 && dy > 0) return "↘";
        if (dx > 0 && dy < 0) return "↗";
        if (dx < 0 && dy > 0) return "↙";
        if (dx < 0 && dy < 0) return "↖";
        return "";
    }

    private static boolean exactLocationKnown(Level level, int cellIndex) {
        if (cellIndex < 0) return false;
        return cellIndex == Dungeon.hero.pos || known(level.heroFOV, cellIndex) || known(level.visited, cellIndex);
    }

    private static boolean known(boolean[] values, int cellIndex) {
        return values != null && cellIndex >= 0 && cellIndex < values.length && values[cellIndex];
    }

    private static int objectiveCell(Level level) {
        if (level instanceof SurfaceEntranceLevel)
            return ((SurfaceEntranceLevel) level).cell(SurfaceEntranceLevel.NORTH_X, SurfaceEntranceLevel.NORTH_Y);
        if (level instanceof OldKingsRoadLevel)
            return ((OldKingsRoadLevel) level).cell(OldKingsRoadLevel.NORTH_X, OldKingsRoadLevel.NORTH_Y);
        if (level instanceof MorningcreekOutskirtsLevel)
            return ((MorningcreekOutskirtsLevel) level).cell(MorningcreekOutskirtsLevel.NORTH_X, MorningcreekOutskirtsLevel.NORTH_Y);
        if (level instanceof MorningcreekMainStreetLevel)
            return ((MorningcreekMainStreetLevel) level).cell(MorningcreekMainStreetLevel.INN_X, MorningcreekMainStreetLevel.INN_Y);
        if (level instanceof OldCrowInnLevel)
            return ((OldCrowInnLevel) level).cell(OldCrowInnLevel.LEDGER_X, OldCrowInnLevel.LEDGER_Y);
        return -1;
    }

    private static String signature() {
        Level level = Dungeon.level;
        int w = level.width(), h = level.height(), hx = Dungeon.hero.pos % w, hy = Dungeon.hero.pos / w;
        int zoom = RegionMapSettings.hudZoom();
        int rx = radiusX(zoom), ry = radiusY(zoom);
        StringBuilder s = new StringBuilder().append(Dungeon.hero.pos)
                .append(":z").append(zoom).append(':');
        for (int y = hy - ry; y <= hy + ry; y++) {
            for (int x = hx - rx; x <= hx + rx; x++) {
                if (x < 0 || y < 0 || x >= w || y >= h) {
                    s.append('x');
                    continue;
                }
                int c = x + y * w;
                if (known(level.heroFOV, c)) s.append('F');
                else if (known(level.visited, c)) s.append('V');
                else if (known(level.mapped, c)) s.append('M');
                else s.append('_');
                s.append((char) ('A' + Math.min(20, Math.max(0, level.map[c]))));
            }
        }
        return s.toString();
    }

    @Override
    protected void layout() {
        bg.x = x;
        bg.y = y;
        bg.size(width, height);
        text.setPos(x + 4, y + 2);

        float gridWidth = (viewRX * 2 + 1) * cell;
        float gridHeight = (viewRY * 2 + 1) * cell;
        float mapLeft = x + 4 + (mapWidth - gridWidth) / 2f;
        float mapTop = y + 10 + (mapHeight - gridHeight) / 2f;
        for (int i = 0; i < pixels.size(); i++) {
            Point p = offsets.get(i);
            pixels.get(i).x = mapLeft + (p.x + viewRX) * cell;
            pixels.get(i).y = mapTop + (p.y + viewRY) * cell;
        }
        if (expand != null) expand.setRect(x, y + 9, width, Math.max(1, height - 20));
        if (zoomButton != null) zoomButton.setRect(x + width - 30, y + height - 10, 28, 8);
    }

    @Override protected void onClose() {}

    @Override
    public void destroy() {
        if (instance == this) instance = null;
        super.destroy();
    }
}
