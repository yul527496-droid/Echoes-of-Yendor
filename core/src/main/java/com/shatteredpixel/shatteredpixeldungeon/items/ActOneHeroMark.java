package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;

/** Non-collectible inspection point representing a familiar field mark on camp gear. */
public class ActOneHeroMark extends Item {
    { image = ItemSpriteSheet.STYLUS; unique = true; }
    @Override public boolean doPickUp(Hero hero, int pos){
        ActOneReturnState state=ActOneReturnState.get();
        boolean first=state!=null&&!state.heroMarkSeen;
        if(state!=null) state.heroMarkSeen=true;
        GameScene.show(new WndMessage(first
                ? "木箱侧面有一道很浅的刻痕。\n\n这是我的记号。\n\n……不可能。\n\n大概只是很像。"
                : "那道刻痕还在。越看越像自己惯用的记号。"));
        return false;
    }
    @Override public String name(){ return "带刻痕的旧木箱"; }
    @Override public String info(){ return "营地里一个受潮的旧木箱。侧板上留着一道很浅的手刻记号。"; }
    @Override public boolean isUpgradable(){ return false; }
}
