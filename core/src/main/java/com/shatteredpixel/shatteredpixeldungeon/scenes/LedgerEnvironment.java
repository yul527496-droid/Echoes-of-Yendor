package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

final class LedgerEnvironment {
    static final String OPEN_BOOK = "interfaces/echoes/ledger_open.png";
    static final int INK = 0x1D1611;
    static final int FADED_INK = 0x5B4938;
    static final int STAMP = 0x8E2929;
    private static final float ART_W = 160f;
    private static final float ART_H = 90f;
    private LedgerEnvironment() {}

    static Image addOpenBook(PixelScene scene) {
        int w = Camera.main.width, h = Camera.main.height;
        scene.add(new ColorBlock(w, h, 0xFF23170F));
        Image book = new Image(OPEN_BOOK);
        float scale = Math.min((w - 8f) / book.width, (h - 8f) / book.height);
        book.scale.set(scale);
        book.x = (w - book.width()) / 2f;
        book.y = (h - book.height()) / 2f;
        PixelScene.align(book);
        scene.add(book);
        LedgerGlow glow = new LedgerGlow(Math.max(56, w * 0.24f), h, 0x18E49A45);
        scene.add(glow);
        ColorBlock candle = new ColorBlock(4, Math.max(14, h * 0.08f), 0xFFD6B47A);
        candle.x = Math.max(5, book.x - 12);
        candle.y = book.y + book.height() * 0.62f;
        scene.add(candle);
        LedgerFlame flame = new LedgerFlame(3, 8, 0xFFFFA52F);
        flame.x = candle.x + 0.5f;
        flame.y = candle.y - 7;
        scene.add(flame);
        return book;
    }

    static RectF leftPage(Image book) {
        float sx = book.width() / ART_W, sy = book.height() / ART_H;
        return new RectF(book.x + 15f*sx, book.y + 9f*sy, book.x + 77f*sx, book.y + 77f*sy);
    }

    static RectF rightPage(Image book) {
        float sx = book.width() / ART_W, sy = book.height() / ART_H;
        return new RectF(book.x + 84f*sx, book.y + 9f*sy, book.x + 146f*sx, book.y + 77f*sy);
    }
}
