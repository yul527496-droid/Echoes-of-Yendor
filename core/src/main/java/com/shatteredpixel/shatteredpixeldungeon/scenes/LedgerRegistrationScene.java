package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;

public class LedgerRegistrationScene extends PixelScene {
    @Override public void create(){
        super.create();
        add(new WndTextInput("遗迹下行者登记","写下进入遗迹前留下的名字。","",20,false,"登记","返回"){
            @Override public void onSelect(boolean ok,String value){
                if(ok&&value!=null&&!value.trim().isEmpty()){
                    LedgerFlow.draft().name=value.trim();
                    Game.switchScene(LedgerSealScene.class);
                }else Game.switchScene(LedgerHeroScene.class);
            }
        });
        fadeIn();
    }
}
