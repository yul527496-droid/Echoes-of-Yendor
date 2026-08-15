package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Dedicated ordinary grey-wolf map sprite for the Old King's Road encounter. */
public class EchoesWolfSprite extends MobSprite {
    public EchoesWolfSprite() {
        super();
        texture(EchoesSurfaceSpriteArt.sheet(EchoesSurfaceSpriteArt.Kind.WOLF));
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(4, true); idle.frames(frames, 0, 1);
        run = new Animation(9, true); run.frames(frames, 1, 2);
        attack = new Animation(12, false); attack.frames(frames, 2, 3, 1);
        die = new Animation(6, false); die.frames(frames, 3);
        play(idle);
    }
}
