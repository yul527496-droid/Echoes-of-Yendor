package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.utils.RectF;

public class LedgerWeaponScene extends PixelScene {

    private int selected;
    private ColorBlock[] marks;
    private StyledButton confirm;

    @Override
    public void create() {
        super.create();
        uiCamera.visible = false;
        LedgerEnvironment.addOpenBook(this);

        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF safe = getCommonInsets();
        float usableW = w - safe.left - safe.right;
        float bookW = Math.min(usableW - 10, 360);
        float gap = Math.max(7, bookW * 0.035f);
        float pageW = (bookW - gap) / 2f;
        float bookX = safe.left + (usableW - bookW) / 2f;
        float leftX = bookX;
        float rightX = bookX + pageW + gap;
        float top = safe.top + Math.max(10, (h - safe.top - safe.bottom) * 0.12f);

        RenderedTextBlock leftHeader = text("登记内容", 10, LedgerEnvironment.INK, (int) pageW - 14);
        leftHeader.setPos(leftX + (pageW - leftHeader.width()) / 2f, top);
        add(leftHeader);

        RenderedTextBlock summary = text(
                "姓名\n" + LedgerFlow.draft().name + "\n\n" +
                "理想职业\n" + Messages.titleCase(LedgerFlow.draft().heroClass.title()) + "\n\n" +
                "去向\n地下遗迹",
                7, LedgerEnvironment.INK, (int) pageW - 24);
        summary.setPos(leftX + 12, leftHeader.bottom() + 12);
        add(summary);

        RenderedTextBlock marginalia = text("最后一项由本人留下。", 6,
                LedgerEnvironment.FADED_INK, (int) pageW - 24);
        marginalia.setPos(leftX + 12, summary.bottom() + 14);
        add(marginalia);

        RenderedTextBlock rightHeader = text("惯用兵器", 10, LedgerEnvironment.INK, (int) pageW - 14);
        rightHeader.setPos(rightX + (pageW - rightHeader.width()) / 2f, top);
        add(rightHeader);

        RenderedTextBlock note = text("这不是地下遗迹中的最终战利品，\n只是出发前本人写下的战斗偏好。", 6,
                LedgerEnvironment.FADED_INK, (int) pageW - 20);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(rightX + (pageW - note.width()) / 2f, rightHeader.bottom() + 6);
        add(note);

        String[] options = LedgerFlow.draft().weaponOptions();
        marks = new ColorBlock[options.length];
        float optionX = rightX + 11;
        float optionW = pageW - 22;
        float optionY = note.bottom() + 11;

        for (int i = 0; i < options.length; i++) {
            final int index = i;
            float y = optionY + i * 33;

            StyledButton choice = new StyledButton(Chrome.Type.BLANK,
                    (i + 1) + "   " + options[i], 8) {
                @Override
                protected void onClick() {
                    super.onClick();
                    select(index);
                }
            };
            choice.leftJustify = true;
            choice.textColor(LedgerEnvironment.INK);
            choice.setRect(optionX, y, optionW, 27);
            add(choice);

            ColorBlock line = new ColorBlock(optionW, 2, 0x553B2A1E);
            line.x = optionX;
            line.y = y + 26;
            marks[i] = line;
            add(line);
        }

        confirm = new StyledButton(Chrome.Type.TOAST_WHITE, "确认登记并盖章", 7) {
            @Override
            protected void onClick() {
                super.onClick();
                LedgerFlow.draft().weaponIndex = selected;
                Game.switchScene(LedgerSealScene.class);
            }
        };
        confirm.textColor(LedgerEnvironment.STAMP);
        confirm.setRect(rightX + 9, h - safe.bottom - 31, pageW - 18, 21);
        add(confirm);

        StyledButton back = new StyledButton(Chrome.Type.BLANK, "‹ 返回登记页", 6) {
            @Override
            protected void onClick() {
                super.onClick();
                Game.switchScene(LedgerRegistrationScene.class);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(leftX + 9, h - safe.bottom - 25, pageW - 18, 17);
        add(back);

        selected = Math.max(0, Math.min(LedgerFlow.draft().weaponIndex, options.length - 1));
        refresh();
        fadeIn();
    }

    private void select(int index) {
        selected = index;
        LedgerFlow.draft().weaponIndex = index;
        refresh();
    }

    private void refresh() {
        for (int i = 0; i < marks.length; i++) {
            marks[i].alpha(i == selected ? 0.95f : 0.25f);
        }
        confirm.enable(selected >= 0);
    }

    private RenderedTextBlock text(String value, int size, int color, int width) {
        RenderedTextBlock block = PixelScene.renderTextBlock(value, size);
        block.maxWidth(width);
        block.hardlight(color);
        return block;
    }
}
