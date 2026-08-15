package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildCost;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildRules;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroHeritage;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Entry;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Kind;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroLoadout;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

import java.util.ArrayList;
import java.util.List;

/** Tier -> weapon -> upgrade reconstruction flow for the returning hero. */
public class LedgerWeaponScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildSummary();
        buildPicker();

        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildSummary() {
        float x = left.header.left;
        float w = left.header.width();

        RenderedTextBlock title = t("名册草页", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("尚未盖印", 5, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.14f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f), w * 0.72f, 0.36f));

        float bx = left.body.left + 5f;
        float bw = left.body.width() - 10f;
        float y = left.body.top + 7f;
        y = summaryField(bx, bw, y, "姓名", LedgerFlow.draft().name);
        y = summaryField(bx, bw, y, "职业",
                Messages.titleCase(LedgerFlow.draft().heroClass.title()));
        y = summaryField(bx, bw, y, "惯用兵器", currentWeaponText());

        int spent = ReturningHeroBuildCost.equipmentSpent(
                LedgerFlow.draft().heroClass, LedgerFlow.draft().loadout);
        String budget = spent < 0 ? "待重新核对" : spent + " / "
                + ReturningHeroBuildRules.EQUIPMENT_RECONSTRUCTION_BUDGET;
        y = summaryField(bx, bw, y, "装备重建值", budget);

        String candidate = LedgerFlow.weaponCandidateId();
        if (candidate != null) {
            int preview = previewRemaining(candidate, LedgerFlow.weaponCandidateLevel());
            String previewText = preview < 0 ? "超过可用额度" : "尚余 " + preview;
            RenderedTextBlock hint = t("当前试算：" + previewText, 5,
                    preview < 0 ? LedgerEnvironment.STAMP : LedgerEnvironment.FADED_INK,
                    (int) bw);
            hint.setPos(bx, Math.min(y + 2f, left.body.bottom - hint.height() - 4f));
            add(hint);
        } else {
            RenderedTextBlock hint = t("先选阶次，再写下最终惯用兵器。", 5,
                    LedgerEnvironment.FADED_INK, (int) bw);
            hint.setPos(bx, left.body.bottom - hint.height() - 4f);
            add(hint);
        }

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));

        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回登记页", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerFlow.resetWeaponPicker();
                LedgerTransitions.turn(LedgerWeaponScene.this,
                        left.paper, LedgerRegistrationScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private String currentWeaponText() {
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        if (loadout != null && loadout.primaryWeaponId != null) {
            return itemName(loadout.primaryWeaponId) + " +" + loadout.primaryWeaponLevel;
        }
        if (LedgerFlow.draft().heroClass == HeroClass.MAGE) {
            return "法师魔杖 +" + ReturningHeroBuildRules.MAGES_STAFF_RETURN_LEVEL;
        }
        return "尚未登记";
    }

    private float summaryField(float x, float width, float y,
                               String labelText, String valueText) {
        RenderedTextBlock label = t(labelText, 5, LedgerEnvironment.FADED_INK, 40);
        label.setPos(x, y);
        add(label);
        RenderedTextBlock value = t(valueText == null ? "—" : valueText,
                6, LedgerEnvironment.INK, (int) width - 45);
        value.setPos(x + 45f, y - 1f);
        add(value);
        return Math.max(label.bottom(), value.bottom()) + 10f;
    }

    private void buildPicker() {
        switch (LedgerFlow.weaponPickerStage()) {
            case 1:
                buildWeaponList();
                break;
            case 2:
                buildUpgradeList();
                break;
            case 0:
            default:
                buildTierList();
                break;
        }
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

    private void buildTierList() {
        header("惯用兵器", "先按阶次翻检");

        float rowH = Math.min(18f, (right.body.height() - 4f) / 5f);
        float y = right.body.top + 2f;
        for (int tier = 1; tier <= 5; tier++) {
            final int selectedTier = tier;
            String label;
            if (tier == 1) {
                label = LedgerFlow.draft().heroClass == HeroClass.MAGE
                        ? "一阶 · 法师魔杖（职业遗产）"
                        : "一阶 · 初行兵器（职业锁定）";
            } else {
                label = chineseTier(tier) + "阶兵器";
            }

            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 6) {
                @Override protected void onClick() {
                    super.onClick();
                    LedgerFlow.weaponTier(selectedTier);
                    LedgerFlow.weaponPickerStage(1);
                    LedgerFlow.weaponCandidateId(null);
                    LedgerFlow.weaponCandidateLevel(0);
                    reload();
                }
            };
            button.leftJustify = true;
            button.textColor(LedgerEnvironment.INK);
            button.setRect(right.body.left + 5f, y,
                    right.body.width() - 10f, rowH - 1f);
            add(button);
            y += rowH;
        }

        footerButton("返回登记页", new Runnable() {
            @Override public void run() {
                LedgerFlow.resetWeaponPicker();
                LedgerTransitions.turn(LedgerWeaponScene.this,
                        right.paper, LedgerRegistrationScene.class, false);
            }
        }, false);
    }

    private void buildWeaponList() {
        final int tier = LedgerFlow.weaponTier();
        header(chineseTier(tier) + "阶兵器", tier == 1 ? "此阶只认本人旧兵器" : "选择一件作为惯用兵器");

        List<Entry> entries = weaponsForTier(tier);
        float rowH = Math.min(17f,
                (right.body.height() - 3f) / Math.max(1, entries.size()));
        float y = right.body.top + 1f;
        float helpW = 13f;
        float helpGap = 1f;
        float rowX = right.body.left + 5f;
        float rowW = right.body.width() - 10f;

        for (Entry entry : entries) {
            final String id = entry.id;
            String label = itemName(id);
            if (tier == 1 && LedgerFlow.draft().heroClass == HeroClass.MAGE) {
                label += "  ·  固定 +" + ReturningHeroBuildRules.MAGES_STAFF_RETURN_LEVEL;
            } else {
                int startCost = ReturningHeroBuildCost.primaryWeaponCost(
                        LedgerFlow.draft().heroClass, id, 0);
                if (startCost >= 0) label += "  ·  起价 " + startCost + "点";
            }

            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 6) {
                @Override protected void onClick() {
                    super.onClick();
                    LedgerFlow.weaponCandidateId(id);
                    ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                    int level = loadout != null && id.equals(loadout.primaryWeaponId)
                            ? loadout.primaryWeaponLevel : 0;
                    if (LedgerFlow.draft().heroClass == HeroClass.MAGE
                            && tier == 1) {
                        level = ReturningHeroBuildRules.MAGES_STAFF_RETURN_LEVEL;
                    }
                    LedgerFlow.weaponCandidateLevel(level);
                    LedgerFlow.weaponPickerStage(2);
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

        footerButton("返回阶次", new Runnable() {
            @Override public void run() {
                LedgerFlow.weaponPickerStage(0);
                LedgerFlow.weaponCandidateId(null);
                LedgerFlow.weaponCandidateLevel(0);
                reload();
            }
        }, false);
    }

    private void buildUpgradeList() {
        final String id = LedgerFlow.weaponCandidateId();
        final int tier = LedgerFlow.weaponTier();
        if (id == null) {
            LedgerFlow.weaponPickerStage(1);
            reload();
            return;
        }

        header(itemName(id), tier == 1 ? "旧兵器也可以一路带到归来" : "写下归来时的强化程度");

        if (LedgerFlow.draft().heroClass == HeroClass.MAGE && tier == 1) {
            float x = right.body.left + 7f;
            float w = right.body.width() - 14f;
            float y = right.body.top + 9f;

            RenderedTextBlock fixed = t("职业遗产 · 固定满级 +"
                            + ReturningHeroBuildRules.MAGES_STAFF_RETURN_LEVEL,
                    7, LedgerEnvironment.INK, (int) w);
            fixed.setPos(x, y);
            add(fixed);

            RenderedTextBlock free = t("不占用 15 点装备重建值。\n最终灌注的法杖会在法杖登记中另行填写。",
                    5, LedgerEnvironment.FADED_INK, (int) w);
            free.setPos(x, fixed.bottom() + 8f);
            add(free);

            footerButton("保留法师魔杖", new Runnable() {
                @Override public void run() {
                    ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                    loadout.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
                    loadout.primaryWeaponId = null;
                    loadout.primaryWeaponLevel = 0;
                    LedgerAudio.write();
                    LedgerFlow.resetWeaponPicker();
                    LedgerTransitions.turn(LedgerWeaponScene.this,
                            right.paper, LedgerSealScene.class, true);
                }
            }, true);
            return;
        }

        float rowH = Math.min(13.5f, (right.body.height() - 2f) / 8f);
        float y = right.body.top + 1f;
        for (int level = 0; level <= 7; level++) {
            final int selectedLevel = level;
            int itemCost = ReturningHeroBuildCost.primaryWeaponCost(
                    LedgerFlow.draft().heroClass, id, level);
            int remaining = previewRemaining(id, level);
            boolean legal = itemCost >= 0 && remaining >= 0;

            String label = "+" + level + "  ·  本项 "
                    + (itemCost < 0 ? "—" : itemCost + "点")
                    + "  ·  " + (remaining < 0 ? "额度不足" : "尚余 " + remaining);

            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 5) {
                @Override protected void onClick() {
                    super.onClick();
                    int remains = previewRemaining(id, selectedLevel);
                    if (remains < 0) {
                        LedgerWeaponScene.this.add(new WndMessage("这份配置已超过可用的装备重建值。"));
                        return;
                    }
                    LedgerFlow.weaponCandidateLevel(selectedLevel);
                    reload();
                }
            };
            button.leftJustify = true;
            boolean selected = level == LedgerFlow.weaponCandidateLevel();
            button.textColor(!legal ? LedgerEnvironment.FADED_INK
                    : selected ? LedgerEnvironment.STAMP : LedgerEnvironment.INK);
            button.setRect(right.body.left + 5f, y,
                    right.body.width() - 10f, rowH - 0.5f);
            add(button);
            y += rowH;
        }

        footerButton("写入惯用兵器", new Runnable() {
            @Override public void run() {
                int level = LedgerFlow.weaponCandidateLevel();
                if (previewRemaining(id, level) < 0) {
                    LedgerWeaponScene.this.add(new WndMessage("这份配置已超过可用的装备重建值。"));
                    return;
                }
                ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                loadout.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
                loadout.primaryWeaponId = id;
                loadout.primaryWeaponLevel = level;
                LedgerAudio.write();
                LedgerFlow.resetWeaponPicker();
                LedgerTransitions.turn(LedgerWeaponScene.this,
                        right.paper, LedgerSealScene.class, true);
            }
        }, true);
    }

    private List<Entry> weaponsForTier(int tier) {
        List<Entry> result = new ArrayList<>();
        if (tier == 1) {
            Class<? extends Item> heritage = ReturningHeroHeritage.initialWeapon(
                    LedgerFlow.draft().heroClass);
            Entry entry = ReturningHeroItemCatalog.byClass(heritage);
            if (entry != null) result.add(entry);
            return result;
        }

        for (Entry entry : ReturningHeroItemCatalog.entries(Kind.MELEE_WEAPON)) {
            if (entry.selectable && entry.tier == tier) result.add(entry);
        }
        return result;
    }

    private int previewRemaining(String id, int level) {
        ReturningHeroLoadout original = LedgerFlow.draft().loadout;
        ReturningHeroLoadout preview = original == null
                ? new ReturningHeroLoadout() : original.copy();
        preview.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;

        if (LedgerFlow.draft().heroClass == HeroClass.MAGE
                && LedgerFlow.weaponTier() == 1) {
            preview.primaryWeaponId = null;
            preview.primaryWeaponLevel = 0;
        } else {
            preview.primaryWeaponId = id;
            preview.primaryWeaponLevel = level;
        }
        return ReturningHeroBuildCost.equipmentRemaining(
                LedgerFlow.draft().heroClass, preview);
    }

    private String itemName(String id) {
        Item item = ReturningHeroItemCatalog.newItem(id);
        return item == null ? "未知兵器" : Messages.titleCase(item.trueName());
    }

    private String chineseTier(int tier) {
        switch (tier) {
            case 1: return "一";
            case 2: return "二";
            case 3: return "三";
            case 4: return "四";
            case 5: return "五";
            default: return Integer.toString(tier);
        }
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

    private void reload() {
        PixelScene.noFade = true;
        Game.switchScene(LedgerWeaponScene.class);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
