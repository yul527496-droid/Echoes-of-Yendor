package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;

public class LedgerHeroScene extends PixelScene {

    private HeroClass selected;
    private RenderedTextBlock selectedName;
    private RenderedTextBlock selectedDesc;
    private LedgerButton confirm;
    private LedgerButton[] classButtons;
    private Image[] previews;
    private ColorBlock[] marks;
    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;
    private float previewY;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildPreviewPage();
        buildChoicePage();

        selected = LedgerFlow.draft().heroClass;
        refresh();
        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildPreviewPage() {
        float x = left.header.left;
        float w = left.header.width();

        RenderedTextBlock title = t("下行者档案", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("晨溪镇 · 老鸦旅店", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);

        add(LedgerPageGrid.rule(x + w * 0.14f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f),
                w * 0.72f, 0.36f));

        previewY = left.body.top + 8f;
        previews = new Image[HeroClass.values().length];
        for (HeroClass cl : HeroClass.values()) {
            Image hero = new Image(cl.spritesheet(), 0, 90, 12, 15);
            hero.scale.set(2.25f);
            hero.x = left.body.left + (left.body.width() - hero.width()) / 2f;
            hero.y = previewY;
            hero.visible = false;
            previews[cl.ordinal()] = hero;
            add(hero);
        }

        selectedName = t("职业未填", 7, LedgerEnvironment.INK, (int) left.body.width());
        selectedName.align(RenderedTextBlock.CENTER_ALIGN);
        add(selectedName);

        selectedDesc = LedgerUI.markupText("职业一栏尚空。", 5,
                LedgerEnvironment.FADED_INK, LedgerEnvironment.INK,
                (int) left.body.width() - 8);
        selectedDesc.align(RenderedTextBlock.CENTER_ALIGN);
        add(selectedDesc);

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));

        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回名册", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerTransitions.turn(LedgerHeroScene.this,
                        left.paper, LedgerRecordsScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildChoicePage() {
        float x = right.header.left;
        float w = right.header.width();

        RenderedTextBlock title = t("职业", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, right.header.top);
        add(title);

        RenderedTextBlock note = t("按本人自报记入", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);

        add(LedgerPageGrid.rule(x + w * 0.10f,
                Math.min(right.header.bottom - 1f, note.bottom() + 4f),
                w * 0.80f, 0.36f));

        HeroClass[] classes = HeroClass.values();
        classButtons = new LedgerButton[classes.length];
        marks = new ColorBlock[classes.length];
        float gapX = 3f;
        float gapY = 2f;
        float cardW = (right.body.width() - gapX) / 2f;
        float cardH = (right.body.height() - gapY * 2f) / 3f;

        for (int i = 0; i < classes.length; i++) {
            final HeroClass cl = classes[i];
            float bx = right.body.left + (i % 2) * (cardW + gapX);
            float by = right.body.top + (i / 2) * (cardH + gapY);

            ColorBlock mark = new ColorBlock(cardW - 2f, 1f, 0xFF9B302C);
            mark.x = bx + 1f;
            mark.y = by + cardH - 1f;
            mark.alpha(0.10f);
            marks[i] = mark;
            add(mark);

            LedgerButton button = new LedgerButton(Chrome.Type.BLANK,
                    Messages.titleCase(cl.title()), 5) {
                @Override protected void onClick() {
                    super.onClick();
                    choose(cl);
                }
                @Override protected String hoverText() {
                    return Messages.titleCase(cl.title());
                }
            };
            Image icon = new Image(cl.spritesheet(), 0, 90, 12, 15);
            icon.scale.set(Math.min(1.35f, Math.max(1f, cardH / 15f)));
            button.icon(icon);
            button.textColor(LedgerEnvironment.INK);
            button.setRect(bx, by, cardW, cardH - 1f);
            classButtons[i] = button;
            add(button);
        }

        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.28f));

        confirm = new LedgerButton(Chrome.Type.BLANK, "记入职业", 6) {
            @Override protected void onClick() {
                super.onClick();
                if (selected != null) {
                    LedgerAudio.write();
                    LedgerTransitions.turn(LedgerHeroScene.this,
                            right.paper, LedgerRegistrationScene.class, true);
                }
            }
        };
        confirm.textColor(LedgerEnvironment.INK);
        confirm.setRect(right.footer.left, right.footer.top + 3f,
                right.footer.width(), right.footer.height() - 3f);
        add(confirm);
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
            marks[cl.ordinal()].alpha(on ? 0.78f : 0.10f);
            if (classButtons != null && classButtons[cl.ordinal()] != null) {
                classButtons[cl.ordinal()].setSelected(on);
            }
        }

        if (selected == null) {
            selectedName.text("职业未填");
            selectedDesc.text("职业一栏尚空。");
            confirm.enable(false);
        } else {
            selectedName.text(Messages.titleCase(selected.title()));
            selectedDesc.text(selected.shortDesc());
            selectedDesc.maxWidth((int) left.body.width() - 8);
            confirm.enable(true);
        }

        selectedName.setPos(
                left.body.left + (left.body.width() - selectedName.width()) / 2f,
                previewY + 38f);
        selectedDesc.setPos(
                left.body.left + (left.body.width() - selectedDesc.width()) / 2f,
                selectedName.bottom() + 4f);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
