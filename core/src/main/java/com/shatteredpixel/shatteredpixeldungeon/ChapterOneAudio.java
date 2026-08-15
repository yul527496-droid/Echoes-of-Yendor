/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;

/**
 * Chapter-one audio facade.
 *
 * The first playable demo deliberately stays on Shattered's already-loaded audio
 * bank so the story slice can ship as one reproducible source commit. Reviewed
 * Chapter 1 field recordings remain source candidates and can replace these cues
 * without changing story or map code.
 */
public final class ChapterOneAudio {

    private static boolean bgmDucked;
    private static boolean appPaused;

    private ChapterOneAudio() {
    }

    public static void preload() {
        // Assets.Sounds.all is already loaded by ShatteredPixelDungeon.create().
    }

    public static void surfaceAmbience() {
        restoreBgm();
    }

    public static void oldRoadAmbience() {
        restoreBgm();
    }

    public static void outskirtsAmbience() {
        restoreBgm();
    }

    public static void updateStreamDistance(int distance) {
        // Spatial stream ambience is reserved for the reviewed Chapter 1 asset pass.
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
        // Intentionally quiet in the demo rather than using an unrelated monster cue.
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
