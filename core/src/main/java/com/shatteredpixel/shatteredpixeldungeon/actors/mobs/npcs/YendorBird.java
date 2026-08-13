/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;

/** First deliberately unexplained reaction to the Amulet on the surface. */
public class YendorBird extends NPC {

    {
        spriteClass = BatSprite.class; // temporary flying-animal silhouette for the prototype
        flying = true;
    }

    @Override
    protected boolean act() {
        SequelState story = SequelState.get();
        if (story != null && story.birdGone) {
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }

        if (Dungeon.level.heroFOV[pos] && sprite != null) {
            // It is meant to feel as if the animal is following the hero's bag with its gaze.
            sprite.turnTo(pos, Dungeon.hero.pos);
        }

        if (Dungeon.level.distance(pos, Dungeon.hero.pos) <= 2) {
            if (story != null) story.birdGone = true;
            // No log, no quest popup, no explanation: the oddness should be noticed visually.
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }

        spend(TICK);
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
    public String name() {
        return "small roadside bird";
    }

    @Override
    public String description() {
        return "A perfectly ordinary-looking bird. It seems much more interested in your pack than in you.";
    }
}
