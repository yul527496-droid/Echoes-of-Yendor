package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Dedicated donkey-and-cart map sprite for Echoes. */
public class EchoesDonkeyCartSprite extends MobSprite {
    public EchoesDonkeyCartSprite() {
        super();
        texture(EchoesSurfaceSpriteArt.sheet(EchoesSurfaceSpriteArt.Kind.DONKEY_CART));
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(3, true); idle.frames(frames, 0, 1);
        run = new Animation(7, true); run.frames(frames, 2, 3);
        attack = idle.clone();
        die = new Animation(1, false); die.frames(frames, 0);
        play(idle);
    }
}
