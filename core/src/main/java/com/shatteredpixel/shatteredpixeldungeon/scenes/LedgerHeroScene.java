package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class LedgerHeroScene extends PixelScene {
    @Override public void create(){
        super.create(); uiCamera.visible=false;
        int w=Camera.main.width,h=Camera.main.height;
        add(new ColorBlock(w,h,0xFF17110D));
        Image bg=new Image("interfaces/echoes/ledger_open.png");
        float s=Math.max(w/bg.width,h/bg.height); bg.scale.set(s);
        bg.x=(w-bg.width())/2f; bg.y=(h-bg.height())/2f; add(bg);
        HeroClass[] classes=HeroClass.values();
        String[] labels=new String[classes.length];
        for(int i=0;i<classes.length;i++) labels[i]=Messages.titleCase(classes[i].title());
        add(new WndOptions("选择你的身份","这是当年写在名册上的理想职业。",labels){
            @Override protected void onSelect(int index){
                LedgerFlow.draft().heroClass=classes[index];
                LedgerFlow.draft().resetDependentChoices();
                Game.switchScene(LedgerRegistrationScene.class);
            }
        });
        fadeIn();
    }
}
