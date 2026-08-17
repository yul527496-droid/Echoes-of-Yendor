package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

/** Scene-owned ground proxy for the one formal Yendor Amulet during the crow chase. */
public class ActOneYendorAtShrine extends Amulet {
    @Override
    public boolean doPickUp(Hero hero, int pos) {
        boolean ok = super.doPickUp(hero, pos);
        if (ok) {
            // The ground proxy must never survive in belongings as a second/alternate Amulet class.
            // Remove it first; recoverYendor() then restores exactly one normal formal Amulet.
            detachAll(hero.belongings.backpack);
            ActOneReturnState state = ActOneReturnState.get();
            if (state != null) state.recoverYendor();
        }
        return ok;
    }
}
