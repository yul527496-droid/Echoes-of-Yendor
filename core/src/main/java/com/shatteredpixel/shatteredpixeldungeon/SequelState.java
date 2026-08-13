/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.watabou.utils.Bundle;

/**
 * Small persistent story-state carrier for the sequel prototype.
 * Keeping these flags on the hero means the normal hero save bundle carries them too.
 */
public class SequelState extends Buff {

    private static final String FARMER_MET = "farmer_met";
    private static final String WOLVES_DEFEATED = "wolves_defeated";
    private static final String CAMP_READ = "camp_read";
    private static final String BIRD_GONE = "bird_gone";

    public boolean farmerMet;
    public boolean wolvesDefeated;
    public boolean campRead;
    public boolean birdGone;

    public static SequelState get() {
        if (Dungeon.hero == null) return null;
        SequelState state = Dungeon.hero.buff(SequelState.class);
        return state != null ? state : Buff.affect(Dungeon.hero, SequelState.class);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FARMER_MET, farmerMet);
        bundle.put(WOLVES_DEFEATED, wolvesDefeated);
        bundle.put(CAMP_READ, campRead);
        bundle.put(BIRD_GONE, birdGone);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        farmerMet = bundle.getBoolean(FARMER_MET);
        wolvesDefeated = bundle.getBoolean(WOLVES_DEFEATED);
        campRead = bundle.getBoolean(CAMP_READ);
        birdGone = bundle.getBoolean(BIRD_GONE);
    }
}
