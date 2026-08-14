# Echoes of Yendor — Ledger audio source notes

Checked: 2026-08-14

The ledger audio layer looks for optional files under `sounds/echoes/` and `music/echoes/`. If they are absent, the game intentionally falls back to already-packaged Shattered Pixel Dungeon effects, so UI development and CI do not depend on remote downloads.

## Pixabay shortlist

The following Pixabay pages were reviewed as direction/source candidates under the Pixabay Content License:

- Book Opening — freesounds123 — `https://pixabay.com/sound-effects/film-special-effects-book-opening-345808/`
- Page Turn — `https://pixabay.com/sound-effects/film-special-effects-page-turn-305789/`
- traditional stamp — I.fekry / Freesound — `https://pixabay.com/sound-effects/film-special-effects-traditional-stamp-44189/`
- Writing / pen-on-paper shortlist — `https://pixabay.com/sound-effects/search/writing%20paper/`
- Tavern ambience with openfire effect (no loops) — Placidplace — `https://pixabay.com/sound-effects/tavern-ambience-with-openfire-effect-no-loops-86151/`

The first three item pages and the tavern ambience page explicitly show “Free for use under the Pixabay Content License” and provide MP3 downloads in the browser. Before committing any downloaded binary, preserve its exact item URL and creator/title here. Do not commit a remote preview or an HTML page renamed as audio.

## Runtime target paths

- `sounds/echoes/ledger_book_open.mp3`
- `sounds/echoes/ledger_page_turn.mp3`
- `sounds/echoes/ledger_pen_write.mp3`
- `sounds/echoes/ledger_stamp.mp3`
- `music/echoes/ledger_tavern.mp3`

## Current status

No Pixabay audio binary is committed by this note. The item pages expose a free download in the browser, but the automated fetch route available during this pass did not expose a stable media-file URL. The runtime hooks, timing, volume, and fallback behavior are therefore implemented first. This avoids repeating the earlier PNG transport failure where an asset could compile into a build without being a valid media file.
