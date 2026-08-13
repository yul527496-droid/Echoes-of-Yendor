# Echoes of Yendor — Surface Art Baseline

This document defines the first outdoor visual baseline for the sequel prototype.

- Terrain cells remain 16×16 pixels to match the existing `DungeonTilemap` contract.
- `environment/tiles_surface.png` is a dedicated 256×256, 16-column terrain atlas compatible with `DungeonTileSheet` indices.
- `environment/water_surface.png` is a dedicated 32×32 repeating water texture.
- The outdoor palette favors warm dirt, readable meadow greens, teal water, and dark forest boundaries.
- Pixel edges stay hard: no antialiasing, no high-resolution paint-over, and only a few value steps per material.
- Existing Shattered Pixel Dungeon atlases are not overwritten; sequel regions should receive their own assets as they become necessary.
- The first surface atlas intentionally implements only the terrain vocabulary required by `SurfaceEntranceLevel`. Unused atlas slots are not a commitment to future art direction.

Development rule: validate layout and readability in-game before increasing decorative detail.
