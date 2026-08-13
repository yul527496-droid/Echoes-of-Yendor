package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
public class RoadFarmer extends NPC{
 private static final int MX=22,MY=19,LX=36,LY=21;private boolean conversationOpen,journeyStarted;{spriteClass=WandmakerSprite.class;}
 @Override protected boolean act(){ensureFov();SequelState s=SequelState.get();if(s==null||conversationOpen){spend(TICK);return true;}if(!s.farmerMet){if(!journeyStarted){int hy=Dungeon.hero.pos/Dungeon.level.width();if(hy>=28){spend(TICK);return true;}journeyStarted=true;}int d=Dungeon.level.distance(pos,Dungeon.hero.pos);if(Dungeon.level.heroFOV[pos]&&d<=6){interact(Dungeon.hero);spend(TICK);return true;}int meet=cell(MX,MY);if(pos==meet){if(Dungeon.level.heroFOV[pos]&&d<=9)interact(Dungeon.hero);spend(TICK);return true;}if(getCloser(meet))spend(1f/speed());else spend(TICK);return true;}int leave=cell(LX,LY);if(pos==leave||!Dungeon.level.insideMap(leave)){SurfaceEntranceLevel.releaseRoadWolves();destroy();if(sprite!=null)sprite.die();return true;}if(getCloser(leave))spend(1f/speed());else spend(TICK);return true;}
 private void ensureFov(){if(fieldOfView==null||fieldOfView.length!=Dungeon.level.length())fieldOfView=new boolean[Dungeon.level.length()];Dungeon.level.updateFieldOfView(this,fieldOfView);}
 @Override public boolean interact(Char c){if(c!=Dungeon.hero||conversationOpen)return true;SequelState s=SequelState.get();if(s!=null&&s.farmerMet)return true;conversationOpen=true;if(sprite!=null)sprite.turnTo(pos,Dungeon.hero.pos);Game.runOnRenderThread(()->GameScene.show(new WndOptions("路边的老农","老人看了看你，又越过你的肩膀看向那座半埋在坡地里的石阶入口。\n\n「你是从那里面出来的？」","「如你所见。」","「差一点就不是了。」","「你知道那里？」","「……」"){@Override protected void onSelect(int i){SequelState st=SequelState.get();if(st!=null)st.farmerMet=true;conversationOpen=false;String r=i==0?"「还活着。这才是稀奇的地方。」":i==1?"「嗯，这就更像我平时听到的故事了。」":i==2?"「只知道得够让我不往里走。」":"老人等了一会儿，最后把你的沉默当成了回答，只点了点头。";GameScene.show(new WndMessage(r+"\n\n「沿着这条旧路一直往北走，天黑前能到晨溪镇。你要真是从遗迹里出来的，就去老鸦旅店看看。老板娘那儿有一本名册。」\n\n「名册？」\n\n「下去的人。没回来的人。总得有人记着。」"));}}));return true;}
 private int cell(int x,int y){return x+y*Dungeon.level.width();}@Override public int defenseSkill(Char e){return INFINITE_EVASION;}@Override public void damage(int d,Object s){}@Override public boolean add(Buff b){return false;}@Override public boolean reset(){return true;}@Override public String name(){return "路边的老农";}@Override public String description(){return "一位沿旧王道赶车的年长农夫。比起你的装备，他显然更在意你身后那座地下遗迹的入口。";}
}
