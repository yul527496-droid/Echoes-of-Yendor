package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildCost;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildRules;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Entry;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Kind;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroLoadout;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

/** Armor base and upgrade reconstruction for the returning hero. */
public class LedgerArmorScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildSummary();
        if (LedgerFlow.armorPickerStage() == 0) buildArmorList();
        else buildUpgradeList();

        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildSummary() {
        float x = left.header.left;
        float w = left.header.width();

        RenderedTextBlock title = t("护甲旧录", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("记归来时所着之甲", 5, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.13f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f), w * 0.74f, 0.36f));

        float bx = left.body.left + 5f;
        float bw = left.body.width() - 10f;
        float y = left.body.top + 7f;

        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        String current = loadout != null && loadout.armorId != null
                ? itemName(loadout.armorId) + " +" + loadout.armorLevel
                : "尚未登记";
        y = field(bx, bw, y, "当前记录", current);

        int spent = ReturningHeroBuildCost.equipmentSpent(
                LedgerFlow.draft().heroClass, loadout);
        int remaining = ReturningHeroBuildCost.equipmentRemaining(
                LedgerFlow.draft().heroClass, loadout);
        y = field(bx, bw, y, "装备重建值",
                spent < 0 || remaining < 0 ? "待重新核对"
                        : spent + " / " + ReturningHeroBuildRules.EQUIPMENT_RECONSTRUCTION_BUDGET
                        + "（余 " + remaining + "）");

        if (LedgerFlow.armorCandidateId() != null) {
            int preview = previewRemaining(
                    LedgerFlow.armorCandidateId(), LedgerFlow.armorCandidateLevel());
            RenderedTextBlock hint = t("当前试算：" + (preview < 0 ? "超过可用额度" : "尚余 " + preview),
                    5, preview < 0 ? LedgerEnvironment.STAMP : LedgerEnvironment.FADED_INK,
                    (int) bw);
            hint.setPos(bx, Math.min(y + 3f, left.body.bottom - hint.height() - 4f));
            add(hint);
        }

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));
        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回旧行装", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerFlow.resetArmorPicker();
                LedgerTransitions.turn(LedgerArmorScene.this,
                        left.paper, LedgerBuildScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildArmorList() {
        header("护甲", "选择归来时改制职业甲的底甲");

        float rowH = Math.min(20f, (right.body.height() - 4f) / 4f);
        float y = right.body.top + 2f;
        float rowX = right.body.left + 5f;
        float rowW = right.body.width() - 10f;
        float helpW = 13f;
        float helpGap = 1f;
        for (Entry entry : ReturningHeroItemCatalog.entries(Kind.ARMOR)) {
            if (!entry.selectable || entry.tier < 2 || entry.tier > 5) continue;
            final String id = entry.id;
            int cost = ReturningHeroBuildCost.armorCost(id, 0);
            String label = itemName(id) + "  ·  " + chineseTier(entry.tier)
                    + "阶  ·  起价 " + cost + "点";

            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 6) {
                @Override protected void onClick() {
                    super.onClick();
                    ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                    int level = loadout != null && id.equals(loadout.armorId)
                            ? loadout.armorLevel : 0;
                    LedgerFlow.armorCandidateId(id);
                    LedgerFlow.armorCandidateLevel(level);
                    LedgerFlow.armorPickerStage(1);
                    reload();
                }
            };
            button.leftJustify = true;
            button.textColor(LedgerEnvironment.INK);
            button.setRect(rowX, y, rowW - helpW - helpGap, rowH - 1f);
            add(button);

            LedgerButton help = LedgerHelp.item(this, id);
            help.setRect(rowX + rowW - helpW, y, helpW, rowH - 1f);
            add(help);
            y += rowH;
        }

        footerButton("返回旧行装", new Runnable() {
            @Override public void run() {
                LedgerFlow.resetArmorPicker();
                LedgerTransitions.turn(LedgerArmorScene.this,
                        right.paper, LedgerBuildScene.class, false);
            }
        }, false);
    }

    private void buildUpgradeList() {
        final String id = LedgerFlow.armorCandidateId();
        Entry entry = ReturningHeroItemCatalog.byId(id);
        if (entry == null || entry.kind != Kind.ARMOR) {
            LedgerFlow.resetArmorPicker();
            reload();
            return;
        }

        header(itemName(id), chineseTier(entry.tier) + "阶底甲 · 选择归来强化");

        float rowH = Math.min(15f, (right.body.height() - 2f) / 7f);
        float y = right.body.top + 1f;
        for (int level = 0; level <= 6; level++) {
            final int selectedLevel = level;
            int itemCost = ReturningHeroBuildCost.armorCost(id, level);
            int remaining = previewRemaining(id, level);
            boolean legal = itemCost >= 0 && remaining >= 0;

            String label = "+" + level + "  ·  本项 " + itemCost + "点  ·  "
                    + (remaining < 0 ? "额度不足" : "尚余 " + remaining);
            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 5) {
                @Override protected void onClick() {
                    super.onClick();
                    if (previewRemaining(id, selectedLevel) < 0) {
                        LedgerArmorScene.this.add(new WndMessage("这份配置已超过可用的装备重建值。"));
                        return;
                    }
                    LedgerFlow.armorCandidateLevel(selectedLevel);
                    reload();
                }
            };
            button.leftJustify = true;
            boolean selected = level == LedgerFlow.armorCandidateLevel();
            button.textColor(!legal ? LedgerEnvironment.FADED_INK
                    : selected ? LedgerEnvironment.STAMP : LedgerEnvironment.INK);
            button.setRect(right.body.left + 5f, y,
                    right.body.width() - 10f, rowH - 0.5f);
            add(button);
            y += rowH;
        }

        footerButton("写入护甲记录", new Runnable() {
            @Override public void run() {
                int level = LedgerFlow.armorCandidateLevel();
                if (previewRemaining(id, level) < 0) {
                    LedgerArmorScene.this.add(new WndMessage("这份配置已超过可用的装备重建值。"));
                    return;
                }
                ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                loadout.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
                loadout.armorId = id;
                loadout.armorLevel = level;
                LedgerAudio.write();
                LedgerFlow.resetArmorPicker();
                LedgerTransitions.turn(LedgerArmorScene.this,
                        right.paper, LedgerBuildScene.class, true);
            }
        }, true);
    }

    private int previewRemaining(String id, int level) {
        ReturningHeroLoadout original = LedgerFlow.draft().loadout;
        ReturningHeroLoadout preview = original == null
                ? new ReturningHeroLoadout() : original.copy();
        preview.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
        preview.armorId = id;
        preview.armorLevel = level;
        return ReturningHeroBuildCost.equipmentRemaining(
                LedgerFlow.draft().heroClass, preview);
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

    private float field(float x, float width, float y, String labelText, String valueText) {
        RenderedTextBlock label = t(labelText, 5, LedgerEnvironment.FADED_INK, 42);
        label.setPos(x, y);
        add(label);
        RenderedTextBlock value = t(valueText, 6, LedgerEnvironment.INK, (int) width - 47);
        value.setPos(x + 47f, y - 1f);
        add(value);
        return Math.max(label.bottom(), value.bottom()) + 10f;
    }

    private void footerButton(String text, final Runnable action, boolean stampColor) {
        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.28f));
        LedgerButton button = new LedgerButton(Chrome.Type.BLANK, text, 6) {
            @Override protected void onClick() {
                super.onClick();
                action.run();
            }
        };
        button.textColor(stampColor ? LedgerEnvironment.STAMP : LedgerEnvironment.FADED_INK);
        button.setRect(right.footer.left, right.footer.top + 3f,
                right.footer.width(), right.footer.height() - 3f);
        add(button);
    }

    private String itemName(String id) {
        Item item = ReturningHeroItemCatalog.newItem(id);
        return item == null ? "未知护甲" : Messages.titleCase(item.trueName());
    }

    private String chineseTier(int tier) {
        switch (tier) {
            case 2: return "二";
            case 3: return "三";
            case 4: return "四";
            case 5: return "五";
            default: return Integer.toString(tier);
        }
    }

    private void reload() {
        PixelScene.noFade = true;
        Game.switchScene(LedgerArmorScene.class);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
