/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.RegionMapSettings;
import com.shatteredpixel.shatteredpixeldungeon.RegionPoi;
import com.shatteredpixel.shatteredpixeldungeon.RegionState;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegionAreaLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

/**
 * Lightweight Area Map renderer for formal semi-open RegionAreaLevel maps.
 *
 * The legacy WndRegionMap creates one Image for every known terrain cell. That is fine for
 * the original small authored maps, but a 96x72 hub can approach seven thousand UI nodes
 * after exploration. This renderer preserves the same knowledge rules while batching each
 * horizontal run of identical terrain/visibility into one Image.
 */
public class WndRegionAreaMap extends Window {

    private static final String PIXEL = "interfaces/echoes/minimap_pixel.png";
    private static final int WIDTH = 220, HEIGHT = 194, MARGIN = 8;
    private static final int VIEW_X = 10, VIEW_Y = 27, VIEW_W = 200, VIEW_H = 112;
    private static final int HERO = 0xF2D36B, WATER = 0x4B93A0, WALL = 0x3E5940,
            ROAD = 0xA78B62, GRASS = 0x6F9954, EXIT = 0xE7D9A0,
            LANDMARK = 0x65C7C0, RUMOR = 0xC8A66B;

    private static Float pendingCenterCellX;
    private static Float pendingCenterCellY;

    private final int zoom;
    private MapScrollPane mapPane;

    public WndRegionAreaMap() {
        super();
        zoom = RegionMapSettings.zoom();
        resize(WIDTH, HEIGHT);

        Level level = Dungeon.level;
        if (!(level instanceof RegionAreaLevel) || Dungeon.hero == null) {
            RenderedTextBlock none = PixelScene.renderTextBlock("暂无正式区域数据", 7);
            none.setPos(MARGIN, 32);
            add(none);
            return;
        }

        RenderedTextBlock title = PixelScene.renderTextBlock(
                "区域地图 · " + ((RegionAreaLevel) level).regionAreaName() + "   N ↑", 9);
        title.hardlight(TITLE_COLOR);
        title.setPos(MARGIN, MARGIN);
        add(title);

        drawMap(level);
        buildZoomControls();

        RegionState region = RegionState.current();
        String objective = region == null ? "" : region.objectiveText();
        RenderedTextBlock legend = PixelScene.renderTextBlock(
                "金：你   青：已发现   黄：听说的大致区域\n" + objective, 6);
        legend.maxWidth(WIDTH - MARGIN * 2);
        legend.setPos(MARGIN, 166);
        add(legend);
    }

    private void drawMap(Level level) {
        int w = level.width(), h = level.height();
        int fit = Math.max(1, Math.min(3,
                Math.min(VIEW_W / Math.max(1, w), VIEW_H / Math.max(1, h))));
        int cell = Math.max(1, fit * zoom);
        int mapW = w * cell;
        int mapH = h * cell;
        int contentW = Math.max(VIEW_W, mapW);
        int contentH = Math.max(VIEW_H, mapH);
        float mapOriginX = (contentW - mapW) / 2f;
        float mapOriginY = (contentH - mapH) / 2f;

        MapContent content = new MapContent(contentW, contentH);

        for (int y = 0; y < h; y++) {
            int runStart = -1;
            int runColor = 0;
            float runAlpha = 1f;

            for (int x = 0; x <= w; x++) {
                boolean knownCell = false;
                int color = 0;
                float alpha = 1f;

                if (x < w) {
                    int c = x + y * w;
                    boolean fov = known(level.heroFOV, c);
                    boolean visited = known(level.visited, c);
                    boolean mapped = known(level.mapped, c);
                    knownCell = c == Dungeon.hero.pos || fov || visited || mapped;
                    if (knownCell) {
                        color = terrainColor(level, c);
                        alpha = (!fov && c != Dungeon.hero.pos) ? (visited ? 0.68f : 0.40f) : 1f;
                    }
                }

                if (knownCell && runStart >= 0 && color == runColor && alpha == runAlpha) {
                    continue;
                }

                if (runStart >= 0) {
                    addRun(content, mapOriginX, mapOriginY, cell, y,
                            runStart, x - 1, runColor, runAlpha);
                    runStart = -1;
                }

                if (knownCell) {
                    runStart = x;
                    runColor = color;
                    runAlpha = alpha;
                }
            }
        }

        ArrayList<Landmark> landmarks = discoveredLandmarks(level);
        for (Landmark landmark : landmarks) {
            drawMarker(content, mapOriginX, mapOriginY, cell, landmark,
                    landmark.approximate ? RUMOR : LANDMARK);
        }

        mapPane = new MapScrollPane(content, landmarks, mapOriginX, mapOriginY, cell, w, h);
        add(mapPane);
        mapPane.setRect(VIEW_X, VIEW_Y, VIEW_W, VIEW_H);

        float centerX;
        float centerY;
        if (pendingCenterCellX != null && pendingCenterCellY != null) {
            centerX = pendingCenterCellX;
            centerY = pendingCenterCellY;
            pendingCenterCellX = null;
            pendingCenterCellY = null;
        } else {
            centerX = Dungeon.hero.pos % w;
            centerY = Dungeon.hero.pos / w;
        }
        mapPane.centerOnCell(centerX, centerY);
    }

    private static int terrainColor(Level level, int c) {
        if (c == Dungeon.hero.pos) return HERO;
        if (level.map[c] == Terrain.WATER) return WATER;
        if (level.map[c] == Terrain.WALL) return WALL;
        if (level.map[c] == Terrain.ENTRANCE || level.map[c] == Terrain.EXIT) return EXIT;
        if (level.map[c] == Terrain.EMPTY || level.map[c] == Terrain.EMPTY_SP
                || level.map[c] == Terrain.EMPTY_DECO) return ROAD;
        return GRASS;
    }

    private static void addRun(Component content, float originX, float originY, int cell, int y,
                               int startX, int endX, int color, float alpha) {
        Image run = new Image(PIXEL);
        run.hardlight(color);
        run.scale.set(Math.max(1, endX - startX + 1) * cell, cell);
        if (alpha < 1f) run.alpha(alpha);
        run.x = originX + startX * cell;
        run.y = originY + y * cell;
        content.add(run);
    }

    private void buildZoomControls() {
        RedButton minus = new RedButton("－", 8) {
            @Override protected void onClick() {
                super.onClick();
                setZoom(zoom - 1);
            }
        };
        minus.setRect(69, 143, 28, 18);
        minus.enable(zoom > 1);
        add(minus);

        RenderedTextBlock scale = PixelScene.renderTextBlock(zoom + "×", 8);
        scale.hardlight(TITLE_COLOR);
        scale.setPos((WIDTH - scale.width()) / 2f, 148);
        add(scale);

        RedButton plus = new RedButton("+", 8) {
            @Override protected void onClick() {
                super.onClick();
                setZoom(zoom + 1);
            }
        };
        plus.setRect(123, 143, 28, 18);
        plus.enable(zoom < 4);
        add(plus);
    }

    private void setZoom(int value) {
        int next = Math.max(1, Math.min(4, value));
        if (next == zoom) return;
        if (mapPane != null) {
            pendingCenterCellX = mapPane.centerCellX();
            pendingCenterCellY = mapPane.centerCellY();
        }
        RegionMapSettings.zoom(next);
        hide();
        GameScene.show(new WndRegionAreaMap());
    }

    private static ArrayList<Landmark> discoveredLandmarks(Level level) {
        ArrayList<Landmark> result = new ArrayList<>();
        RegionState region = RegionState.current();
        if (region == null || !(level instanceof RegionAreaLevel)) return result;

        RegionPoi[] pois = ((RegionAreaLevel) level).regionPois();
        if (pois == null) return result;

        for (RegionPoi poi : pois) {
            if (poi == null || poi.location == null) continue;
            RegionState.Knowledge knowledge = region.knowledge(poi.location);
            if (knowledge == RegionState.Knowledge.UNKNOWN) continue;
            if (knowledge == RegionState.Knowledge.HEARD_OF) {
                add(result, poi.heardX, poi.heardY, "听说 · " + poi.name,
                        categoryText(poi.category) + "\n" + poi.heardDescription
                                + "\n\n位置尚未确认：地图只显示大致区域。", true);
            } else {
                add(result, poi.x, poi.y, poi.name,
                        categoryText(poi.category) + "\n" + poi.discoveredDescription, false);
            }
        }
        return result;
    }

    private static String categoryText(RegionPoi.Category category) {
        if (category == null) return "地点";
        switch (category) {
            case TRAVEL: return "旅行节点";
            case SERVICE: return "服务地点";
            case INVESTIGATION: return "调查地点";
            case SHORTCUT: return "永久捷径";
            case CIVIC: return "公共机构";
            case AMBIENT: return "生活地点";
            case LANDMARK:
            default: return "地标";
        }
    }

    private static void add(ArrayList<Landmark> result, int x, int y,
                            String name, String description, boolean approximate) {
        result.add(new Landmark(x, y, name, description, approximate));
    }

    private static void drawMarker(Component content, float originX, float originY, int cell,
                                   Landmark landmark, int color) {
        float cx = originX + (landmark.cellX + 0.5f) * cell;
        float cy = originY + (landmark.cellY + 0.5f) * cell;
        float arm = Math.max(1f, cell * 0.34f);
        float core = Math.max(1f, cell * 0.46f);

        if (landmark.approximate) {
            addPixel(content, cx - core / 2f, cy - core / 2f, core, core, color);
            addPixel(content, cx - core * 1.5f, cy - arm / 2f, core, arm, color);
            addPixel(content, cx + core * 0.5f, cy - arm / 2f, core, arm, color);
            return;
        }

        addPixel(content, cx - core / 2f, cy - core / 2f, core, core, color);
        addPixel(content, cx - arm / 2f, cy - core / 2f - arm, arm, arm, color);
        addPixel(content, cx - arm / 2f, cy + core / 2f, arm, arm, color);
        addPixel(content, cx - core / 2f - arm, cy - arm / 2f, arm, arm, color);
        addPixel(content, cx + core / 2f, cy - arm / 2f, arm, arm, color);
    }

    private static void addPixel(Component content, float x, float y, float w, float h, int color) {
        Image px = new Image(PIXEL);
        px.hardlight(color);
        px.scale.set(Math.max(1f, w), Math.max(1f, h));
        px.x = x;
        px.y = y;
        content.add(px);
    }

    private static boolean known(boolean[] values, int cell) {
        return values != null && cell >= 0 && cell < values.length && values[cell];
    }

    private static final class Landmark {
        final int cellX;
        final int cellY;
        final String name;
        final String description;
        final boolean approximate;

        Landmark(int cellX, int cellY, String name, String description, boolean approximate) {
            this.cellX = cellX;
            this.cellY = cellY;
            this.name = name;
            this.description = description;
            this.approximate = approximate;
        }
    }

    private static final class MapContent extends Component {
        MapContent(float width, float height) {
            super();
            setSize(width, height);
        }
    }

    private static final class MapScrollPane extends ScrollPane {
        private final ArrayList<Landmark> landmarks;
        private final float mapOriginX;
        private final float mapOriginY;
        private final int cell;
        private final int mapWidthCells;
        private final int mapHeightCells;

        MapScrollPane(Component content, ArrayList<Landmark> landmarks,
                      float mapOriginX, float mapOriginY, int cell,
                      int mapWidthCells, int mapHeightCells) {
            super(content);
            this.landmarks = landmarks;
            this.mapOriginX = mapOriginX;
            this.mapOriginY = mapOriginY;
            this.cell = cell;
            this.mapWidthCells = mapWidthCells;
            this.mapHeightCells = mapHeightCells;
        }

        void centerOnCell(float cellX, float cellY) {
            float clampedX = Math.max(0f, Math.min(mapWidthCells - 1f, cellX));
            float clampedY = Math.max(0f, Math.min(mapHeightCells - 1f, cellY));
            float px = mapOriginX + (clampedX + 0.5f) * cell - width / 2f;
            float py = mapOriginY + (clampedY + 0.5f) * cell - height / 2f;
            scrollTo(px, py);
        }

        float centerCellX() {
            float center = content.camera.scroll.x + width / 2f;
            return Math.max(0f, Math.min(mapWidthCells - 1f,
                    (center - mapOriginX) / cell - 0.5f));
        }

        float centerCellY() {
            float center = content.camera.scroll.y + height / 2f;
            return Math.max(0f, Math.min(mapHeightCells - 1f,
                    (center - mapOriginY) / cell - 0.5f));
        }

        @Override
        public void onClick(float x, float y) {
            float radius = Math.max(4f, cell * 0.9f);
            for (Landmark landmark : landmarks) {
                float lx = mapOriginX + (landmark.cellX + 0.5f) * cell;
                float ly = mapOriginY + (landmark.cellY + 0.5f) * cell;
                float dx = x - lx, dy = y - ly;
                if (dx * dx + dy * dy <= radius * radius) {
                    GameScene.show(new WndMessage(landmark.name + "\n\n" + landmark.description));
                    return;
                }
            }
        }
    }
}
