package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroProfile;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class LedgerRegistrationScene extends PixelScene {
    @Override public void create(){
        super.create(); uiCamera.visible=false;
        int w=Camera.main.width,h=Camera.main.height;
        add(new ColorBlock(w,h,0xFF17110D));
        Image bg=new Image("interfaces/echoes/ledger_open.png");
        float s=Math.max(w/bg.width,h/bg.height); bg.scale.set(s); bg.x=(w-bg.width())/2f; bg.y=(h-bg.height())/2f; add(bg);
        final ReturningHeroProfile p=LedgerFlow.draft();
        add(new WndTextInput("遗迹下行者登记","写下进入遗迹前留下的名字。","",20,false,"登记","返回"){
            @Override public void onSelect(boolean ok,String value){
                if(ok&&value!=null&&!value.trim().isEmpty()){
                    p.name=value.trim();
                    Game.switchScene(LedgerLegacyScene.class);
                }else Game.switchScene(LedgerHeroScene.class);
            }
        });
        fadeIn();
    }
}
