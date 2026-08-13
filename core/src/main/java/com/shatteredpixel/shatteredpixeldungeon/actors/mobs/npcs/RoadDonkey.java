/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SheepSprite;

/** Temporary animal/cart companion for the roadside farmer event. */
public class RoadDonkey extends NPC {

    private static final int LEAVE_X = 36;
    private static final int LEAVE_Y = 22;

    {
        // Stock sheep art is intentionally temporary; a donkey-and-cart sprite will replace it.
        spriteClass = SheepSprite.class;
    }

    @Override
    protected boolean act() {
        RoadFarmer farmer = null;
        for (Mob mob : Dungeon.level.mobs) {
            if (mob instanceof RoadFarmer) {
                farmer = (RoadFarmer) mob;
                break;
            }
        }

        if (farmer != null) {
            if (Dungeon.level.distance(pos, farmer.pos) > 1 && getCloser(farmer.pos)) {
                spend(1f / speed());
            } else {
                spend(TICK);
            }
            return true;
        }

        SequelState story = SequelState.get();
        if (story == null || !story.farmerMet) {
            spend(TICK);
            return true;
        }

        int leave = LEAVE_X + LEAVE_Y * Dungeon.level.width();
        if (pos == leave) {
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }

        if (getCloser(leave)) spend(1f / speed());
        else spend(TICK);
        return true;
    }

    @Override
    public boolean interact(Char c) {
        if (c == Dungeon.hero && sprite != null) sprite.turnTo(pos, Dungeon.hero.pos);
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
    public String name() {
        return "农夫的驴";
    }

    @Override
    public String description() {
        return "一头很有耐心的小驴，身后拖着农夫那辆旧木车。";
    }
}
