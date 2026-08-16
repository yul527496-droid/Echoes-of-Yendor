package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.watabou.noosa.TextureFilm;

/**
 * Roadside farmer using the native-pixel v3 sheet.
 *
 * Frame contract (all 16x16):
 * 0-1 idle, 2-5 walk, 6 confused/fixated, 7 shaken/recoil.
 * The two story reaction frames are selected from stable SequelState checkpoints so
 * save/reload and dialogue paging cannot leave the sprite in a random pose.
 */
public class EchoesFarmerSprite extends MobSprite {

    private static final String TEXTURE = "sprites/echoes_farmer_v3.png";

    private Animation confused;
    private Animation shaken;
    private int storyPose;

    public EchoesFarmerSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);

        // Ordinary SPD NPC motion is intentionally restrained: a quiet two-frame idle and
        // a four-frame walk. The character does not bounce every frame while standing still.
        idle = new Animation(2, true);
        idle.frames(frames, 0, 1);

        run = new Animation(8, true);
        run.frames(frames, 2, 3, 4, 5);

        confused = new Animation(1, true);
        confused.frames(frames, 6);

        shaken = new Animation(1, true);
        shaken.frames(frames, 7);

        attack = idle.clone();
        die = new Animation(1, false);
        die.frames(frames, 0);

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
