/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesFarmerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDialogueStage;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

/** First ordinary surface person and the chapter's first Yendor mind-contamination scene. */
public class RoadFarmer extends NPC {

    private boolean conversationOpen;
    private boolean journeyStarted;
    private boolean approachAudioPlayed;

    {
        spriteClass = EchoesFarmerSprite.class;
    }

    private static class Beat {
        final String speaker;
        final String text;
        final WndDialogueStage.Portrait portrait;

        Beat(String speaker, String text, WndDialogueStage.Portrait portrait) {
            this.speaker = speaker;
            this.text = text;
            this.portrait = portrait;
        }
    }

    @Override
    protected boolean act() {
        ensureFov();

        SequelState story = SequelState.get();
        if (story == null || conversationOpen) {
            spend(TICK);
            return true;
        }

        if (story.isAtLeast(SequelState.Phase.FARMER_DEPARTED)) {
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }

        if (story.isAtLeast(SequelState.Phase.FARMER_RECOVERED) && story.investigationKnown) {
            int leave = cell(SurfaceEntranceLevel.FARMER_LEAVE_X, SurfaceEntranceLevel.FARMER_LEAVE_Y);
            if (pos == leave) {
                story.advanceTo(SequelState.Phase.FARMER_DEPARTED);
                destroy();
                if (sprite != null) sprite.die();
                return true;
            }
            if (getCloser(leave)) spend(1f / speed());
            else spend(TICK);
            return true;
        }

        int meet = cell(SurfaceEntranceLevel.FARMER_MEET_X, SurfaceEntranceLevel.FARMER_MEET_Y);

        if (!story.isAtLeast(SequelState.Phase.FARMER_NORMAL_TALK_DONE)) {
            if (!journeyStarted) {
                int heroY = Dungeon.hero.pos / Dungeon.level.width();
                // Surface Entrance was compacted to 38 rows. The old y>=47 gate could
                // never fire here, so the farmer/cart began pathing the instant the map loaded.
                // Start the approach only once the hero has actually moved north past camp.
                if (heroY >= SurfaceEntranceLevel.CAMP_Y2) {
                    spend(TICK);
                    return true;
                }
                journeyStarted = true;
                story.advanceTo(SequelState.Phase.FARMER_APPROACHING);
            }

            if (!approachAudioPlayed) {
                approachAudioPlayed = true;
                ChapterOneAudio.playFarmerApproach();
            }

            int distance = Dungeon.level.distance(pos, Dungeon.hero.pos);
            if (Dungeon.level.heroFOV[pos] && distance <= 6) {
                interact(Dungeon.hero);
                spend(TICK);
                return true;
            }

            if (pos != meet && getCloser(meet)) spend(1f / speed());
            else {
                if (Dungeon.level.heroFOV[pos] && distance <= 9) interact(Dungeon.hero);
                spend(TICK);
            }
            return true;
        }

        // A reload in the middle of the conversation resumes from the last stable checkpoint.
        if (pos != meet && getCloser(meet)) {
            spend(1f / speed());
        } else {
            if (Dungeon.level.heroFOV[pos] && Dungeon.level.distance(pos, Dungeon.hero.pos) <= 9) {
                interact(Dungeon.hero);
            }
            spend(TICK);
        }
        return true;
    }

    private void ensureFov() {
        if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
            fieldOfView = new boolean[Dungeon.level.length()];
        }
        Dungeon.level.updateFieldOfView(this, fieldOfView);
    }

    @Override
    public boolean interact(Char c) {
        if (c != Dungeon.hero || conversationOpen) return true;

        SequelState story = SequelState.get();
        if (story == null || story.isAtLeast(SequelState.Phase.FARMER_DEPARTED)) return true;

        conversationOpen = true;
        if (sprite != null) sprite.turnTo(pos, Dungeon.hero.pos);

        Game.runOnRenderThread(() -> {
            if (!story.isAtLeast(SequelState.Phase.FARMER_NORMAL_TALK_DONE)) {
                opening();
            } else if (!story.isAtLeast(SequelState.Phase.AMULET_FLARE_DONE)) {
                anomaly();
            } else if (!story.isAtLeast(SequelState.Phase.FARMER_RECOVERED)) {
                recovery();
            } else if (!story.investigationKnown) {
                investigationLead();
            } else {
                conversationOpen = false;
            }
        });
        return true;
    }

    private void opening() {
        showChoice(
                "路边的老农",
                "喂——先等等。\n你……是从那座石阶下面出来的？",
                WndDialogueStage.Portrait.FARMER_NEUTRAL,
                new String[]{"如你所见。", "差一点就不是了。", "你知道那里？", "……"},
                choice -> {
                    Beat reply;
                    switch (choice) {
                        case 0:
                            reply = new Beat("路边的老农", "哈。那我今天总算见到一个会往外走的人了。",
                                    WndDialogueStage.Portrait.FARMER_WARM);
                            break;
                        case 1:
                            reply = new Beat("路边的老农", "这倒更像我平时听到的故事。",
                                    WndDialogueStage.Portrait.FARMER_NEUTRAL);
                            break;
                        case 2:
                            reply = new Beat("路边的老农", "只知道得够让我不往里走。",
                                    WndDialogueStage.Portrait.FARMER_NEUTRAL);
                            break;
                        default:
                            reply = new Beat("路边的老农", "……\n好吧。能自己走出来，已经算回答了。",
                                    WndDialogueStage.Portrait.FARMER_NEUTRAL);
                            break;
                    }

                    Beat[] normal = {
                            reply,
                            new Beat("路边的老农",
                                    "这条旧王道一直往北。\n过了前面的林子就是晨溪。天黑以前怎么也到了。",
                                    WndDialogueStage.Portrait.FARMER_WARM),
                            new Beat("路边的老农",
                                    "镇子不大。\n要吃东西、找床铺，去老鸦旅店准没错。",
                                    WndDialogueStage.Portrait.FARMER_WARM),
                            new Beat("路边的老农",
                                    "那地方见过的探险客，比镇里任何人都多。",
                                    WndDialogueStage.Portrait.FARMER_NEUTRAL),
                            new Beat("路边的老农",
                                    "不过我一直没明白。\n那下面到底有什么，值得那么多人把命搭进去？",
                                    WndDialogueStage.Portrait.FARMER_NEUTRAL)
                    };

                    play(normal, 0, () -> heroMeaningChoice());
                });
    }

    private void heroMeaningChoice() {
        showChoice(
                "返回者",
                "你怎么回答？",
                WndDialogueStage.Portrait.HERO,
                new String[]{"没有什么值得拿命换。", "有些东西，进去以后才知道。", "我还没想明白。"},
                choice -> {
                    String line = choice == 0 ? "没有什么值得拿命换。"
                            : choice == 1 ? "有些东西，进去以后才知道。"
                            : "我还没想明白。";

                    showPage("返回者", line, WndDialogueStage.Portrait.HERO, () -> {
                        SequelState story = SequelState.get();
                        if (story != null) story.advanceTo(SequelState.Phase.FARMER_NORMAL_TALK_DONE);
                        anomaly();
                    });
                });
    }

    private void anomaly() {
        ChapterOneAudio.playYendorPulse();

        Beat[] beats = {
                new Beat("", "背包深处，有什么极轻地震了一下。", WndDialogueStage.Portrait.NONE),
                new Beat("", "不是声音。\n你太熟悉这种感觉了。", WndDialogueStage.Portrait.NONE),
                new Beat("", "不要顺着它想。", WndDialogueStage.Portrait.NONE),
                new Beat("路边的老农", "……\n等等。", WndDialogueStage.Portrait.FARMER_CONFUSED),
                new Beat("路边的老农", "你包里……是什么？", WndDialogueStage.Portrait.FARMER_CONFUSED),
                new Beat("返回者", "什么都别碰。", WndDialogueStage.Portrait.HERO),
                new Beat("路边的老农", "我没有要碰。", WndDialogueStage.Portrait.FARMER_CONFUSED),
                new Beat("路边的老农", "我只是看看。", WndDialogueStage.Portrait.FARMER_FIXATED),
                new Beat("路边的老农", "给我看一眼。", WndDialogueStage.Portrait.FARMER_FIXATED),
                new Beat("路边的老农", "你都已经把它带出来了。", WndDialogueStage.Portrait.FARMER_FIXATED),
                new Beat("路边的老农", "给我。", WndDialogueStage.Portrait.FARMER_FIXATED)
        };

        play(beats, 0, () -> {
            SequelState story = SequelState.get();
            if (story != null) story.advanceTo(SequelState.Phase.AMULET_FLARE_DONE);
            ChapterOneAudio.playDonkeyBreak();
            RoadDonkey.storyJolt();
            recoilFromDonkey();
            recovery();
        });
    }

    private void recovery() {
        Beat[] beats = {
                new Beat("路边的老农", "……", WndDialogueStage.Portrait.FARMER_SHAKEN),
                new Beat("路边的老农", "我的手……", WndDialogueStage.Portrait.FARMER_SHAKEN),
                new Beat("路边的老农", "我刚才说什么了？", WndDialogueStage.Portrait.FARMER_SHAKEN)
        };
        play(beats, 0, this::recoveryChoice);
    }

    private void recoveryChoice() {
        showChoice(
                "返回者",
                "你怎么回答？",
                WndDialogueStage.Portrait.HERO,
                new String[]{"你想抢我的东西。", "你一直盯着我的背包。", "你不记得？"},
                choice -> {
                    String line = choice == 0 ? "你想抢我的东西。"
                            : choice == 1 ? "你一直盯着我的背包。"
                            : "你不记得？";

                    Beat[] after = {
                            new Beat("返回者", line, WndDialogueStage.Portrait.HERO),
                            new Beat("路边的老农", "我不抢东西。", WndDialogueStage.Portrait.FARMER_SHAKEN),
                            new Beat("路边的老农", "至少上一刻，我还不想。", WndDialogueStage.Portrait.FARMER_SHAKEN),
                            new Beat("路边的老农", "我甚至不知道你包里有什么。", WndDialogueStage.Portrait.FARMER_SHAKEN),
                            new Beat("路边的老农", "……我还是离你远一点好。", WndDialogueStage.Portrait.FARMER_SHAKEN)
                    };

                    play(after, 0, () -> {
                        SequelState story = SequelState.get();
                        if (story != null) story.advanceTo(SequelState.Phase.FARMER_RECOVERED);
                        investigationLead();
                    });
                });
    }

    private void investigationLead() {
        Beat[] lead = {
                new Beat("路边的老农", "等等。", WndDialogueStage.Portrait.FARMER_SHAKEN),
                new Beat("路边的老农", "到晨溪以后，你去老鸦旅店。", WndDialogueStage.Portrait.FARMER_SHAKEN),
                new Beat("路边的老农",
                        "老板娘有一本名册。\n每个说要去那座遗迹的人，她只要听见了，就会记下来。",
                        WndDialogueStage.Portrait.FARMER_SHAKEN),
                new Beat("路边的老农", "下去的。", WndDialogueStage.Portrait.FARMER_SHAKEN),
                new Beat("路边的老农", "没回来的。", WndDialogueStage.Portrait.FARMER_SHAKEN),
                new Beat("路边的老农", "还有回来以后变得不太对劲的。", WndDialogueStage.Portrait.FARMER_SHAKEN),
                new Beat("返回者", "不太对劲？", WndDialogueStage.Portrait.HERO),
                new Beat("路边的老农", "你去问她。\n我今天已经够不对劲了。", WndDialogueStage.Portrait.FARMER_SHAKEN)
        };

        play(lead, 0, () -> {
            SequelState story = SequelState.get();
            if (story != null) story.markInvestigationKnown();
            GLog.p("调查线索更新：晨溪镇 · 老鸦旅店 —— 查阅下行者名册。");
            conversationOpen = false;
        });
    }

    private void play(Beat[] beats, int index, Callback done) {
        if (index >= beats.length) {
            if (done != null) done.call();
            return;
        }
        Beat beat = beats[index];
        showPage(beat.speaker, beat.text, beat.portrait, () -> play(beats, index + 1, done));
    }

    private void showPage(String speaker, String text, WndDialogueStage.Portrait portrait, Callback next) {
        GameScene.show(new WndDialogueStage(speaker, text, portrait, next));
    }

    private void showChoice(String speaker, String text, WndDialogueStage.Portrait portrait,
                            String[] choices, WndDialogueStage.ChoiceListener listener) {
        GameScene.show(new WndDialogueStage(speaker, text, portrait, listener, choices));
    }

    private void recoilFromDonkey() {
        if (!(Dungeon.level instanceof SurfaceEntranceLevel)) return;
        int target = pos + Dungeon.level.width();
        if (target >= 0 && target < Dungeon.level.length()
                && Dungeon.level.passable[target] && Actor.findChar(target) == null) {
            int from = pos;
            pos = target;
            if (sprite != null) sprite.move(from, target);
        }
    }

    private int cell(int x, int y) {
        return x + y * Dungeon.level.width();
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
        return "路边的老农";
    }

    @Override
    public String description() {
        return "一位沿旧王道赶车的年长农夫。此刻他更害怕的，似乎是刚才那个不像自己的念头。";
    }
}
