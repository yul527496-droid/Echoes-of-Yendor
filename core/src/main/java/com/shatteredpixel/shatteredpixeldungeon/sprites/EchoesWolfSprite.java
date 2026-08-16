package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Production grey wolf using the Chapter 1 character atlas. */
public class EchoesWolfSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_ch1_character_sprites_v1.png";
    private static final int FIRST = 15 * 8;

    public EchoesWolfSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(4, true); idle.frames(frames, FIRST, FIRST + 1);
        run = new Animation(9, true); run.frames(frames, FIRST + 2, FIRST + 3, FIRST + 4, FIRST + 5);
        attack = new Animation(12, false); attack.frames(frames, FIRST + 6, FIRST + 7, FIRST + 6);
        die = new Animation(6, false); die.frames(frames, FIRST + 7);
        play(idle);
    }
}
