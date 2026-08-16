/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Dedicated Old Crow innkeeper sprite; no longer a recolored generic resident. */
public class EchoesInnkeeperSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_innkeeper_v1.png";

    public EchoesInnkeeperSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(2, true); idle.frames(frames, 0, 1);
        run = new Animation(7, true); run.frames(frames, 2, 3);
        attack = idle.clone();
        die = new Animation(1, false); die.frames(frames, 0);
        play(idle);
    }
}
