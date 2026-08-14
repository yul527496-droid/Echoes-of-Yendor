package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

/** Shared geometry and colors for every ledger page. */
final class LedgerEnvironment {

    static final int INK = 0x1D1611;
    static final int FADED_INK = 0x5B4938;
    static final int STAMP = 0x8E2929;

    // LedgerOpenArtwork is a 160x90 pixel-art reduction of the approved plate.
    private static final float ART_W = 160f;
    private static final float ART_H = 90f;

    private LedgerEnvironment() {}

    static Image addOpenBook(PixelScene scene) {
        int w = Camera.main.width;
        int h = Camera.main.height;

        // Do not recreate candles, glows, table blocks, or page decoration in
        // code. Those elements already belong to the approved artwork.
        Image book = LedgerOpenArtwork.image();
        float scale = Math.min((w - 8f) / book.width, (h - 8f) / book.height);
        book.scale.set(scale);
        book.x = (w - book.width()) / 2f;
        book.y = (h - book.height()) / 2f;
        PixelScene.align(book);
        scene.add(book);
        return book;
    }

    static RectF leftPage(Image book) {
        float sx = book.width() / ART_W;
        float sy = book.height() / ART_H;
        return new RectF(
                book.x + 15f * sx,
                book.y + 9f * sy,
                book.x + 77f * sx,
                book.y + 77f * sy);
    }

    static RectF rightPage(Image book) {
        float sx = book.width() / ART_W;
        float sy = book.height() / ART_H;
        return new RectF(
                book.x + 84f * sx,
                book.y + 9f * sy,
                book.x + 146f * sx,
                book.y + 77f * sy);
    }
}
