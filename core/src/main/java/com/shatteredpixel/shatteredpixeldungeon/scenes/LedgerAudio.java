package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

/** Audio routing for the complete ledger scene. */
final class LedgerAudio {

    static final String BOOK_OPEN = "sounds/echoes/ledger_book_open.mp3";
    static final String PAGE_TURN = "sounds/echoes/ledger_page_turn.mp3";
    static final String BOOK_CLOSE = "sounds/echoes/ledger_book_close.mp3";
    static final String PEN_WRITE = "sounds/echoes/ledger_pen_write.mp3";
    static final String STAMP = "sounds/echoes/ledger_stamp.mp3";
    static final String TAVERN_BGM = "music/echoes/ledger_tavern.mp3";
    static final String TAVERN_AMBIENCE = "music/echoes/ledger_ambience.mp3";

    private static final float AMBIENCE_VOLUME = 0.075f;

    private static boolean shortLoaded;
    private static boolean active;
    // BGM deliberately does NOT live here. Music.INSTANCE is the one and only
    // main-music channel for dungeon levels, training, surface maps, and Ledger.
    // Ambience remains an independent presentation layer.
    private static Music ambience;
    private static float fade;
    private static float fadeTarget;
    private static float fadeRate = 1f / 1.35f;

    private LedgerAudio() {}

    static void enter() {
        ensureShortSfx();

        if (!active) {
            active = true;
            fade = 0f;
            fadeTarget = 1f;
            fadeRate = 1f / 1.35f;

            // Start the Ledger track on Shattered's mature global channel.  play()
            // retires any previous level theme itself, so a training/surface BGM
            // can never remain underneath the book music as a second main track.
            com.watabou.noosa.audio.Music.INSTANCE.volume(0f);
            com.watabou.noosa.audio.Music.INSTANCE.play(TAVERN_BGM, true);
        } else {
            // Re-entering on another Ledger page keeps the same track alive.
            com.watabou.noosa.audio.Music.INSTANCE.play(TAVERN_BGM, true);
            fadeTarget = 1f;
        }

        if (ambience == null) ambience = openMusic(TAVERN_AMBIENCE, true);
        applyVolumes();
    }

    /** One driver is added to every ledger scene; audio no longer depends on candle FX. */
    static Component driver() {
        return new Component() {
            @Override public void update() {
                super.update();
                LedgerAudio.update();
            }
        };
    }

    static void update() {
        if (fade != fadeTarget) {
            float step = Math.max(0f, Game.elapsed) * fadeRate;
            if (fade < fadeTarget) {
                fade = Math.min(fadeTarget, fade + step);
            } else {
                fade = Math.max(fadeTarget, fade - step);
            }
        }
        // Re-apply every frame so changing the user's music preference while a
        // Ledger window is open immediately affects both BGM and ambience.
        applyVolumes();
    }

    static void fadeOut(float seconds) {
        fadeTarget = 0f;
        fadeRate = seconds <= 0f ? 1000f : 1f / seconds;
    }

    static void leave() {
        com.watabou.noosa.audio.Music.INSTANCE.end();
        // Restore Shattered's global user volume before the next gameplay scene
        // starts its own track; the Ledger fade must not leak into later levels.
        com.watabou.noosa.audio.Music.INSTANCE.volume(userMusicGain());

        stopMusic(ambience);
        ambience = null;
        active = false;
        fade = 0f;
        fadeTarget = 0f;
        fadeRate = 1f / 1.35f;
    }

    static void bookOpen() {
        play(BOOK_OPEN, Assets.Sounds.OPEN, 0.66f, 0.96f);
    }

    static void pageTurn() {
        play(PAGE_TURN, Assets.Sounds.READ, 0.48f, 1.00f);
    }

    static void bookClose() {
        play(BOOK_CLOSE, Assets.Sounds.OPEN, 0.70f, 0.82f);
    }

    static void write() {
        play(PEN_WRITE, Assets.Sounds.READ, 0.33f, 1.16f);
    }

    static void stamp() {
        play(STAMP, Assets.Sounds.STURDY, 0.84f, 0.94f);
    }

    static void erase() {
        Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH, 0.24f, 0.72f);
    }

    private static Music openMusic(String path, boolean looping) {
        if (!exists(path)) return null;
        try {
            Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
            music.setLooping(looping);
            music.setVolume(0f);
            music.play();
            return music;
        } catch (Throwable error) {
            Game.reportException(error);
            return null;
        }
    }

    private static void stopMusic(Music music) {
        if (music == null) return;
        try {
            music.stop();
            music.dispose();
        } catch (Throwable ignored) {
            // Optional presentation audio must never block entering the game.
        }
    }

    private static float userMusicGain() {
        if (!SPDSettings.music()) return 0f;
        int setting = SPDSettings.musicVol();
        return setting * setting / 100f;
    }

    private static void applyVolumes() {
        float userGain = userMusicGain();
        com.watabou.noosa.audio.Music.INSTANCE.volume(userGain * fade);
        if (ambience != null) ambience.setVolume(AMBIENCE_VOLUME * userGain * fade);
    }

    private static void ensureShortSfx() {
        if (shortLoaded) return;
        shortLoaded = true;
        if (exists(BOOK_OPEN)) Sample.INSTANCE.load(BOOK_OPEN);
        if (exists(PAGE_TURN)) Sample.INSTANCE.load(PAGE_TURN);
        if (exists(BOOK_CLOSE)) Sample.INSTANCE.load(BOOK_CLOSE);
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
