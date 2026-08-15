/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Echoes of Yendor modifications Copyright (C) 2026
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AmuletScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;

import java.io.IOException;
import java.util.ArrayList;

public class Amulet extends Item {

    private static final String AC_END = "END";
    private static final String AC_INSPECT = "INSPECT";

    {
        image = ItemSpriteSheet.AMULET;
        unique = true;
    }

    private boolean sequelContext(Hero hero) {
        return hero != null && hero.buff(SequelState.class) != null;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);

        if (sequelContext(hero)) {
            actions.add(AC_INSPECT);
        } else if (hero.buff(AscensionChallenge.class) != null) {
            actions.clear();
        } else {
            actions.add(AC_END);
        }
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (AC_INSPECT.equals(action)) return "查看";
        return super.actionName(action, hero);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (AC_INSPECT.equals(action) && sequelContext(hero)) {
            GameScene.show(new WndMessage(
                    "伊恩多护符安静地躺在手中。\n\n"
                            + "你曾经以为，把它带到阳光下就是故事的结尾。"
            ));
        } else if (AC_END.equals(action) && !sequelContext(hero)) {
            showAmuletScene(false);
        }
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (super.doPickUp(hero, pos)) {

            if (!Statistics.amuletObtained && !sequelContext(hero)) {
                Statistics.amuletObtained = true;
                hero.spend(-hero.cooldown());

                Actor.add(new Actor() {
                    {
                        actPriority = VFX_PRIO;
                    }

                    @Override
                    protected boolean act() {
                        Actor.remove(this);
                        showAmuletScene(true);
                        return false;
                    }
                });
            } else if (!Statistics.amuletObtained) {
                Statistics.amuletObtained = true;
            }

            return true;
        }
        return false;
    }

    private void showAmuletScene(boolean showText) {
        AmuletScene.noText = !showText;
        Game.switchScene(AmuletScene.class, new Game.SceneChangeCallback() {
            @Override
            public void beforeCreate() {
            }

            @Override
            public void afterCreate() {
                Badges.validateVictory();
                Badges.validateChampion(Challenges.activeChallenges());
                try {
                    Dungeon.saveAll();
                    Badges.saveGlobal();
                } catch (IOException e) {
                    ShatteredPixelDungeon.reportException(e);
                }
            }
        });
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (Dungeon.hero != null && sequelContext(Dungeon.hero)) {
            desc += "\n\n你已经把它带回了地表。可在阳光下，它仍不像一件真正沉睡的东西。";
        } else if (Dungeon.hero == null || Dungeon.hero.buff(AscensionChallenge.class) == null) {
            desc += "\n\n" + Messages.get(this, "desc_origins");
        } else {
            desc += "\n\n" + Messages.get(this, "desc_ascent");
        }

        return desc;
    }
}
