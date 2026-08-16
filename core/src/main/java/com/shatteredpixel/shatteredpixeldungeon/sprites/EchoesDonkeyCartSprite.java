package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** File-backed donkey-and-cart sprite; each frame is 32x16 so the cart reads at map scale. */
public class EchoesDonkeyCartSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_donkey_cart_v1.png";

    public EchoesDonkeyCartSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 32, 16);
        idle = new Animation(3, true); idle.frames(frames, 0, 1);
        run = new Animation(7, true); run.frames(frames, 2, 3);
        attack = idle.clone();
        die = new Animation(1, false); die.frames(frames, 0);
        play(idle);
    }
}
