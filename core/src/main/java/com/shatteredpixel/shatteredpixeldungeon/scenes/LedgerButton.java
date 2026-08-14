package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;

/** Ledger button skin using the same parchment typography as ledger pages. */
class LedgerButton extends StyledButton {

    LedgerButton(Chrome.Type type, String label, int size) {
        super(type, label, size);

        // StyledButton's normal label is DPI-aware but outlined. On parchment
        // that outline is visually much too heavy, so replace it with the
        // ledger's DPI-aware borderless text instead.
        remove(text);
        text = LedgerUI.rawText(label, size);
        add(text);
        layout();
    }
}
