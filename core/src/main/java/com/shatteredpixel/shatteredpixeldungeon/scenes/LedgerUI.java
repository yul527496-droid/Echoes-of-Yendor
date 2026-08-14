package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.utils.DeviceCompat;

/** Shared typography rules for every Echoes ledger scene. */
final class LedgerUI {

    private LedgerUI() {}

    /**
     * Borderless text suited to dark ink on bright parchment while preserving
     * Shattered's DPI-aware glyph generation. CJK glyphs need to be rasterized
     * at the physical pixel scale first and then zoomed back to logical UI size.
     *
     * The actual CJK font asset remains a separate concern: phase two first
     * locks layout, hierarchy, spacing, and animation so a future pixel-font
     * replacement can be tested in isolation rather than mixed into UI bugs.
     */
    static RenderedTextBlock rawText(String value, int logicalSize) {
        int rasterScale = Math.max(1,
                Math.round(PixelScene.defaultZoom * DeviceCompat.getRealPixelScaleX()));
        RenderedTextBlock block = new RenderedTextBlock(
                value,
                logicalSize * rasterScale,
                false);
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
