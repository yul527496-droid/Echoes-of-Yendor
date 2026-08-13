package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;

final class LedgerEnvironment {

    static final String OPEN_BOOK = "interfaces/echoes/ledger_open.png";
    static final int INK = 0x3B2A1E;
    static final int FADED_INK = 0x6F5B48;
    static final int STAMP = 0x922E2E;

    private LedgerEnvironment() {}

    static Image addOpenBook(PixelScene scene) {
        int w = Camera.main.width;
        int h = Camera.main.height;
        scene.add(new ColorBlock(w, h, 0xFF17110D));

        Image book = new Image(OPEN_BOOK);
        float scale = Math.max(w / book.width, h / book.height);
        book.scale.set(scale);
        book.x = (w - book.width()) / 2f;
        book.y = (h - book.height()) / 2f;
        scene.add(book);

        LedgerGlow glow = new LedgerGlow(Math.max(70, w * 0.34f), h, 0x2AE49A45);
        glow.x = 0;
        glow.y = 0;
        scene.add(glow);

        ColorBlock candle = new ColorBlock(5, Math.max(18, h * 0.12f), 0xFFE1C18A);
        candle.x = Math.max(8, w * 0.12f);
        candle.y = h * 0.56f;
        scene.add(candle);

        LedgerFlame flame = new LedgerFlame(4, 10, 0xFFFFA52F);
        flame.x = candle.x + 0.5f;
        flame.y = candle.y - 9;
        scene.add(flame);
        return book;
    }
}
