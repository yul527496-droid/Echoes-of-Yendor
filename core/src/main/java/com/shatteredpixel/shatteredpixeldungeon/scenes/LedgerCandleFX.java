package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

/**
 * Pixel-art candle animation anchored to the candle already painted into the
 * ledger artwork. The effect is deliberately blocky: two low-alpha light
 * patches plus a tiny animated flame/core, rather than a smooth modern glow.
 */
final class LedgerCandleFX extends Component {

    private final Image source;
    private final float artW;
    private final float artH;
    private final float anchorX;
    private final float anchorY;

    private final ColorBlock wideGlow;
    private final ColorBlock nearGlow;
    private final ColorBlock flame;
    private final ColorBlock core;

    private float time;

    static LedgerCandleFX openBook(Image source) {
        // Approved open plate: candle flame sits at roughly (11, 19) in 160x90 art space.
        return new LedgerCandleFX(source, 160f, 90f, 11.5f, 18.5f);
    }

    static LedgerCandleFX closedBook(Image source) {
        // Approved closed plate: candle is farther in from the left edge.
        return new LedgerCandleFX(source, 128f, 85f, 29.5f, 22f);
    }

    private LedgerCandleFX(Image source, float artW, float artH, float anchorX, float anchorY) {
        super();
        this.source = source;
        this.artW = artW;
        this.artH = artH;
        this.anchorX = anchorX;
        this.anchorY = anchorY;

        wideGlow = new ColorBlock(1, 1, 0xFFFFAD52);
        nearGlow = new ColorBlock(1, 1, 0xFFFFC66B);
        flame = new ColorBlock(1, 1, 0xFFFFB02E);
        core = new ColorBlock(1, 1, 0xFFFFE4A0);

        add(wideGlow);
        add(nearGlow);
        add(flame);
        add(core);
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;

        float sx = source.width() / artW;
        float sy = source.height() / artH;
        float sourceAlpha = source.alpha();

        float slow = (float)Math.sin(time * 2.25f);
        float fast = (float)Math.sin(time * 8.4f + 0.7f);
        float twitch = (float)Math.sin(time * 13.7f + 1.3f);

        float cx = source.x + anchorX * sx;
        float cy = source.y + anchorY * sy;
        float sway = Math.round((fast + twitch * 0.35f) * 0.45f * sx);

        wideGlow.size(15f * sx, 13f * sy);
        wideGlow.x = cx - wideGlow.width() / 2f;
        wideGlow.y = cy - wideGlow.height() / 2f;
        wideGlow.alpha(sourceAlpha * (0.035f + slow * 0.008f + fast * 0.004f));

        nearGlow.size(8f * sx, 8f * sy);
        nearGlow.x = cx - nearGlow.width() / 2f;
        nearGlow.y = cy - nearGlow.height() / 2f;
        nearGlow.alpha(sourceAlpha * (0.060f + slow * 0.012f + fast * 0.007f));

        float flameH = (3.1f + slow * 0.35f + Math.max(0f, fast) * 0.55f) * sy;
        flame.size(Math.max(1f, 1.45f * sx), Math.max(2f, flameH));
        flame.x = cx - flame.width() / 2f + sway;
        flame.y = cy - flame.height() + 0.5f * sy;
        flame.alpha(sourceAlpha * 0.88f);

        core.size(Math.max(1f, 0.85f * sx), Math.max(1f, flameH * 0.55f));
        core.x = cx - core.width() / 2f + sway;
        core.y = cy - core.height() + 0.6f * sy;
        core.alpha(sourceAlpha * (0.72f + fast * 0.08f));
    }
}
