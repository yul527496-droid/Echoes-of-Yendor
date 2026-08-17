# Act 1 Scene 1《归来》v0.2 — Journey & Audio Pass Candidate

Status: real-device pacing/audio correction pass. This document supersedes v0.1 only for the current Return journey geometry and Yendor anomaly audio behavior. It does **not** authorize Morningcreek Town expansion, World Map, Travel Skip, later investigation content, Event X explanation, or HUD/minimap/Area Map redesign.

## Why this pass exists

Windows real-device playtesting of the v0.1 candidate found two production failures:

1. The authored journey was too short. The 88×68 layout placed camp, farmer/wolves, Yendor anomaly, crow/shrine and the north exit close enough that the scene read as a compact trigger test rather than travel across the surface.
2. `sounds/echoes/ch1_yendor_bass.mp3` was judged abrasive as a repeating Yendor signature. It is retired from the Return runtime rather than EQ/pitch/volume-adjusted into continued use.

## v0.2 journey geometry

- Level: `ActOneReturnLevel`
- Runtime candidate: `ActOneReturnCandidateLevel`
- Size: **124×92**
- Start / dungeon basin: `(22,86)`
- Camp center: `(30,69)`
- Farmer event: `(102,59)`
- First Yendor anomaly travel zone: around `(77,40)`
- Crow diversion begins around `(71,35)`
- Shrine: `(41,22)`
- Main-road rejoin: `(86,29)`
- North development exit: `(84,3)`

The level now starts from dense authored forest (`WALL`) and carves a controlled walkable travel ribbon, wide clearings, riverbank space, side pockets and two deliberate side structures. This prevents the expanded dimensions from becoming a shortcut-friendly grass rectangle.

### Authored route metrics

The CI source gate parses the route arrays and enforces minimum travel lengths:

- main authored road polyline: **237 Manhattan cells**
- camp spur: **33 cells one way** (~66 for a visit and return)
- crow spur + shrine return path: **109 cells**
- farmer-area road checkpoint to anomaly checkpoint: **51 cells**
- main-road rejoin after shrine to north exit: **76 cells**

The mandatory crow/shrine route adds roughly **88 cells** compared with staying on the direct old-road segment. A plot-complete traversal therefore carries roughly **325 authored movement cells** before local combat, observation, dialogue and optional exploration; visiting the camp adds roughly another 66 authored cells.

These are geometry/pacing guards, not claims of final player completion time. Real-device timing remains the acceptance authority.

## Spatial rhythm

The same Scene 1 plot beats are preserved and separated by environmental travel:

1. **Dungeon exit basin** — broad mossy clearing, damp ground, old stone trace and quiet river edge.
2. **River valley / old bridge** — an actual non-passable water cut with one authored passable bridge deck plus a small optional riverbank lookout.
3. **Camp spur** — broken/overgrown side trace leading away from the main route; camp remains optional and keeps the existing clue-information boundary.
4. **Camp → farmer travel** — widened/narrowed forest road, logs, stone traces, open pockets and old-road material changes; no extra combat.
5. **Farmer / wolves pasture pinch** — existing event logic, three wolves, ordinary farmer/donkey/cart, no new quest structure.
6. **Farmer → anomaly pacing valley** — long quiet bend with road construction becoming more legible before Yendor acts.
7. **Crow spur** — four sparse crow stop positions across a separate enclosed woodland route; no breadcrumb on every cell.
8. **Shrine loop** — same small shrine information layer, followed by a different path that rejoins farther north.
9. **Civilization tail** — wider road, milestones, low walls and field-edge rhythm before the safe development exit.

No new formal plot conclusion is added.

## Yendor audio decision

### Retired v0.1 cue

The previous Return implementation used:

- runtime path: `sounds/echoes/ch1_yendor_bass.mp3`
- source injected by CI: Mixkit `Mysterious Bass Pulse`, preview asset `2298`
- previous playback: `Sample.INSTANCE.play(...)` through `ChapterOneAudio.playYendorPulse()` at relative SFX volume `0.48`, pitch `0.90`
- previous behavior also ducked the current ambience bed.

Official Mixkit pages identify Sound Effects as using the Mixkit Sound Effects Free License and describe personal/commercial project use without required attribution. The problem in this pass is **quality, not a newly alleged license defect**.

### v0.2 behavior

The independent bass Sample is removed from `ChapterOneAudio.preload()` and from both Windows and Android build-time injection lists. `playYendorPulse()` now performs only a deliberately short environmental attenuation:

- no new third-party Yendor sound asset
- no beep/ping/buzz/bass hit
- ambience temporarily reduced to 42% of its current scene-relative bed
- transient duration managed for two Return actor ticks, then restored automatically
- shrine still uses the existing quieter natural-bed behavior

This follows the production rule: **silence plus subtle environmental response is preferable to a bad signature cue**. A future Audio Pass may add a genuinely superior 0.7–1.2 second Yendor motif, but v0.2 does not pretend the old sound is acceptable.

## Systems intentionally unchanged

This pass does not redesign:

- `RegionState` knowledge/evidence model
- `WndRegionAreaMap` rendering/zoom/HEARD_OF rules
- HUD task card
- minimap scale behavior
- dialogue portraits / Dialogue Stage
- Yendor save-safe temporary removal/recovery state
- farmer outcome persistence
- Morningcreek archived prototype

`visited` / `mapped` continue to be captured/restored through the existing RegionState area logic. Existing Yendor chase state continues to reconstruct the missing/recovered Amulet safely on reload.

## v0.1 save migration

Because geometry changed from 88×68 to 124×92, loading a stored old-size Return level rebuilds the current authored geometry and places the hero safely at the dungeon-exit basin while preserving the separate `ActOneReturnState` story facts. Normal v0.2 saves retain current map exploration through the existing level/RegionState bundle paths.

## Candidate gate

`tools/validate_act1_return_candidate.py` now guards:

- locked opening/cognition boundaries
- 124×92 geometry
- minimum authored journey lengths
- farmer→Yendor pacing valley
- shrine→north tail
- crow stop layout
- Morningcreek heard-only behavior
- Yendor item safety
- render-thread item windows
- absence of `ch1_yendor_bass.mp3` from runtime controller and both CI injection workflows

Final acceptance remains Windows real-device playtesting; this file records the authored candidate, not a claim of final scene completion.
