package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** File-backed small surface bird for the optional Yendor foreshadowing beat. */
public class EchoesBirdSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_bird_v1.png";

    public EchoesBirdSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(5, true); idle.frames(frames, 0, 1);
        run = new Animation(10, true); run.frames(frames, 2, 3);
        attack = run.clone();
        die = new Animation(10, false); die.frames(frames, 2, 3);
        play(idle);
    }
}
