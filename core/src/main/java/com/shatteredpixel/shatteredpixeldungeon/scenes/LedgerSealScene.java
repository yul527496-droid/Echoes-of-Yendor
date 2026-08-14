package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class LedgerSealScene extends PixelScene {

    private float time;
    private boolean struck;
    private boolean leaving;
    private LedgerStamp stamp;
    private float stampX;
    private float stampY;
    private float stampW;
    private float stampH;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        LedgerPageGrid.Page left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        RenderedTextBlock title = t("登记页", 9, LedgerEnvironment.INK,
                (int) left.header.width());
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(left.header.left + (left.header.width() - title.width()) / 2f,
                left.header.top);
        add(title);

        RenderedTextBlock note = t("晨溪镇 · 老鸦旅店", 5, LedgerEnvironment.FADED_INK,
                (int) left.header.width());
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(left.header.left + (left.header.width() - note.width()) / 2f,
                title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(left.header.left + left.header.width() * 0.14f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f),
                left.header.width() * 0.72f, 0.36f));

        RenderedTextBlock entry = t(
                "姓名\n" + LedgerFlow.draft().name
                        + "\n\n职业\n" + Messages.titleCase(LedgerFlow.draft().heroClass.title())
                        + "\n\n惯用兵器\n" + LedgerFlow.draft().weaponName()
                        + "\n\n去向\n地下遗迹"
                        + "\n\n归期\n未定",
                6, LedgerEnvironment.INK, (int) left.body.width() - 10);
        entry.setPos(left.body.left + 5f, left.body.top + 5f);
        add(entry);

        RenderedTextBlock inn = t("旅店补注", 9,
                LedgerEnvironment.INK, (int) right.header.width());
        inn.align(RenderedTextBlock.CENTER_ALIGN);
        inn.setPos(right.header.left + (right.header.width() - inn.width()) / 2f,
                right.header.top);
        add(inn);

        RenderedTextBlock sub = t("后补印记", 5,
                LedgerEnvironment.FADED_INK, (int) right.header.width());
        sub.align(RenderedTextBlock.CENTER_ALIGN);
        sub.setPos(right.header.left + (right.header.width() - sub.width()) / 2f,
                inn.bottom() + 3f);
        add(sub);
        add(LedgerPageGrid.rule(right.header.left + right.header.width() * 0.16f,
                Math.min(right.header.bottom - 1f, sub.bottom() + 5f),
                right.header.width() * 0.68f, 0.28f));

        RenderedTextBlock status = t("归期未录", 6,
                LedgerEnvironment.FADED_INK, (int) right.body.width());
        status.align(RenderedTextBlock.CENTER_ALIGN);
        status.setPos(right.body.left + (right.body.width() - status.width()) / 2f,
                right.body.top + right.body.height() * 0.30f);
        add(status);

        stampW = Math.min(64f, right.body.width() * 0.62f);
        stampH = 30f;
        stampX = right.body.left + (right.body.width() - stampW) * 0.5f;
        stampY = right.body.top + right.body.height() * 0.50f;
        stamp = new LedgerStamp("未归", 10);
        stamp.setRect(stampX, stampY - 20f, stampW, stampH);
        stamp.visual(1.18f, 0f);
        add(stamp);

        RenderedTextBlock hand = t("老鸦旅店留印", 5,
                LedgerEnvironment.FADED_INK, (int) right.body.width());
        hand.align(RenderedTextBlock.CENTER_ALIGN);
        hand.setPos(right.body.left + (right.body.width() - hand.width()) / 2f,
                right.body.bottom - hand.height() - 5f);
        add(hand);

        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;

        // Leave the first 0.4s clear for the incoming page reveal.
        float fall = Math.max(0f, Math.min(1f, (time - 0.42f) / 0.34f));
        float eased = 1f - (1f - fall) * (1f - fall) * (1f - fall);
        stamp.setRect(stampX, stampY - (1f - eased) * 20f, stampW, stampH);
        stamp.visual(1.18f - 0.18f * eased, eased);

        if (!struck && fall >= 1f) {
            struck = true;
            LedgerAudio.stamp();
            PixelScene.shake(1.25f, 0.11f);
        }

        if (struck && time < 1.15f) {
            float settle = Math.max(0f, (time - 0.76f) / 0.39f);
            float pulse = (float) Math.sin(settle * Math.PI * 2f) * (1f - settle) * 0.025f;
            stamp.visual(1f + pulse, 0.90f);
        } else if (struck) {
            stamp.visual(1f, 0.90f);
        }

        if (!leaving && time > 2.05f) {
            leaving = true;
            LedgerTransitions.turn(this, right.paper, LedgerReturnScene.class, true);
        }
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
