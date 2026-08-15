/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShopkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

/** Lightweight ambient resident: one map line, no formal Dialogue Stage. */
public class SurfaceVillager extends NPC {

    private static final String DISPLAY_NAME = "display_name";
    private static final String LINE = "line";

    private String displayName = "晨溪居民";
    private String line = "下午好。";

    {
        spriteClass = ShopkeeperSprite.class;
    }

    public SurfaceVillager() {
    }

    public SurfaceVillager(String displayName, String line) {
        this.displayName = displayName;
        this.line = line;
    }

    @Override
    protected boolean act() {
        spend(TICK);
        return true;
    }

    @Override
    public boolean interact(Char c) {
        if (c == Dungeon.hero) {
            if (sprite != null) sprite.turnTo(pos, Dungeon.hero.pos);
            GLog.p("%s：「%s」", displayName, line);
        }
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
        return displayName;
    }

    @Override
    public String description() {
        return "一个过着普通地表生活的晨溪居民。";
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DISPLAY_NAME, displayName);
        bundle.put(LINE, line);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        displayName = bundle.getString(DISPLAY_NAME);
        line = bundle.getString(LINE);
        if (displayName == null || displayName.isEmpty()) displayName = "晨溪居民";
        if (line == null || line.isEmpty()) line = "下午好。";
    }
}
