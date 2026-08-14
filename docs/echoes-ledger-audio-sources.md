# Echoes of Yendor — Ledger audio source notes

Checked: 2026-08-14

The ledger audio layer looks for optional files under `sounds/echoes/` and `music/echoes/`. If they are absent, the game intentionally falls back to already-packaged Shattered Pixel Dungeon effects, so UI development and CI do not depend on remote downloads.

## Pixabay shortlist

The following Pixabay pages were reviewed as direction/source candidates under the Pixabay Content License:

- Book opening — `https://pixabay.com/sound-effects/book-opening-345805/`
- Page turn — `https://pixabay.com/sound-effects/page-turn-98756/`
- Traditional stamp — `https://pixabay.com/sound-effects/traditional-stamp-133910/`
- Writing / pen-on-paper search category — `https://pixabay.com/sound-effects/search/writing/`
- Tavern ambience search category — `https://pixabay.com/sound-effects/search/tavern/`

Before committing any downloaded binary, preserve its exact item URL and creator/title here. Do not commit a remote preview or an HTML page renamed as audio.

## Runtime target paths

- `sounds/echoes/ledger_book_open.mp3`
- `sounds/echoes/ledger_page_turn.mp3`
- `sounds/echoes/ledger_pen_write.mp3`
- `sounds/echoes/ledger_stamp.mp3`
- `music/echoes/ledger_tavern.mp3`

## Current status

No Pixabay audio binary is committed by this note. The runtime hooks, timing, volume, and fallback behavior are implemented first. This avoids repeating the earlier PNG transport failure where an asset could compile into a build without being a valid media file.
