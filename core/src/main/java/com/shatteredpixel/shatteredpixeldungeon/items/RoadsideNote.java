/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;

import java.util.ArrayList;

/** A small optional story pickup in the abandoned roadside camp. */
public class RoadsideNote extends Item {

    public static final String AC_READ = "READ";

    {
        image = ItemSpriteSheet.TORN_PAGE;
        unique = true;
        defaultAction = AC_READ;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(0, AC_READ);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (AC_READ.equals(action)) return "Read";
        return super.actionName(action, hero);
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        boolean pickedUp = super.doPickUp(hero, pos);
        if (pickedUp) {
            SequelState state = SequelState.get();
            if (state != null) state.campRead = true;
        }
        return pickedUp;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (AC_READ.equals(action)) {
            SequelState state = SequelState.get();
            if (state != null) state.campRead = true;
            GameScene.show(new WndMessage(
                    "Most of the page has gone soft with rain. One line is still legible:\n\n"
                    + "‘The map says the fifth floor opens into a straight passage. If it is real, three days should be enough.’\n\n"
                    + "There is nothing written beneath it."
            ));
        }
    }

    @Override
    public String name() {
        return "weathered expedition note";
    }

    @Override
    public String info() {
        return "A torn page recovered from an abandoned camp near the old road.";
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }
}
