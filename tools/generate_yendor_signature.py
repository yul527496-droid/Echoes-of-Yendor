#!/usr/bin/env python3
"""Generate Echoes' restrained v0.3 Yendor signature cue deterministically.

No third-party recording is used. The output is a short project-owned PCM WAV synthesized from
low/low-mid resonances, extremely quiet mineral partials, and low-passed air noise. The generator
is deterministic so CI and local builds produce identical bytes.
"""
from pathlib import Path
import math
import random
import struct
import wave

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "core/src/main/assets/sounds/echoes/ch1_yendor_signature.wav"
RATE = 44100
DURATION = 0.92
TARGET_PEAK_DBFS = -11.5
SEED = 0x59454E44  # "YEND"


def dbfs(value: float) -> float:
    return 20.0 * math.log10(max(1e-12, value))


def synthesize():
    count = int(RATE * DURATION)
    rng = random.Random(SEED)
    lowpass_noise = 0.0
    result = []

    for i in range(count):
        t = i / RATE
        # Soft attack and short natural decay. Nothing in this envelope can create a click.
        attack = 1.0 - math.exp(-t / 0.060)
        tail = math.exp(-t / 0.430)
        env = attack * tail

        # Low/low-mid body, with tiny downward drift to avoid a clean electronic tone.
        body = (
            0.34 * math.sin(2 * math.pi * (108 * t - 2.8 * t * t))
            + 0.18 * math.sin(2 * math.pi * (176 * t - 1.8 * t * t) + 0.7)
            + 0.08 * math.sin(2 * math.pi * 236 * t + 1.6)
        )

        # Mineral overtones are intentionally extremely quiet: audible texture, never a ping.
        mineral_bloom = (1.0 - math.exp(-max(0.0, t - 0.05) / 0.09)) * math.exp(-t / 0.30)
        mineral = (
            0.026 * math.sin(2 * math.pi * 612 * t + 1.3)
            + 0.009 * math.sin(2 * math.pi * 824 * t + 0.2)
        )

        # A slow one-pole low-pass turns white noise into a faint inward-air texture.
        noise = rng.uniform(-1.0, 1.0)
        lowpass_noise += 0.035 * (noise - lowpass_noise)
        if t < 0.46:
            air_env = math.sin(math.pi * min(1.0, t / 0.46)) ** 2
        else:
            air_env = math.exp(-(t - 0.46) / 0.13)
        air = 0.038 * lowpass_noise * air_env

        result.append(env * body + mineral_bloom * mineral + air)

    source_peak = max(abs(v) for v in result)
    target_peak = 10 ** (TARGET_PEAK_DBFS / 20.0)
    scale = target_peak / source_peak
    return [max(-1.0, min(1.0, v * scale)) for v in result]


def write_wav(samples):
    OUT.parent.mkdir(parents=True, exist_ok=True)
    pcm = b"".join(struct.pack("<h", int(round(v * 32767.0))) for v in samples)
    with wave.open(str(OUT), "wb") as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(RATE)
        wav.writeframes(pcm)


def main():
    samples = synthesize()
    write_wav(samples)
    peak = max(abs(v) for v in samples)
    rms = math.sqrt(sum(v * v for v in samples) / len(samples))
    print(
        f"Generated {OUT.relative_to(ROOT)}: {DURATION:.2f}s mono PCM {RATE}Hz; "
        f"peak={dbfs(peak):.2f} dBFS rms={dbfs(rms):.2f} dBFS"
    )
    if peak >= 0.98:
        raise SystemExit("Yendor signature QA failed: clipping headroom lost")
    if rms > 10 ** (-18.0 / 20.0):
        raise SystemExit("Yendor signature QA failed: cue became too loud")


if __name__ == "__main__":
    main()
