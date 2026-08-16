/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

/** Eight two-frame civilian silhouettes repainted around SPD's compact human-NPC scale. */
public class SurfaceVillagerSprite extends MobSprite {

    private static final String TEXTURE = "sprites/surface_villagers_v3.png";

    public SurfaceVillagerSprite() {
        super();
        texture(TEXTURE);
        TextureFilm film = new TextureFilm(texture, 16, 16);
        int first = Random.Int(8) * 2;

        idle = new Animation(3, true);
        idle.frames(film, first, first, first + 1, first, first);

        run = new Animation(8, true);
        run.frames(film, first, first + 1);

        attack = idle.clone();
        die = new Animation(20, false);
        die.frames(film, first);
        idle();
    }
}
