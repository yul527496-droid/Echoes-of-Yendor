/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;

/** Chapter-one audio scene controller. Long ambience uses Music; short event cues use Sample. */
public final class ChapterOneAudio {

    private static final String SURFACE_AMBIENCE = "music/echoes/ch1_surface_ambience.mp3";
    private static final String STREAM_AMBIENCE = "sounds/echoes/ch1_stream_loop.mp3";
    private static final String FARM_AMBIENCE = "music/echoes/ch1_farm_ambience.mp3";
    private static final String INN_MUSIC = "music/echoes/ledger_tavern.mp3";

    private static final String BIRDS = "sounds/echoes/ch1_birds.mp3";
    private static final String WOLVES = "sounds/echoes/ch1_wolves.mp3";
    private static final String YENDOR_BASS = "sounds/echoes/ch1_yendor_bass.mp3";
    private static final String DONKEY = "sounds/echoes/ch1_donkey.mp3";

    private enum Area { NONE, SURFACE, OLD_ROAD, FARM, INN }

    private static Area area = Area.NONE;
    private static String playingBed;
    private static float bedRelativeVolume = 1f;
    private static boolean samplesLoaded;
    private static boolean bgmDucked;
    private static boolean appPaused;

    private ChapterOneAudio() {
    }

    public static void preload() {
        if (samplesLoaded) return;
        samplesLoaded = true;
        Sample.INSTANCE.load(new String[]{BIRDS, WOLVES, YENDOR_BASS, DONKEY});
    }

    /** The dungeon theme ends here: first surface area is ambience-only. */
    public static void surfaceAmbience() {
        preload();
        area = Area.SURFACE;
        playBed(SURFACE_AMBIENCE, 0.72f);
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
        Music.INSTANCE.volume(musicSettingFactor() * (bgmDucked ? 0.30f : bedRelativeVolume));
    }

    public static void stopAmbience() {
        area = Area.NONE;
        playingBed = null;
        bedRelativeVolume = 1f;
        bgmDucked = false;
        Music.INSTANCE.stop();
    }

    public static void playBird() {
        play(BIRDS, 0.30f, 1f);
    }

    /** Bell/cart sources are still intentionally omitted until a verified source is pinned. */
    public static void playFarmerApproach() {
        // Silence is preferable to a fake dungeon impact cue.
    }

    public static void playYendorPulse() {
        preload();
        if (!appPaused) {
            bgmDucked = true;
            Music.INSTANCE.volume(musicSettingFactor() * bedRelativeVolume * 0.30f);
        }
        play(YENDOR_BASS, 0.48f, 0.90f);
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
    }

    public static void reset() {
        appPaused = false;
        bgmDucked = false;
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
        if (bgmDucked) {
            bgmDucked = false;
            Music.INSTANCE.volume(musicSettingFactor() * bedRelativeVolume);
        }
    }
}
