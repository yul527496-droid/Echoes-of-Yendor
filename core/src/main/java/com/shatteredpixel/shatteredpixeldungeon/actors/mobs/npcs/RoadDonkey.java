/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesDonkeyCartSprite;

/** Farmer companion staged as a wide side-view cart rather than an ordinary one-cell walker. */
public class RoadDonkey extends NPC {

    {
        spriteClass = EchoesDonkeyCartSprite.class;
    }

    @Override
    protected boolean act() {
        ensurePathfindingFov();

        RoadFarmer farmer = findFarmer();
        if (farmer != null) {
            /*
             * The current authored cart only has side-view frames. SPD's normal movement
             * tween is fine for small actors, but visibly moving this 32px cart north/south
             * makes it look as if the entire rig is sliding sideways. Keep its catch-up
             * movement strictly off-screen, then let it read as parked once discovered.
             */
            if (!visibleToHero()
                    && Dungeon.level.distance(pos, farmer.pos) > 1
                    && getCloser(farmer.pos)) {
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

        // Never make the wide side-view cart perform a visible north/south exit animation.
        // Once the player looks away after the farmer has departed, retire the scene actor.
        if (!visibleToHero()) {
            destroy();
            if (sprite != null) sprite.die();
        } else {
            spend(TICK);
        }
        return true;
    }

    /** The story beat is a physical startle, not a one-cell map relocation. */
    public static void storyJolt() {
        if (!(Dungeon.level instanceof SurfaceEntranceLevel)) return;
        for (Mob mob : Dungeon.level.mobs) {
            if (mob instanceof RoadDonkey) {
                RoadDonkey donkey = (RoadDonkey) mob;
                if (donkey.sprite != null) {
                    donkey.sprite.jump(donkey.pos, donkey.pos, 2.5f, 0.16f, null);
                }
                return;
            }
        }
    }

    private boolean visibleToHero() {
        return Dungeon.level.heroFOV != null
                && pos >= 0
                && pos < Dungeon.level.heroFOV.length
                && Dungeon.level.heroFOV[pos];
    }

    private RoadFarmer findFarmer() {
        for (Mob mob : Dungeon.level.mobs) {
            if (mob instanceof RoadFarmer) return (RoadFarmer) mob;
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
        // The current asset is authored in one stable side-view orientation.
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
