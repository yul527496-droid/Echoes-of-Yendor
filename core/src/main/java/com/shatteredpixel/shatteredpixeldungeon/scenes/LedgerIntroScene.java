package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;

public class LedgerIntroScene extends PixelScene {
    private ColorBlock cover;
    private PointerArea input;
    private float time;

    @Override public void create() {
        super.create();
        uiCamera.visible = false;
        int w = Camera.main.width, h = Camera.main.height;
        add(new ColorBlock(w, h, 0xFF17110D));
        float bw = Math.min(150, w * 0.48f), bh = Math.min(126, h * 0.62f);
        float bx = (w-bw)/2f, by = (h-bh)/2f;
        cover = new ColorBlock(bw, bh, 0xFF3E2118);
        cover.x = bx; cover.y = by; add(cover);
        input = new PointerArea(bx, by, bw, bh){
            @Override protected void onClick(PointerEvent e){ open(); }
        };
        add(input);
        fadeIn();
    }

    @Override public void update(){
        super.update();
        time += Game.elapsed;
        cover.brightness(1f + (float)Math.sin(time*2.2f)*0.02f);
    }

    private void open(){
        if (!input.active) return;
        input.active = false;
        Sample.INSTANCE.play(Assets.Sounds.OPEN, 0.5f, 0.9f);
        add(new Tweener(this, 0.55f){
            @Override protected void updateValues(float p){ cover.alpha(1f-p); }
            @Override protected void onComplete(){ Game.switchScene(LedgerScene.class); }
        });
    }
}
