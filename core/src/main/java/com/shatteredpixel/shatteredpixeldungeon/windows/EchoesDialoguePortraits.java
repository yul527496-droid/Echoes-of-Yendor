/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.watabou.noosa.Image;

/** Native 48px dialogue portraits authored directly for Echoes' SPD-scale UI. */
final class EchoesDialoguePortraits {

    private static final String TEXTURE = "interfaces/echoes/echoes_dialogue_portraits_v1.png";
    private static final int SIZE = 48;

    private static final int HERO_WARRIOR = 0;
    private static final int HERO_MAGE = 1;
    private static final int HERO_ROGUE = 2;
    private static final int HERO_HUNTRESS = 3;
    private static final int HERO_DUELIST = 4;
    private static final int HERO_CLERIC = 5;

    private static final int FARMER_NEUTRAL = 6;
    private static final int FARMER_WARM = 7;
    private static final int FARMER_CONFUSED = 8;
    private static final int FARMER_FIXATED = 9;
    private static final int FARMER_SHAKEN = 10;

    private static final int INNKEEPER_NEUTRAL = 11;
    private static final int INNKEEPER_ATTENTIVE = 12;
    private static final int INNKEEPER_SERIOUS = 13;

    private EchoesDialoguePortraits() {}

    static Image hero(HeroClass heroClass) {
        if (heroClass == null) return frame(HERO_WARRIOR);
        switch (heroClass) {
            case MAGE: return frame(HERO_MAGE);
            case ROGUE: return frame(HERO_ROGUE);
            case HUNTRESS: return frame(HERO_HUNTRESS);
            case DUELIST: return frame(HERO_DUELIST);
            case CLERIC: return frame(HERO_CLERIC);
            case WARRIOR:
            default: return frame(HERO_WARRIOR);
        }
    }

    static Image image(WndDialogueStage.Portrait portrait) {
        switch (portrait) {
            case FARMER_WARM: return frame(FARMER_WARM);
            case FARMER_CONFUSED: return frame(FARMER_CONFUSED);
            case FARMER_FIXATED: return frame(FARMER_FIXATED);
            case FARMER_SHAKEN: return frame(FARMER_SHAKEN);
            case INNKEEPER_NEUTRAL: return frame(INNKEEPER_NEUTRAL);
            case INNKEEPER_ATTENTIVE: return frame(INNKEEPER_ATTENTIVE);
            case INNKEEPER_SERIOUS: return frame(INNKEEPER_SERIOUS);
            case FARMER_NEUTRAL:
            default: return frame(FARMER_NEUTRAL);
        }
    }

    private static Image frame(int index) {
        Image image = new Image(TEXTURE);
        image.frame(index * SIZE, 0, SIZE, SIZE);
        return image;
    }
}
