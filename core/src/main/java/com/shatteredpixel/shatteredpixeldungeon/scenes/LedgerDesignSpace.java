package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

/**
 * Canonical 480x270 design-space geometry for every Echoes ledger screen.
 *
 * Runtime artwork may use another pixel resolution, but the painted paper and
 * writable content landmarks must map to these same logical coordinates.
 * Asset choice is never allowed to redefine layout.
 */
final class LedgerDesignSpace {

    static final float WIDTH = 480f;
    static final float HEIGHT = 270f;

    // Painted parchment bounds, calibrated from the current ledger artwork.
    private static final float LEFT_PAGE_L = 87f;
    private static final float LEFT_PAGE_T = 38f;
    private static final float LEFT_PAGE_R = 235f;
    private static final float LEFT_PAGE_B = 220f;

    private static final float RIGHT_PAGE_L = 251f;
    private static final float RIGHT_PAGE_T = 38f;
    private static final float RIGHT_PAGE_R = 405f;
    private static final float RIGHT_PAGE_B = 220f;

    // Writable page areas are explicit design landmarks, not percentages of
    // the paper rectangle. The left page is centered within its parchment so
    // its content no longer hugs the outer edge; the right page was already
    // visually balanced and remains unchanged.
    private static final float LEFT_CONTENT_L = 98f;
    private static final float LEFT_CONTENT_T = 50f;
    private static final float LEFT_CONTENT_R = 223f;
    private static final float LEFT_CONTENT_B = 217f;

    private static final float RIGHT_CONTENT_L = 266f;
    private static final float RIGHT_CONTENT_T = 50f;
    private static final float RIGHT_CONTENT_R = 390f;
    private static final float RIGHT_CONTENT_B = 217f;

    private LedgerDesignSpace() {}

    static RectF leftPage(Image book) {
        return map(book, LEFT_PAGE_L, LEFT_PAGE_T, LEFT_PAGE_R, LEFT_PAGE_B);
    }

    static RectF rightPage(Image book) {
        return map(book, RIGHT_PAGE_L, RIGHT_PAGE_T, RIGHT_PAGE_R, RIGHT_PAGE_B);
    }

    static RectF leftContent(Image book) {
        return map(book, LEFT_CONTENT_L, LEFT_CONTENT_T, LEFT_CONTENT_R, LEFT_CONTENT_B);
    }

    static RectF rightContent(Image book) {
        return map(book, RIGHT_CONTENT_L, RIGHT_CONTENT_T, RIGHT_CONTENT_R, RIGHT_CONTENT_B);
    }

    static RectF map(Image book, float left, float top, float right, float bottom) {
        float sx = book.width() / WIDTH;
        float sy = book.height() / HEIGHT;
        return new RectF(
                book.x + left * sx,
                book.y + top * sy,
                book.x + right * sx,
                book.y + bottom * sy);
    }
}
