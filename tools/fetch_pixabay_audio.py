#!/usr/bin/env python3
"""Fetch the pinned Echoes ledger audio from the original Pixabay item pages.

The repository deliberately keeps the source URLs and hashes rather than raw
Pixabay media files. The Windows packaging job injects the verified media into
assets so it is distributed only as part of the built game.
"""

from __future__ import annotations

import hashlib
import html
import os
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from pathlib import Path

UA = (
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0 Safari/537.36"
)

ROOT = Path(__file__).resolve().parents[1]


@dataclass(frozen=True)
class Asset:
    name: str
    page: str
    target: str
    sha256: str


ASSETS = (
    Asset(
        "Old Tavern - Cinematic Atmosphere Fairytale",
        "https://pixabay.com/music/folk-old-tavern-cinematic-atmosphere-fairytale-273871/",
        "core/src/main/assets/music/echoes/ledger_tavern.mp3",
        "4b114794884397d5a02af3569ec9afefedb791083c66db06f113ed4b2b7eebae",
    ),
    Asset(
        "Tavern ambience with openfire effect",
        "https://pixabay.com/sound-effects/tavern-ambience-with-openfire-effect-no-loops-86151/",
        "core/src/main/assets/music/echoes/ledger_ambience.mp3",
        "a258853368ab9b1e4723c3f992059e09e36bdd0fdce99ddf6b2c88190e248860",
    ),
    Asset(
        "Book Opening",
        "https://pixabay.com/sound-effects/book-opening-345808/",
        "core/src/main/assets/sounds/echoes/ledger_book_open.mp3",
        "137b6b7a15b5415671589c5094394ff9e343000bec42650e04917b0541045ded",
    ),
    Asset(
        "Flipping Book Page",
        "https://pixabay.com/sound-effects/film-special-effects-flipping-book-page-499646/",
        "core/src/main/assets/sounds/echoes/ledger_page_turn.mp3",
        "600be61dd4591f09cdc1711fa8e07098fd3d948f619a7e096513ed6ab5aa82d8",
    ),
    Asset(
        "Book Closing",
        "https://pixabay.com/sound-effects/film-special-effects-book-closing-48311/",
        "core/src/main/assets/sounds/echoes/ledger_book_close.mp3",
        "bec81ccf7cc302fa3dad21b90cb73394cf9060ef7222006590ee85980c78d0da",
    ),
    Asset(
        "Writing,Pen,Signature,Paper",
        "https://pixabay.com/sound-effects/film-special-effects-writingpensignaturepaper-102967/",
        "core/src/main/assets/sounds/echoes/ledger_pen_write.mp3",
        "2bb4ba4c07e3ec0f2f747885b4164e10cb6e3bdd058de740f5910706a831ea59",
    ),
    Asset(
        "Traditional Stamp",
        "https://pixabay.com/sound-effects/film-special-effects-traditional-stamp-44189/",
        "core/src/main/assets/sounds/echoes/ledger_stamp.mp3",
        "8ebefdd86e4c5676888aaf8a50b39453200ae9bfc7f8f815e1874c9d1830213d",
    ),
)


def request(url: str, *, referer: str | None = None) -> bytes:
    headers = {
        "User-Agent": UA,
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Language": "en-US,en;q=0.8",
    }
    if referer:
        headers["Referer"] = referer
    req = urllib.request.Request(url, headers=headers)
    with urllib.request.urlopen(req, timeout=45) as response:
        return response.read()


def candidate_urls(page_bytes: bytes) -> list[str]:
    text = page_bytes.decode("utf-8", errors="replace")
    text = html.unescape(text)
    text = text.replace("\\/", "/").replace("\\u0026", "&")

    patterns = (
        r"https://cdn\.pixabay\.com/download/audio/[^\"'<>\\\s]+",
        r"https://cdn\.pixabay\.com/audio/[^\"'<>\\\s]+",
    )
    found: list[str] = []
    for pattern in patterns:
        for raw in re.findall(pattern, text):
            raw = raw.rstrip(",;)]}")
            if ".mp3" not in raw.lower():
                continue
            if raw not in found:
                found.append(raw)
    return found


def digest(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()


def fetch_asset(asset: Asset) -> None:
    target = ROOT / asset.target
    target.parent.mkdir(parents=True, exist_ok=True)

    # Re-running a local build should not redownload a file that already matches.
    if target.exists():
        current = target.read_bytes()
        if digest(current) == asset.sha256:
            print(f"[audio] verified existing: {asset.name}")
            return

    print(f"[audio] page: {asset.page}")
    page = request(asset.page)
    candidates = candidate_urls(page)
    if not candidates:
        raise RuntimeError(f"No Pixabay MP3 candidates found for {asset.name}")

    observed: list[str] = []
    for index, url in enumerate(candidates, 1):
        try:
            data = request(url, referer=asset.page)
        except (urllib.error.URLError, TimeoutError) as exc:
            observed.append(f"candidate {index}: download error {exc}")
            continue

        actual = digest(data)
        observed.append(f"candidate {index}: {actual} ({len(data)} bytes)")
        if actual == asset.sha256:
            target.write_bytes(data)
            print(f"[audio] OK {asset.name}: {actual}")
            return
        time.sleep(0.15)

    raise RuntimeError(
        f"No download candidate matched the pinned SHA256 for {asset.name}.\n"
        + "\n".join(observed)
    )


def main() -> int:
    failures: list[str] = []
    for asset in ASSETS:
        try:
            fetch_asset(asset)
        except Exception as exc:  # fail the build, but report every broken item
            failures.append(f"{asset.name}: {exc}")

    if failures:
        print("\n[audio] verification failed:", file=sys.stderr)
        for failure in failures:
            print(" - " + failure, file=sys.stderr)
        return 1

    print(f"[audio] all {len(ASSETS)} pinned ledger audio assets verified")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
