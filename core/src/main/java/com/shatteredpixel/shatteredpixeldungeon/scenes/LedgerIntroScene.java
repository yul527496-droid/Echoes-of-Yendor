package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.EchoesOnboarding;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.RectF;

public class LedgerIntroScene extends PixelScene {

    private static boolean resumeAfterTraining;

    private Image closedBook;
    private Image openBook;
    private PointerArea input;
    private float closedScale;
    private float openScale;
    private boolean onboardingWindowOpen;

    public static void resumeAfterTraining() {
        resumeAfterTraining = true;
    }

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

        if (resumeAfterTraining) {
            resumeAfterTraining = false;
            // Give the closed cover a brief beat after the memory fades out, then
            // continue directly into registration instead of asking the player to
            // click the book a second time.
            add(new Tweener(this, 0.22f) {
                @Override protected void updateValues(float progress) {
                }

                @Override protected void onComplete() {
                    openBook();
                }
            });
        } else if (EchoesOnboarding.mode() == EchoesOnboarding.UNSET) {
            showOnboardingChoice();
        }
    }

    private void open() {
        if (!input.active) return;

        if (EchoesOnboarding.mode() == EchoesOnboarding.UNSET) {
            showOnboardingChoice();
            return;
        }

        if (EchoesOnboarding.mode() == EchoesOnboarding.GUIDED
                && !EchoesOnboarding.trainingDone()) {
            startTrainingMemory();
            return;
        }

        openBook();
    }

    private void showOnboardingChoice() {
        if (onboardingWindowOpen) return;
        onboardingWindowOpen = true;

        add(new WndOptions(
                "在翻开登记簿之前",
                "如果你第一次接触这种地牢，可以先回想一段出发前的基础训练。"
                        + "熟悉这里的规矩，就直接登记。\n\n"
                        + "无论怎么选，后面的「?」说明都可以随时查看。",
                "第一次接触地牢",
                "我熟悉这里的规矩"
        ) {
            @Override
            protected void onSelect(int index) {
                onboardingWindowOpen = false;
                if (index == 0) {
                    EchoesOnboarding.mode(EchoesOnboarding.GUIDED);
                    EchoesOnboarding.trainingDone(false);
                    startTrainingMemory();
                } else {
                    EchoesOnboarding.mode(EchoesOnboarding.EXPERIENCED);
                    openBook();
                }
            }

            @Override
            public void hide() {
                super.hide();
                onboardingWindowOpen = false;
            }
        });
    }

    private void startTrainingMemory() {
        if (!input.active) return;
        input.active = false;
        // Ledger owns the current BGM until this exact handoff. End it before
        // constructing the real training GameScene so the latter starts cleanly.
        LedgerAudio.leave();
        if (!SequelGame.startTrainingMemory()) {
            input.active = true;
            showOnboardingChoice();
        }
    }

    private void openBook() {
        if (!input.active) return;
        input.active = false;
        closedBook.resetColor();
        LedgerAudio.bookOpen();

        final RectF left = LedgerEnvironment.leftPage(openBook);
        final RectF right = LedgerEnvironment.rightPage(openBook);
        final ColorBlock leftCover = new ColorBlock(left.width(), left.height(), 0xFFE8C783);
        final ColorBlock rightCover = new ColorBlock(right.width(), right.height(), 0xFFE8C783);
        final ColorBlock leftEdge = new ColorBlock(1.2f, left.height(), 0xFFFFE7AE);
        final ColorBlock rightEdge = new ColorBlock(1.2f, right.height(), 0xFFFFE7AE);
        leftCover.x = left.left;
        leftCover.y = left.top;
        rightCover.x = right.left;
        rightCover.y = right.top;
        leftEdge.y = left.top;
        rightEdge.y = right.top;
        leftCover.alpha(0f);
        rightCover.alpha(0f);
        leftEdge.alpha(0f);
        rightEdge.alpha(0f);
        add(leftCover);
        add(rightCover);
        add(leftEdge);
        add(rightEdge);

        add(new Tweener(this, 0.88f) {
            @Override protected void updateValues(float progress) {
                float close = Math.min(1f, progress / 0.50f);
                float closeEase = close * close * (3f - 2f * close);
                closedBook.alpha(1f - closeEase);
                closedBook.scale.set(closedScale * (1f - 0.13f * closeEase),
                        closedScale * (1f + 0.020f * closeEase));
                center(closedBook);

                float reveal = Math.max(0f, Math.min(1f, (progress - 0.16f) / 0.84f));
                float revealEase = reveal * reveal * (3f - 2f * reveal);
                openBook.alpha(revealEase);
                openBook.scale.set(openScale * (0.965f + 0.035f * revealEase));
                center(openBook);

                float coverFraction = 1f - revealEase;
                float leftW = left.width() * coverFraction;
                float rightW = right.width() * coverFraction;
                leftCover.x = left.left;
                leftCover.size(Math.max(0.01f, leftW), left.height());
                rightCover.x = right.right - rightW;
                rightCover.size(Math.max(0.01f, rightW), right.height());

                float pageAlpha = reveal > 0f ? 0.94f : 0f;
                leftCover.alpha(pageAlpha);
                rightCover.alpha(pageAlpha);
                float edgeAlpha = (float) Math.sin(Math.PI * revealEase) * 0.52f;
                leftEdge.x = left.left + leftW - leftEdge.width;
                rightEdge.x = right.right - rightW;
                leftEdge.alpha(edgeAlpha);
                rightEdge.alpha(edgeAlpha);
            }

            @Override protected void onComplete() {
                leftCover.killAndErase();
                rightCover.killAndErase();
                leftEdge.killAndErase();
                rightEdge.killAndErase();
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
