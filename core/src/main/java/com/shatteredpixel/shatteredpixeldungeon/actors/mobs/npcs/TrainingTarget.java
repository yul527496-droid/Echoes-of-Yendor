/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrainingTargetSprite;

/** Visual-only ranged target used by the training-ground graybox. */
public class TrainingTarget extends NPC {

    {
        spriteClass = TrainingTargetSprite.class;
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
        return "训练靶";
    }

    @Override
    public String description() {
        return "立在训练场边缘的木靶。之后会用于法杖与远程操作教学。";
    }
}
