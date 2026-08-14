package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.tweeners.Tweener;

public class LedgerIntroScene extends PixelScene {

    private Image closedBook;
    private Image openBook;
    private PointerArea input;
    private float closedScale;
    private float openScale;

    @Override
    public void create() {
        super.create();
        int w = Camera.main.width;
        int h = Camera.main.height;

        openBook = LedgerEnvironment.addOpenBook(this);
        openScale = openBook.scale.x;
        openBook.alpha(0f);

        closedBook = LedgerClosedArtwork.image();
        closedScale = Math.min((w - 8f) / closedBook.width, (h - 8f) / closedBook.height);
        closedBook.scale.set(closedScale);
        center(closedBook);
        add(closedBook);
        add(LedgerCandleFX.closedBook(closedBook));

        input = new PointerArea(closedBook.x - 5f, closedBook.y - 5f,
                closedBook.width() + 10f, closedBook.height() + 10f) {
            @Override protected void onClick(PointerEvent e) { open(); }
        };
        add(input);
        fadeIn();
    }

    private void open() {
        if (!input.active) return;
        input.active = false;
        closedBook.resetColor();
        LedgerAudio.bookOpen();

        add(new Tweener(this, 0.78f) {
            @Override protected void updateValues(float progress) {
                float close = Math.min(1f, progress / 0.52f);
                float closeEase = close * close * (3f - 2f * close);
                closedBook.alpha(1f - closeEase);
                closedBook.scale.set(closedScale * (1f - 0.15f * closeEase),
                        closedScale * (1f + 0.025f * closeEase));
                center(closedBook);

                float reveal = Math.max(0f, Math.min(1f, (progress - 0.20f) / 0.80f));
                float revealEase = 1f - (1f - reveal) * (1f - reveal);
                openBook.alpha(revealEase);
                openBook.scale.set(openScale * (0.94f + 0.06f * revealEase));
                center(openBook);
            }

            @Override protected void onComplete() {
                PixelScene.noFade = true;
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
