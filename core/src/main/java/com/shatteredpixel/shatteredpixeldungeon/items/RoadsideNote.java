/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;

import java.util.ArrayList;

/** Optional story pickup from the abandoned expedition camp. */
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
        if (AC_READ.equals(action)) return "阅读";
        return super.actionName(action, hero);
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        boolean pickedUp = super.doPickUp(hero, pos);
        if (pickedUp) {
            SequelState state = SequelState.get();
            if (state != null) state.markCampRead(true);
        }
        return pickedUp;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (AC_READ.equals(action)) {
            SequelState state = SequelState.get();
            if (state != null) state.markCampRead(false);
            GameScene.show(new WndMessage(
                    "纸页被雨水泡得发皱。\n\n"
                            + "「地图上说，第五层以后应该有一条直路。」\n\n"
                            + "「如果是真的，三天应该够。」\n\n"
                            + "最后一行只剩一个潦草的名字：\n"
                            + "莱斯·赫恩"
            ));
        }
    }

    @Override
    public String name() {
        return "风雨侵蚀的远征笔记";
    }

    @Override
    public String info() {
        return "从旧王道旁一处废弃营地里找到的残破纸页。最后还能辨认出的署名是「莱斯·赫恩」。";
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }
}
