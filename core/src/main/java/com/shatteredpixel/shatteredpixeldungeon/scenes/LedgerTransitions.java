package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.RectF;

/**
 * Two-stage 2.5D pixel-paper turn for every ledger navigation.
 *
 * This intentionally stops short of framebuffer/mesh rendering. The moving
 * sheet is approximated by ten vertical paper strips whose projected widths,
 * top/bottom lift and lighting differ slightly while the page bends toward the
 * spine. The scene switch happens at the visual midpoint, when the sheet is
 * almost edge-on and the page shadow is deepest.
 */
final class LedgerTransitions {

    private static final int STRIP_COUNT = 10;
    private static final float HALF_TURN_SECONDS = 0.27f;
    private static final float MIN_PROJECTION = 0.07f;

    private static final int[] PAPER_TONES = {
            0xFFF0CF8C,
            0xFFEBC681,
            0xFFF4D79B,
            0xFFE7BF79,
            0xFFF1D08D,
            0xFFEAC37F,
            0xFFF3D597,
            0xFFE5BC76,
            0xFFEFCB87,
            0xFFF2D292
    };

    private static int pendingSide; // -1 left page, +1 right page
    private static boolean pendingForward;

    private LedgerTransitions() {}

    static void turn(PixelScene scene, RectF paper,
                     Class<? extends PixelScene> destination, boolean forward) {
        final Class<? extends PixelScene> destinationScene = destination;
        pendingSide = (paper.left + paper.right) * 0.5f
                < com.watabou.noosa.Camera.main.width * 0.5f ? -1 : 1;
        pendingForward = forward;

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
        final ColorBlock veil = new ColorBlock(
                paper.width(), paper.height(), 0xFF2D1B10);
        veil.x = paper.left;
        veil.y = paper.top;
        veil.alpha(0f);
        scene.add(veil);

        final ColorBlock shadow = new ColorBlock(1f, paper.height(), 0xFF24150D);
        shadow.y = paper.top;
        shadow.alpha(0f);
        scene.add(shadow);

        final ColorBlock[] strips = new ColorBlock[STRIP_COUNT];
        for (int i = 0; i < STRIP_COUNT; i++) {
            ColorBlock strip = new ColorBlock(1f, paper.height(), PAPER_TONES[i]);
            strip.alpha(covering ? 0.08f : 0.98f);
            strips[i] = strip;
            scene.add(strip);
        }

        final ColorBlock edge = new ColorBlock(1f, paper.height(), 0xFFFFE5AE);
        edge.y = paper.top;
        edge.alpha(0f);
        scene.add(edge);

        final float[] weights = new float[STRIP_COUNT];

        scene.add(new Tweener(scene, HALF_TURN_SECONDS) {
            private boolean soundPlayed;

            @Override
            protected void updateValues(float progress) {
                float eased = ease(progress);
                float phase = covering
                        ? eased * 0.5f
                        : 0.5f + eased * 0.5f;

                // Start the real page SFX after lift-off instead of on frame zero.
                // Its paper-swish peak then lands near the visual midpoint.
                if (covering && !soundPlayed && progress >= 0.20f) {
                    soundPlayed = true;
                    LedgerAudio.pageTurn();
                }

                float bend = (float) Math.sin(Math.PI * phase);
                float projection = Math.max(MIN_PROJECTION,
                        Math.abs((float) Math.cos(Math.PI * phase)));

                // A tiny late overshoot gives the landing phase a paper-like settle
                // without scaling text or the destination page itself.
                float rebound = 1f;
                if (!covering && progress > 0.70f) {
                    float settle = clamp01((progress - 0.70f) / 0.30f);
                    rebound += (float) Math.sin(Math.PI * settle) * 0.018f;
                }

                float sheetWidth = Math.min(paper.width(),
                        paper.width() * projection * rebound);
                float anchor = forward ? paper.left : paper.right;

                float weightTotal = 0f;
                for (int i = 0; i < STRIP_COUNT; i++) {
                    float u = (i + 0.5f) / STRIP_COUNT;
                    float crown = (float) Math.sin(Math.PI * u);
                    float lean = (u - 0.5f) * 0.10f * bend;
                    float weight = Math.max(0.45f,
                            1f - crown * bend * 0.24f + lean);
                    weights[i] = weight;
                    weightTotal += weight;
                }

                float sheetAlpha;
                if (covering) {
                    sheetAlpha = 0.08f + 0.90f
                            * ease(clamp01(progress / 0.28f));
                } else {
                    float release = ease(clamp01((progress - 0.64f) / 0.36f));
                    sheetAlpha = 0.98f - 0.94f * release;
                }

                float cursor = anchor;
                for (int i = 0; i < STRIP_COUNT; i++) {
                    float u = (i + 0.5f) / STRIP_COUNT;
                    float crown = (float) Math.sin(Math.PI * u);
                    float stripWidth = sheetWidth * weights[i] / weightTotal;

                    // Adjacent pieces overlap slightly so the curved sheet never
                    // exposes bright one-pixel cracks during integer alignment.
                    float overlap = Math.min(0.42f, stripWidth * 0.12f);
                    float drawWidth = Math.max(0.05f, stripWidth + overlap);

                    float lift = crown * bend
                            * (1.15f + 1.65f * (1f - projection));
                    float bottomLift = crown * bend
                            * (0.65f + 0.95f * (1f - projection));
                    float drawHeight = Math.max(1f,
                            paper.height() - lift - bottomLift);

                    ColorBlock strip = strips[i];
                    strip.y = paper.top + lift;
                    strip.size(drawWidth, drawHeight);
                    strip.alpha(sheetAlpha);

                    if (forward) {
                        strip.x = cursor - overlap * 0.5f;
                        cursor += stripWidth;
                    } else {
                        cursor -= stripWidth;
                        strip.x = cursor - overlap * 0.5f;
                    }

                    // Uneven light across the strips sells curvature much better
                    // than scaling or jittering one flat rectangle.
                    float lightWave = (float) Math.sin(
                            Math.PI * (u + phase * 0.32f));
                    float brightness = 0.79f
                            + 0.19f * (0.5f + 0.5f * lightWave)
                            - 0.08f * bend
                            + 0.05f * bend * (1f - u);
                    strip.brightness(brightness);
                }

                float outerEdge = forward
                        ? paper.left + sheetWidth
                        : paper.right - sheetWidth;
                float shadowWidth = 2.8f + bend * 10.5f;
                float edgeWidth = 0.85f + bend * 1.45f;

                shadow.size(shadowWidth, paper.height());
                shadow.x = forward ? outerEdge : outerEdge - shadowWidth;
                shadow.alpha(0.06f + bend * 0.42f);

                edge.size(edgeWidth, paper.height());
                edge.x = outerEdge - edgeWidth * 0.5f;
                edge.alpha(0.10f + bend * 0.48f);

                // The midpoint veil hides the scene swap while the sheet is almost
                // edge-on. It is deliberately concentrated around 50%, not a
                // full-duration crossfade.
                veil.alpha(0.80f * bend * bend * bend);
            }

            @Override
            protected void onComplete() {
                if (covering && onComplete != null) {
                    // Game.switchScene is deferred. Keep the edge-on page visible
                    // until this scene is actually replaced so the old page cannot
                    // flash back for one frame at the midpoint.
                    onComplete.run();
                    return;
                }

                veil.killAndErase();
                shadow.killAndErase();
                edge.killAndErase();
                for (ColorBlock strip : strips) strip.killAndErase();
                if (onComplete != null) onComplete.run();
            }
        });
    }

    private static float ease(float value) {
        float p = clamp01(value);
        return p * p * (3f - 2f * p);
    }

    private static float clamp01(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
