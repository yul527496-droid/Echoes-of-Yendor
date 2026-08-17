# Morningcreek Region / Town v0.1 spatial prototype

Status: implementation baseline for `work/world-audio-cohesion-rc`.

This document freezes the first playable spatial prototype for the formal Act 1 direction. The existing five-map sequel demo remains a legacy vertical slice and is not the target architecture for new story content.

## Prototype goals

- Build Morningcreek as a semi-open RPG hub using large authored `Level` instances plus separate building interiors, not one seamless super-map.
- Validate town scale, navigation, FOV, pathfinding, performance, HUD/minimap behavior, Area Map behavior, and repeated indoor/outdoor transitions before expensive final art or dialogue work.
- Preserve the already accepted HUD minimap zoom, compact objective card, and dialogue portrait infrastructure.
- Do not add formal Act 1 plot progression, new Chapter 1 audio, or final environment art in this pass.
- Keep the old `SequelState.Phase` implementation isolated as legacy demo code. New formal-world state is parallel and nonlinear.

## Town prototype scale and spatial rhythm

Target authored footprint: approximately **96 x 72 cells**. The town should feel several times larger than the legacy main-street slice while remaining dense.

Spatial-information cadence target: roughly every **15–30 player cells** the route should change meaningfully through at least one of: junction, facade, landmark, bridge/river edge, courtyard, alley, service building, NPC cluster, environmental prop, investigation-capable POI, shortcut mouth, or district boundary.

Large empty grass fields are explicitly out of scope.

### Primary layout

- **South Gate / arrival apron** anchors the playable spawn.
- A **north-south Main Street** provides the strongest navigation spine.
- A broad **east-west Morningcreek River** cuts across the town, crossed by the **Morningcreek Stone Bridge** on Main Street.
- The **central market** sits south of the bridge so the player reaches activity quickly after entering town.
- **Old Crow Inn** occupies a large west-central parcel near the market and bridge, with a visible rear service yard reserved for its stable.
- The **Ravenfeather district** occupies the northwest, with a tower compound, registry facade and archive court forming a visually distinct civic cluster.
- The **clinic and herb garden** occupy the northeast/east side, away from warehouse traffic.
- The **river warehouse district** follows the southeast riverbank with loading lanes and a landing.
- **Blacksmith and public stables** occupy the southwest approach where carts can reach them without crossing the market.
- Residential blocks and at least one narrow **back alley** prevent the civic/service landmarks from feeling like isolated set pieces.
- The **North Gate** terminates Main Street and reserves the route to the northern highlands.

## v0.1 POI set

The prototype reserves at least 20 meaningful POIs. Their eventual rewards are deliberately mixed; not every POI is combat or loot.

| # | POI | Role / future reward channel |
|---|---|---|
| 1 | Morningcreek South Gate | travel node, orientation |
| 2 | South Gate caravan apron | ambient life, material/service hook |
| 3 | Central Market Square | service/NPC hub, world-state changes |
| 4 | Market well | orientation, local knowledge |
| 5 | Old Crow Inn | travel node, investigation hub, interior |
| 6 | Old Crow rear yard / stable | service, future alternate entrance |
| 7 | Morningcreek Stone Bridge | major landmark, route choke/shortcut reference |
| 8 | Ravenfeather Tower | skyline/civic landmark, world knowledge |
| 9 | Ravenfeather Registry | travel node, investigation permissions |
| 10 | Archive Court | investigation/evidence staging |
| 11 | East Clinic | service, Eileen-related future memory channel |
| 12 | Clinic herb garden | material/recovery/world knowledge |
| 13 | River warehouses | material/investigation/world-state channel |
| 14 | Warehouse loading lane | alternate route / life NPCs |
| 15 | River landing | river-valley route reference |
| 16 | Blacksmith | service |
| 17 | Public stables | travel/service infrastructure |
| 18 | East back alley | spatial shortcut / urban texture |
| 19 | Morningcreek North Gate | travel node, northern-highlands exit |
| 20 | Hunter's Road mouth | permanent shortcut A, initially closed |
| 21 | Riverside Road mouth | permanent shortcut B, initially closed |
| 22 | North Mill Path mouth | permanent shortcut C, initially closed |

## Location cognition

Formal world locations use exactly three knowledge states:

1. `UNKNOWN` — absent from Area/World Map.
2. `HEARD_OF` — Area/World Map may show only a coarse district/region hint. The exact cell is withheld.
3. `DISCOVERED` — exact marker, name and available metadata may be shown.

For v0.1, the Old Crow Inn and Ravenfeather Registry can begin as `HEARD_OF` to exercise fuzzy-map behavior. Physical proximity upgrades POIs to `DISCOVERED`.

## Three map layers

### HUD Minimap

Keep the accepted implementation unchanged in behavior:
- local explored cells only;
- fixed north;
- true 1x / 1.5x / 2x contextual zoom;
- no exact marker for unknown objectives.

### Area Map

Evolve the existing `WndRegionMap` rather than replacing it. It remains a zoomable/pannable knowledge map for the current authored `Level`, and additionally consumes formal region POI cognition:
- `UNKNOWN`: no marker;
- `HEARD_OF`: coarse district marker only, visually distinct from discovered markers;
- `DISCOVERED`: exact POI marker and description;
- POI metadata reserves categories for landmark, service, travel, investigation and shortcut.

### World Map

Not fully rendered in v0.1. Architecture reserves Morningcreek Region as the first world-map region so later Acts can expand without changing the local `Level` model.

## Formal nonlinear state skeleton

A new persistent state object runs parallel to legacy `SequelState`. It reserves these dimensions without implementing the whole Act:

- `EvidenceState`
- `ConclusionState`
- `WorldState`
- `MemoryState`
- `LeadState`
- location cognition (`UNKNOWN / HEARD_OF / DISCOVERED`)
- travel-node unlocks
- shortcut state
- world time (`MORNING / AFTERNOON / EVENING / NIGHT`)

The architecture must support conclusions such as “complete any 2 of 3 investigation paths” without ordered `phase >= N` checks.

## Travel Skip and time

Reserved travel nodes for Act 1:
- Surface Entrance
- Old King's Road Shrine
- Morningcreek South Gate
- Old Crow Inn
- Ravenfeather Registry
- Morningcreek North Gate
- Old Mill

Travel Skip is mundane route traversal, not teleportation. A future call will validate both nodes are unlocked, show an estimated cost, move the player and advance `TimeBand`.

Time has four bands only: `MORNING`, `AFTERNOON`, `EVENING`, `NIGHT`. No minute simulation is introduced.

## Permanent shortcuts

Three persistent shortcut slots exist from v0.1:
- Hunter's Road
- Riverside Road
- North Mill Path

Their mouths are visible spatially before opening. Opening one later is a persistent world-state change that makes Morningcreek cognitively smaller.

## Revisit/save strategy for the prototype

The legacy sequel creates a fresh fixed Level on each transition while all surface maps share depth/branch 0, which is insufficient for a revisitable hub.

For v0.1 we avoid changing the core dungeon depth-file scheme. Instead, formal `RegionState` captures and restores **knowledge/exploration arrays** (`visited` / `mapped`) per authored region area when leaving and re-entering it. This preserves the player's learned street geometry and Area Map through Town -> Interior -> Town transitions and save/load.

Persistent world changes, POI cognition, travel nodes and shortcut state live in `RegionState`, not in off-screen Level actors. Full off-screen actor/item persistence can be designed later if required; it is intentionally not coupled to this prototype.

## First playable interior

`OldCrowInnPrototypeLevel` is the first large building shell. v0.1 needs only the public ground floor, but the geometry explicitly reserves:
- public taproom;
- kitchen/service room;
- bar;
- fireplace/table zones;
- stair landing to future second floor;
- cellar stair/locked service zone;
- rear service door direction toward the future stable yard.

No ledger plot scene or formal Act 1 dialogue is added here.

## v0.1 acceptance checklist

- Town footprint is obviously larger and denser than the legacy demo.
- Player can roam South Gate -> market -> bridge -> Ravenfeather district -> clinic -> warehouses -> North Gate through multiple viable streets/alleys.
- River and stone bridge are unmistakable navigation features.
- Old Crow Inn, Ravenfeather Tower/Registry, clinic/herb garden, warehouse waterfront, blacksmith/stables, back alley and three shortcut mouths are spatially legible.
- At least one large interior is enterable and returning to town preserves explored knowledge.
- A handful of ambient `SurfaceVillager` NPCs exist but do not advance main plot.
- HUD task card and minimap retain accepted layout/zoom behavior.
- Area Map works on the large map and demonstrates `HEARD_OF` versus `DISCOVERED` location knowledge.
- No new formal Act 1 dialogue, no new Chapter 1 audio assets, no final environment-art dependency.
- Android debug APK is produced by CI as the primary user-test artifact; existing Windows build remains a regression check.
