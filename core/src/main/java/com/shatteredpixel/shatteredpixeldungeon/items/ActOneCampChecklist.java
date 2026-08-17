package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import java.util.ArrayList;

public class ActOneCampChecklist extends Item {
    private static final String AC_READ = "READ";
    { image = ItemSpriteSheet.TORN_PAGE; unique = true; defaultAction = AC_READ; }
    @Override public ArrayList<String> actions(Hero hero){ ArrayList<String> a=super.actions(hero); a.add(0,AC_READ); return a; }
    @Override public String actionName(String action, Hero hero){ return AC_READ.equals(action)?"阅读":super.actionName(action,hero); }
    @Override public boolean doPickUp(Hero hero,int pos){ boolean ok=super.doPickUp(hero,pos); if(ok) seen(); return ok; }
    @Override public void execute(Hero hero,String action){
        super.execute(hero,action);
        if(AC_READ.equals(action)){
            seen();
            String message = "纸张被水泡过，边缘几乎粘成一团。还能辨认的只是几行出发前的核对记录：\n\n医疗包——沃……\n测绘工具——霍……\n防水纸——芬……\n\n再往下，墨迹已经彻底化开。";
            Game.runOnRenderThread(() -> GameScene.show(new WndMessage(message)));
        }
    }
    private void seen(){ ActOneReturnState s=ActOneReturnState.get(); if(s!=null) s.campListClueSeen=true; }
    @Override public String name(){ return "泡坏的装备核对单"; }
    @Override public String info(){ return "一张被水侵蚀得很厉害的远征用品核对单。"; }
    @Override public boolean isUpgradable(){ return false; }
}
