/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Persistent, Echoes-specific onboarding choice.
 *
 * Kept separate from Shattered's own intro setting so the sequel tutorial never
 * changes the upstream first-run/tutorial preference.  The choice is account/
 * installation wide rather than save-slot state: the optional memory exists to
 * teach the player, not the returning hero profile.
 */
public final class EchoesOnboarding {

    public static final int UNSET = 0;
    public static final int GUIDED = 1;
    public static final int EXPERIENCED = 2;

    private static final String PREFS = "echoes-of-yendor-onboarding";
    private static final String MODE = "mode";
    private static final String TRAINING_DONE = "training_done";

    private EchoesOnboarding() {
    }

    private static Preferences prefs() {
        return Gdx.app.getPreferences(PREFS);
    }

    public static int mode() {
        return prefs().getInteger(MODE, UNSET);
    }

    public static void mode(int value) {
        int safe = value < UNSET || value > EXPERIENCED ? UNSET : value;
        prefs().putInteger(MODE, safe).flush();
    }

    public static boolean trainingDone() {
        return prefs().getBoolean(TRAINING_DONE, false);
    }

    public static void trainingDone(boolean value) {
        prefs().putBoolean(TRAINING_DONE, value).flush();
    }
}
