package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildRules;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildValidator;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroPreset;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroPresetSettings;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

/** Persistent reusable build presets. Character names are deliberately excluded. */
public class LedgerPresetScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildInfo();
        buildSlots();
        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildInfo() {
        float x = left.header.left;
        float w = left.header.width();
        RenderedTextBlock title = t("构筑留档", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("只存战斗配置，不抄人物姓名", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.13f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f), w * 0.74f, 0.36f));

        float bx = left.body.left + 5f;
        float bw = left.body.width() - 10f;
        float y = left.body.top + 8f;

        RenderedTextBlock info = t(
                "保存内容\n职业 · 子职业 · 护甲能力\n武器 · 护甲 · 法杖\n三个佩位 · 饰品 · 天赋\n\n"
                        + "姓名不会写入预设。\n加载后仍可逐栏修改。\n\n"
                        + "规则版本：" + ReturningHeroBuildRules.RULESET_VERSION,
                5, LedgerEnvironment.INK, (int) bw);
        info.setPos(bx, y);
        add(info);

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));
        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回旧行装", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerFlow.choicePage(0);
                LedgerTransitions.turn(LedgerPresetScene.this,
                        left.paper, LedgerBuildScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildSlots() {
        int total = ReturningHeroPresetSettings.MAX_PRESETS;
        int page = LedgerChoicePages.clampPage(LedgerFlow.choicePage(), total);
        LedgerFlow.choicePage(page);
        int pages = LedgerChoicePages.pageCount(total);

        header("预设页", "第 " + (page + 1) + " / " + pages + " 页");
        int from = LedgerChoicePages.from(page, total);
        int to = LedgerChoicePages.to(page, total);
        float rowH = Math.min(16.5f,
                (right.body.height() - 2f) / Math.max(1, to - from));
        float y = right.body.top + 1f;

        for (int slot = from; slot < to; slot++) {
            final int index = slot;
            ReturningHeroPreset preset = ReturningHeroPresetSettings.load(slot);
            String label;
            int color;
            if (preset == null) {
                label = "留档 " + (slot + 1) + "  ·  空白";
                color = LedgerEnvironment.FADED_INK;
            } else {
                label = "留档 " + (slot + 1) + "  ·  " + preset.presetName
                        + "  ·  " + Messages.titleCase(preset.heroClass.title())
                        + (preset.needsRulesetReview() ? "  [旧规则]" : "");
                color = preset.needsRulesetReview()
                        ? LedgerEnvironment.STAMP : LedgerEnvironment.INK;
            }

            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 5) {
                @Override protected void onClick() {
                    super.onClick();
                    if (ReturningHeroPresetSettings.exists(index)) openExisting(index);
                    else saveIntoEmpty(index);
                }
            };
            button.leftJustify = true;
            button.textColor(color);
            button.setRect(right.body.left + 5f, y,
                    right.body.width() - 10f, rowH - 0.5f);
            add(button);
            y += rowH;
        }

        footerPaged(page, pages, new Runnable() {
            @Override public void run() {
                LedgerFlow.choicePage(0);
                LedgerTransitions.turn(LedgerPresetScene.this,
                        right.paper, LedgerBuildScene.class, false);
            }
        });
    }

    private void saveIntoEmpty(final int slot) {
        LedgerPresetScene.this.add(new WndTextInput(
                "命名构筑预设",
                "给这份战斗配置留一个短名字。",
                "", 24, false, "保存", "取消") {
            @Override public void onSelect(boolean ok, String value) {
                if (!ok) return;
                ReturningHeroBuildValidator.Result result =
                        ReturningHeroBuildValidator.validate(LedgerFlow.draft());
                if (!result.isValid()) {
                    LedgerPresetScene.this.add(new WndMessage("当前构筑仍含有不符合规则的记录，暂时不能保存为预设。"));
                    return;
                }
                ReturningHeroPreset preset = ReturningHeroPreset.fromProfile(value, LedgerFlow.draft());
                ReturningHeroPresetSettings.save(slot, preset);
                LedgerAudio.write();
                reload();
            }
        });
    }

    private void openExisting(final int slot) {
        final ReturningHeroPreset preset = ReturningHeroPresetSettings.load(slot);
        if (preset == null) {
            reload();
            return;
        }
        LedgerPresetScene.this.add(new WndOptions(
                preset.presetName,
                preset.needsRulesetReview()
                        ? "这份预设来自旧规则。可以尝试按当前规则重新核对，也可以直接覆盖或删除。"
                        : "选择如何处理这份构筑预设。",
                "载入", "覆盖", "删除", "取消") {
            @Override protected void onSelect(int index) {
                if (index == 0) loadPreset(preset);
                else if (index == 1) overwritePreset(slot, preset.presetName);
                else if (index == 2) {
                    ReturningHeroPresetSettings.delete(slot);
                    LedgerAudio.erase();
                    reload();
                }
            }
        });
    }

    private void loadPreset(ReturningHeroPreset source) {
        ReturningHeroPreset candidate = source.copy();
        if (candidate.needsRulesetReview()) {
            candidate.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
            if (candidate.loadout != null) {
                candidate.loadout.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
            }
            if (candidate.talentPlan != null) {
                candidate.talentPlan.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
            }
        }

        ReturningHeroBuildValidator.Result result = ReturningHeroBuildValidator.validate(candidate);
        if (!result.isValid()) {
            LedgerPresetScene.this.add(new WndMessage(
                    "这份旧预设无法直接通过当前规则校验。请保留它作为参考，并用当前页面重新配置后覆盖。"));
            return;
        }

        candidate.applyTo(LedgerFlow.draft());
        LedgerAudio.pageTurn();
        LedgerFlow.choicePage(0);
        PixelScene.noFade = true;
        Game.switchScene(LedgerBuildScene.class);
    }

    private void overwritePreset(int slot, String oldName) {
        ReturningHeroBuildValidator.Result result =
                ReturningHeroBuildValidator.validate(LedgerFlow.draft());
        if (!result.isValid()) {
            LedgerPresetScene.this.add(new WndMessage("当前构筑仍含有不符合规则的记录，不能覆盖预设。"));
            return;
        }
        ReturningHeroPresetSettings.save(slot,
                ReturningHeroPreset.fromProfile(oldName, LedgerFlow.draft()));
        LedgerAudio.write();
        reload();
    }

    private void footerPaged(final int page, final int pages, final Runnable backAction) {
        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.28f));
        float y = right.footer.top + 3f;
        float h = right.footer.height() - 3f;
        float third = right.footer.width() / 3f;

        LedgerButton prev = new LedgerButton(Chrome.Type.BLANK, "上页", 5) {
            @Override protected void onClick() {
                super.onClick();
                if (page > 0) {
                    LedgerFlow.choicePage(page - 1);
                    reload();
                }
            }
        };
        prev.textColor(page > 0 ? LedgerEnvironment.INK : LedgerEnvironment.FADED_INK);
        prev.setRect(right.footer.left, y, third, h);
        add(prev);

        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回", 5) {
            @Override protected void onClick() {
                super.onClick();
                backAction.run();
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(right.footer.left + third, y, third, h);
        add(back);

        LedgerButton next = new LedgerButton(Chrome.Type.BLANK, "下页", 5) {
            @Override protected void onClick() {
                super.onClick();
                if (page + 1 < pages) {
                    LedgerFlow.choicePage(page + 1);
                    reload();
                }
            }
        };
        next.textColor(page + 1 < pages ? LedgerEnvironment.INK : LedgerEnvironment.FADED_INK);
        next.setRect(right.footer.left + third * 2f, y, third, h);
        add(next);
    }

    private void header(String titleText, String noteText) {
        float x = right.header.left;
        float w = right.header.width();
        RenderedTextBlock title = t(titleText, 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, right.header.top);
        add(title);

        RenderedTextBlock note = t(noteText, 5, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.10f,
                Math.min(right.header.bottom - 1f, note.bottom() + 4f), w * 0.80f, 0.36f));
    }

    private void reload() {
        PixelScene.noFade = true;
        Game.switchScene(LedgerPresetScene.class);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
