package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class LedgerRegistrationScene extends PixelScene {
    @Override public void create() {
        super.create(); uiCamera.visible=false;
        Image book=LedgerEnvironment.addOpenBook(this);
        RectF left=LedgerEnvironment.leftPage(book), right=LedgerEnvironment.rightPage(book);
        float lx=left.left+7,lw=left.width()-14,rx=right.left+7,rw=right.width()-14,top=left.top+7;

        RenderedTextBlock header=text("遗迹下行者登记",8,LedgerEnvironment.INK,(int)lw);
        header.setPos(lx+(lw-header.width())/2f,top); add(header);
        Image hero=new Image(LedgerFlow.draft().heroClass.spritesheet(),0,90,12,15);
        hero.scale.set(1.7f); hero.x=lx+4; hero.y=header.bottom()+10; add(hero);
        float infoX=hero.x+hero.width()+7;
        RenderedTextBlock idealLabel=text("理想职业",5,LedgerEnvironment.FADED_INK,(int)(left.right-infoX-5));
        idealLabel.setPos(infoX,hero.y); add(idealLabel);
        RenderedTextBlock ideal=text(Messages.titleCase(LedgerFlow.draft().heroClass.title()),6,LedgerEnvironment.INK,(int)(left.right-infoX-5));
        ideal.setPos(infoX,idealLabel.bottom()+2); add(ideal);

        float y=Math.max(hero.y+hero.height(),ideal.bottom())+12;
        RenderedTextBlock nameLabel=text("姓名",5,LedgerEnvironment.FADED_INK,35); nameLabel.setPos(lx+2,y); add(nameLabel);
        String current=LedgerFlow.draft().name;
        boolean unnamed=current==null||current.trim().isEmpty()||current.equals("无名者");
        StyledButton name=new StyledButton(Chrome.Type.BLANK,unnamed?"点击写下姓名":current,6){
            @Override protected void onClick(){super.onClick(); String existing=LedgerFlow.draft().name;
                LedgerRegistrationScene.this.add(new WndTextInput("登记姓名","写下当年进入遗迹前留下的名字。",
                        existing==null||existing.equals("无名者")?"":existing,20,false,"写入名册","取消"){
                    @Override public void onSelect(boolean ok,String value){if(ok&&value!=null&&!value.trim().isEmpty()){
                        LedgerFlow.draft().name=value.trim(); Game.switchScene(LedgerRegistrationScene.class);}}
                });}
        };
        name.leftJustify=true; name.textColor(unnamed?LedgerEnvironment.FADED_INK:LedgerEnvironment.INK);
        name.setRect(lx+37,y-5,lw-39,17); add(name);

        y+=25;
        RenderedTextBlock destinationLabel=text("去向",5,LedgerEnvironment.FADED_INK,35); destinationLabel.setPos(lx+2,y); add(destinationLabel);
        RenderedTextBlock destination=text("地下遗迹",6,LedgerEnvironment.INK,(int)lw-42); destination.setPos(lx+39,y); add(destination);
        RenderedTextBlock small=text("本人于下行前登记。",5,LedgerEnvironment.FADED_INK,(int)lw-4);
        small.setPos(lx+2,Math.min(left.bottom-30,y+31)); add(small);

        RenderedTextBlock rightHeader=text("登记说明",8,LedgerEnvironment.INK,(int)rw);
        rightHeader.setPos(rx+(rw-rightHeader.width())/2f,top); add(rightHeader);
        RenderedTextBlock note=text("这本名册只写下出发前\n能够知道的事情。\n\n姓名\n理想职业\n惯用兵器\n去向\n\n后来发生的事，\n不属于这一页。",5,LedgerEnvironment.FADED_INK,(int)rw-6);
        note.setPos(rx+3,rightHeader.bottom()+11); add(note);

        StyledButton next=new StyledButton(Chrome.Type.BLANK,"继续填写惯用兵器  ›",6){
            @Override protected void onClick(){super.onClick(); String n=LedgerFlow.draft().name;
                if(n==null||n.trim().isEmpty()||n.equals("无名者")){LedgerRegistrationScene.this.add(new WndMessage("先在名册上留下一个名字。"));return;}
                Game.switchScene(LedgerWeaponScene.class);}
        };
        next.textColor(LedgerEnvironment.INK); next.setRect(rx,right.bottom-20,rw,15); add(next);
        StyledButton back=new StyledButton(Chrome.Type.BLANK,"‹ 重新选择身份",5){@Override protected void onClick(){super.onClick();Game.switchScene(LedgerHeroScene.class);}};
        back.textColor(LedgerEnvironment.FADED_INK); back.setRect(lx,left.bottom-20,lw,15); add(back);
        fadeIn();
    }
    private RenderedTextBlock text(String v,int s,int c,int w){RenderedTextBlock b=PixelScene.renderTextBlock(v,s);b.maxWidth(w);b.hardlight(c);return b;}
}
