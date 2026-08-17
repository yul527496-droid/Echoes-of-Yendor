package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;

/** Non-collectible old-road shrine inscription. The wording is canon-locked. */
public class ActOneShrineInscription extends Item {
    { image = ItemSpriteSheet.STONE_HOLDER; unique = true; }
    @Override public boolean doPickUp(Hero hero,int pos){
        ActOneReturnState state=ActOneReturnState.get();
        if(state!=null) state.inscriptionRead=true;
        String message = "石面上的字已经被风雨磨去大半，只剩一句仍然完整：\n\n「力量会使你迷失。」";
        Game.runOnRenderThread(() -> GameScene.show(new WndMessage(message)));
        return false;
    }
    @Override public String name(){ return "古老神龛的刻字"; }
    @Override public String info(){ return "比这次远征古老得多的路边训诫。石槽和苔痕说明这里曾只是普通行路人停步供奉的地方。"; }
    @Override public boolean isUpgradable(){ return false; }
}
