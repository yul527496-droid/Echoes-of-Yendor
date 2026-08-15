package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildCost;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildRules;
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

import java.util.List;

/** Mage staff imbuement and optional carried-wand reconstruction. */
public class LedgerWandScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildSummary();
        switch (LedgerFlow.wandPickerStage()) {
            case 1:
                buildWandList(true);
                break;
            case 2:
                buildWandList(false);
                break;
            case 3:
                buildCarriedLevel();
                break;
            case 0:
            default:
                buildOverview();
                break;
        }

        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildSummary() {
        float x = left.header.left;
        float w = left.header.width();

        RenderedTextBlock title = t("法杖旧录", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("只记归来时仍随身者", 5, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.13f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f), w * 0.74f, 0.36f));

        float bx = left.body.left + 5f;
        float bw = left.body.width() - 10f;
        float y = left.body.top + 7f;

        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        if (LedgerFlow.draft().heroClass == HeroClass.MAGE) {
            String imbue = loadout != null && loadout.mageStaffImbuementId != null
                    ? itemName(loadout.mageStaffImbuementId) : "魔弹法杖";
            y = field(bx, bw, y, "法师魔杖", "满级 +"
                    + ReturningHeroBuildRules.MAGES_STAFF_RETURN_LEVEL);
            y = field(bx, bw, y, "最终灌注", imbue);
        }

        String carried = loadout != null && loadout.carriedWandId != null
                ? itemName(loadout.carriedWandId) + " +" + loadout.carriedWandLevel
                : "未携带";
        y = field(bx, bw, y, "随身法杖", carried);

        int spent = ReturningHeroBuildCost.equipmentSpent(
                LedgerFlow.draft().heroClass, loadout);
        int remaining = ReturningHeroBuildCost.equipmentRemaining(
                LedgerFlow.draft().heroClass, loadout);
        field(bx, bw, y, "装备重建值",
                spent < 0 || remaining < 0 ? "待重新核对"
                        : spent + " / " + ReturningHeroBuildRules.EQUIPMENT_RECONSTRUCTION_BUDGET
                        + "（余 " + remaining + "）");

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));
        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回旧行装", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerFlow.resetWandPicker();
                LedgerTransitions.turn(LedgerWandScene.this,
                        left.paper, LedgerBuildScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildOverview() {
        header("法杖", "法师魔杖与普通随身法杖分开登记");

        float y = right.body.top + 8f;
        float rowH = 22f;
        if (LedgerFlow.draft().heroClass == HeroClass.MAGE) {
            ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
            String current = loadout != null && loadout.mageStaffImbuementId != null
                    ? itemName(loadout.mageStaffImbuementId) : "魔弹法杖";
            y = actionRow(y, rowH, "法师魔杖最终灌注 · " + current, new Runnable() {
                @Override public void run() {
                    LedgerFlow.choicePage(0);
                    LedgerFlow.wandPickerStage(1);
                    reload();
                }
            });
        }

        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        String carried = loadout != null && loadout.carriedWandId != null
                ? itemName(loadout.carriedWandId) + " +" + loadout.carriedWandLevel
                : "未携带";
        y = actionRow(y, rowH, "随身法杖 · " + carried, new Runnable() {
            @Override public void run() {
                LedgerFlow.choicePage(0);
                LedgerFlow.wandPickerStage(2);
                reload();
            }
        });

        if (loadout != null && loadout.carriedWandId != null) {
            LedgerButton clear = new LedgerButton(Chrome.Type.BLANK, "划去随身法杖记录", 5) {
                @Override protected void onClick() {
                    super.onClick();
                    ReturningHeroLoadout current = LedgerFlow.draft().loadout;
                    current.carriedWandId = null;
                    current.carriedWandLevel = 0;
                    LedgerAudio.erase();
                    reload();
                }
            };
            clear.leftJustify = true;
            clear.textColor(LedgerEnvironment.FADED_INK);
            clear.setRect(right.body.left + 6f, y + 5f,
                    right.body.width() - 12f, 15f);
            add(clear);
        }

        footerSingle("返回旧行装", new Runnable() {
            @Override public void run() {
                LedgerFlow.resetWandPicker();
                LedgerTransitions.turn(LedgerWandScene.this,
                        right.paper, LedgerBuildScene.class, false);
            }
        }, false);
    }

    private void buildWandList(final boolean staffImbuement) {
        List<Entry> entries = ReturningHeroItemCatalog.selectableEntries(Kind.WAND);
        int page = LedgerChoicePages.clampPage(LedgerFlow.choicePage(), entries.size());
        LedgerFlow.choicePage(page);
        int pages = LedgerChoicePages.pageCount(entries.size());

        header(staffImbuement ? "最终灌注" : "随身法杖",
                "第 " + (page + 1) + " / " + pages + " 页");

        int from = LedgerChoicePages.from(page, entries.size());
        int to = LedgerChoicePages.to(page, entries.size());
        float rowH = Math.min(16f,
                (right.body.height() - 2f) / Math.max(1, to - from));
        float y = right.body.top + 1f;
        float rowX = right.body.left + 5f;
        float rowW = right.body.width() - 10f;
        float helpW = 13f;
        float helpGap = 1f;

        for (int i = from; i < to; i++) {
            final String id = entries.get(i).id;
            String label = itemName(id);
            if (!staffImbuement) {
                int cost = ReturningHeroBuildCost.carriedWandCost(id, 0);
                label += "  ·  起价 " + cost + "点";
            }
            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 6) {
                @Override protected void onClick() {
                    super.onClick();
                    if (staffImbuement) {
                        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                        loadout.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
                        loadout.mageStaffImbuementId = id;
                        LedgerAudio.write();
                        LedgerFlow.wandPickerStage(0);
                        LedgerFlow.choicePage(0);
                        reload();
                    } else {
                        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                        int level = loadout != null && id.equals(loadout.carriedWandId)
                                ? loadout.carriedWandLevel : 0;
                        LedgerFlow.wandCandidateId(id);
                        LedgerFlow.wandCandidateLevel(level);
                        LedgerFlow.wandPickerStage(3);
                        reload();
                    }
                }
            };
            button.leftJustify = true;
            button.textColor(LedgerEnvironment.INK);
            button.setRect(rowX, y, rowW - helpW - helpGap, rowH - 0.5f);
            add(button);

            LedgerButton help = LedgerHelp.item(this, id);
            help.setRect(rowX + rowW - helpW, y, helpW, rowH - 0.5f);
            add(help);
            y += rowH;
        }

        footerPaged(page, pages, new Runnable() {
            @Override public void run() {
                LedgerFlow.wandPickerStage(0);
                LedgerFlow.choicePage(0);
                reload();
            }
        });
    }

    private void buildCarriedLevel() {
        final String id = LedgerFlow.wandCandidateId();
        if (!ReturningHeroItemCatalog.isSelectable(id, Kind.WAND)) {
            LedgerFlow.wandPickerStage(2);
            reload();
            return;
        }

        header(itemName(id), "选择普通随身法杖的强化程度");
        float rowH = Math.min(17f, (right.body.height() - 2f) / 6f);
        float y = right.body.top + 1f;

        for (int level = 0; level <= ReturningHeroBuildRules.MAX_CARRIED_WAND_LEVEL; level++) {
            final int selectedLevel = level;
            int itemCost = ReturningHeroBuildCost.carriedWandCost(id, level);
            int remaining = previewRemaining(id, level);
            boolean legal = remaining >= 0;
            String label = "+" + level + "  ·  本项 " + itemCost + "点  ·  "
                    + (remaining < 0 ? "额度不足" : "尚余 " + remaining);

            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 5) {
                @Override protected void onClick() {
                    super.onClick();
                    if (previewRemaining(id, selectedLevel) < 0) {
                        LedgerWandScene.this.add(new WndMessage("这份配置已超过可用的装备重建值。"));
                        return;
                    }
                    LedgerFlow.wandCandidateLevel(selectedLevel);
                    reload();
                }
            };
            button.leftJustify = true;
            boolean selected = level == LedgerFlow.wandCandidateLevel();
            button.textColor(!legal ? LedgerEnvironment.FADED_INK
                    : selected ? LedgerEnvironment.STAMP : LedgerEnvironment.INK);
            button.setRect(right.body.left + 5f, y,
                    right.body.width() - 10f, rowH - 0.5f);
            add(button);
            y += rowH;
        }

        footerSingle("写入随身法杖", new Runnable() {
            @Override public void run() {
                int level = LedgerFlow.wandCandidateLevel();
                if (previewRemaining(id, level) < 0) {
                    LedgerWandScene.this.add(new WndMessage("这份配置已超过可用的装备重建值。"));
                    return;
                }
                ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                loadout.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
                loadout.carriedWandId = id;
                loadout.carriedWandLevel = level;
                LedgerAudio.write();
                LedgerFlow.wandPickerStage(0);
                LedgerFlow.choicePage(0);
                LedgerFlow.wandCandidateId(null);
                LedgerFlow.wandCandidateLevel(0);
                reload();
            }
        }, true);
    }

    private int previewRemaining(String id, int level) {
        ReturningHeroLoadout original = LedgerFlow.draft().loadout;
        ReturningHeroLoadout preview = original == null
                ? new ReturningHeroLoadout() : original.copy();
        preview.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
        preview.carriedWandId = id;
        preview.carriedWandLevel = level;
        return ReturningHeroBuildCost.equipmentRemaining(
                LedgerFlow.draft().heroClass, preview);
    }

    private float actionRow(float y, float rowH, String text, final Runnable action) {
        LedgerButton button = new LedgerButton(Chrome.Type.BLANK, text, 6) {
            @Override protected void onClick() {
                super.onClick();
                action.run();
            }
        };
        button.leftJustify = true;
        button.textColor(LedgerEnvironment.INK);
        button.setRect(right.body.left + 6f, y,
                right.body.width() - 12f, rowH - 1f);
        add(button);
        return y + rowH;
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

    private void footerSingle(String text, final Runnable action, boolean stampColor) {
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
        return Math.max(label.bottom(), value.bottom()) + 9f;
    }

    private String itemName(String id) {
        Item item = ReturningHeroItemCatalog.newItem(id);
        return item == null ? "未知法杖" : Messages.titleCase(item.trueName());
    }

    private void reload() {
        PixelScene.noFade = true;
        Game.switchScene(LedgerWandScene.class);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
