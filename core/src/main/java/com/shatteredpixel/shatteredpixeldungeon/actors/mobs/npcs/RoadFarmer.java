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
                "Roadside farmer",
                "The old man looks at you, then at the buried stair behind you.\n\n‘You came out of there?’",
                "As you can see.",
                "Nearly didn't.",
                "You know that place?",
                "..."
        ) {
            @Override
            protected void onSelect(int index) {
                SequelState state = SequelState.get();
                if (state != null) state.farmerMet = true;
                conversationOpen = false;

                String reply;
                switch (index) {
                    case 0:
                        reply = "‘Alive, too. That's the unusual part.’";
                        break;
                    case 1:
                        reply = "‘Aye. That's closer to what I usually hear.’";
                        break;
                    case 2:
                        reply = "‘Only enough to stay out of it.’";
                        break;
                    default:
                        reply = "The farmer waits a moment, decides silence is answer enough, and nods.";
                        break;
                }

                GameScene.show(new WndMessage(
                        reply + "\n\n‘Follow this road north and you'll reach Morningcreek before dark. "
                                + "If you really came from the ruins, stop at the old inn. The keeper has a ledger.’\n\n"
                                + "‘A ledger?’\n\n‘Names of the ones who went down. Someone ought to remember them.’"
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
        return "roadside farmer";
    }

    @Override
    public String description() {
        return "An older farmer guiding a small cart along the old road. He looks far more interested in the dungeon entrance than in your equipment.";
    }
}
