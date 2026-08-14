package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.utils.DeviceCompat;

/** Typography entry point for every Echoes ledger page. */
final class LedgerUI {

    private LedgerUI() {}

    /**
     * V2 baseline: ledger Chinese uses neutral tracking. The old SPD text
     * overlap (-0.667) stays untouched outside the ledger.
     */
    static RenderedTextBlock rawText(String value, int logicalSize) {
        return rawText(value, logicalSize, 0f);
    }

    static RenderedTextBlock rawText(String value, int logicalSize, float tracking) {
        int rasterScale = Math.max(1,
                Math.round(PixelScene.defaultZoom * DeviceCompat.getRealPixelScaleX()));

        RenderedTextBlock block = new RenderedTextBlock(
                value, logicalSize * rasterScale, false);

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
