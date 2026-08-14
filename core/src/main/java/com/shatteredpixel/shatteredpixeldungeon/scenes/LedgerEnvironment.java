package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

/** Shared geometry, palette, and ambient presentation for every ledger page. */
final class LedgerEnvironment {

    static final int INK = 0x3B281B;
    static final int FADED_INK = 0x755E45;
    static final int STAMP = 0x9B302C;
    static final int RULE = 0x8B6B49;
    static final int PAPER_ACCENT = 0xB98A55;

    // LedgerOpenArtwork is a 160x90 pixel-art reduction of the approved plate.
    private static final float ART_W = 160f;
    private static final float ART_H = 90f;

    private LedgerEnvironment() {}

    static Image addOpenBook(PixelScene scene) {
        int w = Camera.main.width;
        int h = Camera.main.height;

        Image book = LedgerOpenArtwork.image();
        float scale = Math.min((w - 8f) / book.width, (h - 8f) / book.height);
        book.scale.set(scale);
        book.x = (w - book.width()) / 2f;
        book.y = (h - book.height()) / 2f;
        PixelScene.align(book);
        scene.add(book);

        scene.add(new LedgerWarmth(book, 0.014f));
        scene.add(LedgerCandleFX.openBook(book));
        return book;
    }

    static RectF leftPage(Image book) {
        float sx = book.width() / ART_W;
        float sy = book.height() / ART_H;
        return new RectF(
                book.x + 18f * sx,
                book.y + 11f * sy,
                book.x + 74f * sx,
                book.y + 75f * sy);
    }

    static RectF rightPage(Image book) {
        float sx = book.width() / ART_W;
        float sy = book.height() / ART_H;
        // Intentionally conservative. The approved art's right edge contains
        // heavy page curl/shadow; interactive content should stop well before it.
        return new RectF(
                book.x + 87f * sx,
                book.y + 11f * sy,
                book.x + 139f * sx,
                book.y + 75f * sy);
    }

    static LedgerPageGrid.Page leftGrid(Image book) {
        return LedgerPageGrid.from(leftPage(book));
    }

    static LedgerPageGrid.Page rightGrid(Image book) {
        return LedgerPageGrid.from(rightPage(book));
    }
}
