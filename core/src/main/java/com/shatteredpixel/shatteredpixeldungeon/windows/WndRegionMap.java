/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

/**
 * North-up explored-region map. It renders only knowledge already present in Level's
 * visited/mapped/FOV arrays, so it cannot reveal secret or unseen terrain.
 */
public class WndRegionMap extends Window {

    private static final int WIDTH = 220;
    private static final int MARGIN = 6;
    private static final int MAX_COLS = 36;
    private static final int MAX_ROWS = 22;

    public WndRegionMap() {
        super();

        RenderedTextBlock title = PixelScene.renderTextBlock("区域地图 · 北 ↑", 9);
        title.hardlight(TITLE_COLOR);
        title.setPos(MARGIN, MARGIN);
        add(title);

        String map = buildMap();
        RenderedTextBlock body = PixelScene.renderTextBlock(map, 6);
        body.maxWidth(WIDTH - MARGIN * 2);
        body.setPos(MARGIN, title.bottom() + 4);
        add(body);

        SequelState state = Dungeon.hero == null ? null : Dungeon.hero.buff(SequelState.class);
        String objective = state == null ? "" : state.objectiveText();
        RenderedTextBlock footer = PixelScene.renderTextBlock(
                "@ 你   ≈ 水   △ 出口   · 已探索/地图记录\n" + objective,
                6
        );
        footer.maxWidth(WIDTH - MARGIN * 2);
        footer.setPos(MARGIN, body.bottom() + 5);
        add(footer);

        resize(WIDTH, (int)footer.bottom() + MARGIN);
    }

    private String buildMap() {
        Level level = Dungeon.level;
        if (level == null || Dungeon.hero == null) return "暂无区域数据";

        int w = level.width();
        int h = level.height();
        int stepX = Math.max(1, (int)Math.ceil(w / (float)MAX_COLS));
        int stepY = Math.max(1, (int)Math.ceil(h / (float)MAX_ROWS));

        StringBuilder out = new StringBuilder();
        for (int y = 0; y < h; y += stepY) {
            for (int x = 0; x < w; x += stepX) {
                int cell = x + y * w;
                if (cell == Dungeon.hero.pos) {
                    out.append('@');
                    continue;
                }

                boolean inFov = level.heroFOV != null && cell < level.heroFOV.length && level.heroFOV[cell];
                boolean visited = level.visited != null && cell < level.visited.length && level.visited[cell];
                boolean mapped = level.mapped != null && cell < level.mapped.length && level.mapped[cell];
                if (!inFov && !visited && !mapped) {
                    out.append(' ');
                    continue;
                }

                int terrain = level.map[cell];
                if (terrain == Terrain.WATER) out.append('≈');
                else if (terrain == Terrain.ENTRANCE || terrain == Terrain.EXIT) out.append('△');
                else if (inFov) out.append('•');
                else if (visited) out.append('·');
                else out.append('˙');
            }
            out.append('\n');
        }
        return out.toString();
    }
}
