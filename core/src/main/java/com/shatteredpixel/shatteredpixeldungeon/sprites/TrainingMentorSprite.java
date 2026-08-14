/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Visual-only trainer sprite for the tutorial graybox. */
public class TrainingMentorSprite extends MobSprite {

    private static final String TEXTURE = "sprites/veteran_trainer.png";
    private Animation gesture;

    public TrainingMentorSprite() {
        super();

        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 12, 15);

        idle = new Animation(2, true);
        idle.frames(frames, 0, 0, 0, 1, 0, 0, 1);

        run = new Animation(15, true);
        run.frames(frames, 2, 3, 4, 5, 6, 7);

        gesture = new Animation(8, false);
        gesture.frames(frames, 8, 9);

        attack = gesture.clone();
        die = new Animation(1, false);
        die.frames(frames, 0);

        play(idle);
    }

    public void gesture() {
        play(gesture);
    }

    @Override
    public void onComplete(Animation anim) {
        super.onComplete(anim);
        if (anim == gesture) idle();
    }
}
