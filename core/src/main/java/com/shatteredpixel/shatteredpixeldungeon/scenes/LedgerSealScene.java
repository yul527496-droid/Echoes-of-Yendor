package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.RectF;

public class LedgerSealScene extends PixelScene {

    private float time;
    private boolean struck;
    private RenderedTextBlock stamp;
    private float stampX;
    private float stampY;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        RectF l = LedgerEnvironment.leftPage(book);
        RectF r = LedgerEnvironment.rightPage(book);
        float lx = l.left + 8;
        float lw = l.width() - 16;
        float rx = r.left + 8;
        float rw = r.width() - 16;
        float top = l.top + 8;

        RenderedTextBlock h = t("登记完成", 8, LedgerEnvironment.INK, (int) lw);
        h.setPos(lx + (lw - h.width()) / 2f, top);
        add(h);

        RenderedTextBlock entry = t(
                "姓名  " + LedgerFlow.draft().name
                        + "\n\n理想职业  " + Messages.titleCase(LedgerFlow.draft().heroClass.title())
                        + "\n\n惯用兵器  " + LedgerFlow.draft().weaponName()
                        + "\n\n去向  地下遗迹",
                5,
                LedgerEnvironment.INK,
                (int) lw);
        entry.setPos(lx, h.bottom() + 12);
        add(entry);

        RenderedTextBlock note = t("此后未再登记归期。", 5, LedgerEnvironment.FADED_INK, (int) lw);
        note.setPos(lx, Math.min(l.bottom - 28, entry.bottom() + 14));
        add(note);

        RenderedTextBlock rh = t("晨溪镇 · 老鸦旅店", 6, LedgerEnvironment.FADED_INK, (int) rw);
        rh.setPos(rx + (rw - rh.width()) / 2f, top + 3);
        add(rh);

        stamp = t("未  归", 14, LedgerEnvironment.STAMP, (int) rw);
        stampX = rx + (rw - stamp.width()) / 2f;
        stampY = r.top + r.height() * 0.52f;
        stamp.setPos(stampX, stampY - 12);
        stamp.alpha(0f);
        add(stamp);

        RenderedTextBlock hand = t("—— 名册原注", 5, LedgerEnvironment.FADED_INK, (int) rw);
        hand.setPos(rx + (rw - hand.width()) / 2f, r.bottom - 25);
        add(hand);

        fadeIn();
    }

    @Override
    public void update() {
        super.update();
        time += Game.elapsed;
        float fall = Math.min(1f, time / 0.28f);
        stamp.alpha(fall);
        stamp.setPos(stampX, stampY - (1f - fall) * 12f);
        if (!struck && fall >= 1f) {
            struck = true;
            Sample.INSTANCE.play(Assets.Sounds.STURDY, 0.85f, 0.92f);
        }
        if (time > 1.05f) {
            Game.switchScene(LedgerReturnScene.class);
        }
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        RenderedTextBlock block = PixelScene.renderTextBlock(value, size);
        block.maxWidth(width);
        block.hardlight(color);
        return block;
    }
}
