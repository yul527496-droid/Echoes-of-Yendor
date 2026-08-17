#!/usr/bin/env python3
"""Source-level canon/production gate for Act 1 Scene 1 Return v0.2 Journey & Audio candidate."""
from pathlib import Path
import re

ROOT=Path(__file__).resolve().parents[1]
def read(rel): return (ROOT/rel).read_text(encoding="utf-8")
def require(text, needle, label):
    if needle not in text: raise SystemExit(f"Return gate failed: {label}")
def forbid(text, needle, label):
    if needle in text: raise SystemExit(f"Return gate failed: {label}")
def require_true(value, label):
    if not value: raise SystemExit(f"Return gate failed: {label}")
def points(source, name):
    match=re.search(rf"private static final int\[\]\[\] {name} = \{{(.*?)\n    \}};", source, re.S)
    if not match: raise SystemExit(f"Return gate failed: route array {name} missing")
    result=[(int(x),int(y)) for x,y in re.findall(r"\{(\d+),(\d+)\}",match.group(1))]
    if len(result)<2: raise SystemExit(f"Return gate failed: route array {name} too short")
    return result
def manhattan_route(route):
    return sum(abs(b[0]-a[0])+abs(b[1]-a[1]) for a,b in zip(route,route[1:]))

def subroute_distance(route, start, end):
    try:
        a=route.index(start); b=route.index(end)
    except ValueError as exc:
        raise SystemExit(f"Return gate failed: journey checkpoint missing: {exc}")
    if a>b: a,b=b,a
    return manhattan_route(route[a:b+1])

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
require(level,"public static final int WIDTH = 124, HEIGHT = 92;","Return v0.2 map is not the authored 124x92 journey")
require(level,"new int[]{cell(62,33),cell(54,31),cell(47,27),cell(42,22)}","crow chase staging changed unexpectedly")
require(level,"v0.2 Journey & Audio Pass Candidate","north-end v0.2 safe transition missing")
forbid(level,"enterMorningcreekTownPrototype","Return north exit must not enter archived Town prototype")
forbid(level,"SequelState.Phase","formal Return reintroduced legacy linear Phase")

road=points(level,"ROAD")
camp=points(level,"CAMP_SPUR")
crow=points(level,"CROW_SPUR")
shrine_return=points(level,"SHRINE_RETURN")
main_steps=manhattan_route(road)
camp_steps=manhattan_route(camp)
crow_loop_steps=manhattan_route(crow)+manhattan_route(shrine_return)
farmer_to_anomaly=subroute_distance(road,(103,59),(77,40))
post_shrine_tail=subroute_distance(road,(86,29),(84,3))
require_true(main_steps>=220,f"main authored journey collapsed to {main_steps} cells")
require_true(camp_steps>=30,f"camp side journey collapsed to {camp_steps} cells one-way")
require_true(crow_loop_steps>=100,f"crow/shrine detour collapsed to {crow_loop_steps} cells")
require_true(farmer_to_anomaly>=45,f"farmer-to-Yendor pacing valley collapsed to {farmer_to_anomaly} cells")
require_true(post_shrine_tail>=65,f"post-shrine civilization tail collapsed to {post_shrine_tail} cells")

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

for label, source in (("hero mark", mark), ("camp checklist", checklist), ("shrine inscription", shrine)):
    require(source,
            "Game.runOnRenderThread(() -> GameScene.show(new WndMessage(",
            f"{label} window is not marshalled to render thread")

farmer=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/npcs/ActOneReturnFarmer.java")
farmer_body=farmer.split("public class ActOneReturnFarmer",1)[-1]
require(farmer_body,"我正要回晨溪。","farmer no longer introduces Morningcreek naturally")
for forbidden in ("Yendor","远征队","诅咒","预言","沃斯","霍尔特","梅芙"):
    forbid(farmer_body,forbidden,f"ordinary farmer knows forbidden mystery info: {forbidden}")

candidate=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/ActOneReturnCandidateLevel.java")
for prop in ("BEDROLL_A","BEDROLL_B","BEDROLL_C","BEDROLL_D","FADED_TENT","MILESTONE","OFFERING_BOWL","FOUR_RECESSES","FIELD_EDGE"):
    require(candidate,prop,f"candidate dressing missing {prop}")
require(candidate,"123, 91","Morningcreek exact POI anchor is not safely unreachable in 124x92 geometry")

region=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/RegionState.java")
require(region,"ACT_ONE_RETURN","Region Area integration missing")
require(region,"beginFormalActOne()","formal-region prototype-knowledge isolation missing")
require(region,"ABANDONED_EXPEDITION_CAMP","camp Region knowledge missing")
require(region,"MORNINGCREEK","Morningcreek HEARD_OF Region knowledge missing")

audio=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ChapterOneAudio.java")
require(audio,"tickTransientReturnAudio","Return transient ambience duck missing")
require(audio,"transientDuckTicks = 2","Yendor ambience attenuation is no longer intentionally short")
forbid(audio,"YENDOR_BASS","retired Yendor bass symbol returned")
forbid(audio,"ch1_yendor_bass.mp3","retired Yendor bass asset returned to runtime controller")
for workflow in (".github/workflows/rpg-build.yml",".github/workflows/android-apk.yml"):
    forbid(read(workflow),"ch1_yendor_bass.mp3",f"retired Yendor bass still packaged by {workflow}")

print(
    "Act 1 Scene 1 Return v0.2 gate OK: "
    f"main={main_steps} cells, camp spur={camp_steps}, crow+loop={crow_loop_steps}, "
    f"farmer->anomaly={farmer_to_anomaly}, post-shrine={post_shrine_tail}; "
    "canon, state, UI-thread safety and retired-Yendor-audio locks intact."
)
