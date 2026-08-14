/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrainingMentorSprite;

/**
 * The unnamed veteran used by the pre-dungeon tutorial.
 * Graybox pass only: dialogue scripting is intentionally not connected yet.
 */
public class TrainingMentor extends NPC {

    {
        spriteClass = TrainingMentorSprite.class;
    }

    @Override
    protected boolean act() {
        spend(TICK);
        return true;
    }

    @Override
    public boolean interact(Char c) {
        if (sprite != null) sprite.turnTo(pos, c.pos);
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
        return "老冒险者";
    }

    @Override
    public String description() {
        return "一位守在地下城入口营地附近的老练冒险者。灰发、旧围巾和磨损的装备都说明他在这里待了很久。";
    }
}
