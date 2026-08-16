/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Dedicated Old Crow innkeeper production sprite. */
public class EchoesInnkeeperSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_ch1_character_sprites_v1.png";
    private static final int FIRST = 8;

    public EchoesInnkeeperSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);

        idle = new Animation(3, true);
        idle.frames(frames, FIRST, FIRST + 1, FIRST);

        run = new Animation(8, true);
        run.frames(frames, FIRST + 2, FIRST + 3, FIRST + 4, FIRST + 5);

        operate = new Animation(5, false);
        operate.frames(frames, FIRST + 6, FIRST + 7);

        attack = operate.clone();
        die = new Animation(1, false);
        die.frames(frames, FIRST);
        play(idle);
    }
}
