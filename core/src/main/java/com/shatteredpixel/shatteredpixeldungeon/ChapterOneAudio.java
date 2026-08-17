/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

/** Chapter-one audio scene controller. Long ambience uses Music; short event cues use Sample. */
public final class ChapterOneAudio {

    private static final String SURFACE_AMBIENCE = "music/echoes/ch1_surface_ambience.mp3";
    private static final String STREAM_AMBIENCE = "sounds/echoes/ch1_stream_loop.mp3";
    private static final String FARM_AMBIENCE = "music/echoes/ch1_farm_ambience.mp3";
    private static final String INN_MUSIC = "music/echoes/ledger_tavern.mp3";

    private static final String BIRDS = "sounds/echoes/ch1_birds.mp3";
    private static final String WOLVES = "sounds/echoes/ch1_wolves.mp3";
    private static final String DONKEY = "sounds/echoes/ch1_donkey.mp3";

    private enum Area { NONE, SURFACE, OLD_ROAD, FARM, INN }

    private static Area area = Area.NONE;
    private static String playingBed;
    private static float bedRelativeVolume = 1f;
    private static boolean samplesLoaded;
    private static boolean bgmDucked;
    private static int transientDuckTicks;
    private static boolean appPaused;
    private static boolean surfaceTransitionPending;

    private ChapterOneAudio() {
    }

    public static void preload() {
        if (samplesLoaded) return;
        samplesLoaded = true;
        Sample.INSTANCE.load(new String[]{BIRDS, WOLVES, DONKEY});
    }

    /**
     * Final Stair is the one deliberate seam between the old dungeon score and Echoes' surface soundscape.
     * The existing Noosa music engine already owns a real fade, so use it instead of hard-cutting THEME_FINALE.
     */
    public static void leaveDungeonForSurface(final Runnable onComplete) {
        if (surfaceTransitionPending) return;
        surfaceTransitionPending = true;
        area = Area.NONE;
        playingBed = null;
        bgmDucked = false;
        transientDuckTicks = 0;
        Music.INSTANCE.fadeOut(0.42f, new Callback() {
            @Override public void call() {
                Music.INSTANCE.stop();
                surfaceTransitionPending = false;
                if (onComplete != null) onComplete.run();
            }
        });
    }

    /** The dungeon theme ends before this point: first surface area is ambience-only. */
    public static void surfaceAmbience() {
        preload();
        surfaceTransitionPending = false;
        area = Area.SURFACE;
        playBed(SURFACE_AMBIENCE, 0.72f);
    }

    /** Return's old shrine gets no new mystery score: only the same forest bed, thinned out. */
    public static void returnShrineAmbience() {
        preload();
        area = Area.SURFACE;
        transientDuckTicks = 0;
        bgmDucked = false;
        playBed(SURFACE_AMBIENCE, 0.34f);
    }

    /** Old King's Road deliberately reuses the forest bed at a lower, quieter level. */
    public static void oldRoadAmbience() {
        preload();
        area = Area.OLD_ROAD;
        playBed(SURFACE_AMBIENCE, 0.46f);
    }

    public static void outskirtsAmbience() {
        preload();
        area = Area.FARM;
        playBed(FARM_AMBIENCE, 0.68f);
    }

    /** Old Crow Inn owns the chapter's indoor music bed through the same lifecycle controller. */
    public static void innAmbience() {
        preload();
        area = Area.INN;
        playBed(INN_MUSIC, 0.66f);
    }

    /**
     * The engine exposes one streamed Music bed. Near the stream we cross over from the
     * forest bed to the water recording, then return to forest outside the audible radius.
     */
    public static void updateStreamDistance(int distance) {
        if (area != Area.SURFACE || appPaused) return;
        if (distance <= 5) {
            float proximity = 1f - Math.min(5, Math.max(0, distance)) / 6f;
            playBed(STREAM_AMBIENCE, 0.42f + proximity * 0.38f);
        } else {
            playBed(SURFACE_AMBIENCE, 0.72f);
        }
    }

    public static void syncSettings() {
        if (appPaused || playingBed == null) return;
        Music.INSTANCE.volume(musicSettingFactor() * (bgmDucked ? 0.42f : bedRelativeVolume));
    }

    public static void stopAmbience() {
        area = Area.NONE;
        playingBed = null;
        bedRelativeVolume = 1f;
        bgmDucked = false;
        transientDuckTicks = 0;
        Music.INSTANCE.stop();
    }

    public static void playBird() {
        play(BIRDS, 0.30f, 1f);
    }

    /** Bell/cart sources are still intentionally omitted until a verified source is pinned. */
    public static void playFarmerApproach() {
        // Silence is preferable to a fake dungeon impact cue.
    }

    /**
     * v0.2 intentionally retires the old Yendor bass Sample after real-device testing found it
     * abrasive. The anomaly now works by briefly thinning the natural ambience only. This is a
     * deliberate quality decision, not a missing asset fallback: a future signature cue must be
     * genuinely better before it is reintroduced.
     */
    public static void playYendorPulse() {
        preload();
        if (!appPaused && playingBed != null) {
            bgmDucked = true;
            transientDuckTicks = 2;
            Music.INSTANCE.volume(musicSettingFactor() * bedRelativeVolume * 0.42f);
        }
    }

    /** Advance the very short Return-only environmental duck without adding a timer subsystem. */
    public static void tickTransientReturnAudio() {
        if (transientDuckTicks <= 0) return;
        transientDuckTicks--;
        if (transientDuckTicks == 0) restoreBgm();
    }

    public static void playDonkeyBreak() {
        play(DONKEY, 0.42f, 1f);
        restoreBgm();
    }

    /** Raven remains a declared gap; an unrelated bird cue would defeat the soundscape pass. */
    public static void playRaven() {
    }

    /**
     * Fireplace/room-tone remains a declared secondary-loop gap. Sample is intentionally
     * not abused as a looping channel; add it only when a verified source and loop path exist.
     */
    public static void syncInnRoomTone() {
        // The tavern music bed is valid; a second independent loop is not wired yet.
    }

    public static void playWolfWarning() {
        play(WOLVES, 0.38f, 1f);
    }

    public static void pause() {
        appPaused = true;
        Music.INSTANCE.pause();
    }

    public static void resume() {
        appPaused = false;
        Music.INSTANCE.resume();
        syncSettings();
    }

    public static void reset() {
        appPaused = false;
        bgmDucked = false;
        transientDuckTicks = 0;
        surfaceTransitionPending = false;
        area = Area.NONE;
        playingBed = null;
        bedRelativeVolume = 1f;
        samplesLoaded = false;
    }

    private static void playBed(String asset, float relativeVolume) {
        if (appPaused) return;
        bedRelativeVolume = relativeVolume;
        if (!asset.equals(playingBed)) {
            playingBed = asset;
            Music.INSTANCE.play(asset, true);
        }
        if (!bgmDucked) Music.INSTANCE.volume(musicSettingFactor() * bedRelativeVolume);
    }

    private static void play(String asset, float volume, float pitch) {
        if (appPaused || !SPDSettings.soundFx()) return;
        preload();
        float actual = volume * sfxSettingFactor();
        if (actual > 0f) Sample.INSTANCE.play(asset, actual, pitch);
    }

    private static float sfxSettingFactor() {
        int setting = SPDSettings.SFXVol();
        return setting * setting / 100f;
    }

    private static float musicSettingFactor() {
        int setting = SPDSettings.musicVol();
        return setting * setting / 100f;
    }

    private static void restoreBgm() {
        transientDuckTicks = 0;
        if (bgmDucked) {
            bgmDucked = false;
            if (!appPaused && playingBed != null) {
                Music.INSTANCE.volume(musicSettingFactor() * bedRelativeVolume);
            }
        }
    }
}
