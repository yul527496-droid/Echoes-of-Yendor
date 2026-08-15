/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;

/**
 * Persistent, non-modal objective text for the training memory. It deliberately
 * has no close affordance: the controller replaces it only when the real game
 * state advances to the next lesson.
 */
public class TrainingObjectiveToast extends Toast {

    private static TrainingObjectiveToast instance;

    private TrainingObjectiveToast(String text) {
        super(text);
        close.visible = false;
        close.active = false;
    }

    public static void show(String text) {
        if (instance != null) {
            instance.killAndErase();
            instance = null;
        }

        if (text == null || text.isEmpty() || Game.scene() == null) return;

        instance = new TrainingObjectiveToast(text);
        instance.camera = PixelScene.uiCamera;
        instance.setPos(
                (PixelScene.uiCamera.width - instance.width()) / 2f,
                PixelScene.uiCamera.height - instance.height() - 48
        );
        PixelScene.align(instance);
        Game.scene().addToFront(instance);
    }

    @Override
    protected void onClose() {
        // Objectives disappear only when the tutorial stage changes.
    }

    @Override
    public void destroy() {
        if (instance == this) instance = null;
        super.destroy();
    }
}
