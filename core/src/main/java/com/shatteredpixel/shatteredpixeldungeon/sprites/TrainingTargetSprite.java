/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Static target sprite for the training-range graybox. */
public class TrainingTargetSprite extends MobSprite {

    private static final String TEXTURE = "sprites/training_target.png";

    public TrainingTargetSprite() {
        super();

        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);

        idle = new Animation(1, true);
        idle.frames(frames, 0);
        run = idle.clone();
        attack = idle.clone();

        die = new Animation(1, false);
        die.frames(frames, 2);

        play(idle);
    }
}
