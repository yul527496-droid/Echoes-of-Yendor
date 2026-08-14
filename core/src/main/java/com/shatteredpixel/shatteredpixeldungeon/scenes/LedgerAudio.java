package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

/**
 * Audio routing for the ledger vertical slice.
 *
 * Optional Echoes-specific assets take priority when present. Until those
 * licensed files are committed, the original SPD effects provide safe,
 * already-packaged fallbacks so the interaction timings can be tuned now.
 */
final class LedgerAudio {

    static final String BOOK_OPEN = "sounds/echoes/ledger_book_open.mp3";
    static final String PAGE_TURN = "sounds/echoes/ledger_page_turn.mp3";
    static final String PEN_WRITE = "sounds/echoes/ledger_pen_write.mp3";
    static final String STAMP = "sounds/echoes/ledger_stamp.mp3";
    static final String TAVERN_AMBIENCE = "music/echoes/ledger_tavern.mp3";

    private static boolean shortLoaded;
    private static com.badlogic.gdx.audio.Music ambience;
    private static float ambienceFade;
    private static long lastUpdateNanos;

    private LedgerAudio() {}

    static void enter() {
        ensureShortSfx();
        if (ambience == null && exists(TAVERN_AMBIENCE)) {
            try {
                ambience = Gdx.audio.newMusic(Gdx.files.internal(TAVERN_AMBIENCE));
                ambience.setLooping(true);
                ambience.setVolume(0f);
                ambience.play();
                ambienceFade = 0f;
                lastUpdateNanos = 0L;
            } catch (Exception e) {
                Game.reportException(e);
                ambience = null;
            }
        }
    }

    static void update() {
        if (ambience == null || !ambience.isPlaying() || ambienceFade >= 1f) return;

        // Intro briefly owns both open- and closed-book candle FX. A tiny
        // real-time guard prevents the same fade being advanced twice in one frame.
        long now = System.nanoTime();
        if (lastUpdateNanos != 0L && now - lastUpdateNanos < 1_000_000L) return;
        lastUpdateNanos = now;

        ambienceFade = Math.min(1f, ambienceFade + Game.elapsed / 1.35f);
        ambience.setVolume(0.15f * ambienceFade);
    }

    static void leave() {
        if (ambience != null) {
            try {
                ambience.stop();
                ambience.dispose();
            } catch (Exception ignored) {
                // Do not let optional ambience interfere with entering the game.
            }
            ambience = null;
            ambienceFade = 0f;
            lastUpdateNanos = 0L;
        }
    }

    static void bookOpen() {
        play(BOOK_OPEN, Assets.Sounds.OPEN, 0.58f, 0.94f);
    }

    static void pageTurn() {
        play(PAGE_TURN, Assets.Sounds.READ, 0.42f, 1.04f);
    }

    static void write() {
        play(PEN_WRITE, Assets.Sounds.READ, 0.28f, 1.28f);
    }

    static void stamp() {
        play(STAMP, Assets.Sounds.STURDY, 0.82f, 0.91f);
    }

    static void erase() {
        Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH, 0.30f, 0.78f);
    }

    private static void ensureShortSfx() {
        if (shortLoaded) return;
        shortLoaded = true;
        if (exists(BOOK_OPEN)) Sample.INSTANCE.load(BOOK_OPEN);
        if (exists(PAGE_TURN)) Sample.INSTANCE.load(PAGE_TURN);
        if (exists(PEN_WRITE)) Sample.INSTANCE.load(PEN_WRITE);
        if (exists(STAMP)) Sample.INSTANCE.load(STAMP);
    }

    private static void play(String preferred, String fallback, float volume, float pitch) {
        ensureShortSfx();
        if (exists(preferred)) {
            Sample.INSTANCE.play(preferred, volume, pitch);
        } else {
            Sample.INSTANCE.play(fallback, volume, pitch);
        }
    }

    private static boolean exists(String path) {
        try {
            return Gdx.files.internal(path).exists();
        } catch (Throwable ignored) {
            return false;
        }
    }
}
