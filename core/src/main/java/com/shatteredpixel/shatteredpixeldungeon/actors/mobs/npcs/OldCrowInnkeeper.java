/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesInnkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDialogueStage;

/** First named Morningcreek resident with dedicated sprite and dialogue portrait. */
public class OldCrowInnkeeper extends NPC {

    {
        spriteClass = EchoesInnkeeperSprite.class;
    }

    @Override protected boolean act() {
        spend(TICK);
        return true;
    }

    @Override public boolean interact(Char c) {
        if (c == Dungeon.hero) {
            if (sprite != null) sprite.turnTo(pos, Dungeon.hero.pos);
            GameScene.show(new WndDialogueStage(
                    "老鸦旅店老板娘",
                    "坐吧。你从南边那条旧路来，对吗？\n先喘口气。等你准备好，我给你看一本东西。",
                    WndDialogueStage.Portrait.INNKEEPER_ATTENTIVE,
                    () -> {}));
        }
        return true;
    }

    @Override public int defenseSkill(Char enemy) { return INFINITE_EVASION; }
    @Override public void damage(int dmg, Object src) {}
    @Override public boolean add(Buff buff) { return false; }
    @Override public boolean reset() { return true; }
    @Override public String name() { return "老鸦旅店老板娘"; }
    @Override public String description() { return "老鸦旅店的老板娘。她看人的眼神像在确认一页旧账。"; }
}
