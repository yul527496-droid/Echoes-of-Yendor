/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.levels.TrainingGroundLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrainingMentorSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

/**
 * The unnamed veteran used by the pre-dungeon tutorial.
 *
 * T0 is intentionally tiny: the mentor calls the new adventurer over once,
 * gives a single movement hint, then opens the first conversation when the
 * hero comes within two cells. Later tutorial beats build on this state rather
 * than front-loading the entire tutorial here.
 */
public class TrainingMentor extends NPC {

    private static final String INTRO_CALLED = "intro_called";
    private static final String FIRST_TALK_DONE = "first_talk_done";

    private boolean introCalled;
    private boolean firstTalkDone;

    {
        spriteClass = TrainingMentorSprite.class;
    }

    @Override
    protected boolean act() {
        if (Dungeon.level instanceof TrainingGroundLevel && Dungeon.hero != null) {
            if (!introCalled) {
                introCalled = true;
                Game.runOnRenderThread(() -> {
                    if (sprite != null) {
                        sprite.turnTo(pos, Dungeon.hero.pos);
                        sprite.showStatus(0xFFF1C7, "新来的，这边。");
                    }
                    GLog.i("移动：点击地面，或使用方向键 / WASD。");
                });
            }

            if (!firstTalkDone && Dungeon.level.distance(pos, Dungeon.hero.pos) <= 2) {
                openFirstConversation();
            }
        }

        spend(TICK);
        return true;
    }

    @Override
    public boolean interact(Char c) {
        if (sprite != null) sprite.turnTo(pos, c.pos);

        if (c == Dungeon.hero) {
            if (!firstTalkDone) {
                openFirstConversation();
            } else {
                Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                        "「先别急。最基本的几样过一遍。」"
                )));
            }
        }
        return true;
    }

    private void openFirstConversation() {
        if (firstTalkDone) return;
        firstTalkDone = true;

        if (sprite != null && Dungeon.hero != null) {
            sprite.turnTo(pos, Dungeon.hero.pos);
        }

        Game.runOnRenderThread(() -> {
            if (!(Dungeon.level instanceof TrainingGroundLevel)) return;
            GameScene.show(new WndMessage(
                    "老人把目光从你身上移到北边的石阶。\n\n"
                            + "「第一次下去？」\n\n"
                            + "他没追着等答案，只朝旁边的训练场偏了偏头。\n\n"
                            + "「先把最基本的几样过一遍。花不了多久。」"
            ));
        });
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(INTRO_CALLED, introCalled);
        bundle.put(FIRST_TALK_DONE, firstTalkDone);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        introCalled = bundle.getBoolean(INTRO_CALLED);
        firstTalkDone = bundle.getBoolean(FIRST_TALK_DONE);
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
        return "老冒险者";
    }

    @Override
    public String description() {
        return "一位守在地下城入口营地附近的老练冒险者。灰发、旧围巾和磨损的装备都说明他在这里待了很久。";
    }
}
