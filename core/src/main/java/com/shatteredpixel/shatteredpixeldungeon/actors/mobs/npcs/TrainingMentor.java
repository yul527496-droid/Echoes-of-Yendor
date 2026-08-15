/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.levels.TrainingGroundLevel;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrainingMentorSprite;

/**
 * The veteran NPC itself stays deliberately small. Tutorial sequencing belongs
 * to TrainingTutorialController, not to the character class.
 */
public class TrainingMentor extends NPC {

    {
        spriteClass = TrainingMentorSprite.class;
        // The mentor gets the first chance to establish the opening camera beat
        // before a newly-created hero can act.
        actPriority = HERO_PRIO + 1;
    }

    @Override
    protected boolean act() {
        if (Dungeon.level instanceof TrainingGroundLevel && Dungeon.hero != null) {
            ((TrainingGroundLevel) Dungeon.level).tutorial().tick(this);
        }
        spend(TICK);
        return true;
    }

    @Override
    public boolean interact(Char c) {
        if (sprite != null) sprite.turnTo(pos, c.pos);
        if (c == Dungeon.hero && Dungeon.level instanceof TrainingGroundLevel) {
            ((TrainingGroundLevel) Dungeon.level).tutorial().onMentorInteract(this);
        }
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
