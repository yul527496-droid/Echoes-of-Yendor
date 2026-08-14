package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

/**
 * V2 candle presentation for the ledger scenes.
 *
 * The closed and open artworks use different design spaces, so their flame
 * anchors and flame sizes must never be inferred from one another. Light is a
 * small runtime-generated radial texture instead of a full-screen breathing
 * overlay; the open book also receives a very subtle moving page-shadow layer.
 */
final class LedgerCandleFX extends Component {

    private static final String GLOW_KEY = "echoes-ledger-candle-glow-v2";
    private static final int GLOW_SIZE = 48;

    // A deliberately non-linear frame order keeps the tiny loop from reading
    // like a six-frame metronome while remaining deterministic.
    private static final int[] FRAME_SEQUENCE = {0, 1, 2, 1, 3, 4, 5, 4, 2, 3};
    private static final float[] JITTER_X = {0f, 0.28f, -0.16f, 0.12f, -0.30f, 0.18f};
    private static final float[] JITTER_Y = {0f, -0.12f, 0.16f, -0.20f, 0.10f, -0.06f};

    private static final Profile OPEN = new Profile(
            46.5f, 40.0f, 1.00f,
            54.0f, 51.0f, 94.0f, 82.0f, 0.105f,
            112.0f, 88.0f, 214.0f, 168.0f, 0.030f,
            true);

    // LedgerClosedArtwork is 128x85. Its candle sits near the upper-left, so
    // a 16x16 flame must be scaled independently from the artwork or it becomes
    // enormous on screen.
    private static final Profile CLOSED = new Profile(
            17.7f, 10.7f, 0.50f,
            21.4f, 17.2f, 47.0f, 40.0f, 0.090f,
            34.0f, 27.0f, 78.0f, 58.0f, 0.024f,
            false);

    private final Image source;
    private final Profile profile;
    private final Image shadow;
    private final Image spill;
    private final Image halo;
    private final Image flame;
    private float time;

    static LedgerCandleFX openBook(Image source) {
        return new LedgerCandleFX(source, OPEN);
    }

    static LedgerCandleFX closedBook(Image source) {
        return new LedgerCandleFX(source, CLOSED);
    }

    private LedgerCandleFX(Image source, Profile profile) {
        super();
        this.source = source;
        this.profile = profile;

        if (profile.pageShadow) {
            shadow = LedgerEnvironment.tryLoad(LedgerEnvironment.LEDGER_SHADOW);
            if (shadow != null) {
                shadow.alpha(0.12f);
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

        Image loadedFlame = LedgerEnvironment.tryLoad(LedgerEnvironment.CANDLE_FLAME);
        if (loadedFlame != null) {
            loadedFlame.frame(0, 0, 16, 16);
            add(loadedFlame);
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

        // Two frequencies stop the light from behaving like a single sine-wave
        // breathing effect. The range is intentionally small.
        float slow = (float) Math.sin(t * 2.05f + 0.35f);
        float quick = (float) Math.sin(t * 8.35f + 1.10f);
        float flicker = slow * 0.58f + quick * 0.42f;
        float sourceAlpha = source.alpha();

        if (shadow != null) {
            shadow.visible = source.visible;
            shadow.scale.set(source.scale.x, source.scale.y);
            // Shadows move very slightly opposite the flame movement.
            shadow.x = source.x - jx * 0.30f * source.scale.x;
            shadow.y = source.y - jy * 0.18f * source.scale.y;
            shadow.alpha(sourceAlpha * clamp(0.115f - 0.018f * flicker, 0.085f, 0.145f));
        }

        placeGlow(spill,
                profile.spillX + jx * 0.10f,
                profile.spillY + jy * 0.08f,
                profile.spillW,
                profile.spillH);
        spill.visible = source.visible;
        spill.alpha(sourceAlpha * clamp(
                profile.spillAlpha + slow * 0.004f + quick * 0.003f,
                profile.spillAlpha * 0.72f,
                profile.spillAlpha * 1.34f));

        placeGlow(halo,
                profile.glowX + jx * 0.34f,
                profile.glowY + jy * 0.28f,
                profile.glowW,
                profile.glowH);
        halo.visible = source.visible;
        halo.alpha(sourceAlpha * clamp(
                profile.glowAlpha + slow * 0.012f + quick * 0.009f,
                profile.glowAlpha * 0.72f,
                profile.glowAlpha * 1.30f));

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
            flame.alpha(sourceAlpha * clamp(0.90f + 0.055f * flicker, 0.82f, 0.98f));
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
                // Smooth radial falloff, then quantize it so the result still
                // belongs beside the pixel-art ledger instead of looking airbrushed.
                float smooth = falloff * falloff * (3f - 2f * falloff);
                float stepped = Math.round(smooth * 7f) / 7f;
                float alpha = stepped * 0.70f;
                pixmap.setColor(1.00f, 0.55f, 0.20f, alpha);
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

        Profile(float flameX, float flameY, float flameScale,
                float glowX, float glowY, float glowW, float glowH, float glowAlpha,
                float spillX, float spillY, float spillW, float spillH, float spillAlpha,
                boolean pageShadow) {
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
        }
    }
}
