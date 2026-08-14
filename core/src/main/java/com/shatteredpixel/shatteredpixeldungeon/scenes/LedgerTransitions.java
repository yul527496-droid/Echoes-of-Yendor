package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.RectF;

/**
 * Two-stage in-book page turn.
 *
 * The outgoing scene is first covered by a moving parchment face; after the
 * scene switch the same page face retracts to reveal the destination. This
 * avoids the old "three coloured bars then teleport" look without requiring
 * framebuffer capture in the first vertical slice.
 */
final class LedgerTransitions {

    private static int pendingSide; // -1 left page, +1 right page
    private static boolean pendingForward;

    private LedgerTransitions() {}

    static void turn(PixelScene scene, RectF paper,
                     Class<? extends PixelScene> destination, boolean forward) {
        final Class<? extends PixelScene> destinationScene = destination;
        pendingSide = (paper.left + paper.right) * 0.5f < com.watabou.noosa.Camera.main.width * 0.5f
                ? -1 : 1;
        pendingForward = forward;

        LedgerAudio.pageTurn();
        animate(scene, paper, forward, true, new Runnable() {
            @Override public void run() {
                PixelScene.noFade = true;
                Game.switchScene(destinationScene);
            }
        });
    }

    /** Call after destination content has been added, immediately before fadeIn(). */
    static void revealIfPending(PixelScene scene, RectF leftPaper, RectF rightPaper) {
        if (pendingSide == 0) return;
        RectF paper = pendingSide < 0 ? leftPaper : rightPaper;
        boolean forward = pendingForward;
        pendingSide = 0;
        animate(scene, paper, forward, false, null);
    }

    private static void animate(PixelScene scene, RectF paper, boolean forward,
                                boolean covering, Runnable onComplete) {
        final ColorBlock dim = new ColorBlock(paper.width(), paper.height(), 0xFF5B3A20);
        final ColorBlock page = new ColorBlock(1f, paper.height(), 0xFFF0CF8C);
        final ColorBlock fold = new ColorBlock(2f, paper.height(), 0xFFFFE4AC);
        final ColorBlock warm = new ColorBlock(1f, paper.height(), 0xFFD6A15D);
        final ColorBlock shadow = new ColorBlock(4f, paper.height(), 0xFF2D1B10);

        dim.x = page.x = fold.x = warm.x = shadow.x = paper.left;
        dim.y = page.y = fold.y = warm.y = shadow.y = paper.top;
        dim.alpha(0f);
        page.alpha(0.985f);
        fold.alpha(0f);
        warm.alpha(0f);
        shadow.alpha(0f);

        scene.add(dim);
        scene.add(shadow);
        scene.add(page);
        scene.add(warm);
        scene.add(fold);

        if (!covering) {
            page.x = paper.left;
            page.size(paper.width(), paper.height());
        }

        scene.add(new Tweener(scene, 0.30f) {
            @Override
            protected void updateValues(float progress) {
                float p = progress * progress * (3f - 2f * progress);
                float wave = (float) Math.sin(Math.PI * progress);
                float coverFraction = covering ? p : (1f - p);
                float coverW = paper.width() * coverFraction;

                if (covering) {
                    page.x = forward ? paper.right - coverW : paper.left;
                } else {
                    // Reveal from the page's outer edge toward the spine.
                    page.x = forward ? paper.left : paper.right - coverW;
                }
                page.size(Math.max(0.01f, coverW), paper.height());

                float edge;
                if (covering) {
                    edge = forward ? page.x : page.x + coverW;
                } else {
                    edge = forward ? page.x + coverW : page.x;
                }

                float foldW = 2.4f + wave * 6.8f;
                float shadowW = 4.5f + wave * 8.0f;
                float warmW = 1.1f + wave * 1.6f;
                fold.size(foldW, paper.height());
                shadow.size(shadowW, paper.height());
                warm.size(warmW, paper.height());

                if (forward) {
                    fold.x = edge - foldW * 0.45f;
                    warm.x = fold.x - warmW * 0.55f;
                    shadow.x = fold.x - shadowW;
                } else {
                    fold.x = edge - foldW * 0.55f;
                    warm.x = fold.x + foldW - warmW * 0.45f;
                    shadow.x = fold.x + foldW;
                }

                dim.alpha(wave * 0.075f);
                fold.alpha(0.38f + wave * 0.32f);
                warm.alpha(wave * 0.30f);
                shadow.alpha(wave * 0.24f);
                page.alpha(covering ? 0.985f : 0.94f + coverFraction * 0.045f);
            }

            @Override
            protected void onComplete() {
                dim.killAndErase();
                page.killAndErase();
                fold.killAndErase();
                warm.killAndErase();
                shadow.killAndErase();
                if (onComplete != null) onComplete.run();
            }
        });
    }
}
