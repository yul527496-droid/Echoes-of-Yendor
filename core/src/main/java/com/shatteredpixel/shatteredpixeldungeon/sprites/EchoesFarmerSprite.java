package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Dedicated roadside-farmer map sprite for Echoes. */
public class EchoesFarmerSprite extends MobSprite {
    public EchoesFarmerSprite() {
        super();
        texture(EchoesSurfaceSpriteArt.sheet(EchoesSurfaceSpriteArt.Kind.FARMER));
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(4, true); idle.frames(frames, 0, 1);
        run = new Animation(8, true); run.frames(frames, 2, 3);
        attack = idle.clone();
        die = new Animation(1, false); die.frames(frames, 0);
        play(idle);
    }
}
