/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.TrainingGroundLevel;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrainingDummySprite;
import com.watabou.utils.Bundle;

/** Static target that becomes hostile-targetable only during the melee lesson. */
public class TrainingDummy extends Mob {

    private static final String ARMED = "training_armed";
    private boolean armed;

    {
        spriteClass = TrainingDummySprite.class;
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
        // Never swap places with the fixed wooden prop.
        return true;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return armed ? 0 : INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (armed && src == Dungeon.hero && Dungeon.level instanceof TrainingGroundLevel) {
            ((TrainingGroundLevel) Dungeon.level).tutorial().onDummyHit();
        }
        // The prop records the strike but does not lose HP.
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
        return "训练木人";
    }

    @Override
    public String description() {
        return "被无数新手砍出浅痕的木制人偶。木头不会还手，真正的敌人会。";
    }
}
