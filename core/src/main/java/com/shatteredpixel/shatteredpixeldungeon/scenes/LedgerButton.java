package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;

/**
 * Ledger button skin.
 *
 * StyledButton already creates its label through PixelScene.renderTextBlock(),
 * which is the DPI-aware path used by Shattered for CJK text. Do not replace
 * that label with a raw RenderedTextBlock here.
 */
class LedgerButton extends StyledButton {

    LedgerButton(Chrome.Type type, String label, int size) {
        super(type, label, size);
    }
}
