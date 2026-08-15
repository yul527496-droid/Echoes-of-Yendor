# Chapter 1 audio provenance

These files are **game-specific derivative assets**, mixed from the source recordings the project owner downloaded from Pixabay on 2026-08-15. The untouched source downloads are intentionally not committed to the public repository.

All listed source pages were offered under the Pixabay Content License at the time of selection. The derivatives below are cut, level-adjusted and/or mixed with other source recordings for use inside *Echoes of Yendor*.

| Game asset | Source recordings |
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

## Source-page references

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

## Processing notes

- Long ambience beds were shortened and mixed so the committed files are not untouched source downloads.
- The cart cue combines rolling wood, harness bell and creak layers.
- The donkey interruption combines bray, harness bell and wood stress into one scripted cue.
- The Yendor cue combines a low-frequency throb with a much quieter magic layer.
- The wolf cue is a short, faded excerpt from a real-wolf recording, explicitly avoiding monster/werewolf material.

## Repository packaging

The nine derivative MP3s are packed into a small set of UTF-8 `sounds/echoes/ch1_audio_XX.pack` parts as Base64 payloads. `ChapterOneAudio` decodes that pack into the application's private/local cache on first use, then streams ambience with libGDX `Music` and plays short cues with `Sound`. This packaging keeps the Git commit atomic and avoids publishing the untouched Pixabay source downloads.
