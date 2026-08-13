/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;

/** First ordinary surface NPC. Uses a temporary stock sprite during the prototype. */
public class RoadFarmer extends NPC {

    private static final int MEET_X = 22;
    private static final int MEET_Y = 19;
    private static final int LEAVE_X = 36;
    private static final int LEAVE_Y = 21;

    private boolean conversationOpen;

    {
        spriteClass = WandmakerSprite.class;
    }

    @Override
    protected boolean act() {
        SequelState story = SequelState.get();
        if (story == null) {
            spend(TICK);
            return true;
        }

        if (conversationOpen) {
            spend(TICK);
            return true;
        }

        if (!story.farmerMet) {
            if (Dungeon.level.distance(pos, Dungeon.hero.pos) <= 3 && Dungeon.level.heroFOV[pos]) {
                interact(Dungeon.hero);
                spend(TICK);
                return true;
            }

            int meet = cell(MEET_X, MEET_Y);
            if (pos != meet && getCloser(meet)) {
                spend(1f / speed());
            } else {
                spend(TICK);
            }
            return true;
        }

        int leave = cell(LEAVE_X, LEAVE_Y);
        if (pos == leave || !Dungeon.level.insideMap(leave)) {
            SurfaceEntranceLevel.releaseRoadWolves();
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }

        if (getCloser(leave)) {
            spend(1f / speed());
        } else {
            spend(TICK);
        }
        return true;
    }

    @Override
    public boolean interact(Char c) {
        if (c != Dungeon.hero) return true;
        if (conversationOpen) return true;

        SequelState story = SequelState.get();
        if (story != null && story.farmerMet) return true;

        conversationOpen = true;
        if (sprite != null) sprite.turnTo(pos, Dungeon.hero.pos);

        Game.runOnRenderThread(() -> GameScene.show(new WndOptions(
                "路边的老农",
                "老人先看了看你，又越过你的肩膀看向那座半埋在坡地里的石阶入口。\n\n「你是从那里面出来的？」",
                "「如你所见。」",
                "「差一点就不是了。」",
                "「你知道那里？」",
                "「……」"
        ) {
            @Override
            protected void onSelect(int index) {
                SequelState state = SequelState.get();
                if (state != null) state.farmerMet = true;
                conversationOpen = false;

                String reply;
                switch (index) {
                    case 0:
                        reply = "「还活着。这才是稀奇的地方。」";
                        break;
                    case 1:
                        reply = "「嗯，这就更像我平时听到的故事了。」";
                        break;
                    case 2:
                        reply = "「只知道得够让我不往里走。」";
                        break;
                    default:
                        reply = "老人等了一会儿，最后把你的沉默当成了回答，只点了点头。";
                        break;
                }

                GameScene.show(new WndMessage(
                        reply + "\n\n「沿着这条旧路一直往北走，天黑前能到晨溪镇。你要真是从遗迹里出来的，就去老鸦旅店看看。老板娘那儿有一本名册。」\n\n"
                                + "「名册？」\n\n「下去的人。没回来的人。总得有人记着。」"
                ));
            }
        }));

        return true;
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
        // Ordinary, not attackable: combat is not a dialogue shortcut.
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
        return "一位沿旧王道赶车的年长农夫。比起你的装备，他显然更在意你身后那座地下遗迹的入口。";
    }
}
