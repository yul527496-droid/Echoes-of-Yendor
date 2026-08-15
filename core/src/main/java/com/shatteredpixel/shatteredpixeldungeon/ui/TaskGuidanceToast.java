/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndRegionMap;
import com.watabou.noosa.Game;

/** Persistent non-modal current-objective display for the sequel campaign. */
public class TaskGuidanceToast extends Toast {

    private static TaskGuidanceToast instance;
    private static String shownText;

    private TaskGuidanceToast(String text) {
        super(text);
        close.icon(Icons.get(Icons.MAGNIFY));
        close.visible = true;
        close.active = true;
    }

    public static void showObjective(String text) {
        if (text == null) text = "";
        if (text.equals(shownText) && instance != null) return;

        shownText = text;
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
        if (text.isEmpty() || Game.scene() == null) return;

        instance = new TaskGuidanceToast(text);
        instance.camera = PixelScene.uiCamera;
        instance.setPos(
                (PixelScene.uiCamera.width - instance.width()) / 2f,
                PixelScene.uiCamera.height - instance.height() - 48
        );
        PixelScene.align(instance);
        Game.scene().addToFront(instance);
    }

    public static void clear() {
        shownText = null;
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }
    }

    @Override
    protected void onClose() {
        // The right-side icon is intentionally a map button, not a close affordance.
        GameScene.show(new WndRegionMap());
    }

    @Override
    public void destroy() {
        if (instance == this) instance = null;
        super.destroy();
    }
}
