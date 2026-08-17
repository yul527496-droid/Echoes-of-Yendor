#!/usr/bin/env python3
"""Production/canon gate for Act 1 Scene 1 Return v0.3 Event Readability & Signature Audio."""
from pathlib import Path
import re

ROOT=Path(__file__).resolve().parents[1]
def read(rel): return (ROOT/rel).read_text(encoding="utf-8")
def require(text, needle, label):
    if needle not in text: raise SystemExit(f"Return v0.3 gate failed: {label}")
def forbid(text, needle, label):
    if needle in text: raise SystemExit(f"Return v0.3 gate failed: {label}")
def require_true(value, label):
    if not value: raise SystemExit(f"Return v0.3 gate failed: {label}")
def points(source, name):
    match=re.search(rf"private static final int\[\]\[\] {name} = \{{(.*?)\n    \}};", source, re.S)
    if not match: raise SystemExit(f"Return v0.3 gate failed: route array {name} missing")
    result=[(int(x),int(y)) for x,y in re.findall(r"\{(\d+),(\d+)\}",match.group(1))]
    if len(result)<2: raise SystemExit(f"Return v0.3 gate failed: route array {name} too short")
    return result
def manhattan_route(route):
    return sum(abs(b[0]-a[0])+abs(b[1]-a[1]) for a,b in zip(route,route[1:]))
def subroute_distance(route, start, end):
    try:
        a=route.index(start); b=route.index(end)
    except ValueError as exc:
        raise SystemExit(f"Return v0.3 gate failed: journey checkpoint missing: {exc}")
    if a>b: a,b=b,a
    return manhattan_route(route[a:b+1])

opening=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/scenes/ActOneOpeningScene.java")
locked='''private static final String[] LINES = {\n            "我成功了。",\n            "我杀死了古神。",\n            "我带着 Yendor 回来了。",\n            "……",\n            "我独自一人。"\n    };'''
require(opening,locked,"locked five-line opening changed")
for extra in ("可是","为什么","似乎","队友","Yog","剪影","glitch","portrait"):
    forbid(opening,extra,f"opening acquired forbidden hint/token: {extra}")
require(opening,"new ActOneReturnCandidateLevel()","formal opening no longer enters runtime Return candidate")

entry=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/SequelGame.java")
require(entry,"ShatteredPixelDungeon.switchNoFade(ActOneOpeningScene.class);","formal sequel entry bypasses opening")
start_block=entry.split("public static boolean startTrainingMemory",1)[0]
forbid(start_block,"enterSurfaceEntrance();","formal sequel start fell back to legacy Surface demo")

level=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/ActOneReturnLevel.java")
require(level,"public static final int WIDTH = 124, HEIGHT = 92;","accepted Return geometry is no longer 124x92")
require(level,"new int[]{cell(62,33),cell(54,31),cell(47,27),cell(42,22)}","crow stops changed from accepted v0.2 staging")
forbid(level,"enterMorningcreekTownPrototype","Return north exit must not enter archived Town prototype")
forbid(level,"SequelState.Phase","formal Return reintroduced legacy linear Phase")

# v0.3 freezes the accepted v0.2 journey instead of gradually re-expanding it.
road=points(level,"ROAD")
camp=points(level,"CAMP_SPUR")
crow=points(level,"CROW_SPUR")
shrine_return=points(level,"SHRINE_RETURN")
main_steps=manhattan_route(road)
camp_steps=manhattan_route(camp)
crow_loop_steps=manhattan_route(crow)+manhattan_route(shrine_return)
farmer_to_anomaly=subroute_distance(road,(103,59),(77,40))
post_shrine_tail=subroute_distance(road,(86,29),(84,3))
require_true(main_steps==275,f"frozen main journey changed to {main_steps} cells")
require_true(camp_steps==33,f"frozen camp spur changed to {camp_steps} cells")
require_true(crow_loop_steps==109,f"frozen crow/shrine route changed to {crow_loop_steps} cells")
require_true(farmer_to_anomaly==51,f"frozen farmer-to-anomaly valley changed to {farmer_to_anomaly} cells")
require_true(post_shrine_tail==84,f"frozen post-shrine tail changed to {post_shrine_tail} cells")
for stop in ((62,33),(54,31),(47,27),(42,22)):
    require_true(stop in crow,f"crow stop {stop} is no longer on the authored crow spur")

state=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ActOneReturnState.java")
for key in ("openingShown","journeyStarted","campDiscovered","campBedrollClueSeen","campListClueSeen",
            "heroMarkSeen","farmerEventSeen","farmerOutcome","donkeyOutcome","cartOutcome","wolvesOutcome",
            "morningcreekHeardOf","yendorAnomalyStarted","yendorTemporarilyMissing","crowChaseActive",
            "crowChaseResolved","crowTheftPresented","shrineDiscovered","inscriptionRead","yendorRecovered",
            "northExitReached"):
    require(state,key,f"persistent Return state missing {key}")
for objective in ("离开地下城，返回地表","沿旧路前行","前往晨溪","找回 Yendor"):
    require(state,objective,f"objective flow missing: {objective}")
require(state,"markJourneyStarted()","exit-basin objective boundary missing")
require(state,"beginYendorChase()","Yendor chase start missing")
require(state,"recoverYendor()","Yendor recovery missing")
require(state,"detachAll(Dungeon.hero.belongings.backpack)","Yendor temporary removal is not inventory-safe")
require(state,"new Amulet().collect()","normal Yendor recovery repair path missing")
require_true(state.count("syncHud();") >= 3,"objective-changing state transitions are not explicitly HUD-synced")

farmer=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/npcs/ActOneReturnFarmer.java")
farmer_body=farmer.split("public class ActOneReturnFarmer",1)[-1]
require(farmer_body,"我正要回晨溪。","farmer no longer introduces Morningcreek naturally")
require(farmer_body,"region.syncHud();","Morningcreek discovery does not immediately refresh HUD")
for forbidden in ("Yendor","远征队","诅咒","预言","沃斯","霍尔特","梅芙"):
    forbid(farmer_body,forbidden,f"ordinary farmer knows forbidden mystery info: {forbidden}")

# Shrine inscription is architecture/inspection now, never loot.
shrine_tile=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/tiles/ActOneReturnShrineTilemap.java")
require(shrine_tile,"extends EchoesLandmarkTilemap","shrine inspection is not a fixed landmark")
require(shrine_tile,"石面上的字已经被风雨磨去大半，只剩一句仍然完整：","shrine inspection preface changed")
require(shrine_tile,"「力量会使你迷失。」","locked shrine warning changed")
require(shrine_tile,"state.inscriptionRead = true;","shrine inspection no longer persists read state")
forbid(shrine_tile,"extends Item","shrine inscription regressed to Item semantics")
forbid(shrine_tile,"doPickUp","shrine inspection regressed to pickup semantics")

legacy_shrine=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/ActOneShrineInscription.java")
require(legacy_shrine,"class ActOneShrineInscription","old-save compatibility class disappeared")

candidate=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/ActOneReturnCandidateLevel.java")
for prop in ("BEDROLL_A","BEDROLL_B","BEDROLL_C","BEDROLL_D","FADED_TENT","MILESTONE","OFFERING_BOWL","FOUR_RECESSES","FIELD_EDGE"):
    require(candidate,prop,f"candidate dressing missing {prop}")
for anchor in ("prop(EchoesReturnPropTilemap.LOW_WALL, 61, 33)",
               "prop(EchoesReturnPropTilemap.STUMP, 54, 30)",
               "prop(EchoesReturnPropTilemap.ROOTS, 47, 26)"):
    require(candidate,anchor,f"crow stop lost visual anchor: {anchor}")
require(candidate,"installReturnShrineInteraction();","runtime shrine inspect tile not installed")
require(candidate,"removeLegacyShrineInscriptionHeaps();","v0.2 shrine-item save cleanup missing")
require(candidate,"item instanceof ActOneShrineInscription","legacy shrine Heap cleanup no longer targets old item")
require(candidate,"ChapterOneAudio.playYendorPulse(0.92f);","second Yendor anomaly intensity is not explicit")
require(candidate,"ensureReadableCrow();","second anomaly no longer establishes readable crow presentation")
require(candidate,"ensureSingleShrineYendor();","second anomaly no longer establishes one shrine Yendor proxy")
require(candidate,"123, 91","Morningcreek exact POI anchor is not safely unreachable")
require(candidate,"sealUnintendedEarlyFord();","accepted v0.2 river-shortcut seal disappeared")
require(candidate,"paintTerrainLine(45,82,47,78,1,Terrain.EMPTY);","authored old bridge crossing disappeared")

# Crow stage state is durable; flight is visual-only and no normal stage uses place(next).
crow_npc=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/mobs/npcs/ActOneReturnCrow.java")
require(crow_npc,"bundle.put(STAGE, stage);","crow stage is not saved")
require(crow_npc,"stage = Math.max(0, bundle.getInt(STAGE));","crow stage is not restored safely")
require(crow_npc,"flyToNextStop(stops);","crow chase no longer uses readable staged flight")
require(crow_npc,"((EchoesBirdSprite) sprite).flyTo(from, next, 0.58f","normal crow leg lacks visible interpolation")
require(crow_npc,"playRavenDistant();","lost-player sparse audio recovery missing")
require(crow_npc,"Normal stage travel never uses place()","sprite.place is no longer restricted to save recovery")
forbid(crow_npc,"pos = next;\n            if (sprite != null) sprite.place(pos);","crow stage regressed to direct teleport placement")

bird_sprite=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/EchoesBirdSprite.java")
require(bird_sprite,"public void flyTo(int from, int to, float duration, Callback callback)","crow sprite flight helper missing")
require(bird_sprite,"jump(from, to, height","crow flight does not use Noosa tween presentation")
forbid(bird_sprite,"isMoving = true","crow flight became actor-blocking movement")

# Scene proxy is removed after pickup, then state restores one normal Amulet.
yendor_proxy=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/ActOneYendorAtShrine.java")
require(yendor_proxy,"detachAll(hero.belongings.backpack);","scene Yendor proxy remains in belongings")
require(yendor_proxy,"state.recoverYendor();","scene Yendor pickup does not normalize formal Amulet state")

# Camp cognition remains intentionally pre-answer.
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

region=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/RegionState.java")
for token in ("ACT_ONE_RETURN","beginFormalActOne()","ABANDONED_EXPEDITION_CAMP","MORNINGCREEK"):
    require(region,token,f"Region integration missing {token}")

# Audio: old abrasive cue remains retired; v0.3 owns real crow one-shots + generated Yendor motif.
audio=read("core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ChapterOneAudio.java")
for token in ("ch1_raven_caw.wav","ch1_raven_wings.ogg","ch1_yendor_signature.wav",
              "playYendorPulse(0.58f)","play(YENDOR_SIGNATURE, 0.30f * strength, 1f)",
              "playRaven()","playRavenDistant()","playRavenWings()","transientDuckTicks = 2"):
    require(audio,token,f"v0.3 audio contract missing {token}")
forbid(audio,"YENDOR_BASS","retired Yendor bass symbol returned")
forbid(audio,"ch1_yendor_bass.mp3","retired Yendor bass asset returned")

generator=read("tools/generate_yendor_signature.py")
for token in ("DURATION = 0.92","TARGET_PEAK_DBFS = -11.5","SEED = 0x59454E44",
              "ch1_yendor_signature.wav"):
    require(generator,token,f"deterministic Yendor generator contract missing {token}")

for workflow in (".github/workflows/rpg-build.yml",".github/workflows/android-apk.yml"):
    wf=read(workflow)
    forbid(wf,"ch1_yendor_bass.mp3",f"retired Yendor bass still packaged by {workflow}")
    require(wf,"bba22883209b435905471d057160f1badae59b89a62e9a8b3199d7e327b17339",
            f"crow WAV is not SHA-pinned in {workflow}")
    require(wf,"f0907c3639f1cc828ac54c9527e8e458d0ca173626d050a8eb8df54a9e36c1b5",
            f"wing ZIP is not SHA-pinned in {workflow}")
    require(wf,"060d1e2b9403893c4aaff7148ea604c4bcfc6bf3e7bab44913a56429ff261a9c",
            f"wing OGG member is not SHA-pinned in {workflow}")
    require(wf,"python tools/generate_yendor_signature.py",f"Yendor generator missing from {workflow}")

print(
    "Act 1 Scene 1 Return v0.3 gate OK: "
    f"frozen journey={main_steps}, camp={camp_steps}, crow+loop={crow_loop_steps}, "
    f"farmer->anomaly={farmer_to_anomaly}, post-shrine={post_shrine_tail}; "
    "opening/cognition locked, shrine is inspect-only, crow flight/readability and save stage locked, "
    "Yendor uniqueness/objectives locked, and crow/Yendor audio provenance is deterministic/pinned."
)
