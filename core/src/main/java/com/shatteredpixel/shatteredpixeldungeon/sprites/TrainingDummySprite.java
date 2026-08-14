/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Static training-dummy sprite. Damage states are reserved for the tutorial interaction pass. */
public class TrainingDummySprite extends MobSprite {

    private static final String TEXTURE = "sprites/training_dummy.png";

    public TrainingDummySprite() {
        super();

        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 12, 15);

        idle = new Animation(1, true);
        idle.frames(frames, 0);
        run = idle.clone();
        attack = idle.clone();

        die = new Animation(1, false);
        die.frames(frames, 3);

        play(idle);
    }
}
