package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;

public class LedgerWeaponScene extends PixelScene {

    private int selected;
    private ColorBlock[] marks;
    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildSummary();
        buildChoices();

        String[] opts = LedgerFlow.draft().weaponOptions();
        selected = Math.max(0, Math.min(LedgerFlow.draft().weaponIndex, opts.length - 1));
        refresh();
        fadeIn();
    }

    private void buildSummary() {
        float x = left.header.left;
        float w = left.header.width();

        RenderedTextBlock title = t("登记内容", 8, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("待盖章原稿", 4, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.14f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f), w * 0.72f, 0.36f));

        float bx = left.body.left + 3f;
        float bw = left.body.width() - 6f;
        RenderedTextBlock summary = t(
                "姓名\n" + LedgerFlow.draft().name
                        + "\n\n理想职业\n" + Messages.titleCase(LedgerFlow.draft().heroClass.title())
                        + "\n\n去向\n地下遗迹",
                5, LedgerEnvironment.INK, (int) bw);
        summary.setPos(bx, left.body.top + 4f);
        add(summary);

        RenderedTextBlock hint = t("最后一项由本人留下。", 4,
                LedgerEnvironment.FADED_INK, (int) bw);
        hint.setPos(bx, left.body.bottom - hint.height() - 3f);
        add(hint);

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));

        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回登记页", 4) {
            @Override protected void onClick() {
                super.onClick();
                LedgerTransitions.turn(LedgerWeaponScene.this,
                        left.paper, LedgerRegistrationScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildChoices() {
        float x = right.header.left;
        float w = right.header.width();

        RenderedTextBlock title = t("惯用兵器", 8, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, right.header.top);
        add(title);

        RenderedTextBlock note = t("只记录出发前的战斗偏好", 4,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.10f,
                Math.min(right.header.bottom - 1f, note.bottom() + 4f), w * 0.80f, 0.36f));

        String[] opts = LedgerFlow.draft().weaponOptions();
        marks = new ColorBlock[opts.length];
        float rowH = Math.min(18f, (right.body.height() - 2f) / Math.max(1, opts.length));
        float y = right.body.top + 1f;

        for (int i = 0; i < opts.length; i++) {
            final int choice = i;
            final float rowY = y;

            ColorBlock mark = new ColorBlock(right.body.width() - 2f, 1f, 0xFF9B302C);
            mark.x = right.body.left + 1f;
            mark.y = rowY + rowH - 1f;
            mark.alpha(0.10f);
            marks[i] = mark;
            add(mark);

            LedgerButton button = new LedgerButton(Chrome.Type.BLANK,
                    String.format("%02d   %s", i + 1, opts[i]), 5) {
                @Override protected void onClick() {
                    super.onClick();
                    select(choice);
                }
            };
            button.leftJustify = true;
            button.textColor(LedgerEnvironment.INK);
            button.setRect(right.body.left + 1f, rowY,
                    right.body.width() - 2f, rowH - 1f);
            add(button);
            y += rowH;
        }

        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.28f));

        LedgerButton ok = new LedgerButton(Chrome.Type.BLANK, "确认登记并盖章", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerFlow.draft().weaponIndex = selected;
                LedgerTransitions.turn(LedgerWeaponScene.this,
                        right.paper, LedgerSealScene.class, true);
            }
        };
        ok.textColor(LedgerEnvironment.STAMP);
        ok.setRect(right.footer.left, right.footer.top + 3f,
                right.footer.width(), right.footer.height() - 3f);
        add(ok);
    }

    private void select(int i) {
        selected = i;
        LedgerFlow.draft().weaponIndex = i;
        refresh();
    }

    private void refresh() {
        if (marks == null) return;
        for (int i = 0; i < marks.length; i++) {
            marks[i].alpha(i == selected ? 0.80f : 0.10f);
        }
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
