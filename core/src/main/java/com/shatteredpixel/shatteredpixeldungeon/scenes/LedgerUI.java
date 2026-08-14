package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.FontPreviewMode;
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

        // Only text constructed through LedgerUI asks DesktopPlatformSupport
        // for the Fusion Pixel face. If that face was not packaged, platform
        // support safely falls back to the normal game fonts.
        boolean previous = FontPreviewMode.ledgerPixelFont;
        FontPreviewMode.ledgerPixelFont = true;
        try {
            RenderedTextBlock block = new RenderedTextBlock(
                    value, logicalSize * rasterScale, false);
            block.zoom(1f / rasterScale);
            block.tracking(tracking);
            block.setHightlighting(false);
            return block;
        } finally {
            FontPreviewMode.ledgerPixelFont = previous;
        }
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
}
