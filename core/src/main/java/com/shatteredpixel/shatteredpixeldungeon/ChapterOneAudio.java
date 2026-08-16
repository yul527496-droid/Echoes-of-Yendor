/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;

/** Chapter-one audio facade. */
public final class ChapterOneAudio {

    private static final String FARM_AMBIENCE = "music/echoes/ch1_farm_ambience.mp3";

    private static boolean bgmDucked;
    private static boolean appPaused;

    private ChapterOneAudio() {
    }

    public static void preload() {
        // Music is streamed by libGDX when each surface area is entered.
    }

    public static void surfaceAmbience() {
        restoreBgm();
    }

    public static void oldRoadAmbience() {
        restoreBgm();
    }

    /** The first audited Chapter 1 field recording that is wired into real playback. */
    public static void outskirtsAmbience() {
        restoreBgm();
        Music.INSTANCE.play(FARM_AMBIENCE, true);
    }

    public static void updateStreamDistance(int distance) {
        // Stream-distance playback still waits for the audited stream source binary.
    }

    public static void syncSettings() {
        if (!SPDSettings.soundFx()) restoreBgm();
    }

    public static void stopAmbience() {
        restoreBgm();
    }

    public static void playBird() {
        play(Assets.Sounds.PUFF, 0.24f, 1.18f);
    }

    public static void playFarmerApproach() {
        play(Assets.Sounds.STURDY, 0.18f, 0.82f);
    }

    public static void playYendorPulse() {
        if (!appPaused) {
            bgmDucked = true;
            Music.INSTANCE.volume(musicSettingFactor() * 0.42f);
        }
        play(Assets.Sounds.CURSED, 0.30f, 0.72f);
    }

    public static void playDonkeyBreak() {
        play(Assets.Sounds.SHEEP, 0.30f, 0.82f);
        play(Assets.Sounds.STURDY, 0.24f, 0.88f);
        restoreBgm();
    }

    public static void playRaven() {
        // Do not substitute an unrelated stock monster cue.
    }

    public static void playWolfWarning() {
        play(Assets.Sounds.ALERT, 0.22f, 0.78f);
    }

    public static void pause() {
        appPaused = true;
        restoreBgm();
    }

    public static void resume() {
        appPaused = false;
    }

    public static void reset() {
        appPaused = false;
        restoreBgm();
    }

    private static void play(String asset, float volume, float pitch) {
        if (appPaused || !SPDSettings.soundFx()) return;
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
            Music.INSTANCE.volume(musicSettingFactor());
        }
    }
}
