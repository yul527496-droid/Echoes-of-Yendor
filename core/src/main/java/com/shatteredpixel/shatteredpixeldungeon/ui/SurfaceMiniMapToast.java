/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;

/**
 * Small north-up knowledge-only minimap for the sequel surface chapter.
 * It reads Level's exploration arrays directly; unknown cells never render.
 */
public class SurfaceMiniMapToast extends Toast {

    private static SurfaceMiniMapToast instance;
    private static String lastMap;

    private SurfaceMiniMapToast(String text) {
        super(text);
        close.visible = false;
        close.active = false;
    }

    public static void sync() {
        if (!(Game.scene() instanceof GameScene)
                || Dungeon.level == null
                || Dungeon.hero == null) return;

        String text = buildLocalMap();
        if (text.equals(lastMap)
                && instance != null
                && instance.exists
                && instance.parent == Game.scene()) {
            return;
        }
        lastMap = text;

        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }

        instance = new SurfaceMiniMapToast(text);
        instance.camera = PixelScene.uiCamera;
        instance.setPos(
                Math.max(1, PixelScene.uiCamera.width - instance.width() - 2),
                40
        );
        PixelScene.align(instance);
        Game.scene().addToFront(instance);
    }

    public static void dismissMiniMap() {
        lastMap = null;
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
    }

    private static String buildLocalMap() {
        Level level = Dungeon.level;
        int w = level.width();
        int h = level.height();
        int heroX = Dungeon.hero.pos % w;
        int heroY = Dungeon.hero.pos / w;
        int rx = 5;
        int ry = 3;

        StringBuilder out = new StringBuilder("[区域地图] N↑\n");
        for (int y = heroY - ry; y <= heroY + ry; y++) {
            out.append('│');
            for (int x = heroX - rx; x <= heroX + rx; x++) {
                if (x < 0 || y < 0 || x >= w || y >= h) {
                    out.append(' ');
                    continue;
                }
                int cell = x + y * w;
                if (cell == Dungeon.hero.pos) {
                    out.append('@');
                    continue;
                }
                boolean fov = level.heroFOV != null && cell < level.heroFOV.length && level.heroFOV[cell];
                boolean visited = level.visited != null && cell < level.visited.length && level.visited[cell];
                boolean mapped = level.mapped != null && cell < level.mapped.length && level.mapped[cell];
                if (!fov && !visited && !mapped) {
                    out.append(' ');
                    continue;
                }
                int terrain = level.map[cell];
                if (terrain == Terrain.WATER) out.append('≈');
                else if (terrain == Terrain.ENTRANCE || terrain == Terrain.EXIT) out.append('△');
                else if (terrain == Terrain.WALL) out.append('■');
                else if (fov) out.append('•');
                else if (visited) out.append('·');
                else out.append('˙');
            }
            out.append('│');
            if (y < heroY + ry) out.append('\n');
        }
        return out.toString();
    }

    @Override
    protected void onClose() {
    }

    @Override
    public void destroy() {
        if (instance == this) instance = null;
        super.destroy();
    }
}
