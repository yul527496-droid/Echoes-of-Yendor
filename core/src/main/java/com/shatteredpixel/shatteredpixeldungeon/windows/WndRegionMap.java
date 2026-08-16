/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.RegionMapSettings;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekMainStreetLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
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
 * North-up explored-region map.
 *
 * Terrain is knowledge-only: FOV is bright, visited is normal, mapped-only is dim,
 * and unknown cells are never rendered. Exact objective/landmark markers are also
 * withheld until the relevant location has actually been seen or learned by story.
 * The scroll pane provides mouse/touch panning at enlarged zoom levels.
 */
public class WndRegionMap extends Window {

    private static final String PIXEL = "interfaces/echoes/minimap_pixel.png";
    private static final int WIDTH = 220, HEIGHT = 194, MARGIN = 8;
    private static final int VIEW_X = 10, VIEW_Y = 27, VIEW_W = 200, VIEW_H = 112;
    private static final int HERO = 0xF2D36B, WATER = 0x4B93A0, WALL = 0x3E5940,
            ROAD = 0xA78B62, GRASS = 0x6F9954, EXIT = 0xE7D9A0,
            TARGET = 0xD64D9C, LANDMARK = 0x65C7C0;

    // Used only while changing zoom so the map stays on the player's current observation center.
    private static Float pendingCenterCellX;
    private static Float pendingCenterCellY;

    private final int zoom;
    private MapScrollPane mapPane;

    public WndRegionMap() {
        super();
        zoom = RegionMapSettings.zoom();
        resize(WIDTH, HEIGHT);

        RenderedTextBlock title = PixelScene.renderTextBlock("区域地图   N ↑", 9);
        title.hardlight(TITLE_COLOR);
        title.setPos(MARGIN, MARGIN);
        add(title);

        Level level = Dungeon.level;
        if (level == null || Dungeon.hero == null) {
            RenderedTextBlock none = PixelScene.renderTextBlock("暂无区域数据", 7);
            none.setPos(MARGIN, 32);
            add(none);
            return;
        }

        drawMap(level);
        buildZoomControls();

        SequelState state = Dungeon.hero.buff(SequelState.class);
        String objectiveText = state == null ? "" : state.objectiveText();
        RenderedTextBlock legend = PixelScene.renderTextBlock(
                "金：你   紫：已确认目标   青：已发现地标\n" + objectiveText, 6);
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
        int objective = objectiveCell(level);
        boolean objectiveKnown = exactLocationKnown(level, objective);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = x + y * w;
                boolean fov = known(level.heroFOV, c);
                boolean visited = known(level.visited, c);
                boolean mapped = known(level.mapped, c);
                if (c != Dungeon.hero.pos && !fov && !visited && !mapped) continue;

                int color;
                if (c == Dungeon.hero.pos) color = HERO;
                else if (objectiveKnown && c == objective) color = TARGET;
                else if (level.map[c] == Terrain.WATER) color = WATER;
                else if (level.map[c] == Terrain.WALL) color = WALL;
                else if (level.map[c] == Terrain.ENTRANCE || level.map[c] == Terrain.EXIT) color = EXIT;
                else if (level.map[c] == Terrain.EMPTY || level.map[c] == Terrain.EMPTY_SP
                        || level.map[c] == Terrain.EMPTY_DECO) color = ROAD;
                else color = GRASS;

                Image px = new Image(PIXEL);
                px.hardlight(color);
                px.scale.set(cell, cell);
                if (!fov && c != Dungeon.hero.pos) px.alpha(visited ? 0.68f : 0.40f);
                px.x = mapOriginX + x * cell;
                px.y = mapOriginY + y * cell;
                content.add(px);
            }
        }

        ArrayList<Landmark> landmarks = discoveredLandmarks(level);
        for (Landmark landmark : landmarks) {
            drawMarker(content, mapOriginX, mapOriginY, cell, landmark, LANDMARK);
        }

        mapPane = new MapScrollPane(content, landmarks, mapOriginX, mapOriginY, cell, w, h);
        mapPane.setRect(VIEW_X, VIEW_Y, VIEW_W, VIEW_H);
        add(mapPane);

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

        if (!objectiveKnown && objective >= 0) {
            String direction = objectiveDirection(level, objective);
            if (!direction.isEmpty()) {
                RenderedTextBlock hint = PixelScene.renderTextBlock("◇ " + direction, 7);
                hint.hardlight(TARGET);
                hint.setPos(VIEW_X + VIEW_W - hint.width() - 4, VIEW_Y + 3);
                add(hint);
            }
        }
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
        GameScene.show(new WndRegionMap());
    }

    private static ArrayList<Landmark> discoveredLandmarks(Level level) {
        ArrayList<Landmark> result = new ArrayList<>();
        SequelState state = Dungeon.hero == null ? null : Dungeon.hero.buff(SequelState.class);

        if (level instanceof SurfaceEntranceLevel) {
            if (state != null && state.campVisited) {
                add(result, 12, 27, "废弃营地", "林间的旧远征营地。你已经确认这里有人停留过。" );
            }
            if (exactLocationKnown(level, ((SurfaceEntranceLevel) level).cell(30, 6))) {
                add(result, 30, 6, "北行路牌", "沿这条路继续向北，可进入旧王道。" );
            }
        } else if (level instanceof OldKingsRoadLevel) {
            OldKingsRoadLevel road = (OldKingsRoadLevel) level;
            if (state != null && state.shrineRead) {
                add(result, OldKingsRoadLevel.SHRINE_X, OldKingsRoadLevel.SHRINE_Y,
                        "旧路神龛", "苔藓覆盖的旧神龛，仍留着模糊的归路祝词。" );
            }
            if (state != null && state.wagonInspected) {
                add(result, OldKingsRoadLevel.WAGON_X, OldKingsRoadLevel.WAGON_Y,
                        "坏车", "废弃在路旁的坏车，没有血迹，也没有可取之物。" );
            }
            int sign = road.cell(31, 7);
            if (exactLocationKnown(level, sign)) {
                add(result, 31, 7, "旧王道路牌", "晨溪在北面。道路越过这里后开始出现人烟。" );
            }
        } else if (level instanceof MorningcreekOutskirtsLevel) {
            MorningcreekOutskirtsLevel outskirts = (MorningcreekOutskirtsLevel) level;
            if (state != null && state.missingNoticeRead) {
                add(result, MorningcreekOutskirtsLevel.NOTICE_X, MorningcreekOutskirtsLevel.NOTICE_Y,
                        "公告板", "这里贴着莱斯·赫恩的寻人告示，也标出了进镇方向。" );
            }
            int westFarm = outskirts.cell(10, 30);
            int eastFarm = outskirts.cell(41, 31);
            if (exactLocationKnown(level, westFarm)) {
                add(result, 10, 30, "西侧农舍", "水渠旁的农舍和田地，是晨溪外围最明显的生活地标之一。" );
            }
            if (exactLocationKnown(level, eastFarm)) {
                add(result, 41, 31, "东侧农舍", "农舍、谷物和篱笆把这里与旧王道的荒野彻底区分开。" );
            }
        } else if (level instanceof MorningcreekMainStreetLevel) {
            MorningcreekMainStreetLevel town = (MorningcreekMainStreetLevel) level;
            if (exactLocationKnown(level, town.cell(23, 33))) {
                add(result, 24, 34, "晨溪镇门", "从南面进入晨溪主街的镇门。" );
            }
            if (exactLocationKnown(level, town.cell(26, 20))) {
                add(result, 26, 20, "水井", "主街中央的公共水井，是居民指路时最常用的参照物。" );
            }
            if (exactLocationKnown(level, town.cell(8, 10))) {
                add(result, 8, 10, "铁匠", "锤声从主街西侧传来。" );
            }
            if (state != null && state.investigationKnown
                    && exactLocationKnown(level, town.cell(30, 7))) {
                add(result, 27, 7, "老鸦旅店", "黑乌鸦招牌下面就是老鸦旅店。" );
            }
        } else if (level instanceof OldCrowInnLevel) {
            OldCrowInnLevel inn = (OldCrowInnLevel) level;
            if (exactLocationKnown(level, inn.cell(7, 10))) {
                add(result, 7, 10, "壁炉", "旅店里持续燃烧的壁炉。" );
            }
            if (exactLocationKnown(level, inn.cell(18, 8))) {
                add(result, 18, 8, "吧台", "老板娘守着吧台，也守着这里留下的许多故事。" );
            }
            if (exactLocationKnown(level, inn.cell(OldCrowInnLevel.LEDGER_X, OldCrowInnLevel.LEDGER_Y))) {
                add(result, OldCrowInnLevel.LEDGER_X, OldCrowInnLevel.LEDGER_Y,
                        "下行者名册", "记录几十年来南下地下城之人的名册。" );
            }
        }
        return result;
    }

    private static void add(ArrayList<Landmark> result, int x, int y, String name, String description) {
        result.add(new Landmark(x, y, name, description));
    }

    private static void drawMarker(Component content, float originX, float originY, int cell,
                                   Landmark landmark, int color) {
        float cx = originX + (landmark.cellX + 0.5f) * cell;
        float cy = originY + (landmark.cellY + 0.5f) * cell;
        float arm = Math.max(1f, cell * 0.34f);
        float core = Math.max(1f, cell * 0.46f);

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

    private static String objectiveDirection(Level level, int objective) {
        if (objective < 0 || Dungeon.hero == null) return "";
        int w = level.width();
        int hx = Dungeon.hero.pos % w, hy = Dungeon.hero.pos / w;
        int tx = objective % w, ty = objective / w;
        int dx = tx - hx, dy = ty - hy;
        if (Math.abs(dx) > Math.abs(dy) * 1.4f) return dx > 0 ? "→" : "←";
        if (Math.abs(dy) > Math.abs(dx) * 1.4f) return dy > 0 ? "↓" : "↑";
        if (dx > 0 && dy > 0) return "↘";
        if (dx > 0 && dy < 0) return "↗";
        if (dx < 0 && dy > 0) return "↙";
        if (dx < 0 && dy < 0) return "↖";
        return "";
    }

    private static final class Landmark {
        final int cellX;
        final int cellY;
        final String name;
        final String description;

        Landmark(int cellX, int cellY, String name, String description) {
            this.cellX = cellX;
            this.cellY = cellY;
            this.name = name;
            this.description = description;
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
