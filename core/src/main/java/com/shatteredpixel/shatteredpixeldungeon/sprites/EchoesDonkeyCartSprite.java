package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/**
 * File-backed donkey-and-cart sprite; each frame is 32x16 so the cart reads at map scale.
 * The sheet remains intentionally side-view only, so visible north/south cart travel is
 * suppressed by RoadDonkey rather than faked with a mirrored sprite.
 */
public class EchoesDonkeyCartSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_donkey_cart_v2.png";

    public EchoesDonkeyCartSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 32, 16);
        idle = new Animation(1, true);
        idle.frames(frames, 0);
        run = new Animation(7, true);
        run.frames(frames, 2, 3);
        attack = idle.clone();
        die = new Animation(1, false);
        die.frames(frames, 0);
        flipHorizontal = false;
        play(idle);
    }

    @Override
    public void turnTo(int from, int to) {
        flipHorizontal = false;
    }
}
