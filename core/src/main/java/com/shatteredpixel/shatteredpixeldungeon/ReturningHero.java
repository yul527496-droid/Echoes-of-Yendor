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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

/** Applies only the minimum sequel baseline needed for early RPG prototypes. */
public final class ReturningHero {

    private static final int RETURNING_STRENGTH = 20;

    private ReturningHero() {
    }

    public static void apply(Hero hero) {
        if (hero == null) return;

        hero.lvl = Hero.MAX_LEVEL;
        hero.exp = 0;
        hero.STR = RETURNING_STRENGTH;
        hero.updateHT(true);
        hero.HP = hero.HT;
    }
}
