package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;

final class LedgerGlow extends ColorBlock {
    private float time;

    LedgerGlow(float width, float height, int color) {
        super(width, height, color);
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;
        alpha(0.72f
                + (float)Math.sin(time * 2.3f) * 0.035f
                + (float)Math.sin(time * 7.1f + 0.8f) * 0.018f);
    }
}
