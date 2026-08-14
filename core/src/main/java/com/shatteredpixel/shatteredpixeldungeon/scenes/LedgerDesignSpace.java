package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

/**
 * Canonical design-space geometry for every Echoes ledger screen.
 *
 * The ledger is always authored in a 480x270 logical plate. Runtime assets may
 * be higher/lower resolution (including the 160x90 embedded fallback), but
 * they must map to this same design space. Asset choice is never allowed to
 * change page geometry.
 */
final class LedgerDesignSpace {

    static final float WIDTH = 480f;
    static final float HEIGHT = 270f;

    private static final float LEFT_PAGE_L = 78f;
    private static final float LEFT_PAGE_T = 32f;
    private static final float LEFT_PAGE_R = 235f;
    private static final float LEFT_PAGE_B = 238f;

    private static final float RIGHT_PAGE_L = 250f;
    private static final float RIGHT_PAGE_T = 32f;
    private static final float RIGHT_PAGE_R = 405f;
    private static final float RIGHT_PAGE_B = 238f;

    private LedgerDesignSpace() {}

    static RectF leftPage(Image book) {
        return map(book, LEFT_PAGE_L, LEFT_PAGE_T, LEFT_PAGE_R, LEFT_PAGE_B);
    }

    static RectF rightPage(Image book) {
        return map(book, RIGHT_PAGE_L, RIGHT_PAGE_T, RIGHT_PAGE_R, RIGHT_PAGE_B);
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
