package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;

public class LedgerLegacyScene extends PixelScene {
    @Override public void create(){
        super.create(); uiCamera.visible=false;
        int w=Camera.main.width,h=Camera.main.height;
        add(new ColorBlock(w,h,0xFF17110D));
        Image bg=new Image("interfaces/echoes/ledger_open.png");
        float s=Math.max(w/bg.width,h/bg.height); bg.scale.set(s); bg.x=(w-bg.width())/2f; bg.y=(h-bg.height())/2f; add(bg);
        add(new WndOptions("那段地下旅途","名册在这里停止记录。接下来补全归还者的状态。","继续"){
            @Override protected void onSelect(int index){ SequelGame.start(LedgerFlow.draft()); }
        });
        fadeIn();
    }
}
