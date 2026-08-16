package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** File-backed ordinary grey wolf repainted for SPD-scale silhouette readability. */
public class EchoesWolfSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_wolf_v2.png";

    public EchoesWolfSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(4, true); idle.frames(frames, 0, 1);
        run = new Animation(9, true); run.frames(frames, 1, 2);
        attack = new Animation(12, false); attack.frames(frames, 2, 3, 1);
        die = new Animation(6, false); die.frames(frames, 3);
        play(idle);
    }
}
