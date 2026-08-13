package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class LedgerHeroScene extends PixelScene {

    private HeroClass selected;
    private RenderedTextBlock selectedName;
    private RenderedTextBlock selectedDesc;
    private StyledButton confirm;
    private Image[] previews;
    private ColorBlock[] selectionMarks;
    private float leftX;
    private float pageW;
    private float previewY;

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
        pageW = (bookW - gap) / 2f;
        float bookX = safe.left + (usableW - bookW) / 2f;
        leftX = bookX;
        float rightX = bookX + pageW + gap;
        float top = safe.top + Math.max(10, (h - safe.top - safe.bottom) * 0.12f);

        RenderedTextBlock leftHeader = text("下行者档案", 10, LedgerEnvironment.INK, (int) pageW - 12);
        leftHeader.setPos(leftX + (pageW - leftHeader.width()) / 2f, top);
        add(leftHeader);

        RenderedTextBlock leftNote = text("理想职业", 6, LedgerEnvironment.FADED_INK, (int) pageW - 18);
        leftNote.setPos(leftX + 9, leftHeader.bottom() + 5);
        add(leftNote);

        float lineY = leftNote.bottom() + 3;
        ColorBlock leftRule = new ColorBlock(pageW - 18, 1, 0x663B2A1E);
        leftRule.x = leftX + 9;
        leftRule.y = lineY;
        add(leftRule);

        previews = new Image[HeroClass.values().length];
        previewY = lineY + 14;
        for (HeroClass cl : HeroClass.values()) {
            Image hero = new Image(cl.spritesheet(), 0, 90, 12, 15);
            hero.scale.set(3f);
            hero.x = leftX + (pageW - hero.width()) / 2f;
            hero.y = previewY;
            hero.visible = false;
            previews[cl.ordinal()] = hero;
            add(hero);
        }

        selectedName = text("尚未登记", 9, LedgerEnvironment.INK, (int) pageW - 18);
        selectedName.setPos(leftX + (pageW - selectedName.width()) / 2f, previewY + 52);
        add(selectedName);

        selectedDesc = text("从右页选择一个英雄单位。", 6, LedgerEnvironment.FADED_INK, (int) pageW - 24);
        selectedDesc.align(RenderedTextBlock.CENTER_ALIGN);
        selectedDesc.setPos(leftX + (pageW - selectedDesc.width()) / 2f, selectedName.bottom() + 5);
        add(selectedDesc);

        RenderedTextBlock rightHeader = text("选择你的身份", 10, LedgerEnvironment.INK, (int) pageW - 12);
        rightHeader.setPos(rightX + (pageW - rightHeader.width()) / 2f, top);
        add(rightHeader);

        RenderedTextBlock rightNote = text("这是当年写进名册的理想，而不是后来的战绩。", 6,
                LedgerEnvironment.FADED_INK, (int) pageW - 18);
        rightNote.align(RenderedTextBlock.CENTER_ALIGN);
        rightNote.setPos(rightX + (pageW - rightNote.width()) / 2f, rightHeader.bottom() + 5);
        add(rightNote);

        HeroClass[] classes = HeroClass.values();
        selectionMarks = new ColorBlock[classes.length];
        float cardW = (pageW - 20) / 2f;
        float cardH = 30;
        float gridX = rightX + 8;
        float gridY = rightNote.bottom() + 9;

        for (int i = 0; i < classes.length; i++) {
            final HeroClass cl = classes[i];
            float x = gridX + (i % 2) * (cardW + 4);
            float y = gridY + (i / 2) * (cardH + 4);

            ColorBlock mark = new ColorBlock(cardW, 2, 0x553B2A1E);
            mark.x = x;
            mark.y = y + cardH - 2;
            selectionMarks[i] = mark;
            add(mark);

            HeroChoiceButton button = new HeroChoiceButton(cl);
            button.setRect(x, y, cardW, cardH - 2);
            add(button);
        }

        confirm = new StyledButton(Chrome.Type.TOAST_WHITE, "以此身份登记", 8) {
            @Override
            protected void onClick() {
                super.onClick();
                if (selected == null) return;
                Game.switchScene(LedgerRegistrationScene.class);
            }
        };
        confirm.textColor(LedgerEnvironment.INK);
        confirm.setRect(rightX + 8,
                Math.min(h - safe.bottom - 28, gridY + 3 * (cardH + 4) + 5),
                pageW - 16, 21);
        add(confirm);

        StyledButton back = new StyledButton(Chrome.Type.BLANK, "‹ 返回名册", 6) {
            @Override
            protected void onClick() {
                super.onClick();
                if (GamesInProgress.checkAll().isEmpty()) Game.switchScene(LedgerIntroScene.class);
                else Game.switchScene(LedgerRecordsScene.class);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(leftX + 8, h - safe.bottom - 22, pageW - 16, 17);
        add(back);

        selected = LedgerFlow.draft().heroClass;
        refreshSelection(false);
        fadeIn();
    }

    private void choose(HeroClass cl) {
        if (selected != cl) {
            selected = cl;
            LedgerFlow.draft().heroClass = cl;
            LedgerFlow.draft().resetDependentChoices();
        }
        refreshSelection(true);
    }

    private void refreshSelection(boolean pulse) {
        for (HeroClass cl : HeroClass.values()) {
            boolean active = cl == selected;
            previews[cl.ordinal()].visible = active;
            selectionMarks[cl.ordinal()].alpha(active ? 0.95f : 0.28f);
            if (active && pulse) previews[cl.ordinal()].brightness(1.12f);
            else previews[cl.ordinal()].resetColor();
        }

        if (selected == null) {
            selectedName.text("尚未登记");
            selectedDesc.text("从右页选择一个英雄单位。");
            confirm.enable(false);
        } else {
            selectedName.text(Messages.titleCase(selected.title()));
            selectedDesc.text(selected.shortDesc());
            selectedDesc.maxWidth((int) pageW - 24);
            confirm.enable(true);
        }

        selectedName.setPos(leftX + (pageW - selectedName.width()) / 2f, previewY + 52);
        selectedDesc.setPos(leftX + (pageW - selectedDesc.width()) / 2f, selectedName.bottom() + 5);
    }

    private RenderedTextBlock text(String value, int size, int color, int width) {
        RenderedTextBlock block = PixelScene.renderTextBlock(value, size);
        block.maxWidth(width);
        block.hardlight(color);
        return block;
    }

    private class HeroChoiceButton extends StyledButton {
        private final HeroClass heroClass;

        HeroChoiceButton(HeroClass heroClass) {
            super(Chrome.Type.BLANK, Messages.titleCase(heroClass.title()), 7);
            this.heroClass = heroClass;
            textColor(LedgerEnvironment.INK);
            icon(new Image(heroClass.spritesheet(), 0, 90, 12, 15));
        }

        @Override
        protected void onClick() {
            super.onClick();
            choose(heroClass);
        }

        @Override
        public void update() {
            super.update();
            if (icon != null) icon.brightness(heroClass == selected ? 1f : 0.56f);
            textColor(heroClass == selected ? 0x2C1B12 : LedgerEnvironment.FADED_INK);
        }
    }
}
