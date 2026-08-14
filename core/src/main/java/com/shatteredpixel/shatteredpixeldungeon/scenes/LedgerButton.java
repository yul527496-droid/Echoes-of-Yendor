package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;

/** Ledger button skin using the same parchment typography as ledger pages. */
class LedgerButton extends StyledButton {

    private boolean pressed;

    LedgerButton(Chrome.Type type, String label, int size) {
        super(type, label, size);

        // StyledButton's normal label is DPI-aware but outlined. On parchment
        // that outline is visually too heavy, so replace it with the ledger's
        // DPI-aware borderless text.
        remove(text);
        text = LedgerUI.rawText(label, size);
        add(text);
        layout();
    }

    @Override
    protected void onPointerDown() {
        super.onPointerDown();
        if (!pressed) {
            pressed = true;
            if (text != null) {
                text.setPos(text.left() + 1f, text.top() + 1f);
                text.alpha(0.78f);
            }
            if (icon != null) {
                icon.x += 1f;
                icon.y += 1f;
                icon.alpha(0.82f);
            }
        }
    }

    @Override
    protected void onPointerUp() {
        if (pressed) {
            if (text != null) {
                text.setPos(text.left() - 1f, text.top() - 1f);
                text.alpha(active ? 1f : 0.3f);
            }
            if (icon != null) {
                icon.x -= 1f;
                icon.y -= 1f;
                icon.alpha(active ? 1f : 0.3f);
            }
            pressed = false;
        }
        super.onPointerUp();
    }
}
