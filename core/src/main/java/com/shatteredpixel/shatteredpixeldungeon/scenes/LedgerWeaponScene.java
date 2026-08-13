package com.shatteredpixel.shatteredpixeldungeon.scenes;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;
public class LedgerWeaponScene extends PixelScene{
 private int selected; private ColorBlock[] marks;
 @Override public void create(){super.create();uiCamera.visible=false;Image book=LedgerEnvironment.addOpenBook(this);RectF l=LedgerEnvironment.leftPage(book),r=LedgerEnvironment.rightPage(book);float lx=l.left+7,lw=l.width()-14,rx=r.left+7,rw=r.width()-14,top=l.top+7;
  RenderedTextBlock a=t("登记内容",8,LedgerEnvironment.INK,(int)lw);a.setPos(lx+(lw-a.width())/2f,top);add(a);
  RenderedTextBlock s=t("姓名\n"+LedgerFlow.draft().name+"\n\n理想职业\n"+Messages.titleCase(LedgerFlow.draft().heroClass.title())+"\n\n去向\n地下遗迹",5,LedgerEnvironment.INK,(int)lw-6);s.setPos(lx+3,a.bottom()+11);add(s);
  RenderedTextBlock m=t("最后一项由本人留下。",5,LedgerEnvironment.FADED_INK,(int)lw-6);m.setPos(lx+3,Math.min(l.bottom-33,s.bottom()+13));add(m);
  RenderedTextBlock h=t("惯用兵器",8,LedgerEnvironment.INK,(int)rw);h.setPos(rx+(rw-h.width())/2f,top);add(h);
  RenderedTextBlock n=t("这里只记录出发前的战斗偏好。",5,LedgerEnvironment.FADED_INK,(int)rw);n.setPos(rx+(rw-n.width())/2f,h.bottom()+5);add(n);
  String[] opts=LedgerFlow.draft().weaponOptions();marks=new ColorBlock[opts.length];float oy=n.bottom()+9;
  for(int i=0;i<opts.length;i++){final int q=i;float y=oy+i*24;StyledButton b=new StyledButton(Chrome.Type.BLANK,(i+1)+"  "+opts[i],6){@Override protected void onClick(){super.onClick();select(q);}};b.leftJustify=true;b.textColor(LedgerEnvironment.INK);b.setRect(rx+2,y,rw-4,18);add(b);ColorBlock line=new ColorBlock(rw-6,1,0x663B2A1E);line.x=rx+3;line.y=y+19;marks[i]=line;add(line);}
  StyledButton ok=new StyledButton(Chrome.Type.BLANK,"确认登记并盖章  ›",6){@Override protected void onClick(){super.onClick();LedgerFlow.draft().weaponIndex=selected;Game.switchScene(LedgerSealScene.class);}};ok.textColor(LedgerEnvironment.STAMP);ok.setRect(rx,r.bottom-20,rw,15);add(ok);
  StyledButton back=new StyledButton(Chrome.Type.BLANK,"‹ 返回登记页",5){@Override protected void onClick(){super.onClick();Game.switchScene(LedgerRegistrationScene.class);}};back.textColor(LedgerEnvironment.FADED_INK);back.setRect(lx,l.bottom-20,lw,15);add(back);
  selected=Math.max(0,Math.min(LedgerFlow.draft().weaponIndex,opts.length-1));refresh();fadeIn();}
 private void select(int i){selected=i;LedgerFlow.draft().weaponIndex=i;refresh();}
 private void refresh(){for(int i=0;i<marks.length;i++)marks[i].alpha(i==selected?0.95f:0.22f);}
 private RenderedTextBlock t(String v,int s,int c,int w){RenderedTextBlock b=PixelScene.renderTextBlock(v,s);b.maxWidth(w);b.hardlight(c);return b;}
}
