# Act 1 Scene 1《归来》v0.3 — Event Readability & Signature Audio Candidate

Status: focused candidate pass for event readability, crow presentation, Yendor signature audio, shrine interaction semantics, objective synchronization and save/load boundary hardening.

This pass does **not** authorize a new map, Morningcreek Town development, World Map, Travel Skip, later Act 1 investigation content, Event X explanation, new bosses, new NPC schedules, HUD/minimap redesign, Dialogue Portrait redesign or Audio Engine reconstruction.

## Frozen scene geometry

The accepted v0.2 journey is intentionally frozen:

- map size: **124×92**
- authored main-road metric: **275 Manhattan cells**
- camp spur: **33 cells one-way**
- crow spur + shrine return: **109 cells**
- farmer-to-anomaly pacing valley: **51 cells**
- post-shrine civilization tail: **84 cells**
- crow stops remain exactly:
  1. `(62,33)`
  2. `(54,31)`
  3. `(47,27)`
  4. `(42,22)`

v0.3 does not redraw or enlarge this route. Existing props are used to make the first three crow stops read as a low wall/root area, stump/deeper-forest area and roots/first-shrine-read area. The fourth stop remains beside the shrine/Yendor composition.

## Shrine inscription bug: root cause and fix

### v0.2 root cause

`ActOneShrineInscription` was implemented as an `Item`. Its `doPickUp()` displayed the inscription and returned `false`. In normal SPD semantics, a failed pickup is still a failed pickup, so the engine could follow the interaction with an out-of-fiction carry failure such as:

`你无法携带：古老神龛的刻字。`

The bug was therefore not the inscription copy. The bug was representing architecture as loot.

### v0.3 interaction

The runtime Return candidate now uses `ActOneReturnShrineTilemap`, a fixed `CustomTilemap`/landmark interaction. SPD's normal cell-inspection window obtains the landmark name/description directly from the tile.

The only inscription content is:

`石面上的字已经被风雨磨去大半，只剩一句仍然完整：`

`「力量会使你迷失。」`

Inspecting it sets `ActOneReturnState.inscriptionRead = true`. There is no pickup action, backpack object, obtain message or failed-carry message.

The old `ActOneShrineInscription` class remains available only so older v0.2 saves can deserialize safely. `ActOneReturnCandidateLevel` removes any old inscription item from deserialized Heaps and destroys an otherwise-empty legacy Heap. No other Heap is removed.

## Crow presentation: old vs new

### v0.2

A durable `stage` existed, but normal movement advanced by assigning the next logical `pos` and immediately calling `sprite.place(pos)`. State changed correctly, but there was almost no visual information connecting A to B. `ChapterOneAudio.playRaven()` was also empty.

### v0.3

The four fixed logical stops remain unchanged. On each normal stage transition, logical state moves immediately to the next durable stop for save safety, while `EchoesBirdSprite.flyTo()` uses SPD/Noosa's existing `CharSprite.jump()` tweener to interpolate the sprite visually for roughly **0.58 seconds**.

This is presentation-only movement:

- it does not set normal actor `isMoving` state;
- it does not wait on `Char.onMotionComplete()`;
- it does not consume extra hero turns to finish the animation;
- the crow remains noncombatant, nonblocking and unkillable;
- if a save/reload interrupts a visual tween, the saved `stage` reconstructs the crow at that stage's valid fixed stop rather than attempting to serialize animation internals.

### Initial theft presentation

The second Yendor anomaly no longer begins with the old explanatory theft paragraph. The runtime candidate suppresses that one base trigger presentation, then performs:

1. stronger-but-still-restrained Yendor signature + ambience attenuation;
2. tiny hero/Yendor visual flash;
3. ordinary crow call and light wing movement;
4. visible short flight from a nearby visual perch toward the hero;
5. visible flight from the hero to the first fixed stop;
6. only after landing, a short support message:

`吊链从指间滑了出去。`

`乌鸦衔着它，落向左侧林间。`

The bird receives no glow, smoke, dialogue portrait, magical particle, speech or identity reveal. The abnormal object remains Yendor, not the crow.

### Chase readability / recovery

Normal legs use visible tweened flight. Wing accents are one-shots; a nearby caw is not repeated at every stop. One restrained mid-chase call remains. If the player stays more than roughly fourteen cells away from the current stop for a sustained interval, one quieter distant call may play once for that stage. There is no looping crow audio or GPS marker.

At the final stop the crow gives one restrained departure call/wing accent and visibly flies away before being removed.

## Crow audio provenance

See `docs/chapter1-audio-sources.md` for full URLs and SHA-256 records.

### Call

- OpenGameArt: **Crow caw**
- author: **zeroisnotnull**
- license: **CC0**
- runtime path: `sounds/echoes/ch1_raven_caw.wav`
- SHA-256: `bba22883209b435905471d057160f1badae59b89a62e9a8b3199d7e327b17339`
- nearby relative volume: `0.25`
- distant recovery relative volume: `0.13`, pitch `0.96`

### Wing movement

- OpenGameArt: **Large Wings Flap**
- author: **AntumDeluge**
- license: **CC0**
- runtime path: `sounds/echoes/ch1_raven_wings.ogg`
- source ZIP SHA-256: `f0907c3639f1cc828ac54c9527e8e458d0ca173626d050a8eb8df54a9e36c1b5`
- selected OGG SHA-256: `060d1e2b9403893c4aaff7148ea604c4bcfc6bf3e7bab44913a56429ff261a9c`
- runtime relative volume: `0.14`, pitch `1.20`

Windows and Android CI download these sources from their official OpenGameArt file URLs and reject content whose SHA-256 no longer matches the pinned values.

## Yendor signature candidate

The retired v0.2 `ch1_yendor_bass.mp3` remains forbidden and is not re-enabled.

v0.3 deliberately avoids another stock 'magic' effect. `tools/generate_yendor_signature.py` synthesizes one project-owned deterministic candidate during the build:

- runtime path: `sounds/echoes/ch1_yendor_signature.wav`
- third-party source: **none**
- duration: **0.92 s**
- format: **44.1 kHz mono 16-bit PCM**
- target peak: **-11.5 dBFS**
- CI-measured deterministic RMS: **-22.56 dBFS**
- deterministic seed: `0x59454E44`
- structure: soft low/low-mid resonances + extremely quiet mineral partials + low-passed air texture.

Runtime hierarchy:

- first anomaly: signature intensity `0.58`, about `0.174` relative sample level before user SFX setting, plus a light two-tick ambience duck;
- theft anomaly: intensity `0.92`, about `0.276` relative sample level before user SFX setting, plus a somewhat clearer two-tick ambience duck and then the crow cue;
- no Yendor pitch variation, loop, long sustain, UI beep or bass-drop behavior.

The generator checks clipping/headroom and a conservative RMS ceiling in CI. Frequency analysis of the deterministic candidate is strongly weighted toward low/low-mid energy; high-frequency energy is intentionally minimal. This is technical QA only. The automated environment cannot replace human headphone/phone listening, so final perceptual acceptance remains real-device playtesting.

## Objective / HUD state flow

`RegionState`/HUD rendering itself is not redesigned. `ActOneReturnState` now owns a sparse, explicit Return objective flow and calls HUD synchronization at state-changing boundaries:

1. exit basin: `离开地下城，返回地表`
2. after leaving the basin / entering the real journey: `沿旧路前行`
3. after the farmer naturally introduces Morningcreek: `前往晨溪`
4. while Yendor is absent during the crow chase: `找回 Yendor`
5. immediately after Yendor recovery: `前往晨溪`
6. reading the shrine does not replace the travel goal; it remains `前往晨溪`.

`journeyStarted` is persisted. Older saves that predate that field also derive the basin boundary from the hero's current Return position, avoiding a stale opening objective after loading farther north.

## Yendor save/load and uniqueness boundary

`ActOneReturnState` continues to own the canonical temporary-missing/recovered flags and repair inventory consistency during scene ticks.

v0.3 adds one normalization fix: `ActOneYendorAtShrine` is a scene-owned ground proxy subclass of `Amulet`. After a successful shrine pickup, the proxy removes itself from the backpack first, then `recoverYendor()` creates/repairs exactly one normal formal `Amulet`. This avoids leaving the scene proxy as the player's long-term Amulet object.

Expected durable boundaries are therefore:

- before first anomaly: one normal Amulet;
- after first anomaly: one normal Amulet;
- immediately before theft: one normal Amulet;
- chase active: no normal inventory Amulet + one scene Yendor proxy at the shrine;
- reload during chase: state removes any accidental inventory Amulet and reconstructs/retains the shrine proxy;
- shrine before pickup: exactly one scene proxy;
- after pickup: proxy removed, exactly one normal Amulet restored;
- reload after pickup: normal Amulet remains; no shrine duplicate is generated.

Crow `stage` is separately bundle-persisted. Visual tween state is deliberately transient, so a reload resumes at the current fixed stop rather than softlocking an in-progress animation.

## Candidate gate / acceptance boundary

`tools/validate_act1_return_candidate.py` now guards:

- exact locked five-line opening;
- exact 124×92 accepted journey metrics;
- unchanged four crow stops and their relationship to the authored crow corridor;
- no Morningcreek prototype transition / no legacy linear phase;
- objective state strings and explicit sync points;
- inspect-only shrine interaction and old-save Heap cleanup;
- visible nonblocking crow tween contract and saved stage;
- visual anchors at the crow stops;
- Yendor scene-proxy normalization;
- old bass remaining forbidden;
- Crow source SHA pins in both build workflows;
- deterministic Yendor generator contract.

CI/build success proves compilation, packaging and these source-level invariants. It does not prove that a human player can subjectively hear/see every event clearly on every device. That final event-readability judgment remains the purpose of this Candidate build.
