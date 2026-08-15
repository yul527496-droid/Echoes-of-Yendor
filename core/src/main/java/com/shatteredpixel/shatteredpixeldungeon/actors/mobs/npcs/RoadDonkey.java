/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesDonkeyCartSprite;

/** Farmer companion with its own embedded donkey-and-cart surface sprite. */
public class RoadDonkey extends NPC {

    {
        spriteClass = EchoesDonkeyCartSprite.class;
    }

    @Override
    protected boolean act() {
        ensurePathfindingFov();

        RoadFarmer farmer = findFarmer();
        if (farmer != null) {
            if (Dungeon.level.distance(pos, farmer.pos) > 1 && getCloser(farmer.pos)) {
                spend(1f / speed());
            } else {
                spend(TICK);
            }
            return true;
        }

        SequelState story = SequelState.get();
        if (story == null || !story.isAtLeast(SequelState.Phase.FARMER_DEPARTED)) {
            spend(TICK);
            return true;
        }

        int leave = SurfaceEntranceLevel.FARMER_LEAVE_X
                + (SurfaceEntranceLevel.FARMER_LEAVE_Y + 1) * Dungeon.level.width();
        if (pos == leave) {
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }

        if (getCloser(leave)) spend(1f / speed());
        else spend(TICK);
        return true;
    }

    public static void storyJolt() {
        if (!(Dungeon.level instanceof SurfaceEntranceLevel)) return;
        for (Mob mob : Dungeon.level.mobs) {
            if (mob instanceof RoadDonkey) {
                RoadDonkey donkey = (RoadDonkey)mob;
                int target = donkey.pos + Dungeon.level.width();
                if (target >= 0 && target < Dungeon.level.length()
                        && Dungeon.level.passable[target] && Actor.findChar(target) == null) {
                    int from = donkey.pos;
                    donkey.pos = target;
                    if (donkey.sprite != null) donkey.sprite.move(from, target);
                }
                return;
            }
        }
    }

    private RoadFarmer findFarmer() {
        for (Mob mob : Dungeon.level.mobs) {
            if (mob instanceof RoadFarmer) return (RoadFarmer)mob;
        }
        return null;
    }

    private void ensurePathfindingFov() {
        if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
            fieldOfView = new boolean[Dungeon.level.length()];
        }
        Dungeon.level.updateFieldOfView(this, fieldOfView);
    }

    @Override
    public boolean interact(Char c) {
        if (c == Dungeon.hero && sprite != null) sprite.turnTo(pos, Dungeon.hero.pos);
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
        return "农夫的驴";
    }

    @Override
    public String description() {
        return "一头拖着旧木车的小驴。刚才正是它突然一挣，把主人从那个念头里拽了回来。";
    }
}
