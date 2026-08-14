package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.RectF;

/** Small in-book transitions. They never fade the entire screen to black. */
final class LedgerTransitions {

    private LedgerTransitions() {}

    static void turn(PixelScene scene, RectF paper,
                     Class<? extends PixelScene> destination, boolean forward) {

        // Tweener inherits a Gizmo field named "target". Capturing a method
        // parameter with the same name inside the anonymous Tweener makes the
        // simple name resolve to that Gizmo instead of the scene class.
        final Class<? extends PixelScene> destinationScene = destination;

        final ColorBlock fold = new ColorBlock(4f, paper.height(), 0xFFFFE1A1);
        final ColorBlock shadow = new ColorBlock(3f, paper.height(), 0xFF3A2415);
        final ColorBlock warmEdge = new ColorBlock(2f, paper.height(), 0xFFD8A15D);

        fold.y = shadow.y = warmEdge.y = paper.top;
        fold.alpha(0f);
        shadow.alpha(0f);
        warmEdge.alpha(0f);

        scene.add(shadow);
        scene.add(warmEdge);
        scene.add(fold);

        scene.add(new Tweener(scene, 0.34f) {
            @Override
            protected void updateValues(float progress) {
                float p = progress * progress * (3f - 2f * progress);
                float wave = (float)Math.sin(Math.PI * progress);

                float outer = forward ? paper.right : paper.left;
                float inner = forward ? paper.left : paper.right;
                float x = outer + (inner - outer) * p;
                float foldW = 3.5f + wave * 7.5f;

                if (forward) {
                    fold.x = x - foldW;
                    warmEdge.x = fold.x - 1f;
                    shadow.x = fold.x - 3f;
                } else {
                    fold.x = x;
                    warmEdge.x = fold.x + foldW - 1f;
                    shadow.x = fold.x + foldW;
                }

                fold.size(foldW, paper.height());
                fold.alpha(0.50f + wave * 0.32f);
                warmEdge.alpha(wave * 0.36f);
                shadow.alpha(wave * 0.30f);
            }

            @Override
            protected void onComplete() {
                // Every ledger scene calls fadeIn(); suppress that one fade so
                // the moving page edge is the transition the player actually sees.
                PixelScene.noFade = true;
                Game.switchScene(destinationScene);
            }
        });
    }
}
