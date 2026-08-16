/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndRegionMap;
import com.watabou.noosa.Game;

/** Persistent non-modal current-objective display for the sequel campaign. */
public class TaskGuidanceToast extends Toast {

    private static final String BUILD_BADGE = "◆ Ch1XP 0.0.7 Art-v2  |  ";

    private static TaskGuidanceToast instance;
    private static String shownText;

    private TaskGuidanceToast(String text) {
        super(text);
        close.icon(Icons.get(Icons.MAGNIFY));
        close.visible = true;
        close.active = true;
    }

    public static void showObjective(String text) {
        if (!(Game.scene() instanceof GameScene)) return;

        SurfaceMiniMapToast.sync();
        if (text == null) text = "";
        String display = text.isEmpty() ? "" : BUILD_BADGE + text;

        if (display.equals(shownText)
                && instance != null
                && instance.exists
                && instance.parent == Game.scene()) {
            return;
        }

        shownText = display;
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
        if (display.isEmpty()) return;

        instance = new TaskGuidanceToast(display);
        instance.camera = PixelScene.uiCamera;
        instance.setPos(
                (PixelScene.uiCamera.width - instance.width()) / 2f,
                22
        );
        PixelScene.align(instance);
        Game.scene().addToFront(instance);
    }

    public static void dismissObjective() {
        shownText = null;
        SurfaceMiniMapToast.dismissMiniMap();
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
    }

    @Override
    protected void onClose() {
        GameScene.show(new WndRegionMap());
    }

    @Override
    public void destroy() {
        if (instance == this) instance = null;
        super.destroy();
    }
}
