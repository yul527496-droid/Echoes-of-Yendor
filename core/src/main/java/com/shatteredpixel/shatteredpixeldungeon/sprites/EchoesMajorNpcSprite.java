/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Shared animation contract for the five named Morningcreek NPC production sprites. */
public abstract class EchoesMajorNpcSprite extends MobSprite {

    protected static final String TEXTURE = "sprites/echoes_ch1_character_sprites_v1.png";

    protected EchoesMajorNpcSprite(int row) {
        super();
        texture(TEXTURE);
        TextureFilm film = new TextureFilm(texture, 16, 16);
        int first = row * 8;

        idle = new Animation(3, true);
        idle.frames(film, first, first + 1, first);

        run = new Animation(8, true);
        run.frames(film, first + 2, first + 3, first + 4, first + 5);

        operate = new Animation(5, false);
        operate.frames(film, first + 6, first + 7);

        attack = operate.clone();
        die = new Animation(1, false);
        die.frames(film, first);
        idle();
    }
}
