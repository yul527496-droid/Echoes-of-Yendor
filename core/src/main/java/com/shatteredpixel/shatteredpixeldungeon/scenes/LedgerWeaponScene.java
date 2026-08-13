package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class LedgerWeaponScene extends PixelScene {
    @Override public void create(){
        super.create(); uiCamera.visible=false;
        int w=Camera.main.width,h=Camera.main.height;
        add(new ColorBlock(w,h,0xFF17110D));
        Image bg=new Image("interfaces/echoes/ledger_open.png");
        float s=Math.max(w/bg.width,h/bg.height); bg.scale.set(s); bg.x=(w-bg.width())/2f; bg.y=(h-bg.height())/2f; add(bg);
        add(new WndOptions("惯用兵器","这是下行前本人留下的偏好。",LedgerFlow.draft().weaponOptions()){
            @Override protected void onSelect(int index){
                LedgerFlow.draft().weaponIndex=index;
                Game.switchScene(LedgerSealScene.class);
            }
        });
        fadeIn();
    }
}
