package com.shatteredpixel.shatteredpixeldungeon.scenes;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
public class LedgerSubclassScene extends PixelScene{
 @Override public void create(){super.create();HeroSubClass[] a=LedgerFlow.draft().heroClass.subClasses();String[] n=new String[a.length];for(int i=0;i<a.length;i++)n[i]=Messages.titleCase(a[i].title());add(new WndOptions("后来专精","选择地下旅途中形成的专精。",n){@Override protected void onSelect(int i){LedgerFlow.draft().subclassIndex=i;Game.switchScene(LedgerAbilityScene.class);}});fadeIn();}
}
