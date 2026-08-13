package com.shatteredpixel.shatteredpixeldungeon.scenes;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroProfile;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
public class LedgerTalentScene extends PixelScene{
 @Override public void create(){super.create();add(new WndOptions("天赋分配","这些点数属于归还者本人，不写进旧名册。","均衡分配","偏进攻","偏生存"){@Override protected void onSelect(int i){LedgerFlow.draft().growthPreset=ReturningHeroProfile.GrowthPreset.values()[i];SequelGame.start(LedgerFlow.draft());}});fadeIn();}
}
