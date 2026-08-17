# Chapter 1 audio provenance

This document contains both historical Chapter 1 sourcing notes and the currently active Return-scene additions. Where a later section conflicts with an older prototype note, the later, asset-specific record is authoritative for that asset.

## Act 1 Scene 1 Return v0.3 additions — current runtime provenance

### Crow call — `sounds/echoes/ch1_raven_caw.wav`

- Source title: **Crow caw**
- Author: **zeroisnotnull**
- Official source page: https://opengameart.org/content/crow-caw
- Official file URL used by CI: https://opengameart.org/sites/default/files/crow_caw.wav
- License on the official source page: **CC0 / Public Domain dedication**
- Source description: a real crow caw recorded in Cairo.
- Source-file SHA-256 pinned by both Windows and Android CI:
  `bba22883209b435905471d057160f1badae59b89a62e9a8b3199d7e327b17339`
- Runtime treatment:
  - nearby scripted call: relative SFX volume `0.25`, pitch `1.00`
  - distant lost-player reminder: relative SFX volume `0.13`, pitch `0.96`
  - never looped.

The raw CC0 WAV is not hand-copied into Git history. CI retrieves the official file and refuses it if the pinned SHA-256 changes, then packages that verified file into the build.

### Crow wing movement — `sounds/echoes/ch1_raven_wings.ogg`

- Source title: **Large Wings Flap**
- Author: **AntumDeluge**
- Official source page: https://opengameart.org/content/large-wings-flap
- Official archive URL used by CI: https://opengameart.org/sites/default/files/wings_flap_large.zip
- License on the official source page: **CC0 / Public Domain dedication**
- Source archive SHA-256:
  `f0907c3639f1cc828ac54c9527e8e458d0ca173626d050a8eb8df54a9e36c1b5`
- Selected archive member: `wings_flap_large.ogg`
- Selected OGG SHA-256:
  `060d1e2b9403893c4aaff7148ea604c4bcfc6bf3e7bab44913a56429ff261a9c`
- Runtime treatment: relative SFX volume `0.14`, pitch `1.20`, one-shot only.

The source recording is described as a large-wing flap, so Echoes deliberately uses it very quietly and slightly pitched up. It is only a light movement accent under the visible crow flight, not a creature-impact effect.

### Yendor signature — `sounds/echoes/ch1_yendor_signature.wav`

- Source: **project-owned deterministic synthesis; no third-party recording**.
- Generator: `tools/generate_yendor_signature.py`
- Distribution: generated during both Windows and Android builds from repository source; the generated WAV follows the project's GPL-3.0-or-later distribution baseline.
- Duration: **0.92 seconds**
- Format: **44.1 kHz, mono, 16-bit PCM WAV**
- Generator target peak: **-11.5 dBFS**
- CI-measured RMS for the deterministic output: **-22.56 dBFS**
- Deterministic seed: `0x59454E44` (`YEND`)
- Design layers:
  - soft low / low-mid resonances;
  - extremely quiet mineral partials;
  - low-passed air texture;
  - click-free soft attack and short decay.
- Runtime intensity:
  - first anomaly: `0.58`, giving sample relative volume about `0.174` before the player's SFX setting;
  - theft anomaly: `0.92`, giving sample relative volume about `0.276` before the player's SFX setting;
  - pitch remains `1.00` so the motif retains one timbre.
- Both events also apply the existing two-tick ambience attenuation; the second attenuation is intentionally somewhat clearer than the first.

The old `sounds/echoes/ch1_yendor_bass.mp3` remains retired and is forbidden by the Return source gate and both build workflows. The v0.3 generator performs basic clipping/loudness QA during CI. This is engineering QA, not a claim that a human has approved the cue on every speaker/headphone; real-device listening remains part of candidate acceptance.

---

## Historical Chapter 1 prototype sourcing notes

The section below records an earlier Chapter 1 audio sourcing/prototype path. It is retained for provenance history, but its packaging description does **not** describe the v0.3 Crow/Yendor assets above.

These files were game-specific derivative assets, mixed from source recordings the project owner downloaded from Pixabay on 2026-08-15. The untouched source downloads were intentionally not committed to the public repository.

All listed source pages were offered under the Pixabay Content License at the time of selection. The derivatives below were cut, level-adjusted and/or mixed with other source recordings for use inside *Echoes of Yendor*.

| Historical game asset | Source recordings |
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
- https://pixabay.com/sound-effects/search/floresta/  (source ID 67458)
- https://pixabay.com/sound-effects/search/tiny-bells/  (source ID 83604)
- https://pixabay.com/sound-effects/film-special-effects-wood-creaks-411791/
- https://pixabay.com/sound-effects/nature-donkey-bray-36757/
- https://pixabay.com/sound-effects/musical-bass-throb-10sec-106995/
- https://pixabay.com/sound-effects/magic-pulse-452856/
- https://pixabay.com/sound-effects/distant-crows-ravens-forest-birds-332962/
- https://pixabay.com/sound-effects/nature-wolves-fighting-227005/
- https://pixabay.com/sound-effects/nature-daytime-farm-ambience-409990/

### Historical processing notes

- Long ambience beds were shortened and mixed so the committed files were not untouched source downloads.
- The cart cue combined rolling wood, harness bell and creak layers.
- The donkey interruption combined bray, harness bell and wood stress into one scripted cue.
- The old prototype Yendor cue combined a low-frequency throb with a quieter magic layer; it is not the v0.3 signature and must not be reintroduced.
- The wolf cue used a short, faded excerpt from a real-wolf recording, explicitly avoiding monster/werewolf material.

### Historical packaging note

An earlier implementation packed nine derivative MP3s into `sounds/echoes/ch1_audio_XX.pack` parts. Current v0.3 builds instead use the repository's active audio controller/build workflows, including SHA-verified build-time injection for the two CC0 crow sources and deterministic synthesis for the Yendor signature. This historical paragraph is retained only to explain older commits/saves and should not be used as the current packaging specification.
