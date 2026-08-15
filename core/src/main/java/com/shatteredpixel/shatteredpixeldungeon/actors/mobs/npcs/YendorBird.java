/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesBirdSprite;

/** Optional, deliberately unexplained animal reaction to the Amulet. */
public class YendorBird extends NPC {

    {
        spriteClass = EchoesBirdSprite.class;
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

        if (Dungeon.level.heroFOV[pos]) {
            if (story != null && !story.birdSeen) {
                story.birdSeen = true;
                ChapterOneAudio.playBird();
            }
            if (sprite != null) sprite.turnTo(pos, Dungeon.hero.pos);
        }

        if (Dungeon.level.distance(pos, Dungeon.hero.pos) <= 2) {
            if (story != null) story.birdGone = true;
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
    public boolean reset() {
        return true;
    }

    @Override
    public String name() {
        return "路边的小鸟";
    }

    @Override
    public String description() {
        return "看起来再普通不过。可它的视线似乎总落在你的行囊上，而不是你的脸上。";
    }
}
