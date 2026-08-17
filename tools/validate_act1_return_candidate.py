#!/usr/bin/env python3
"""Source-level canon/production gate for Act 1 Scene 1 Return v0.1 candidate."""
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
def read(rel): return (ROOT/rel).read_text(encoding="utf-8")
def require(text, needle, label):
    if needle not in text: raise SystemExit(f"Return gate failed: {label}")
def forbid(text, needle, label):
    if needle in text: raise SystemExit(f"Return gate failed: {label}")

opening=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/ActOneOpeningScene.java")
locked='''private static final String[] LINES = {\n            "我成功了。",\n            "我杀死了古神。",\n            "我带着 Yendor 回来了。",\n            "……",\n            "我独自一人。"\n    };'''
require(opening,locked,"locked five-line opening changed")
for extra in ("可是","为什么","似乎","队友","Yog","剪影","glitch","portrait"):
    forbid(opening,extra,f"opening acquired forbidden hint/token: {extra}")
require(opening,"new ActOneReturnCandidateLevel()","opening no longer enters formal Return candidate")

entry=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/SequelGame.java")
require(entry,"ShatteredPixelDungeon.switchNoFade(ActOneOpeningScene.class);","formal sequel entry bypasses opening")
start_block=entry.split("public static boolean startTrainingMemory",1)[0]
forbid(start_block,"enterSurfaceEntrance();","formal sequel start fell back to legacy Surface demo")

level=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/ActOneReturnLevel.java")
require(level,"public static final int WIDTH = 88, HEIGHT = 68;","formal Return map is not 88x68")
require(level,"new int[]{cell(58,24),cell(64,22),cell(68,20),cell(72,18)}","crow chase staging changed unexpectedly")
require(level,"开发占位 · 晨溪方向","north-end safe development transition missing")
forbid(level,"enterMorningcreekTownPrototype","Return north exit must not enter archived Town prototype")
forbid(level,"SequelState.Phase","formal Return reintroduced legacy linear Phase")

state=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ActOneReturnState.java")
for key in ("openingShown","campDiscovered","campBedrollClueSeen","campListClueSeen","heroMarkSeen",
            "farmerEventSeen","farmerOutcome","donkeyOutcome","cartOutcome","wolvesOutcome",
            "morningcreekHeardOf","yendorAnomalyStarted","yendorTemporarilyMissing","crowChaseActive",
            "crowChaseResolved","shrineDiscovered","inscriptionRead","yendorRecovered","northExitReached"):
    require(state,key,f"persistent Return state missing {key}")
require(state,"detachAll(Dungeon.hero.belongings.backpack)","Yendor temporary removal is not inventory-safe")
require(state,"new Amulet().collect()","Yendor recovery repair path missing")

checklist=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/ActOneCampChecklist.java")
for fragment in ("医疗包——沃……","测绘工具——霍……","防水纸——芬……"):
    require(checklist,fragment,f"camp checklist fragment missing: {fragment}")
for full_name in ("艾琳·沃斯","布兰·霍尔特","梅芙·芬","Eileen Vos","Bran Holt","Maeve Finn"):
    forbid(checklist,full_name,"camp checklist revealed a full teammate name")

mark=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/ActOneHeroMark.java")
for phrase in ("这是我的记号。","……不可能。","大概只是很像。"):
    require(mark,phrase,f"hero-mark denial beat missing: {phrase}")
for theory in ("失忆","诅咒","篡改","队友"):
    forbid(mark,theory,"hero mark prematurely theorizes the mystery")

shrine=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/ActOneShrineInscription.java")
require(shrine,"「力量会使你迷失。」","locked shrine warning changed")

farmer=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/npcs/ActOneReturnFarmer.java")
farmer_body=farmer.split("public class ActOneReturnFarmer",1)[-1]
require(farmer_body,"我正要回晨溪。","farmer no longer introduces Morningcreek naturally")
for forbidden in ("Yendor","远征队","诅咒","预言","沃斯","霍尔特","梅芙"):
    forbid(farmer_body,forbidden,f"ordinary farmer knows forbidden mystery info: {forbidden}")

candidate=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/ActOneReturnCandidateLevel.java")
for prop in ("BEDROLL_A","BEDROLL_B","BEDROLL_C","BEDROLL_D","FADED_TENT","MILESTONE","OFFERING_BOWL","FOUR_RECESSES","FIELD_EDGE"):
    require(candidate,prop,f"candidate dressing missing {prop}")
require(candidate,"87, 67","Morningcreek exact POI anchor is not safely unreachable")

region=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/RegionState.java")
require(region,"ACT_ONE_RETURN","Region Area integration missing")
require(region,"beginFormalActOne()","formal-region prototype-knowledge isolation missing")
require(region,"ABANDONED_EXPEDITION_CAMP","camp Region knowledge missing")
require(region,"MORNINGCREEK","Morningcreek HEARD_OF Region knowledge missing")

print("Act 1 Scene 1 Return canon gate OK: opening, cognition, state, route and transition locks are intact.")
