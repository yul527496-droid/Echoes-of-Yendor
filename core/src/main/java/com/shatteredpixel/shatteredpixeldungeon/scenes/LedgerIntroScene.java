package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;

/**
 * The single cold-start entrance for Echoes of Yendor.
 *
 * This scene owns only the closed-ledger presentation and the opening
 * transition. Once the book has opened, the player always lands on the ledger
 * records page. New-character creation starts from that page as well, so the
 * meaning of "open the ledger" never changes based on save state.
 */
public class LedgerIntroScene extends PixelScene {

    private Image closedBook;
    private Image openBook;
    private PointerArea input;
    private float time;
    private float closedScale;
    private float openScale;

    @Override
    public void create() {
        super.create();

        int w = Camera.main.width;
        int h = Camera.main.height;

        // The approved artwork is the single visual source for both states.
        openBook = LedgerEnvironment.addOpenBook(this);
        openScale = openBook.scale.x;
        openBook.alpha(0f);

        closedBook = LedgerClosedArtwork.image();
        closedScale = Math.min((w - 8f) / closedBook.width, (h - 8f) / closedBook.height);
        closedBook.scale.set(closedScale);
        center(closedBook);
        add(closedBook);
        add(new LedgerWarmth(closedBook, 0.040f));

        input = new PointerArea(
                closedBook.x - 5f,
                closedBook.y - 5f,
                closedBook.width() + 10f,
                closedBook.height() + 10f) {
            @Override
            protected void onClick(PointerEvent e) {
                open();
            }
        };
        add(input);

        fadeIn();
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;
        if (input.active) {
            // Tiny idle motion: the book should feel present, not like a button
            // pasted onto the screen. The separate warmth layer supplies the
            // higher-frequency candle flicker.
            closedBook.brightness(1f + (float) Math.sin(time * 1.9f) * 0.012f);
        }
    }

    private void open() {
        if (!input.active) return;

        input.active = false;
        closedBook.resetColor();
        Sample.INSTANCE.play(Assets.Sounds.OPEN, 0.58f, 0.92f);

        add(new Tweener(this, 0.78f) {
            @Override
            protected void updateValues(float progress) {
                float close = Math.min(1f, progress / 0.52f);
                float closeEase = close * close * (3f - 2f * close);
                closedBook.alpha(1f - closeEase);
                closedBook.scale.set(
                        closedScale * (1f - 0.15f * closeEase),
                        closedScale * (1f + 0.025f * closeEase));
                center(closedBook);

                float reveal = Math.max(0f, Math.min(1f, (progress - 0.20f) / 0.80f));
                float revealEase = 1f - (1f - reveal) * (1f - reveal);
                openBook.alpha(revealEase);
                openBook.scale.set(openScale * (0.94f + 0.06f * revealEase));
                center(openBook);
            }

            @Override
            protected void onComplete() {
                Game.switchScene(LedgerRecordsScene.class);
            }
        });
    }

    private void center(Image image) {
        image.x = (Camera.main.width - image.width()) / 2f;
        image.y = (Camera.main.height - image.height()) / 2f;
        PixelScene.align(image);
    }
}
