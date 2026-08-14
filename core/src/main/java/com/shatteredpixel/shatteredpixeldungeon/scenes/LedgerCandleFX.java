package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

/** Runtime candle flame and local light modulation for the 480x270 ledger plate. */
final class LedgerCandleFX extends Component {

    private final Image source;
    private final Image light;
    private final Image flame;
    private float time;

    static LedgerCandleFX openBook(Image source) {
        return new LedgerCandleFX(source);
    }

    static LedgerCandleFX closedBook(Image source) {
        return new LedgerCandleFX(source);
    }

    private LedgerCandleFX(Image source) {
        super();
        this.source = source;

        light = LedgerEnvironment.tryLoad(LedgerEnvironment.LEDGER_LIGHT);
        if (light != null) {
            light.scale.set(source.scale.x, source.scale.y);
            light.x = source.x;
            light.y = source.y;
            light.alpha(0.20f);
            add(light);
        }

        Image loadedFlame = LedgerEnvironment.tryLoad(LedgerEnvironment.CANDLE_FLAME);
        if (loadedFlame != null) {
            // A six-frame horizontal sprite sheet, 16x16 per frame.
            loadedFlame.frame(0, 0, 16, 16);
            loadedFlame.scale.set(source.scale.x, source.scale.y);
            add(loadedFlame);
        }
        flame = loadedFlame;
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;

        if (light != null) {
            light.scale.set(source.scale.x, source.scale.y);
            light.x = source.x;
            light.y = source.y;
            light.alpha(source.alpha() * (0.17f + 0.035f * (float)Math.sin(time * 5.2f)));
        }

        if (flame != null) {
            int frame = ((int)(time / 0.12f)) % 6;
            flame.frame(frame * 16, 0, 16, 16);
            flame.scale.set(source.scale.x, source.scale.y);

            // Flame anchor in the 480x270 base plate.
            flame.x = source.x + 43f * source.scale.x;
            flame.y = source.y + 39f * source.scale.y;
            flame.alpha(source.alpha());
        }
    }
}
