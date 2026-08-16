/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
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
 * never precise GPS markers. Clicking/tapping the panel opens the expanded region map.
 */
public class SurfaceMiniMapToast extends Toast {

    private static final String PIXEL = "interfaces/echoes/minimap_pixel.png";
    private static final int RX = 5, RY = 3, CELL = 3, COLS = RX * 2 + 1, ROWS = RY * 2 + 1;
    private static final int HERO = 0xF2D36B, WATER = 0x4B93A0, WALL = 0x3E5940,
            ROAD = 0xA78B62, GRASS = 0x6F9954, EXIT = 0xE7D9A0, TARGET = 0xD64D9C;

    private static SurfaceMiniMapToast instance;
    private static String lastSignature;

    private final ArrayList<Image> pixels = new ArrayList<>();
    private final ArrayList<Point> offsets = new ArrayList<>();
    private Button expand;

    private SurfaceMiniMapToast() {
        super(headerText());
        close.visible = false;
        close.active = false;
        width = COLS * CELL + 8;
        height = ROWS * CELL + 13;
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

    private void buildPixels() {
        Level level = Dungeon.level;
        int w = level.width(), h = level.height();
        int hx = Dungeon.hero.pos % w, hy = Dungeon.hero.pos / w;
        int objective = objectiveCell(level);
        boolean objectiveKnown = exactLocationKnown(level, objective);

        for (int gy = 0; gy < ROWS; gy++) {
            int y = hy - RY + gy;
            for (int gx = 0; gx < COLS; gx++) {
                int x = hx - RX + gx;
                if (x < 0 || y < 0 || x >= w || y >= h) continue;
                int cell = x + y * w;
                boolean fov = known(level.heroFOV, cell);
                boolean visited = known(level.visited, cell);
                boolean mapped = known(level.mapped, cell);
                if (cell != Dungeon.hero.pos && !fov && !visited && !mapped) continue;

                int color;
                if (cell == Dungeon.hero.pos) color = HERO;
                else if (objectiveKnown && cell == objective) color = TARGET;
                else if (level.map[cell] == Terrain.WATER) color = WATER;
                else if (level.map[cell] == Terrain.WALL) color = WALL;
                else if (level.map[cell] == Terrain.ENTRANCE || level.map[cell] == Terrain.EXIT) color = EXIT;
                else if (level.map[cell] == Terrain.EMPTY || level.map[cell] == Terrain.EMPTY_SP
                        || level.map[cell] == Terrain.EMPTY_DECO) color = ROAD;
                else color = GRASS;

                Image px = new Image(PIXEL);
                px.hardlight(color);
                if (!fov && cell != Dungeon.hero.pos) px.alpha(visited ? 0.68f : 0.42f);
                px.scale.set(CELL, CELL);
                pixels.add(px);
                offsets.add(new Point(gx, gy));
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
        boolean inLocalWindow = Math.abs(tx - hx) <= RX && Math.abs(ty - hy) <= RY;
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

    private static boolean exactLocationKnown(Level level, int cell) {
        if (cell < 0) return false;
        return cell == Dungeon.hero.pos || known(level.heroFOV, cell) || known(level.visited, cell);
    }

    private static boolean known(boolean[] values, int cell) {
        return values != null && cell >= 0 && cell < values.length && values[cell];
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
        StringBuilder s = new StringBuilder().append(Dungeon.hero.pos).append(':');
        for (int y = hy - RY; y <= hy + RY; y++) {
            for (int x = hx - RX; x <= hx + RX; x++) {
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
        for (int i = 0; i < pixels.size(); i++) {
            Point p = offsets.get(i);
            pixels.get(i).x = x + 4 + p.x * CELL;
            pixels.get(i).y = y + 10 + p.y * CELL;
        }
        if (expand != null) expand.setRect(x, y, width, height);
    }

    @Override protected void onClose() {}

    @Override
    public void destroy() {
        if (instance == this) instance = null;
        super.destroy();
    }
}
