/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrainingDummySprite;

/** Visual-only training dummy used by the first graybox pass. */
public class TrainingDummy extends NPC {

    {
        spriteClass = TrainingDummySprite.class;
    }

    @Override
    protected boolean act() {
        spend(TICK);
        return true;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public String name() {
        return "训练木人";
    }

    @Override
    public String description() {
        return "营地里的木制训练人偶。真正的受击与损坏逻辑会在教程交互阶段接入。";
    }
}
