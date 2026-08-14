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
    static final String LEDGER_CLOSED = "interfaces/echoes/ledger/ledger_closed.png";
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
        LedgerAudio.enter();

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

        scene.add(embeddedFallback
                ? LedgerCandleFX.openBookEmbedded(base)
                : LedgerCandleFX.openBook(base));
        scene.add(LedgerAudio.driver());

        return base;
    }

    static boolean usingEmbeddedFallback() {
        return embeddedFallback;
    }

    static RectF leftPage(Image book) {
        return LedgerDesignSpace.leftPage(book);
    }

    static RectF rightPage(Image book) {
        return LedgerDesignSpace.rightPage(book);
    }

    static LedgerPageGrid.Page leftGrid(Image book) {
        return LedgerPageGrid.from(leftPage(book), LedgerDesignSpace.leftContent(book));
    }

    static LedgerPageGrid.Page rightGrid(Image book) {
        return LedgerPageGrid.from(rightPage(book), LedgerDesignSpace.rightContent(book));
    }
}
