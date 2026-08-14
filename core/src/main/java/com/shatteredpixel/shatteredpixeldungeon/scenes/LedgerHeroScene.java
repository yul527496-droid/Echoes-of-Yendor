package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class LedgerHeroScene extends PixelScene {

    private HeroClass selected;
    private RenderedTextBlock selectedName;
    private RenderedTextBlock selectedDesc;
    private LedgerButton confirm;
    private Image[] previews;
    private ColorBlock[] marks;
    private float lx;
    private float lw;
    private float previewY;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        RectF l = LedgerEnvironment.leftPage(book);
        RectF r = LedgerEnvironment.rightPage(book);
        lx = l.left + 7;
        lw = l.width() - 14;
        float rx = r.left + 7;
        float rw = r.width() - 14;
        float top = l.top + 7;

        RenderedTextBlock lh = t("下行者档案", 8, LedgerEnvironment.INK, (int) lw);
        lh.setPos(lx + (lw - lh.width()) / 2f, top);
        add(lh);

        RenderedTextBlock note = t("理想职业", 5, LedgerEnvironment.FADED_INK, (int) lw);
        note.setPos(lx + 2, lh.bottom() + 5);
        add(note);

        previewY = note.bottom() + 12;
        previews = new Image[HeroClass.values().length];
        for (HeroClass cl : HeroClass.values()) {
            Image hero = new Image(cl.spritesheet(), 0, 90, 12, 15);
            hero.scale.set(2.4f);
            hero.x = lx + (lw - hero.width()) / 2f;
            hero.y = previewY;
            hero.visible = false;
            previews[cl.ordinal()] = hero;
            add(hero);
        }

        selectedName = t("尚未登记", 7, LedgerEnvironment.INK, (int) lw);
        add(selectedName);
        selectedDesc = t("从右页选择一个英雄身份。", 5, LedgerEnvironment.FADED_INK, (int) lw - 4);
        selectedDesc.align(RenderedTextBlock.CENTER_ALIGN);
        add(selectedDesc);

        RenderedTextBlock rh = t("选择身份", 8, LedgerEnvironment.INK, (int) rw);
        rh.setPos(rx + (rw - rh.width()) / 2f, top);
        add(rh);

        RenderedTextBlock rn = t("写下当年想成为怎样的人。", 5, LedgerEnvironment.FADED_INK, (int) rw);
        rn.setPos(rx + (rw - rn.width()) / 2f, rh.bottom() + 5);
        add(rn);

        HeroClass[] classes = HeroClass.values();
        marks = new ColorBlock[classes.length];
        float cw = (rw - 6) / 2f;
        float ch = 24;
        float gx = rx;
        float gy = rn.bottom() + 8;
        for (int i = 0; i < classes.length; i++) {
            final HeroClass cl = classes[i];
            float x = gx + (i % 2) * (cw + 6);
            float y = gy + (i / 2) * (ch + 4);
            ColorBlock line = new ColorBlock(cw, 1, 0x553B2A1E);
            line.x = x;
            line.y = y + ch - 1;
            marks[i] = line;
            add(line);

            LedgerButton b = new LedgerButton(Chrome.Type.BLANK, Messages.titleCase(cl.title()), 5) {
                @Override
                protected void onClick() {
                    super.onClick();
                    choose(cl);
                }
            };
            b.icon(new Image(cl.spritesheet(), 0, 90, 12, 15));
            b.textColor(LedgerEnvironment.INK);
            b.setRect(x, y, cw, ch - 2);
            add(b);
        }

        confirm = new LedgerButton(Chrome.Type.BLANK, "以此身份登记  ›", 6) {
            @Override
            protected void onClick() {
                super.onClick();
                if (selected != null) Game.switchScene(LedgerRegistrationScene.class);
            }
        };
        confirm.textColor(LedgerEnvironment.INK);
        confirm.setRect(rx, r.bottom - 20, rw, 15);
        add(confirm);

        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "‹ 返回名册", 5) {
            @Override
            protected void onClick() {
                super.onClick();
                Game.switchScene(LedgerRecordsScene.class);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(lx, l.bottom - 20, lw, 15);
        add(back);

        selected = LedgerFlow.draft().heroClass;
        refresh();
        fadeIn();
    }

    private void choose(HeroClass cl) {
        if (selected != cl) {
            selected = cl;
            LedgerFlow.draft().heroClass = cl;
            LedgerFlow.draft().resetDependentChoices();
        }
        refresh();
    }

    private void refresh() {
        for (HeroClass cl : HeroClass.values()) {
            boolean on = cl == selected;
            previews[cl.ordinal()].visible = on;
            marks[cl.ordinal()].alpha(on ? 0.95f : 0.22f);
        }
        if (selected == null) {
            selectedName.text("尚未登记");
            selectedDesc.text("从右页选择一个英雄身份。");
            confirm.enable(false);
        } else {
            selectedName.text(Messages.titleCase(selected.title()));
            selectedDesc.text(selected.shortDesc());
            selectedDesc.maxWidth((int) lw - 4);
            confirm.enable(true);
        }
        selectedName.setPos(lx + (lw - selectedName.width()) / 2f, previewY + 42);
        selectedDesc.setPos(lx + (lw - selectedDesc.width()) / 2f, selectedName.bottom() + 5);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
