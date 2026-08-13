package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;

final class LedgerFlame extends ColorBlock {
    private final float baseW;
    private final float baseH;
    private float baseX;
    private float baseY;
    private float time;
    private boolean anchored;

    LedgerFlame(float width, float height, int color) {
        super(width, height, color);
        baseW = width;
        baseH = height;
    }

    @Override
    public void update() {
        super.update();
        if (!anchored) {
            baseX = x;
            baseY = y;
            anchored = true;
        }
        time += Game.elapsed;
        float sway = (float)Math.sin(time * 8.3f);
        float pulse = (float)Math.sin(time * 11.7f + 0.6f);
        x = baseX + Math.round(sway);
        y = baseY - Math.max(0, Math.round(pulse));
        size(baseW, Math.max(5f, baseH + pulse * 1.4f));
    }
}
