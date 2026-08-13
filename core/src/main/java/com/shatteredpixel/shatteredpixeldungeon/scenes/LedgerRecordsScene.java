package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroProfile;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class LedgerRecordsScene extends PixelScene {
    @Override public void create(){
        super.create(); uiCamera.visible=false; LedgerEnvironment.addOpenBook(this);
        final ArrayList<GamesInProgress.Info> saves=GamesInProgress.checkAll();
        String[] labels=new String[saves.size()+1];
        for(int i=0;i<saves.size();i++){
            GamesInProgress.Info info=saves.get(i);
            ReturningHeroProfile p=ReturningHeroProfile.loadFromSlot(info.slot);
            labels[i]=p.name+" · "+Messages.titleCase(info.heroClass.title())+" · Lv."+info.level;
        }
        labels[saves.size()]="＋ 登记新的下行者";
        add(new WndOptions("现存记录","翻开已经写过的那一页，或登记一个新的名字。",labels){
            @Override protected void onSelect(int index){
                if(index==saves.size()){
                    LedgerFlow.resetDraft();
                    Game.switchScene(LedgerHeroScene.class);
                }else{
                    GamesInProgress.curSlot=saves.get(index).slot;
                    InterlevelScene.mode=InterlevelScene.Mode.CONTINUE;
                    Game.switchScene(InterlevelScene.class);
                }
            }
        });
        fadeIn();
    }
}
