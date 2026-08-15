package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
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

    private static final float BGM_VOLUME = 0.17f;
    private static final float AMBIENCE_VOLUME = 0.075f;

    private static boolean shortLoaded;
    private static Music bgm;
    private static Music ambience;
    private static float fade;
    private static float fadeTarget;
    private static float fadeRate = 1f / 1.35f;

    private LedgerAudio() {}

    static void enter() {
        ensureShortSfx();

        // Gameplay levels (including the training memory) use Noosa's global
        // Music.INSTANCE channel. The ledger currently owns a separate LibGDX
        // presentation BGM, so explicitly retire the gameplay channel before
        // starting it. Without this handoff, returning from training leaves the
        // level theme running underneath the tavern music.
        com.watabou.noosa.audio.Music.INSTANCE.end();

        if (bgm == null) bgm = openMusic(TAVERN_BGM, true);
        if (ambience == null) ambience = openMusic(TAVERN_AMBIENCE, true);

        fadeTarget = 1f;
        fadeRate = 1f / 1.35f;
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
        if (fade == fadeTarget) return;
        float step = Math.max(0f, Game.elapsed) * fadeRate;
        if (fade < fadeTarget) {
            fade = Math.min(fadeTarget, fade + step);
        } else {
            fade = Math.max(fadeTarget, fade - step);
        }
        applyVolumes();
    }

    static void fadeOut(float seconds) {
        fadeTarget = 0f;
        fadeRate = seconds <= 0f ? 1000f : 1f / seconds;
    }

    static void leave() {
        stopMusic(bgm);
        stopMusic(ambience);
        bgm = null;
        ambience = null;
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

    private static void applyVolumes() {
        if (bgm != null) bgm.setVolume(BGM_VOLUME * fade);
        if (ambience != null) ambience.setVolume(AMBIENCE_VOLUME * fade);
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
