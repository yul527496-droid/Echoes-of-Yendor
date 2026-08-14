package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

/**
 * Very low-alpha warm duplicate of the approved artwork. The actual visible
 * flicker comes from LedgerCandleFX; this layer only prevents the surrounding
 * paper from feeling completely static.
 */
final class LedgerWarmth extends Image {

    private final Image source;
    private final float baseAlpha;
    private float time;

    LedgerWarmth(Image source, float baseAlpha) {
        super(source);
        this.source = source;
        this.baseAlpha = baseAlpha;
        hardlight(0xE0A25D);
        alpha(baseAlpha);
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;

        x = source.x;
        y = source.y;
        scale.set(source.scale.x, source.scale.y);
        visible = source.visible;

        float slow = (float) Math.sin(time * 2.15f) * 0.010f;
        float flame = (float) Math.sin(time * 7.7f + 0.8f) * 0.005f;
        alpha(source.alpha() * Math.max(0f, baseAlpha + slow + flame));
    }
}
