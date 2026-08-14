package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

/**
 * A text stamp drawn entirely from runtime UI primitives.
 *
 * The ledger uses this instead of font-only status labels so a missing PNG or
 * glyph can never remove the visual language of the inn's red ink stamps.
 */
final class LedgerStamp extends Component {

    private final ColorBlock outerTop;
    private final ColorBlock outerBottom;
    private final ColorBlock outerLeft;
    private final ColorBlock outerRight;
    private final ColorBlock innerTop;
    private final ColorBlock innerBottom;
    private final ColorBlock innerLeft;
    private final ColorBlock innerRight;
    private final RenderedTextBlock label;

    private float visualScale = 1f;
    private float visualAlpha = 1f;

    LedgerStamp(String text, int textSize) {
        outerTop = line();
        outerBottom = line();
        outerLeft = line();
        outerRight = line();
        innerTop = line();
        innerBottom = line();
        innerLeft = line();
        innerRight = line();

        add(outerTop);
        add(outerBottom);
        add(outerLeft);
        add(outerRight);
        add(innerTop);
        add(innerBottom);
        add(innerLeft);
        add(innerRight);

        label = LedgerUI.rawText(text, textSize);
        label.hardlight(LedgerEnvironment.STAMP);
        add(label);
    }

    private ColorBlock line() {
        ColorBlock block = new ColorBlock(1f, 1f, 0xFF9B302C);
        block.alpha(0.92f);
        return block;
    }

    void visual(float scale, float alpha) {
        visualScale = scale;
        visualAlpha = alpha;
        layout();
    }

    void alpha(float alpha) {
        visualAlpha = alpha;
        layout();
    }

    @Override
    protected void layout() {
        super.layout();
        if (width <= 0 || height <= 0) return;

        float w = width * visualScale;
        float h = height * visualScale;
        float ox = x + (width - w) * 0.5f;
        float oy = y + (height - h) * 0.5f;
        float thick = Math.max(0.58f, Math.min(0.92f, h * 0.055f));
        float inset = Math.max(1.5f, thick * 2.1f);

        setLine(outerTop, ox, oy, w, thick);
        setLine(outerBottom, ox, oy + h - thick, w, thick);
        setLine(outerLeft, ox, oy, thick, h);
        setLine(outerRight, ox + w - thick, oy, thick, h);

        float iw = Math.max(1f, w - inset * 2f);
        float ih = Math.max(1f, h - inset * 2f);
        float iox = ox + inset;
        float ioy = oy + inset;
        float innerThick = Math.max(0.42f, thick * 0.62f);

        setLine(innerTop, iox, ioy, iw, innerThick);
        setLine(innerBottom, iox, ioy + ih - innerThick, iw, innerThick);
        setLine(innerLeft, iox, ioy, innerThick, ih);
        setLine(innerRight, iox + iw - innerThick, ioy, innerThick, ih);

        label.setPos(
                ox + (w - label.width()) * 0.5f,
                oy + (h - label.height()) * 0.5f - 0.35f);

        float outerAlpha = visualAlpha * 0.92f;
        float innerAlpha = visualAlpha * 0.68f;
        outerTop.alpha(outerAlpha);
        outerBottom.alpha(outerAlpha);
        outerLeft.alpha(outerAlpha);
        outerRight.alpha(outerAlpha);
        innerTop.alpha(innerAlpha);
        innerBottom.alpha(innerAlpha);
        innerLeft.alpha(innerAlpha);
        innerRight.alpha(innerAlpha);
        label.alpha(visualAlpha);
    }

    private void setLine(ColorBlock block, float x, float y, float w, float h) {
        block.x = x;
        block.y = y;
        block.size(w, h);
    }
}
