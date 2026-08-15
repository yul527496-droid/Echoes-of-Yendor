/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.utils.Random;

/**
 * Uses the original rat sprite and behaviour, but with deliberately gentle
 * numbers so the last tutorial beat tests decisions rather than punishing a
 * first-time player.
 */
public class TrainingRat extends Rat {

    {
        HP = HT = 5;
        defenseSkill = 1;
        EXP = 0;
        maxLvl = 0;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 2);
    }

    @Override
    public int attackSkill(Char target) {
        return 6;
    }

    @Override
    public int drRoll() {
        return 0;
    }
}
