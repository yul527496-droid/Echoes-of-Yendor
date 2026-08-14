package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

/** Candle ambience for ledger scenes. */
final class LedgerCandleFX extends Component {

    private static final String GLOW_KEY = "echoes-ledger-candle-glow-v3";
    private static final int GLOW_SIZE = 48;

    private static final int[] FRAME_SEQUENCE = {0, 1, 2, 1, 3, 4, 5, 4, 2, 3};
    private static final float[] JITTER_X = {0f, 0.28f, -0.16f, 0.12f, -0.30f, 0.18f};
    private static final float[] JITTER_Y = {0f, -0.12f, 0.16f, -0.20f, 0.10f, -0.06f};

    // 480x270 external plate. This profile may use the six-frame flame because
    // the intended final external plate is the no-static-flame artwork.
    private static final Profile OPEN_EXTERNAL = new Profile(
            46.5f, 40.0f, 1.00f,
            54.0f, 51.0f, 76.0f, 66.0f, 0.080f,
            96.0f, 78.0f, 174.0f, 138.0f, 0.020f,
            true, true);

    // LedgerOpenArtwork is 160x90 and already contains a painted candle flame.
    // Do not place a second flame on top of it. Animate only the local light and
    // page shadow so the painted flame feels alive without obvious compositing.
    private static final Profile OPEN_EMBEDDED = new Profile(
            9.5f, 12.5f, 0.42f,
            10.2f, 13.5f, 22.0f, 20.0f, 0.070f,
            17.0f, 18.0f, 42.0f, 34.0f, 0.018f,
            true, false);

    // LedgerClosedArtwork is 128x85 and also contains its own painted flame.
    // Keep that flame and animate only the surrounding illumination.
    private static final Profile CLOSED_EMBEDDED = new Profile(
            17.7f, 10.7f, 0.50f,
            18.0f, 12.8f, 20.0f, 18.0f, 0.068f,
            26.0f, 19.0f, 40.0f, 32.0f, 0.017f,
            false, false);

    private final Image source;
    private final Profile profile;
    private final Image shadow;
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

        if (profile.pageShadow) {
            shadow = LedgerEnvironment.tryLoad(LedgerEnvironment.LEDGER_SHADOW);
            if (shadow != null) {
                shadow.alpha(0.10f);
                add(shadow);
            }
        } else {
            shadow = null;
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
        int sequenceIndex = ((int) (t / 0.09f)) % FRAME_SEQUENCE.length;
        int frame = FRAME_SEQUENCE[sequenceIndex];
        float jx = JITTER_X[frame];
        float jy = JITTER_Y[frame];

        float slow = (float) Math.sin(t * 2.05f + 0.35f);
        float quick = (float) Math.sin(t * 8.35f + 1.10f);
        float flicker = slow * 0.58f + quick * 0.42f;
        float sourceAlpha = source.alpha();

        if (shadow != null) {
            shadow.visible = source.visible;
            if (LedgerEnvironment.usingEmbeddedFallback()) {
                // The shadow asset belongs to the external 480x270 plate. Do not
                // stretch it over the embedded 160x90 fallback artwork.
                shadow.visible = false;
            } else {
                shadow.scale.set(source.scale.x, source.scale.y);
                shadow.x = source.x - jx * 0.22f * source.scale.x;
                shadow.y = source.y - jy * 0.14f * source.scale.y;
                shadow.alpha(sourceAlpha * clamp(0.105f - 0.014f * flicker, 0.080f, 0.125f));
            }
        }

        placeGlow(spill,
                profile.spillX + jx * 0.08f,
                profile.spillY + jy * 0.06f,
                profile.spillW,
                profile.spillH);
        spill.visible = source.visible;
        spill.alpha(sourceAlpha * clamp(
                profile.spillAlpha + slow * 0.003f + quick * 0.002f,
                profile.spillAlpha * 0.78f,
                profile.spillAlpha * 1.24f));

        placeGlow(halo,
                profile.glowX + jx * 0.24f,
                profile.glowY + jy * 0.20f,
                profile.glowW,
                profile.glowH);
        halo.visible = source.visible;
        halo.alpha(sourceAlpha * clamp(
                profile.glowAlpha + slow * 0.008f + quick * 0.006f,
                profile.glowAlpha * 0.80f,
                profile.glowAlpha * 1.22f));

        if (flame != null) {
            flame.visible = source.visible;
            flame.frame(frame * 16, 0, 16, 16);

            float breatheX = 0.985f + quick * 0.018f;
            float breatheY = 1.000f + slow * 0.028f + quick * 0.012f;
            flame.scale.set(
                    source.scale.x * profile.flameScale * breatheX,
                    source.scale.y * profile.flameScale * breatheY);

            flame.x = source.x + (profile.flameX + jx) * source.scale.x;
            flame.y = source.y + (profile.flameY + jy) * source.scale.y;
            flame.alpha(sourceAlpha * clamp(0.88f + 0.050f * flicker, 0.80f, 0.96f));
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
                // More levels than V2 removes the obvious circular patch while
                // nearest filtering still keeps the light consistent with pixel art.
                float stepped = Math.round(smooth * 15f) / 15f;
                float alpha = stepped * 0.50f;
                pixmap.setColor(1.00f, 0.70f, 0.34f, alpha);
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
        final float flameScale;
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
        final boolean pageShadow;
        final boolean animatedFlame;

        Profile(float flameX, float flameY, float flameScale,
                float glowX, float glowY, float glowW, float glowH, float glowAlpha,
                float spillX, float spillY, float spillW, float spillH, float spillAlpha,
                boolean pageShadow, boolean animatedFlame) {
            this.flameX = flameX;
            this.flameY = flameY;
            this.flameScale = flameScale;
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
            this.pageShadow = pageShadow;
            this.animatedFlame = animatedFlame;
        }
    }
}
