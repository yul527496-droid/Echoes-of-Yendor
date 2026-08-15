/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.levels.TrainingGroundLevel;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrainingTargetSprite;
import com.watabou.utils.Bundle;

/** Static range prop that only becomes targetable during the wand lesson. */
public class TrainingTarget extends Mob {

    private static final String ARMED = "training_armed";
    private boolean armed;

    {
        spriteClass = TrainingTargetSprite.class;
        HP = HT = 999;
        defenseSkill = 0;
        EXP = 0;
        maxLvl = 0;
        alignment = Alignment.NEUTRAL;
        state = PASSIVE;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
        alignment = armed ? Alignment.ENEMY : Alignment.NEUTRAL;
    }

    @Override
    protected boolean act() {
        spend(TICK);
        return true;
    }

    @Override
    public boolean interact(Char c) {
        return true;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return armed ? 0 : INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (armed && src instanceof Wand && Dungeon.level instanceof TrainingGroundLevel) {
            ((TrainingGroundLevel) Dungeon.level).tutorial().onTargetHit(src);
        }
        // Range props never lose HP; they only confirm a valid wand impact.
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
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ARMED, armed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        setArmed(bundle.getBoolean(ARMED));
    }

    @Override
    public String name() {
        return "训练靶";
    }

    @Override
    public String description() {
        return "训练场边缘的木靶。上面已经留下不少法术灼痕和凹坑。";
    }
}
