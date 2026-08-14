package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

/**
 * Very low-alpha warm duplicate of the approved artwork. It gives the baked
 * candle lighting a living flicker without drawing replacement candles or
 * hard-edged fake light blocks over the composition.
 */
final class LedgerWarmth extends Image {

    private final float baseAlpha;
    private float time;

    LedgerWarmth(Image source, float baseAlpha) {
        super(source);
        this.baseAlpha = baseAlpha;
        hardlight(0xE0A25D);
        alpha(baseAlpha);
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;
        float slow = (float) Math.sin(time * 2.15f) * 0.010f;
        float flame = (float) Math.sin(time * 7.7f + 0.8f) * 0.005f;
        alpha(Math.max(0f, baseAlpha + slow + flame));
    }
}
