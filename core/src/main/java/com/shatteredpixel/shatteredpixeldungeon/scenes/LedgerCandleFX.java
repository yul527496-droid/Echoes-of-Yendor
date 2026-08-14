package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

/** Layered, directional candle ambience for ledger scenes. */
final class LedgerCandleFX extends Component {

    private static final String GLOW_KEY = "echoes-ledger-candle-glow-v4";
    private static final int GLOW_SIZE = 48;

    // Deliberately irregular 9-10 fps loop; readable as pixel animation without looking mechanical.
    private static final int[] FRAME_SEQUENCE = {0, 1, 2, 1, 3, 4, 3, 5, 4, 2, 1};
    private static final float[] JITTER_X = {0f, 0.46f, -0.34f, 0.20f, -0.52f, 0.36f};
    private static final float[] JITTER_Y = {0f, -0.24f, 0.28f, -0.36f, 0.16f, -0.18f};

    // 480x270 detailed open-book plate. The candle wick is around x=54,y=42.
    private static final Profile OPEN_EXTERNAL = new Profile(
            46.5f, 37.5f, 0.66f, 1.28f, 0.40f,
            54.0f, 49.0f, 82.0f, 72.0f, 0.115f,
            98.0f, 77.0f, 182.0f, 146.0f, 0.034f,
            0.105f, 0.125f,
            true, true, true);

    // 160x90 emergency artwork. It already contains a painted flame, so the sprite is low-alpha accent only.
    private static final Profile OPEN_EMBEDDED = new Profile(
            9.4f, 10.2f, 0.30f, 0.58f, 0.28f,
            10.2f, 13.0f, 25.0f, 22.0f, 0.105f,
            17.0f, 18.0f, 45.0f, 37.0f, 0.030f,
            0f, 0f,
            false, true, false);

    // 128x85 title artwork. The animated sprite softly reshapes the existing painted flame.
    private static final Profile CLOSED_EMBEDDED = new Profile(
            17.5f, 9.8f, 0.35f, 0.72f, 0.30f,
            19.5f, 14.0f, 24.0f, 22.0f, 0.120f,
            28.0f, 20.0f, 47.0f, 37.0f, 0.035f,
            0f, 0f,
            false, true, false);

    private final Image source;
    private final Profile profile;
    private final Image shadow;
    private final Image lightMap;
    private final Image spill;
    private final Image halo;
    private final Image flame;
    private float time;

    static LedgerCandleFX openBook(Image source) {
        return new LedgerCandleFX(source, OPEN_EXTERNAL);
    }

    static LedgerCandleFX openBookEmbedded(Image source) {
        return new LedgerCandleFX(source, OPEN_EMBEDDED);
    }

    static LedgerCandleFX closedBook(Image source) {
        return new LedgerCandleFX(source, CLOSED_EMBEDDED);
    }

    private LedgerCandleFX(Image source, Profile profile) {
        super();
        this.source = source;
        this.profile = profile;

        if (profile.fullPlateLighting) {
            shadow = LedgerEnvironment.tryLoad(LedgerEnvironment.LEDGER_SHADOW);
            lightMap = LedgerEnvironment.tryLoad(LedgerEnvironment.LEDGER_LIGHT);
            if (shadow != null) add(shadow);
            if (lightMap != null) add(lightMap);
        } else {
            shadow = null;
            lightMap = null;
        }

        SmartTexture glowTexture = glowTexture();
        spill = new Image(glowTexture);
        halo = new Image(glowTexture);
        add(spill);
        add(halo);

        Image loadedFlame = null;
        if (profile.animatedFlame) {
            loadedFlame = LedgerEnvironment.tryLoad(LedgerEnvironment.CANDLE_FLAME);
            if (loadedFlame != null) {
                loadedFlame.frame(0, 0, 16, 16);
                add(loadedFlame);
            }
        }
        flame = loadedFlame;

        updateVisuals(0f);
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;
        updateVisuals(time);
    }

    private void updateVisuals(float t) {
        int sequenceIndex = ((int) (t / 0.105f)) % FRAME_SEQUENCE.length;
        int frame = FRAME_SEQUENCE[sequenceIndex];
        float jx = JITTER_X[frame];
        float jy = JITTER_Y[frame];

        float slow = (float) Math.sin(t * 2.15f + 0.31f);
        float quick = (float) Math.sin(t * 8.85f + 1.07f);
        float flicker = slow * 0.56f + quick * 0.44f;
        float sourceAlpha = source.alpha();

        if (shadow != null) {
            shadow.visible = source.visible;
            shadow.scale.set(source.scale.x, source.scale.y);
            shadow.x = source.x - jx * 0.40f * source.scale.x;
            shadow.y = source.y - jy * 0.22f * source.scale.y;
            shadow.alpha(sourceAlpha * clamp(
                    profile.shadowAlpha - flicker * 0.026f,
                    profile.shadowAlpha * 0.70f,
                    profile.shadowAlpha * 1.28f));
        }

        if (lightMap != null) {
            lightMap.visible = source.visible;
            lightMap.scale.set(source.scale.x, source.scale.y);
            lightMap.x = source.x + jx * 0.16f * source.scale.x;
            lightMap.y = source.y + jy * 0.10f * source.scale.y;
            lightMap.alpha(sourceAlpha * clamp(
                    profile.plateLightAlpha + flicker * 0.030f,
                    profile.plateLightAlpha * 0.68f,
                    profile.plateLightAlpha * 1.34f));
        }

        placeGlow(spill,
                profile.spillX + jx * 0.15f,
                profile.spillY + jy * 0.10f,
                profile.spillW,
                profile.spillH);
        spill.visible = source.visible;
        spill.alpha(sourceAlpha * clamp(
                profile.spillAlpha + slow * 0.006f + quick * 0.005f,
                profile.spillAlpha * 0.66f,
                profile.spillAlpha * 1.42f));

        placeGlow(halo,
                profile.glowX + jx * 0.38f,
                profile.glowY + jy * 0.30f,
                profile.glowW,
                profile.glowH);
        halo.visible = source.visible;
        halo.alpha(sourceAlpha * clamp(
                profile.glowAlpha + slow * 0.020f + quick * 0.015f,
                profile.glowAlpha * 0.62f,
                profile.glowAlpha * 1.40f));

        if (flame != null) {
            flame.visible = source.visible;
            flame.frame(frame * 16, 0, 16, 16);

            float breatheX = 0.97f + quick * 0.045f;
            float breatheY = 1.00f + slow * 0.075f + quick * 0.035f;
            flame.scale.set(
                    source.scale.x * profile.flameScaleX * breatheX,
                    source.scale.y * profile.flameScaleY * breatheY);

            flame.x = source.x + (profile.flameX + jx) * source.scale.x;
            flame.y = source.y + (profile.flameY + jy) * source.scale.y;
            flame.alpha(sourceAlpha * clamp(
                    profile.flameAlpha + 0.055f * flicker,
                    profile.flameAlpha * 0.72f,
                    Math.min(0.78f, profile.flameAlpha * 1.30f)));
            PixelScene.align(flame);
        }
    }

    private void placeGlow(Image glow, float centerX, float centerY,
                           float designW, float designH) {
        glow.scale.set(
                source.scale.x * designW / GLOW_SIZE,
                source.scale.y * designH / GLOW_SIZE);
        glow.x = source.x + (centerX - designW * 0.5f) * source.scale.x;
        glow.y = source.y + (centerY - designH * 0.5f) * source.scale.y;
    }

    private static SmartTexture glowTexture() {
        boolean existed = TextureCache.contains(GLOW_KEY);
        SmartTexture texture = TextureCache.create(GLOW_KEY, GLOW_SIZE, GLOW_SIZE);
        if (existed) return texture;

        Pixmap pixmap = texture.bitmap;
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();

        float center = (GLOW_SIZE - 1) * 0.5f;
        float radius = center + 0.5f;
        for (int y = 0; y < GLOW_SIZE; y++) {
            for (int x = 0; x < GLOW_SIZE; x++) {
                float dx = (x - center) / radius;
                float dy = (y - center) / radius;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance >= 1f) continue;

                float falloff = 1f - distance;
                float smooth = falloff * falloff * (3f - 2f * falloff);
                float stepped = Math.round(smooth * 15f) / 15f;
                float alpha = stepped * 0.52f;
                pixmap.setColor(1.00f, 0.68f, 0.30f, alpha);
                pixmap.drawPixel(x, y);
            }
        }

        texture.filter(Texture.NEAREST, Texture.NEAREST);
        texture.wrap(Texture.CLAMP, Texture.CLAMP);
        return texture;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class Profile {
        final float flameX;
        final float flameY;
        final float flameScaleX;
        final float flameScaleY;
        final float flameAlpha;
        final float glowX;
        final float glowY;
        final float glowW;
        final float glowH;
        final float glowAlpha;
        final float spillX;
        final float spillY;
        final float spillW;
        final float spillH;
        final float spillAlpha;
        final float shadowAlpha;
        final float plateLightAlpha;
        final boolean fullPlateLighting;
        final boolean animatedFlame;
        final boolean reserved;

        Profile(float flameX, float flameY, float flameScaleX, float flameScaleY, float flameAlpha,
                float glowX, float glowY, float glowW, float glowH, float glowAlpha,
                float spillX, float spillY, float spillW, float spillH, float spillAlpha,
                float shadowAlpha, float plateLightAlpha,
                boolean fullPlateLighting, boolean animatedFlame, boolean reserved) {
            this.flameX = flameX;
            this.flameY = flameY;
            this.flameScaleX = flameScaleX;
            this.flameScaleY = flameScaleY;
            this.flameAlpha = flameAlpha;
            this.glowX = glowX;
            this.glowY = glowY;
            this.glowW = glowW;
            this.glowH = glowH;
            this.glowAlpha = glowAlpha;
            this.spillX = spillX;
            this.spillY = spillY;
            this.spillW = spillW;
            this.spillH = spillH;
            this.spillAlpha = spillAlpha;
            this.shadowAlpha = shadowAlpha;
            this.plateLightAlpha = plateLightAlpha;
            this.fullPlateLighting = fullPlateLighting;
            this.animatedFlame = animatedFlame;
            this.reserved = reserved;
        }
    }
}
