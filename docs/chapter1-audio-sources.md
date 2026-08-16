# Chapter 1 audio provenance

This document separates the **current runtime delivery chain** from the earlier Chapter 1 source-selection notes. The two should not be confused.

## Current runtime delivery (0.0.9)

Chapter 1 audio binaries are intentionally not stored in the source tree. The Windows build injects them in `.github/workflows/rpg-build.yml` before Gradle packaging. Every pinned remote asset is downloaded to its final `core/src/main/assets/...` runtime path and SHA256-verified before the build is allowed to continue.

This means paths used by `ChapterOneAudio`, such as `music/echoes/ch1_surface_ambience.mp3` and `sounds/echoes/ch1_wolves.mp3`, are present in the packaged game even though they do not appear in a normal repository checkout before the workflow injection step.

| Runtime asset | Current build source | Pinned SHA256 |
|---|---|---|
| `music/echoes/ch1_surface_ambience.mp3` | Mixkit Forest Birds Ambience, asset 1210 | `47ab079aac704576d45d252c416de1da31dd016b69b5b9a2eaaa720efb6c2411` |
| `sounds/echoes/ch1_stream_loop.mp3` | Mixkit Water Flowing Ambience Loop, asset 3126 | `f66d790fde40c2bad3f36039dd570b819509519d0fb8a3a458b3571a0928a2a0` |
| `sounds/echoes/ch1_birds.mp3` | Mixkit Little Birds Singing, asset 17 | `31afb3341b8c583038acbdc1a120f2c45699767ddd3a8fa200ec5f72fb54e89f` |
| `sounds/echoes/ch1_farmer_cart.mp3` | Mixkit Street Ambien with Carriage and People, asset 353 | `9ecd3121e994e25af34b3e69325cb301f773330d1640acc751f300bad248e9c5` |
| `sounds/echoes/ch1_donkey.mp3` | Mixkit Donkey Scream, asset 1770 | `ee2617a3404c0239262d6a0a52590e31eb5b8b6b1a3931b065fd1969decb13d2` |
| `sounds/echoes/ch1_yendor_bass.mp3` | Mixkit Mysterious Bass Pulse, asset 2298 | `91f21dd769074523820477d31b28b09c775426100926c3c40ecf2513de29f5aa` |
| `sounds/echoes/ch1_wolves.mp3` | Mixkit Wolves at Scary Forest, asset 2485 | `d164d2d1abeaf633199cc482b580b224724c94ebab71fd6fe0749aa6d77d09b4` |
| `music/echoes/ch1_farm_ambience.mp3` | Daytime Farm Ambience 409990, pinned GitHub mirror | `bc6ddb1b50b2082a3d29af3d5c1b6a9b554d33c0745cb0e7c15caa8f2660fe25` |

The workflow itself is the source of truth for exact download URLs and hashes. If an upstream file changes, the build must fail rather than silently accepting different audio.

### Runtime mix notes

- Surface Entrance uses the forest ambience bed.
- Near the stream, `ChapterOneAudio` crossfades by switching the single streamed music bed to the water recording; outside the audible radius it returns to the forest bed.
- Old King's Road deliberately reuses the forest bed at a lower volume instead of pretending there is a separate old-road recording.
- Morningcreek Outskirts uses the farm ambience bed.
- The farmer's first approach now has a dedicated carriage one-shot before the dialogue beat.
- The Yendor flare ducks the active ambience bed and restores it after the scripted interruption.
- Old Crow Inn uses the pinned tavern music asset already shared with the Ledger audio set.
- A dedicated raven call and an independent secondary inn/fireplace room-tone loop remain declared gaps. Do not substitute unrelated dungeon or monster sounds simply to fill those hooks.

## Historical source candidates selected on 2026-08-15

The first Surface Return vertical-slice pass documented a set of Pixabay recordings as source candidates for a game-specific derivative pack. That pack/decoder delivery design was never the final runtime pipeline and no `sounds/echoes/ch1_audio_XX.pack` files are required by 0.0.9.

The historical selections are retained here for provenance and future remix reference:

| Planned derivative | Historical source recordings |
|---|---|
| `ch1_forest.mp3` | Forest wind and birds — Pixabay 6881; Bird chirps — Pixabay 343624 |
| `ch1_stream.mp3` | Small Gentle Stream - LOOP — Pixabay 514373; Forest wind and birds — Pixabay 6881 |
| `ch1_bird.mp3` | Bird chirps — Pixabay 343624; Bird Chirps — Pixabay 499426 |
| `ch1_farmer_cart.mp3` | Carroça andando na floresta — Pixabay 67458; tiny bell3 — Pixabay 83604; Wood creaks — Pixabay 411791 |
| `ch1_donkey_break.mp3` | Donkey Bray — Pixabay 36757; tiny bell3 — Pixabay 83604; Wood creaks — Pixabay 411791 |
| `ch1_yendor_pulse.mp3` | Bass Throb 10sec — Pixabay 106995; Magic Pulse — Pixabay 452856 |
| `ch1_oldroad.mp3` | Forest wind and birds — Pixabay 6881; Distant crows, ravens, forest birds — Pixabay 332962 |
| `ch1_wolves.mp3` | Wolves Fighting — Pixabay 227005 |
| `ch1_farm.mp3` | Daytime Farm Ambience — Pixabay 409990; Bird chirps — Pixabay 343624 |

### Historical source-page references

- https://pixabay.com/sound-effects/nature-forest-wind-and-birds-6881/
- https://pixabay.com/sound-effects/film-special-effects-small-gentle-stream-loop-514373/
- https://pixabay.com/sound-effects/nature-bird-chirps-343624/
- https://pixabay.com/sound-effects/nature-bird-chirps-499426/
- https://pixabay.com/sound-effects/search/floresta/ (source ID 67458)
- https://pixabay.com/sound-effects/search/tiny-bells/ (source ID 83604)
- https://pixabay.com/sound-effects/film-special-effects-wood-creaks-411791/
- https://pixabay.com/sound-effects/nature-donkey-bray-36757/
- https://pixabay.com/sound-effects/musical-bass-throb-10sec-106995/
- https://pixabay.com/sound-effects/magic-pulse-452856/
- https://pixabay.com/sound-effects/distant-crows-ravens-forest-birds-332962/
- https://pixabay.com/sound-effects/nature-wolves-fighting-227005/
- https://pixabay.com/sound-effects/nature-daytime-farm-ambience-409990/

Historical processing ideas included shortening ambience beds, layering cart/bell/creak elements, combining donkey/bray/harness stress, and mixing the Yendor bass throb with a quieter magic layer. They remain remix notes, not a description of the current packaged binaries.
