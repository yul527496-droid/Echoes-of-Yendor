package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

public class LedgerSealScene extends PixelScene {
    private float time;
    private boolean struck;
    private RenderedTextBlock stamp;
    private float stampX, stampY;

    @Override public void create(){
        super.create(); uiCamera.visible=false;
        int w=Camera.main.width,h=Camera.main.height;
        LedgerEnvironment.addOpenBook(this);

        RenderedTextBlock name=PixelScene.renderTextBlock(LedgerFlow.draft().name,8);
        name.hardlight(LedgerEnvironment.INK);
        name.setPos((w-name.width())/2f,h*0.36f); add(name);

        stamp=PixelScene.renderTextBlock("未  归",16);
        stamp.hardlight(LedgerEnvironment.STAMP);
        stampX=(w-stamp.width())/2f; stampY=h*0.52f;
        stamp.setPos(stampX,stampY-10); stamp.alpha(0f); add(stamp);
        fadeIn();
    }

    @Override public void update(){
        super.update(); time+=Game.elapsed;
        float fall=Math.min(1f,time/0.22f);
        stamp.alpha(fall);
        stamp.setPos(stampX,stampY-(1f-fall)*10f);
        if(!struck && fall>=1f){
            struck=true;
            Sample.INSTANCE.play(Assets.Sounds.STURDY,0.85f,0.92f);
        }
        if(time>0.85f) Game.switchScene(LedgerSubclassScene.class);
    }
}
