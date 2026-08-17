# Act 1 Scene 1 — Return / 归来 v0.1

Status: **production source-of-truth for the playable candidate**. This document supersedes the legacy Surface Entrance demo story for the formal campaign opening. Morningcreek Town / Old Crow Inn / Region prototype work remains archived and intact for later Act 1 reuse.

## 1. Locked opening

The opening is a near-silent black screen. It may contain **only** the following text, in this exact order and spelling, with no added narration, portrait, objective, glitch, flashback, silhouette, voice or implication that the final statement is false:

1. `我成功了。`
2. `我杀死了古神。`
3. `我带着 Yendor 回来了。`
4. `……`
5. `我独自一人。`

After the final line there is a short pause, then the formal surface scene fades in. The first viewing must allow the player to believe `我独自一人。` naturally.

## 2. Formal scene boundary

The formal Level is `ActOneReturnLevel`; legacy `SurfaceEntranceLevel` remains compatibility/reference code only and must not supply its `SequelState.Phase` Farmer→Amulet→Old Road story to the formal opening.

Target authored footprint: **88×68**. Spatial order is fixed:

`Yendor Dungeon exit basin → explorable forest edge → optional abandoned expedition camp → old road → farmer/donkey/wolves → quiet road → Yendor anomaly + one crow → forest spur → old shrine → short loop rejoining farther north → increasing civilization traces → north exit toward the future formal Morningcreek approach scene`.

The map is neither a corridor nor an open world: one main road, two side exploration structures (camp and shrine), and one explicit shrine loop. Normal first exploration target is 15–25 minutes; thorough exploration should remain under about 30 minutes. Long empty forest corridors are not acceptable.

## 3. Player knowledge contract

At scene end the player may know only that:

- the abandoned camp appears to have accommodated roughly four people;
- a personal mark resembling the returner's appears there;
- Morningcreek is north;
- Yendor itself seems not entirely normal;
- an old shrine bears an uncomfortable warning.

The scene must **not** establish three teammates, their complete names, the missing 25 days, a memory curse, Event X, the gods, Yog's ultimate state or Yendor's origin. The future Ravenfeather Registry remains the first hard proof of a fixed four-person expedition.

## 4. Dungeon exit basin

The player receives control in a slightly sunken forest basin around an ancient, mossy dungeon mouth: broken stair masonry, roots, damp soil, worn stone and restrained ruins. No NPC, monster, magical spectacle or lore explanation appears here.

Three small non-mystery exploration beats establish the surface as a real explorable place: a creek edge, a collapsed old carving and a nearly overgrown travel trace. Initial objective may be `离开地下城旧址`.

## 5. Abandoned expedition camp

The camp is optional, visible from normal travel, and offset from the main route rather than made into a mandatory room. It uses natural tree/stone boundaries and contains a dead fire, four non-identical bedroll positions, cooking/travel gear, faded cloth, bags/crates and a maintenance point.

Evidence escalation is deliberately bounded:

1. Visual composition suggests about four occupants without interaction.
2. Mundane four-person gear can be rationalized as another expedition.
3. Optional water-damaged checklist may preserve only fragments such as `医疗包——沃…… / 测绘工具——霍…… / 防水纸——芬……`.
4. The strongest sting is the returner's familiar personal mark. Reaction is denial only: it looks like his mark, it should not be here, perhaps it only resembles it.

No full teammate names, lost-memory theory, curse theory, 0/4 checklist UI or hard team conclusion is allowed. Three tiny scratches may exist as non-interactive future re-visit dressing.

After the camp, the route intentionally gives the player ordinary forest/road breathing room before the next event.

## 6. Old King's Road progression

Human construction gradually increases northward: mud and fragmented paving first, then wheel ruts, low walls, an old milestone/sign and increasingly maintained road edges. The road itself is a visual meter for approaching civilization.

## 7. Farmer / donkey / wolves

This is Scene 1's only formal combat and the first ordinary living person. It is **not** a choice menu. The world event already exists: a tilted farm cart, frightened donkey, old farmer and about three wolves. Early cues may be donkey distress, cart impact and one wolf warning.

The player may fight directly, pull at range, flank, approach, lure or leave. Leaving resolves naturally as non-intervention and cannot block progression.

The farmer knows nothing about Yendor, the expedition mystery, Event X or prophecy. If rescued he thanks the player, is surprised someone came from the south road, says he is returning to **晨溪**, and explains that the north road reaches it. This is the first formal Morningcreek-name reveal. All speech uses Dialogue Stage text; there is **no recorded human voice**.

Persistent outcome records at least farmer event seen, farmer rescued/ignored, donkey survival, cart abandoned/usable state and wolves resolved/abandoned.

## 8. Yendor anomaly and crow

After another quiet road interval, Yendor itself produces a short, restrained pulse/warmth. A single ordinary crow is present. A second brief reaction startles the returner; the crow catches the chain/wrapping and carries the amulet away. The beat must read as an unsettling Yendor anomaly that attracts an animal, never as a super-powered comedy bird defeating the Yog victor.

The chase is a short 15–25 tile side route. Guidance is environmental and sparse: at most a few separated caws/flaps, visible crow staging and route composition. Temporary objective may become `找回 Yendor`. The physical Amulet is removed/recovered idempotently so save/load before, during and after the event cannot duplicate or permanently lose it.

## 9. Old shrine

The shrine is a small restrained forest clearing with an old stone altar, moss and worn offering architecture. No god, projection, boss, magical smoke, giant statue or explanatory score is allowed. The crow leaves as the player approaches and Yendor is recovered at the altar.

The only important inscription is exactly:

**「力量会使你迷失。」**

It is an old warning, not a prophecy or Yendor manual. The scene may contain a subtle four-part worn recess with no explanatory interaction. A short alternate path rejoins the main road farther north so the first optional detour teaches that exploration can reveal route loops.

## 10. Final road and transition

After the shrine there are no additional core mysteries or reversals. Road repair, fences, stumps, field edge and weak distant life traces increase. The returner's motivation is practical fatigue/rest/food and going to Morningcreek, not a sudden investigation manifesto.

The north exit marks completion and uses an explicit safe development transition until the next formal Morningcreek approach scene is approved. It **must not** drop the player into the archived `MorningcreekTownPrototypeLevel` while pretending that prototype is the approved next scene.

## 11. Scene state

Formal state is centralized in `ActOneReturnState`, Bundle-persistent and independent of legacy `SequelState.Phase`. It tracks at minimum:

- opening shown;
- camp discovered / bedroll clue / list clue / hero mark;
- farmer event / farmer outcome / donkey outcome / cart state / wolves outcome;
- Morningcreek heard-of;
- Yendor anomaly / temporarily missing;
- crow chase active/resolved;
- shrine discovered / inscription read / Yendor recovered;
- north exit reached.

Old saves missing fields default safely. World actors are reconstructed from stable state so reload does not duplicate farmer, wolves, crow or Yendor.

## 12. Region / HUD rules

The accepted HUD, MiniMap scale controls, Dialogue Portrait v2 and Area Map remain in place. Formal Scene 1 uses Region Area knowledge without exposing future locations.

- Dungeon exit starts `DISCOVERED`.
- Camp becomes `DISCOVERED` only on arrival.
- Shrine becomes `DISCOVERED` only on arrival.
- Morningcreek becomes only `HEARD_OF` when the farmer names it; no exact GPS coordinate is revealed.
- No opening map full of question marks.

Objective cadence: none or `离开地下城旧址` → after Morningcreek reveal `前往晨溪` → during crow event `找回 Yendor` → after recovery `前往晨溪`.

## 13. Visual production policy

Runtime remains SPD-native **16×16**, nearest-neighbor/no-resampling. Reuse the accepted project-owned Echoes surface vertical-slice pipeline (`EchoesSurfaceTilemap`, `EchoesLandmarkTilemap`, `tools/generate_surface_vertical_slice.py`) and the existing Chapter 1 character atlas where suitable. Scene readability requires distinct languages for mossy dungeon exit, layered forest, faded human camp, degraded-to-maintained old road, ordinary farmer/cart space and restrained shrine.

Existing runtime art used by this candidate is project-owned deterministic Echoes art; no new third-party pixels are required for v0.1. External packs remain verified legal reference/mother-material options only unless a later manifest explicitly says pixels were imported:

| Source | Official page | License | 16×16 | Actual pixels imported by Return v0.1 |
| --- | --- | --- | --- | --- |
| Kenney Roguelike/RPG Pack | https://kenney.nl/assets/roguelike-rpg-pack | CC0 | yes | no |
| Kenney Tiny Town | https://kenney.nl/assets/tiny-town | CC0 | yes | no |

Runtime paths reused include:

- `environment/echoes/ch1_surface/grass.png`
- `environment/echoes/ch1_surface/forest.png`
- `environment/echoes/ch1_surface/road.png`
- `environment/echoes/ch1_surface/river.png`
- `environment/echoes/ch1_surface/bridge.png`
- `environment/echoes/ch1_surface/camp_ruin.png`
- `environment/custom_tiles/echoes_landmarks_v1.png`
- `sprites/echoes_ch1_character_sprites_v1.png`

## 14. Audio policy / provenance

Opening is near-silent. Surface uses only the already integrated restrained forest ambience, stream proximity, wolf/donkey warning one-shots and the short Yendor pulse where verified. Human/street/tavern chatter is forbidden in this scene. Crow audio remains optional: silence is better than an unverified or wrong bird file.

Sonniss #GameAudioGDC remains a possible source only under its official license at https://sonniss.com/gdc-bundle-license/ (worldwide, non-exclusive, royalty-free use; modification and synchronization in games allowed; source recordings may not be sold as-is). Restricted original source sounds are not to be committed as a standalone extractable library. Existing build-time fetch + checksum packaging policy remains preferred. No new Sonniss raw source is added by Return v0.1 unless separately pinned and documented.

## 15. Candidate gate

`Act 1 Scene 1 — Return / 归来 v0.1 candidate` is a playable prototype candidate, **not final art or final story polish**. Candidate status requires the exact opening, complete traversable route, optional camp without knowledge leak, rescue/ignore continuation, save-safe Yendor chase, restrained shrine, safe north-end transition, preserved Region/HUD behavior, no human audio contamination, and green Android + Windows CI including asset generation/validation and artifact upload.
