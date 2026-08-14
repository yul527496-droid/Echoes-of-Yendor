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

    private static boolean embeddedFallback;

    private LedgerEnvironment() {}

    static Image tryLoad(String asset) {
        try {
            return new Image(asset);
        } catch (Throwable ignored) {
            return null;
        }
    }

    static Image addOpenBook(PixelScene scene) {
        int w = Camera.main.width;
        int h = Camera.main.height;

        Image base = tryLoad(LEDGER_BASE);
        embeddedFallback = base == null;
        if (embeddedFallback) {
            base = LedgerOpenArtwork.image();
        }

        float scale = Math.min(w / base.width, h / base.height);
        base.scale.set(scale);
        base.x = (w - base.width()) / 2f;
        base.y = (h - base.height()) / 2f;
        PixelScene.align(base);
        scene.add(base);

        if (!embeddedFallback) {
            Image shadow = tryLoad(LEDGER_SHADOW);
            if (shadow != null) {
                shadow.scale.set(base.scale.x, base.scale.y);
                shadow.x = base.x;
                shadow.y = base.y;
                scene.add(shadow);
            }
            scene.add(LedgerCandleFX.openBook(base));
        }

        return base;
    }

    static boolean usingEmbeddedFallback() {
        return embeddedFallback;
    }

    static RectF leftPage(Image book) {
        if (embeddedFallback) {
            float sx = book.width() / 160f;
            float sy = book.height() / 90f;
            return new RectF(
                    book.x + 18f * sx,
                    book.y + 11f * sy,
                    book.x + 74f * sx,
                    book.y + 75f * sy);
        }

        float sx = book.width() / 480f;
        float sy = book.height() / 270f;
        return new RectF(
                book.x + 78f * sx,
                book.y + 32f * sy,
                book.x + 235f * sx,
                book.y + 238f * sy);
    }

    static RectF rightPage(Image book) {
        if (embeddedFallback) {
            float sx = book.width() / 160f;
            float sy = book.height() / 90f;
            return new RectF(
                    book.x + 87f * sx,
                    book.y + 11f * sy,
                    book.x + 139f * sx,
                    book.y + 75f * sy);
        }

        float sx = book.width() / 480f;
        float sy = book.height() / 270f;
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
