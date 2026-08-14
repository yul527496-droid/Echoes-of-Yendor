package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.utils.DeviceCompat;

/** Typography entry point for every Echoes ledger page. */
final class LedgerUI {

    static final int TITLE = 10;
    static final int SMALL_TITLE = 8;
    static final int BODY = 7;
    static final int SECONDARY = 6;
    static final int FOOTNOTE = 5;

    private static final float TITLE_TRACKING = 0.20f;
    private static final float BODY_TRACKING = 0.35f;
    private static final float BUTTON_TRACKING = 0.45f;

    private LedgerUI() {}

    /**
     * Legacy ledger callers still pass the old 5/6/7/9 sizes. Resolve those
     * through one global hierarchy so every page gets the readability pass at
     * once instead of drifting page by page.
     */
    private static int resolveSize(int requested) {
        if (requested <= 5) return SECONDARY;
        if (requested == 6) return BODY;
        if (requested == 7 || requested == 8) return SMALL_TITLE;
        if (requested == 9) return TITLE;
        return requested;
    }

    private static boolean cjkTracking() {
        return Messages.lang() == Languages.CHI_SMPL
                || Messages.lang() == Languages.CHI_TRAD
                || Messages.lang() == Languages.JAPANESE;
    }

    private static float defaultTracking(int resolvedSize) {
        if (!cjkTracking()) return 0f;
        return resolvedSize >= SMALL_TITLE ? TITLE_TRACKING : BODY_TRACKING;
    }

    private static RenderedTextBlock make(String value, int resolvedSize, float tracking) {
        int rasterScale = Math.max(1,
                Math.round(PixelScene.defaultZoom * DeviceCompat.getRealPixelScaleX()));

        RenderedTextBlock block = new RenderedTextBlock(
                value, resolvedSize * rasterScale, false);

        // Persist the ledger font choice on the block itself. maxWidth(), text(),
        // and highlighting can all rebuild RenderedText later; the block must
        // therefore remember that every rebuild still belongs to Fusion Pixel.
        block.setLedgerPixelFont(true);
        block.zoom(1f / rasterScale);
        block.tracking(tracking);

        // Plain ledger text treats underscores/asterisks literally. Authored
        // game descriptions that intentionally use SPD markup opt in via
        // markupText(), so player-entered names are never parsed as markup.
        block.setHightlighting(false);
        return block;
    }

    static RenderedTextBlock rawText(String value, int logicalSize) {
        int resolved = resolveSize(logicalSize);
        return make(value, resolved, defaultTracking(resolved));
    }

    static RenderedTextBlock rawText(String value, int logicalSize, float tracking) {
        return make(value, resolveSize(logicalSize), tracking);
    }

    static RenderedTextBlock buttonText(String value, int logicalSize) {
        int resolved = resolveSize(logicalSize);
        return make(value, resolved, cjkTracking() ? BUTTON_TRACKING : 0f);
    }

    static RenderedTextBlock footnote(String value, int color, int maxWidth) {
        RenderedTextBlock block = make(value, FOOTNOTE,
                cjkTracking() ? BODY_TRACKING : 0f);
        if (maxWidth > 0) block.maxWidth(maxWidth);
        block.hardlight(color);
        return block;
    }

    static RenderedTextBlock text(String value, int logicalSize, int color, int maxWidth) {
        RenderedTextBlock block = rawText(value, logicalSize);
        if (maxWidth > 0) block.maxWidth(maxWidth);
        block.hardlight(color);
        return block;
    }

    static RenderedTextBlock text(String value, int logicalSize, int color,
                                  int maxWidth, float tracking) {
        RenderedTextBlock block = rawText(value, logicalSize, tracking);
        if (maxWidth > 0) block.maxWidth(maxWidth);
        block.hardlight(color);
        return block;
    }

    /**
     * For trusted, game-authored strings such as HeroClass.shortDesc().
     * SPD uses _text_ / **text** as emphasis markers. Enabling highlighting
     * consumes those marker tokens instead of drawing the underscores.
     */
    static RenderedTextBlock markupText(String value, int logicalSize,
                                        int color, int highlightColor, int maxWidth) {
        RenderedTextBlock block = rawText(value, logicalSize);
        if (maxWidth > 0) block.maxWidth(maxWidth);
        block.hardlight(color);
        block.setHightlighting(true, highlightColor);
        return block;
    }
}
