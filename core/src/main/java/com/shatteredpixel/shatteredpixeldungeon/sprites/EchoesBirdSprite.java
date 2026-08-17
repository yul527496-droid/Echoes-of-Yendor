package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

/** Production ordinary surface crow using the Chapter 1 character atlas. */
public class EchoesBirdSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_ch1_character_sprites_v1.png";
    private static final int FIRST = 16 * 8;

    public EchoesBirdSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(5, true); idle.frames(frames, FIRST, FIRST + 1);
        run = new Animation(10, true); run.frames(frames, FIRST + 2, FIRST + 3, FIRST + 4, FIRST + 5);
        attack = new Animation(10, false); attack.frames(frames, FIRST + 6, FIRST + 7, FIRST + 6);
        die = new Animation(10, false); die.frames(frames, FIRST + 7);
        play(idle);
    }

    /**
     * Presentation-only flight. CharSprite.jump owns a Noosa tweener but deliberately does not
     * set isMoving or wait on Char.onMotionComplete, so this visual never blocks the SPD actor queue.
     * The NPC is free to update its logical fixed-stop position before this interpolation begins.
     */
    public void flyTo(int from, int to, float duration, Callback callback) {
        play(run);
        float height = 5f;
        jump(from, to, height, Math.max(0.18f, duration), () -> {
            idle();
            if (callback != null) callback.call();
        });
    }
}
