package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.watabou.noosa.TextureFilm;

/**
 * Roadside farmer using the Chapter 1 production character atlas.
 *
 * Frame contract (16x16 padded slots):
 * 0-1 idle, 2-5 walk, 6 confused/fixated, 7 shaken/recoil.
 */
public class EchoesFarmerSprite extends MobSprite {

    private static final String TEXTURE = "sprites/echoes_ch1_character_sprites_v1.png";
    private static final int FIRST = 0;

    private Animation confused;
    private Animation shaken;
    private int storyPose;

    public EchoesFarmerSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);

        idle = new Animation(2, true);
        idle.frames(frames, FIRST, FIRST + 1);

        run = new Animation(8, true);
        run.frames(frames, FIRST + 2, FIRST + 3, FIRST + 4, FIRST + 5);

        confused = new Animation(1, true);
        confused.frames(frames, FIRST + 6);

        shaken = new Animation(1, true);
        shaken.frames(frames, FIRST + 7);

        attack = idle.clone();
        die = new Animation(1, false);
        die.frames(frames, FIRST);
        play(idle);
    }

    @Override
    public void update() {
        super.update();
        if (ch == null || !ch.isAlive() || isMoving) return;

        SequelState story = SequelState.get();
        int desiredPose = 0;
        if (story != null) {
            if (story.isAtLeast(SequelState.Phase.AMULET_FLARE_DONE)
                    && !story.isAtLeast(SequelState.Phase.FARMER_RECOVERED)) {
                desiredPose = 2;
            } else if (story.isAtLeast(SequelState.Phase.FARMER_NORMAL_TALK_DONE)
                    && !story.isAtLeast(SequelState.Phase.AMULET_FLARE_DONE)) {
                desiredPose = 1;
            }
        }

        if (desiredPose == storyPose) return;
        storyPose = desiredPose;
        if (storyPose == 1) play(confused);
        else if (storyPose == 2) play(shaken);
        else play(idle);
    }
}
