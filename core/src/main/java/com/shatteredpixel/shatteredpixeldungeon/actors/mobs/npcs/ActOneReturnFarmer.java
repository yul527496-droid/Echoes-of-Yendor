/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.RegionState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesFarmerSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDialogueStage;
import com.watabou.noosa.Game;

/** Ordinary civilian in Scene 1. He deliberately knows nothing about the Act 1 mystery. */
public class ActOneReturnFarmer extends NPC {

    private boolean dialogueOpen;

    {
        spriteClass = EchoesFarmerSprite.class;
        HP = HT = 20;
    }

    @Override
    protected boolean act() {
        ActOneReturnState state = ActOneReturnState.current();
        if (state != null && state.farmerOutcome == ActOneReturnState.FarmerOutcome.IGNORED) {
            destroy();
            if (sprite != null) sprite.die();
            return true;
        }
        spend(TICK);
        return true;
    }

    @Override
    public boolean interact(Char c) {
        if (c != Dungeon.hero || dialogueOpen) return true;
        ActOneReturnState state = ActOneReturnState.get();
        if (state == null) return true;

        dialogueOpen = true;
        if (sprite != null) sprite.turnTo(pos, Dungeon.hero.pos);

        if (state.wolvesOutcome == ActOneReturnState.WolvesOutcome.UNRESOLVED) {
            Game.runOnRenderThread(() -> GameScene.show(new WndDialogueStage(
                    "路边的老农",
                    "先别过来——那几头狼还没散！",
                    WndDialogueStage.Portrait.FARMER_CONFUSED,
                    () -> dialogueOpen = false)));
            return true;
        }

        if (state.farmerDialogueCompleted) {
            Game.runOnRenderThread(() -> GameScene.show(new WndDialogueStage(
                    "路边的老农",
                    "我得把这车收拾起来。你往北走就是了。",
                    WndDialogueStage.Portrait.FARMER_WARM,
                    () -> dialogueOpen = false)));
            return true;
        }

        Game.runOnRenderThread(() -> GameScene.show(new WndDialogueStage(
                "路边的老农",
                "多谢。真是吓坏我这把老骨头了。\n\n你是从南边那条旧路过来的？那边已经很久没见人走了。\n\n我正要回晨溪。你也往北的话，就顺着这条大路走。天黑以前，应该能看见镇上的烟。",
                WndDialogueStage.Portrait.FARMER_WARM,
                () -> {
                    ActOneReturnState current = ActOneReturnState.get();
                    if (current != null) {
                        current.farmerOutcome = ActOneReturnState.FarmerOutcome.RESCUED;
                        current.donkeyOutcome = ActOneReturnState.DonkeyOutcome.SURVIVED;
                        current.cartOutcome = ActOneReturnState.CartOutcome.USABLE;
                        current.morningcreekHeardOf = true;
                        current.farmerDialogueCompleted = true;
                    }
                    RegionState region = RegionState.current();
                    if (region != null) {
                        region.hear(RegionState.Location.MORNINGCREEK);
                        region.syncHud();
                    }
                    dialogueOpen = false;
                })));
        return true;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
        // Scene 1 wolves threaten the situation, not an escort-health minigame.
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
        return "一个赶着小农车回家的老人。衣服、车轮和手上的泥都再普通不过。";
    }
}
