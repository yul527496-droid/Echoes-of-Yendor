package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;

public class LedgerIntroScene extends PixelScene {
    private ColorBlock cover;
    private Image openBook;
    private PointerArea input;
    private float time;

    @Override public void create() {
        super.create(); uiCamera.visible=false;
        int w=Camera.main.width,h=Camera.main.height;
        openBook=LedgerEnvironment.addOpenBook(this); openBook.alpha(0f);
        float bw=Math.min(150,w*0.48f),bh=Math.min(126,h*0.62f),bx=(w-bw)/2f,by=(h-bh)/2f;
        cover=new ColorBlock(bw,bh,0xFF3E2118); cover.x=bx; cover.y=by; add(cover);
        input=new PointerArea(bx,by,bw,bh){@Override protected void onClick(PointerEvent e){open();}};
        add(input); fadeIn();
    }

    @Override public void update(){
        super.update(); time+=Game.elapsed;
        if(input.active) cover.brightness(1f+(float)Math.sin(time*2.2f)*0.02f);
    }

    private void open(){
        if(!input.active)return; input.active=false; LedgerFlow.resetDraft();
        Sample.INSTANCE.play(Assets.Sounds.OPEN,0.5f,0.9f);
        add(new Tweener(this,0.62f){
            @Override protected void updateValues(float p){cover.alpha(1f-p);openBook.alpha(p);}
            @Override protected void onComplete(){Game.switchScene(LedgerHeroScene.class);}
        });
    }
}
