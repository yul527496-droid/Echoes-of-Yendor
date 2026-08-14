package com.watabou.noosa;

/**
 * Temporary V2 typography switch used only by the Echoes ledger font A/B page.
 * DesktopPlatformSupport consults this flag while constructing RenderedText.
 * Existing text keeps the BitmapFont it was measured with, so the flag can be
 * toggled between the left and right comparison columns without changing the
 * rest of the game's UI.
 */
public final class FontPreviewMode {

    private FontPreviewMode() {}

    public static boolean ledgerPixelFont;
    public static boolean ledgerPixelFontAvailable;
}
