/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;

/** Stable production-art paths for Chapter 1 character presentation. */
public final class EchoesCharacterArt {

    public static final String FARMER_SPLASH = "splashes/echoes/ch1/farmer_v3.png";
    public static final String INNKEEPER_SPLASH = "splashes/echoes/ch1/innkeeper_v1.png";
    public static final String BIGORANGER_SPLASH = "splashes/echoes/ch1/bigoranger_v2.png";
    public static final String MASTER_SPLASH = "splashes/echoes/ch1/master_v2.png";
    public static final String SERENA_SPLASH = "splashes/echoes/ch1/serena_v2.png";
    public static final String ELAN_SPLASH = "splashes/echoes/ch1/elan_v2.png";
    public static final String SINDEL_SPLASH = "splashes/echoes/ch1/sindel_v2.png";

    private EchoesCharacterArt() {}

    public static String returningHeroSplash(HeroClass heroClass) {
        String id;
        if (heroClass == null) id = "warrior";
        else switch (heroClass) {
            case MAGE: id = "mage"; break;
            case ROGUE: id = "rogue"; break;
            case HUNTRESS: id = "huntress"; break;
            case DUELIST: id = "duelist"; break;
            case CLERIC: id = "cleric"; break;
            case WARRIOR:
            default: id = "warrior"; break;
        }
        return "splashes/echoes/ch1/returning_" + id + "_v1.png";
    }
}
