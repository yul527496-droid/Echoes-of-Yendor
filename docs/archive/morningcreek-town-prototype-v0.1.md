# Morningcreek Town Prototype v0.1 — Deferred Checkpoint

Status: **deferred / archived for later Act 1 use**, not abandoned.

Checkpoint branch: `archive/morningcreek-town-prototype-v0.1`

Checkpoint commit: `014bb878a3aa51b26b03fb27321014fcf71ef148`

This checkpoint preserves the current Morningcreek Region prototype while active campaign development returns to the real Act 1 surface opening outside the Yendor dungeon. Morningcreek Town remains intended for later reuse once the opening structure, pacing and first-act scene flow are settled.

## Preserved prototype scope

The checkpoint includes the current approximately 96×72 `MorningcreekTownPrototypeLevel` blockout with the central river and Morningcreek Stone Bridge, the differentiated south-gate / market / Ravenfeather / clinic / warehouse districts, micro-routes and ambient scale-test NPC placement. It also preserves `OldCrowInnPrototypeLevel` and the Town ↔ Old Crow prototype transitions.

The region infrastructure remains part of the active codebase and is intentionally not reverted:

- `RegionState` non-linear state skeleton for Evidence / Conclusion / World / Memory / Lead state, location knowledge, travel-node/shortcut reservations and Morning/Afternoon/Evening/Night bands.
- `RegionPoi` and location cognition with `UNKNOWN → HEARD_OF → DISCOVERED`.
- `WndRegionAreaMap` / formal Area Map support and POI markers.
- `RegionAreaLevel` integration and visited/mapped exploration capture + restore.
- HUD/minimap compatibility, including the already accepted minimap scale behavior.
- Existing dialogue portrait infrastructure.

The visual prototype is also preserved:

- `EchoesTownBuildingTilemap` and `EchoesTownPropTilemap`.
- `tools/generate_morningcreek_visual_assets_v1.py`.
- generated runtime atlas targets under `environment/echoes/morningcreek/`.
- the Visual Asset Pass v0.1 provenance/audit document and PNG validation coverage.

## Known incomplete areas

Morningcreek is still a prototype, not final town art or final Act 1 content. Known incomplete areas include:

- building roofs/facades and civic/warehouse/clinic art remain prototype-level and need later screenshot-led repainting;
- ordinary housing still relies on modular facade language rather than final authored building art;
- formal Act 1 investigation/story content is not connected here;
- World Map is not implemented;
- Travel Skip is not implemented;
- time-driven world/NPC schedules are not implemented;
- the current ambient NPCs are scale/life tests rather than final authored town population;
- the wider Morningcreek Region beyond this prototype is not complete.

## Active-development handoff

Morningcreek Town is **deferred**, not deleted, deprecated, or superseded by a revert. The archive branch is a safe point from which town work can resume later.

The active `work/world-audio-cohesion-rc` campaign entry should no longer spawn directly inside Morningcreek Town. Until a new opening scene is designed, the project may temporarily enter the existing legacy `SurfaceEntranceLevel` only as a technical placeholder.

**FORMAL ACT 1 SURFACE REDESIGN PENDING.**

The legacy `SurfaceEntranceLevel` must not be treated as the final opening design merely because it becomes the temporary default entry. Its old Demo story/audio/state behavior remains legacy compatibility material and should not be expanded into formal Act 1 content before a new opening brief is approved.

Do not resume Morningcreek expansion, add new town POIs/NPC systems, or implement World Map/Travel Skip as part of this handoff.
