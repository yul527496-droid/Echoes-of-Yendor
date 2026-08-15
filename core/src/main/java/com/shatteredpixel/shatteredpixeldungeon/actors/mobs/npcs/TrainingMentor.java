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
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

/**
 * The unnamed veteran used by the pre-dungeon tutorial.
 *
 * T0 deliberately starts as a short blocking camera beat: the player cannot
 * accidentally walk past the first instruction, the camera establishes who is
 * speaking, and control returns only after the player explicitly acknowledges
 * the mentor. Later tutorial beats build on the two saved acknowledgement flags.
 */
public class TrainingMentor extends NPC {

    // Versioned during development so saves made against the old one-line T0
    // still exercise the new blocking camera beat once during acceptance.
    private static final String INTRO_CALLED = "t0_cinematic_intro_done";
    private static final String FIRST_TALK_DONE = "t0_cinematic_first_talk_done";

    private static final float CAMERA_PAN_INTENSITY = 3.5f;
    private static final int DIALOGUE_Y_OFFSET = 34;

    private boolean introCalled;
    private boolean firstTalkDone;
    private boolean conversationOpen;

    {
        spriteClass = TrainingMentorSprite.class;
        // Give the opening beat the first chance to run when both hero and mentor
        // are scheduled at the same time on a freshly-created training level.
        actPriority = HERO_PRIO + 1;
    }

    @Override
    protected boolean act() {
        if (Dungeon.level instanceof TrainingGroundLevel && Dungeon.hero != null) {
            if (!introCalled && !conversationOpen) {
                openIntro();
            } else if (introCalled && !firstTalkDone && !conversationOpen
                    && Dungeon.level.distance(pos, Dungeon.hero.pos) <= 2) {
                openFirstConversation();
            }
        }

        spend(TICK);
        return true;
    }

    @Override
    public boolean interact(Char c) {
        if (sprite != null) sprite.turnTo(pos, c.pos);

        if (c == Dungeon.hero && !conversationOpen) {
            if (!introCalled) {
                openIntro();
            } else if (!firstTalkDone) {
                openFirstConversation();
            } else {
                Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                        "「先别急。下一步从行装开始。」"
                )));
            }
        }
        return true;
    }

    private void openIntro() {
        if (conversationOpen || introCalled) return;
        conversationOpen = true;

        if (sprite != null && Dungeon.hero != null) {
            sprite.turnTo(pos, Dungeon.hero.pos);
        }

        Game.runOnRenderThread(() -> {
            if (!(Dungeon.level instanceof TrainingGroundLevel) || Dungeon.hero == null) {
                conversationOpen = false;
                return;
            }

            focusCameraOnMentor();

            WndOptions intro = new WndOptions(
                    "老冒险者",
                    "老人抬起手，朝你招了两下。\n\n"
                            + "「新来的，先别往石阶那边走。过来。」\n\n"
                            + "他看了眼北边的入口，又看回你。\n\n"
                            + "「第一次下去？那就更别急。下面可没人等你站着翻半天行囊。」\n\n"
                            + "「过来。剑、甲、药，还有那些乱七八糟的东西，至少先认全。花不了几分钟。」\n\n"
                            + "移动：点击地面，或使用方向键 / WASD。",
                    "过去看看"
            ) {
                @Override
                protected void onSelect(int index) {
                    finishIntro();
                }

                @Override
                public void onBackPressed() {
                    // This is the one tutorial beat that must be acknowledged.
                    // Outside clicks, WAIT, and BACK therefore do not discard it.
                }
            };

            GameScene.show(intro);
            intro.offset(0, DIALOGUE_Y_OFFSET);
            intro.boundOffsetWithMargin(4);
        });
    }

    private void finishIntro() {
        if (introCalled) return;

        introCalled = true;
        conversationOpen = false;
        returnCameraToHero();
        GLog.h("移动：点击地面，或使用方向键 / WASD。走到老冒险者身边。");
    }

    private void openFirstConversation() {
        if (conversationOpen || firstTalkDone) return;
        conversationOpen = true;

        if (sprite != null && Dungeon.hero != null) {
            sprite.turnTo(pos, Dungeon.hero.pos);
        }

        Game.runOnRenderThread(() -> {
            if (!(Dungeon.level instanceof TrainingGroundLevel) || Dungeon.hero == null) {
                conversationOpen = false;
                return;
            }

            focusCameraOnMentor();

            WndOptions talk = new WndOptions(
                    "老冒险者",
                    "老人等你走近，才把手放下。\n\n"
                            + "「行，至少走路不用教第二遍。」\n\n"
                            + "他扫了一眼你的行装。\n\n"
                            + "「先看武器和护甲。地牢里捡到什么就往身上套的人，我见得够多了。」\n\n"
                            + "「弄明白再下去。少吃点这种亏。」",
                    "继续"
            ) {
                @Override
                protected void onSelect(int index) {
                    finishFirstConversation();
                }

                @Override
                public void onBackPressed() {
                    // Keep the first real instruction persistent for the same
                    // reason as the opening callout: it should not be missable.
                }
            };

            GameScene.show(talk);
            talk.offset(0, DIALOGUE_Y_OFFSET);
            talk.boundOffsetWithMargin(4);
        });
    }

    private void finishFirstConversation() {
        if (firstTalkDone) return;

        firstTalkDone = true;
        conversationOpen = false;
        returnCameraToHero();
    }

    private void focusCameraOnMentor() {
        if (Camera.main != null && sprite != null) {
            Camera.main.panTo(sprite.center(), CAMERA_PAN_INTENSITY);
        }
    }

    private void returnCameraToHero() {
        if (Camera.main != null && Dungeon.hero != null && Dungeon.hero.sprite != null) {
            Camera.main.panTo(Dungeon.hero.sprite.center(), CAMERA_PAN_INTENSITY);
        }
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
        conversationOpen = false;
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
