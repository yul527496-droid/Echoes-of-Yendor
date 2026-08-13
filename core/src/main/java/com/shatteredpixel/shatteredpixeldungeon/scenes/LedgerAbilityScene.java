package com.shatteredpixel.shatteredpixeldungeon.scenes;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
public class LedgerAbilityScene extends PixelScene{
 @Override public void create(){
  super.create();
  ArmorAbility[] a=LedgerFlow.draft().heroClass.armorAbilities();
  String[] n=new String[a.length];
  for(int i=0;i<a.length;i++)n[i]=a[i].name();
  add(new WndOptions("最终战技","选择归还时掌握的最终战技。",n){
   @Override protected void onSelect(int i){
    LedgerFlow.draft().abilityIndex=i;
    Game.switchScene(LedgerTalentScene.class);
   }
  });
  fadeIn();
 }
}
