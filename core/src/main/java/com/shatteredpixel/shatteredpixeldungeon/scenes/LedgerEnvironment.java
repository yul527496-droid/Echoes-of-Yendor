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

    static final String LEDGER_BASE = "interfaces/echoes/ledger/ledger_base.png";
    static final String LEDGER_SHADOW = "interfaces/echoes/ledger/ledger_shadow.png";
    static final String LEDGER_LIGHT = "interfaces/echoes/ledger/ledger_light.png";
    static final String CANDLE_FLAME = "interfaces/echoes/ledger/candle_flame.png";
    static final String STAMP_UNRETURNED = "interfaces/echoes/ledger/stamp_unreturned.png";

    private static final float ART_W = 480f;
    private static final float ART_H = 270f;

    private LedgerEnvironment() {}

    static Image addOpenBook(PixelScene scene) {
        int w = Camera.main.width;
        int h = Camera.main.height;

        Image base = new Image(LEDGER_BASE);
        float scale = Math.min(w / base.width, h / base.height);
        base.scale.set(scale);
        base.x = (w - base.width()) / 2f;
        base.y = (h - base.height()) / 2f;
        PixelScene.align(base);
        scene.add(base);

        Image shadow = new Image(LEDGER_SHADOW);
        shadow.scale.set(base.scale.x, base.scale.y);
        shadow.x = base.x;
        shadow.y = base.y;
        scene.add(shadow);

        scene.add(LedgerCandleFX.openBook(base));
        return base;
    }

    static RectF leftPage(Image book) {
        float sx = book.width() / ART_W;
        float sy = book.height() / ART_H;
        return new RectF(
                book.x + 78f * sx,
                book.y + 32f * sy,
                book.x + 235f * sx,
                book.y + 238f * sy);
    }

    static RectF rightPage(Image book) {
        float sx = book.width() / ART_W;
        float sy = book.height() / ART_H;
        return new RectF(
                book.x + 250f * sx,
                book.y + 32f * sy,
                book.x + 405f * sx,
                book.y + 238f * sy);
    }

    static LedgerPageGrid.Page leftGrid(Image book) {
        return LedgerPageGrid.from(leftPage(book));
    }

    static LedgerPageGrid.Page rightGrid(Image book) {
        return LedgerPageGrid.from(rightPage(book));
    }
}
