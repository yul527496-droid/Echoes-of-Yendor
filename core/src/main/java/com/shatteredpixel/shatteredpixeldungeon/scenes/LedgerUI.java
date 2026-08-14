package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.utils.DeviceCompat;

/** Shared typography rules for every Echoes ledger scene. */
final class LedgerUI {

    private LedgerUI() {}

    static RenderedTextBlock rawText(String value, int logicalSize) {
        int rasterScale = Math.max(1,
                Math.round(PixelScene.defaultZoom * DeviceCompat.getRealPixelScaleX()));
        RenderedTextBlock block = new RenderedTextBlock(value, logicalSize * rasterScale, false);
        block.zoom(1f / rasterScale);
        block.setHightlighting(false);
        return block;
    }

    static RenderedTextBlock text(String value, int logicalSize, int color, int maxWidth) {
        RenderedTextBlock block = rawText(value, logicalSize);
        if (maxWidth > 0) block.maxWidth(maxWidth);
        block.hardlight(color);
        return block;
    }
}
