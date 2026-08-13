package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;

public class LedgerSealScene extends PixelScene {
    private float time;
    @Override public void create(){
        super.create(); uiCamera.visible=false;
        int w=Camera.main.width,h=Camera.main.height;
        add(new ColorBlock(w,h,0xFF17110D));
        RenderedTextBlock name=PixelScene.renderTextBlock(LedgerFlow.draft().name,8);
        name.hardlight(0x3B2A1E); name.setPos((w-name.width())/2f,h*0.36f); add(name);
        RenderedTextBlock stamp=PixelScene.renderTextBlock("未  归",16);
        stamp.hardlight(0x922E2E); stamp.setPos((w-stamp.width())/2f,h*0.52f); add(stamp);
        fadeIn();
    }
    @Override public void update(){
        super.update(); time+=Game.elapsed;
        if(time>0.65f) Game.switchScene(LedgerLegacyScene.class);
    }
}
