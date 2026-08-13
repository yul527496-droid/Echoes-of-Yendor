/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Random;

/** Low-threat surface wildlife. RatSprite is temporary prototype art. */
public class RoadWolf extends Mob {

    {
        spriteClass = RatSprite.class;
        HP = HT = 28;
        defenseSkill = 7;
        EXP = 0;
        maxLvl = 30;
        state = WANDERING;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(3, 7);
    }

    @Override
    public int attackSkill(Char target) {
        return 10;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        boolean anotherWolfLives = false;
        if (Dungeon.level != null) {
            for (Mob mob : Dungeon.level.mobs) {
                if (mob instanceof RoadWolf && mob != this && mob.isAlive()) {
                    anotherWolfLives = true;
                    break;
                }
            }
        }

        if (!anotherWolfLives) {
            SequelState state = SequelState.get();
            if (state != null) state.wolvesDefeated = true;
        }
    }

    @Override
    public String name() {
        return "灰狼";
    }

    @Override
    public String description() {
        return "一只沿旧王道觅食的瘦狼。对毫无准备的旅人很危险，但对一个从地下城活着回来的人来说，算不上真正的威胁。";
    }
}
