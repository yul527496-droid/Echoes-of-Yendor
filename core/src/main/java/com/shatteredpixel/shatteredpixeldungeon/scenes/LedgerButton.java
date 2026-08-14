package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.PointF;

/** Ledger button skin using the same parchment typography as ledger pages. */
class LedgerButton extends StyledButton {

    private boolean pressed;
    private boolean hovered;
    private boolean selected;

    private ColorBlock fill;
    private ColorBlock top;
    private ColorBlock bottom;
    private ColorBlock left;
    private ColorBlock right;
    private ColorBlock selectedAccent;

    LedgerButton(Chrome.Type type, String label, int size) {
        super(type, label, size);

        remove(text);

        fill = new ColorBlock(1f, 1f, 0xFF6A4630);
        top = new ColorBlock(1f, 1f, 0xFF4A3024);
        bottom = new ColorBlock(1f, 1f, 0xFF4A3024);
        left = new ColorBlock(1f, 1f, 0xFF4A3024);
        right = new ColorBlock(1f, 1f, 0xFF4A3024);
        selectedAccent = new ColorBlock(1f, 1f, 0xFF9B302C);

        add(fill);
        add(top);
        add(bottom);
        add(left);
        add(right);
        add(selectedAccent);

        text = LedgerUI.buttonText(label, size);
        add(text);
        applyVisualState();
        layout();
    }

    void setSelected(boolean value) {
        if (selected == value) return;
        selected = value;
        applyVisualState();
        layoutFrame();
    }

    boolean selected() {
        return selected;
    }

    @Override
    public void update() {
        super.update();

        // Desktop hover is sampled from the same pointer position used by Noosa.
        // This keeps the interaction entirely inside LedgerButton and does not
        // require invasive changes to the mature Button/PointerArea classes.
        if (DeviceCompat.isDesktop()) {
            PointF pointer = PointerEvent.currentHoverPos();
            boolean over = active && visible && hotArea != null
                    && hotArea.overlapsScreenPoint(Math.round(pointer.x), Math.round(pointer.y));
            if (hovered != over) {
                hovered = over;
                applyVisualState();
                layoutFrame();
            }
        } else if (hovered) {
            hovered = false;
            applyVisualState();
            layoutFrame();
        }
    }

    @Override
    protected void layout() {
        super.layout();
        layoutFrame();
    }

    private void layoutFrame() {
        if (fill == null) return;

        float grow = hovered ? 1.2f : (selected ? 0.6f : 0f);
        float fx = x - grow;
        float fy = y - grow;
        float fw = width + grow * 2f;
        float fh = height + grow * 2f;
        float line = 0.75f;

        fill.x = fx;
        fill.y = fy;
        fill.size(fw, fh);

        top.x = fx;
        top.y = fy;
        top.size(fw, line);

        bottom.x = fx;
        bottom.y = fy + fh - line;
        bottom.size(fw, line);

        left.x = fx;
        left.y = fy;
        left.size(line, fh);

        right.x = fx + fw - line;
        right.y = fy;
        right.size(line, fh);

        selectedAccent.x = fx + 1.5f;
        selectedAccent.y = fy + fh - 1.5f;
        selectedAccent.size(Math.max(0.01f, fw - 3f), 0.8f);
    }

    private void applyVisualState() {
        if (fill == null) return;

        if (!active) {
            fill.alpha(0.015f);
            top.alpha(0.12f);
            bottom.alpha(0.12f);
            left.alpha(0.12f);
            right.alpha(0.12f);
            selectedAccent.alpha(0f);
            return;
        }

        float borderAlpha;
        float fillAlpha;
        if (selected) {
            borderAlpha = hovered ? 0.88f : 0.72f;
            fillAlpha = hovered ? 0.12f : 0.08f;
        } else if (hovered) {
            borderAlpha = 0.68f;
            fillAlpha = 0.09f;
        } else {
            borderAlpha = 0.30f;
            fillAlpha = 0.035f;
        }

        fill.alpha(fillAlpha);
        top.alpha(borderAlpha);
        bottom.alpha(borderAlpha);
        left.alpha(borderAlpha);
        right.alpha(borderAlpha);
        selectedAccent.alpha(selected ? (hovered ? 0.95f : 0.82f) : 0f);
    }

    @Override
    public void enable(boolean value) {
        super.enable(value);
        if (!value) hovered = false;
        applyVisualState();
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
        applyVisualState();
    }
}
