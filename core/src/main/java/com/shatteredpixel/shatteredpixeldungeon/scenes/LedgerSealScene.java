package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class LedgerSealScene extends PixelScene {

    private float time;
    private boolean struck;
    private boolean leaving;
    private RenderedTextBlock stamp;
    private float stampX;
    private float stampY;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        LedgerPageGrid.Page left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        RenderedTextBlock title = t("登记完成", 8,
                LedgerEnvironment.INK, (int) left.header.width());
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(left.header.left + (left.header.width() - title.width()) / 2f,
                left.header.top);
        add(title);

        RenderedTextBlock note = t("名册原稿", 4,
                LedgerEnvironment.FADED_INK, (int) left.header.width());
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(left.header.left + (left.header.width() - note.width()) / 2f,
                title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(left.header.left + left.header.width() * 0.14f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f),
                left.header.width() * 0.72f, 0.36f));

        RenderedTextBlock entry = t(
                "姓名\n" + LedgerFlow.draft().name
                        + "\n\n理想职业\n" + Messages.titleCase(LedgerFlow.draft().heroClass.title())
                        + "\n\n惯用兵器\n" + LedgerFlow.draft().weaponName()
                        + "\n\n去向\n地下遗迹",
                5,
                LedgerEnvironment.INK,
                (int) left.body.width() - 6);
        entry.setPos(left.body.left + 3f, left.body.top + 4f);
        add(entry);

        RenderedTextBlock original = t("此后未再登记归期。", 4,
                LedgerEnvironment.FADED_INK, (int) left.body.width() - 6);
        original.setPos(left.body.left + 3f,
                left.body.bottom - original.height() - 3f);
        add(original);

        RenderedTextBlock inn = t("晨溪镇 · 老鸦旅店", 5,
                LedgerEnvironment.FADED_INK, (int) right.header.width());
        inn.align(RenderedTextBlock.CENTER_ALIGN);
        inn.setPos(right.header.left + (right.header.width() - inn.width()) / 2f,
                right.header.top + 2f);
        add(inn);
        add(LedgerPageGrid.rule(right.header.left + right.header.width() * 0.16f,
                Math.min(right.header.bottom - 1f, inn.bottom() + 5f),
                right.header.width() * 0.68f, 0.28f));

        stamp = t("未  归", 13,
                LedgerEnvironment.STAMP, (int) right.body.width());
        stampX = right.body.left + (right.body.width() - stamp.width()) / 2f;
        stampY = right.body.top + right.body.height() * 0.52f;
        stamp.setPos(stampX, stampY - 18f);
        stamp.alpha(0f);
        add(stamp);

        RenderedTextBlock hand = t("—— 名册原注", 4,
                LedgerEnvironment.FADED_INK, (int) right.body.width());
        hand.align(RenderedTextBlock.CENTER_ALIGN);
        hand.setPos(right.body.left + (right.body.width() - hand.width()) / 2f,
                right.body.bottom - hand.height() - 4f);
        add(hand);

        fadeIn();
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;

        float fall = Math.max(0f, Math.min(1f, (time - 0.18f) / 0.34f));
        float eased = 1f - (1f - fall) * (1f - fall);
        stamp.alpha(eased);
        stamp.setPos(stampX, stampY - (1f - eased) * 18f);

        if (!struck && fall >= 1f) {
            struck = true;
            Sample.INSTANCE.play(Assets.Sounds.STURDY, 0.88f, 0.90f);
            PixelScene.shake(1.35f, 0.12f);
        }

        if (!leaving && time > 1.55f) {
            leaving = true;
            LedgerTransitions.turn(this, right.paper, LedgerReturnScene.class, true);
        }
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
