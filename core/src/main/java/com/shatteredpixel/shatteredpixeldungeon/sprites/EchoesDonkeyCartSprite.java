package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/**
 * File-backed donkey-and-cart sprite; each frame is 32x16 so the cart reads at map scale.
 *
 * The current sheet is deliberately side-view only. SPD's normal CharSprite facing model
 * mirrors sprites left/right and has no north/south frame selection, so a two-cell-wide cart
 * must not inherit random facing or a stepping idle while it is staged as parked scenery.
 */
public class EchoesDonkeyCartSprite extends MobSprite {
    private static final String TEXTURE = "sprites/echoes_donkey_cart_v1.png";

    public EchoesDonkeyCartSprite() {
        super();
        texture(TEXTURE);
        TextureFilm frames = new TextureFilm(texture, 32, 16);

        // A parked cart should read as parked. The old 0/1 idle loop made the legs
        // continuously step in place and amplified the impression that the cart was glitching.
        idle = new Animation(1, true);
        idle.frames(frames, 0);

        // Retained for off-screen catch-up only; visible cart travel is suppressed by RoadDonkey.
        run = new Animation(7, true);
        run.frames(frames, 2, 3);

        attack = idle.clone();
        die = new Animation(1, false);
        die.frames(frames, 0);
        flipHorizontal = false;
        play(idle);
    }

    /**
     * Lock the authored orientation. A side-view cart cannot truthfully turn north/south,
     * and random horizontal mirroring on link() made identical scenes load facing opposite ways.
     */
    @Override
    public void turnTo(int from, int to) {
        flipHorizontal = false;
    }
}
