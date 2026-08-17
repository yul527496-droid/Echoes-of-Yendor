package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

/** Scene-owned ground representation of the same formal Yendor Amulet during the crow chase. */
public class ActOneYendorAtShrine extends Amulet {
    @Override public boolean doPickUp(Hero hero, int pos){
        boolean ok=super.doPickUp(hero,pos);
        if(ok){
            ActOneReturnState state=ActOneReturnState.get();
            if(state!=null) state.recoverYendor();
        }
        return ok;
    }
}
