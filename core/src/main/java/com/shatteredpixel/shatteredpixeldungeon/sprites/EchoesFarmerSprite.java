package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** File-backed roadside farmer repainted against SPD's compact 16px human-NPC scale. */
public class EchoesFarmerSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_farmer_v2.png";

    public EchoesFarmerSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(4, true); idle.frames(frames, 0, 1);
        run = new Animation(8, true); run.frames(frames, 2, 3);
        attack = idle.clone();
        die = new Animation(1, false); die.frames(frames, 0);
        play(idle);
    }
}
