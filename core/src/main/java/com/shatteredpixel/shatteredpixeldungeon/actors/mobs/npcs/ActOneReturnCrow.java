/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.levels.ActOneReturnLevel;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesBirdSprite;
import com.watabou.utils.Bundle;

/** One ordinary crow used as sparse visual guidance during the short Yendor chase. */
public class ActOneReturnCrow extends NPC {

    private static final String STAGE = "return_crow_stage";
    private int stage;

    {
        spriteClass = EchoesBirdSprite.class;
        flying = true;
    }

    @Override
    protected boolean act() {
        ActOneReturnState state = ActOneReturnState.current();
        if (!(Dungeon.level instanceof ActOneReturnLevel)
                || state == null || !state.crowChaseActive || state.yendorRecovered) {
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }

        ActOneReturnLevel level = (ActOneReturnLevel) Dungeon.level;
        int[] stops = level.crowStops();
        stage = Math.max(0, Math.min(stage, stops.length - 1));
        int target = stops[stage];
        if (pos != target) {
            pos = target;
            if (sprite != null) sprite.place(pos);
        }

        int distance = Dungeon.level.distance(Dungeon.hero.pos, pos);
        if (distance <= 4 && stage < stops.length - 1) {
            stage++;
            ChapterOneAudio.playRaven();
            int next = stops[stage];
            pos = next;
            if (sprite != null) sprite.place(pos);
        } else if (stage == stops.length - 1 && distance <= 3) {
            // At the shrine the bird simply leaves. The amulet is recovered separately.
            ChapterOneAudio.playRaven();
            destroy();
            if (sprite != null) sprite.die();
            return true;
        } else if (Dungeon.level.heroFOV != null
                && pos >= 0 && pos < Dungeon.level.heroFOV.length
                && Dungeon.level.heroFOV[pos] && sprite != null) {
            sprite.turnTo(pos, Dungeon.hero.pos);
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
        return "乌鸦";
    }

    @Override
    public String description() {
        return "一只普通的乌鸦。只是从刚才开始，它似乎对 Yendor 的吊链格外在意。";
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STAGE, stage);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        stage = Math.max(0, bundle.getInt(STAGE));
    }
}
