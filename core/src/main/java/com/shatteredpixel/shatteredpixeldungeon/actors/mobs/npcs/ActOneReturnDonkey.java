/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesDonkeyCartSprite;

/** Static, ordinary donkey/cart staging for the roadside wolf event. */
public class ActOneReturnDonkey extends NPC {

    {
        spriteClass = EchoesDonkeyCartSprite.class;
        HP = HT = 20;
    }

    @Override
    protected boolean act() {
        ActOneReturnState state = ActOneReturnState.current();
        if (state != null && state.farmerOutcome == ActOneReturnState.FarmerOutcome.IGNORED) {
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }
        spend(TICK);
        return true;
    }

    @Override
    public boolean interact(Char c) {
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
        return "受惊的驴";
    }

    @Override
    public String description() {
        return "它紧贴着歪斜的木车，不安地蹬着地。狼群才是它眼下唯一关心的事。";
    }
}
