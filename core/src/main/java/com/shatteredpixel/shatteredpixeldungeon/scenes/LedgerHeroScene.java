package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildValidator;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroPreset;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroPresetSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;

import java.util.ArrayList;

/** Hero/class selection now owns full returning-build preset selection. */
public class LedgerHeroScene extends PixelScene {

    private HeroClass selected;
    private RenderedTextBlock selectedName;
    private RenderedTextBlock selectedDesc;
    private RenderedTextBlock presetStatus;
    private LedgerButton presetButton;
    private LedgerButton confirm;
    private LedgerButton[] classButtons;
    private Image[] previews;
    private ColorBlock[] marks;
    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;
    private float previewY;

    private static String selectedPresetName = "自定义";

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

        RenderedTextBlock note = t("先定职业，再定归来构筑", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);

        add(LedgerPageGrid.rule(x + w * 0.14f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f),
                w * 0.72f, 0.36f));

        previewY = left.body.top + 5f;
        previews = new Image[HeroClass.values().length];
        for (HeroClass cl : HeroClass.values()) {
            Image hero = new Image(cl.spritesheet(), 0, 90, 12, 15);
            hero.scale.set(2.05f);
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

        presetStatus = t("完整预设：自定义", 5, LedgerEnvironment.FADED_INK,
                (int) left.body.width() - 8);
        presetStatus.align(RenderedTextBlock.CENTER_ALIGN);
        add(presetStatus);

        presetButton = new LedgerButton(Chrome.Type.BLANK, "选择完整构筑预设", 5) {
            @Override protected void onClick() {
                super.onClick();
                openPresetPicker();
            }
        };
        presetButton.textColor(LedgerEnvironment.INK);
        add(presetButton);

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

        RenderedTextBlock note = t("预设包含专精、装备与四阶天赋", 5,
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
        float helpW = 12f;
        float helpGap = 1f;

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
            button.setRect(bx, by, cardW - helpW - helpGap, cardH - 1f);
            classButtons[i] = button;
            add(button);

            LedgerButton help = LedgerHelp.heroClass(this, cl);
            help.setRect(bx + cardW - helpW, by, helpW, cardH - 1f);
            add(help);
        }

        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.28f));

        confirm = new LedgerButton(Chrome.Type.BLANK, "以此职业与构筑继续", 6) {
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
            selectedPresetName = "自定义";
        }
        refresh();
    }

    private void openPresetPicker() {
        if (selected == null) return;

        final ArrayList<ReturningHeroPreset> presets = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();
        labels.add("自定义 · 逐栏填写天赋与装备");
        presets.add(null);

        for (int i = 0; i < ReturningHeroPresetSettings.MAX_PRESETS; i++) {
            ReturningHeroPreset preset = ReturningHeroPresetSettings.load(i);
            if (preset == null || preset.heroClass != selected) continue;
            presets.add(preset);
            labels.add(preset.presetName + (preset.needsRulesetReview() ? "  [需核对]" : ""));
        }

        LedgerHeroScene.this.add(new WndOptions(
                Messages.titleCase(selected.title()) + " · 完整构筑预设",
                "预设会一次写入专精、英雄战技、装备与四阶天赋；之后仍可逐栏修改。",
                labels.toArray(new String[0])) {
            @Override protected void onSelect(int index) {
                ReturningHeroPreset preset = presets.get(index);
                if (preset == null) {
                    LedgerFlow.draft().heroClass = selected;
                    LedgerFlow.draft().resetDependentChoices();
                    selectedPresetName = "自定义";
                    refresh();
                    return;
                }
                if (preset.needsRulesetReview()) {
                    LedgerHeroScene.this.add(new WndMessage("这份预设来自旧规则，请先用当前规则重新保存后再作为完整归来构筑载入。"));
                    return;
                }
                ReturningHeroBuildValidator.Result result = ReturningHeroBuildValidator.validate(preset);
                if (!result.isValid()) {
                    LedgerHeroScene.this.add(new WndMessage("这份预设无法通过当前构筑规则校验。"));
                    return;
                }
                preset.applyTo(LedgerFlow.draft());
                selected = preset.heroClass;
                selectedPresetName = preset.presetName;
                LedgerAudio.pageTurn();
                refresh();
            }
        });
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
            presetStatus.text("完整预设：—");
            presetButton.enable(false);
            confirm.enable(false);
        } else {
            selectedName.text(Messages.titleCase(selected.title()));
            selectedDesc.text(selected.shortDesc());
            selectedDesc.maxWidth((int) left.body.width() - 8);
            presetStatus.text("完整预设：" + selectedPresetName);
            presetButton.enable(true);
            confirm.enable(true);
        }

        selectedName.setPos(
                left.body.left + (left.body.width() - selectedName.width()) / 2f,
                previewY + 34f);
        selectedDesc.setPos(
                left.body.left + (left.body.width() - selectedDesc.width()) / 2f,
                selectedName.bottom() + 3f);
        presetStatus.setPos(
                left.body.left + (left.body.width() - presetStatus.width()) / 2f,
                selectedDesc.bottom() + 5f);
        presetButton.setRect(left.body.left + 8f, presetStatus.bottom() + 2f,
                left.body.width() - 16f, 13f);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
