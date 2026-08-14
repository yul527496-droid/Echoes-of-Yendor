package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.ColorBlock;
import com.watabou.utils.RectF;

/**
 * Canonical safe layout for ledger pages.
 *
 * All interactive/text content must live inside {@link Page#content}. This is
 * intentionally stricter than the painted paper bounds so page decorations,
 * book shadows, and screen edges can never eat UI controls.
 */
final class LedgerPageGrid {

    private LedgerPageGrid() {}

    static Page from(RectF paper) {
        float insetX = Math.max(6f, paper.width() * 0.10f);
        float insetTop = Math.max(6f, paper.height() * 0.085f);
        float insetBottom = Math.max(7f, paper.height() * 0.10f);

        RectF content = new RectF(
                paper.left + insetX,
                paper.top + insetTop,
                paper.right - insetX,
                paper.bottom - insetBottom);

        float headerH = Math.max(15f, content.height() * 0.20f);
        float footerH = Math.max(12f, content.height() * 0.16f);

        RectF header = new RectF(
                content.left,
                content.top,
                content.right,
                Math.min(content.bottom, content.top + headerH));

        RectF footer = new RectF(
                content.left,
                Math.max(header.bottom, content.bottom - footerH),
                content.right,
                content.bottom);

        RectF body = new RectF(
                content.left,
                header.bottom,
                content.right,
                footer.top);

        return new Page(paper, content, header, body, footer);
    }

    static ColorBlock rule(float x, float y, float width, float alpha) {
        ColorBlock line = new ColorBlock(width, 1f, 0xFF8B6B49);
        line.x = x;
        line.y = y;
        line.alpha(alpha);
        return line;
    }

    static final class Page {
        final RectF paper;
        final RectF content;
        final RectF header;
        final RectF body;
        final RectF footer;

        private Page(RectF paper, RectF content, RectF header, RectF body, RectF footer) {
            this.paper = paper;
            this.content = content;
            this.header = header;
            this.body = body;
            this.footer = footer;
        }
    }
}
