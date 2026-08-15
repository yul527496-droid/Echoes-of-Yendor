package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Dedicated small surface bird for the optional Yendor foreshadowing beat. */
public class EchoesBirdSprite extends MobSprite {
    public EchoesBirdSprite() {
        super();
        texture(EchoesSurfaceSpriteArt.sheet(EchoesSurfaceSpriteArt.Kind.BIRD));
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(5, true); idle.frames(frames, 0, 1);
        run = new Animation(10, true); run.frames(frames, 2, 3);
        attack = run.clone();
        die = new Animation(10, false); die.frames(frames, 2, 3);
        play(idle);
    }
}
