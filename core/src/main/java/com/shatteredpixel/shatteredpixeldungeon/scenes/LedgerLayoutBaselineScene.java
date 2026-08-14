package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

/** Temporary V2 layout acceptance page. */
public class LedgerLayoutBaselineScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        drawGrid(LedgerEnvironment.leftGrid(book));
        drawGrid(LedgerEnvironment.rightGrid(book));
        fadeIn();
    }

    private void drawGrid(LedgerPageGrid.Page page) {
        // Green = calibrated painted parchment boundary.
        outline(page.paper, 0xFF2C8B57, 0.70f);

        // Blue = fixed writable design-space rectangle.
        fill(page.content, 0xFF4E78A8, 0.035f);
        outline(page.content, 0xFF4E78A8, 0.70f);

        // Structural bands inside the fixed writable region.
        fill(page.header, 0xFFD69A42, 0.055f);
        outline(page.header, 0xFFD69A42, 0.65f);

        fill(page.body, 0xFF8C765B, 0.025f);
        outline(page.body, 0xFF8C765B, 0.32f);

        fill(page.footer, 0xFFB54B46, 0.045f);
        outline(page.footer, 0xFFB54B46, 0.62f);
    }

    private void fill(RectF r, int color, float alpha) {
        ColorBlock block = new ColorBlock(r.width(), r.height(), color);
        block.x = r.left;
        block.y = r.top;
        block.alpha(alpha);
        add(block);
    }

    private void outline(RectF r, int color, float alpha) {
        ColorBlock top = new ColorBlock(r.width(), 1f, color);
        top.x = r.left;
        top.y = r.top;
        top.alpha(alpha);
        add(top);

        ColorBlock bottom = new ColorBlock(r.width(), 1f, color);
        bottom.x = r.left;
        bottom.y = r.bottom - 1f;
        bottom.alpha(alpha);
        add(bottom);

        ColorBlock left = new ColorBlock(1f, r.height(), color);
        left.x = r.left;
        left.y = r.top;
        left.alpha(alpha);
        add(left);

        ColorBlock right = new ColorBlock(1f, r.height(), color);
        right.x = r.right - 1f;
        right.y = r.top;
        right.alpha(alpha);
        add(right);
    }
}
