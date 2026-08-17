/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesWolfSprite;
import com.watabou.utils.Random;

/** Low-stakes roadside wolf: ordinary scale after the Yog victory, not a boss. */
public class ActOneReturnWolf extends Mob {

    {
        spriteClass = EchoesWolfSprite.class;
        HP = HT = 12;
        defenseSkill = 4;
        EXP = 0;
        maxLvl = 30;
    }

    @Override
    protected boolean act() {
        ActOneReturnState state = ActOneReturnState.current();
        if (state != null && state.wolvesOutcome == ActOneReturnState.WolvesOutcome.ABANDONED) {
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }
        return super.act();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(2, 5);
    }

    @Override
    public int attackSkill(Char target) {
        return 9;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }

    @Override
    public String name() {
        return "灰狼";
    }

    @Override
    public String description() {
        return "一头被饥饿逼到旧路边的灰狼。和地下深处那些东西相比，它只是野兽。";
    }
}
