/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.watabou.noosa.Image;

/** Native 48px dialogue portraits authored directly for Echoes' SPD-scale UI. */
final class EchoesDialoguePortraits {

    private static final String TEXTURE = "interfaces/echoes/echoes_dialogue_portraits_v2.png";
    private static final int SIZE = 48;

    enum HeroExpression {
        NEUTRAL, ALERT, CONCERNED
    }

    // Heroes occupy frames 0..17: class-major, then neutral/alert/concerned.
    private static final int HERO_EXPRESSIONS = 3;
    private static final int HERO_WARRIOR = 0;
    private static final int HERO_MAGE = 1;
    private static final int HERO_ROGUE = 2;
    private static final int HERO_HUNTRESS = 3;
    private static final int HERO_DUELIST = 4;
    private static final int HERO_CLERIC = 5;

    private static final int FARMER_NEUTRAL = 18;
    private static final int FARMER_WARM = 19;
    private static final int FARMER_CONFUSED = 20;
    private static final int FARMER_FIXATED = 21;
    private static final int FARMER_SHAKEN = 22;

    private static final int INNKEEPER_NEUTRAL = 23;
    private static final int INNKEEPER_ATTENTIVE = 24;
    private static final int INNKEEPER_SERIOUS = 25;

    private EchoesDialoguePortraits() {}

    static Image hero(HeroClass heroClass, HeroExpression expression) {
        int classIndex;
        if (heroClass == null) classIndex = HERO_WARRIOR;
        else {
            switch (heroClass) {
                case MAGE: classIndex = HERO_MAGE; break;
                case ROGUE: classIndex = HERO_ROGUE; break;
                case HUNTRESS: classIndex = HERO_HUNTRESS; break;
                case DUELIST: classIndex = HERO_DUELIST; break;
                case CLERIC: classIndex = HERO_CLERIC; break;
                case WARRIOR:
                default: classIndex = HERO_WARRIOR; break;
            }
        }
        int expressionIndex = expression == null ? 0 : expression.ordinal();
        return frame(classIndex * HERO_EXPRESSIONS + expressionIndex);
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
