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

import java.util.List;

/** Independent trinket reconstruction using the original alchemy-energy curves. */
public class LedgerTrinketScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildSummary();
        if (LedgerFlow.trinketPickerStage() == 0) buildTrinketList();
        else buildLevelList();

        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildSummary() {
        float x = left.header.left;
        float w = left.header.width();

        RenderedTextBlock title = t("饰品旧录", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("只记一件随身炼金饰品", 5, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.13f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f), w * 0.74f, 0.36f));

        float bx = left.body.left + 5f;
        float bw = left.body.width() - 10f;
        float y = left.body.top + 7f;
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;

        String current = loadout != null && loadout.trinketId != null
                ? itemName(loadout.trinketId) + " +" + loadout.trinketLevel
                : "未携带";
        y = field(bx, bw, y, "当前记录", current);

        int remaining = ReturningHeroBuildCost.trinketAlchemyRemaining(loadout);
        y = field(bx, bw, y, "炼金重建值",
                remaining < 0 ? "待重新核对"
                        : (ReturningHeroBuildRules.TRINKET_ALCHEMY_RECONSTRUCTION_BUDGET - remaining)
                        + " / " + ReturningHeroBuildRules.TRINKET_ALCHEMY_RECONSTRUCTION_BUDGET
                        + "（余 " + remaining + "）");

        RenderedTextBlock hint = t("饰品不占武器、护甲或三个佩物槽。\n不同饰品的炼金升级成本并不相同。",
                5, LedgerEnvironment.FADED_INK, (int) bw);
        hint.setPos(bx, y + 4f);
        add(hint);

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));
        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回旧行装", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerFlow.resetTrinketPicker();
                LedgerTransitions.turn(LedgerTrinketScene.this,
                        left.paper, LedgerBuildScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildTrinketList() {
        List<Entry> entries = ReturningHeroItemCatalog.selectableEntries(Kind.TRINKET);
        int page = LedgerChoicePages.clampPage(LedgerFlow.choicePage(), entries.size());
        LedgerFlow.choicePage(page);
        int pages = LedgerChoicePages.pageCount(entries.size());

        header("随身饰品", "第 " + (page + 1) + " / " + pages + " 页");

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
            String label = itemName(id) + "  ·  +3需 "
                    + ReturningHeroBuildCost.trinketAlchemyCost(id, 3) + "点";
            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 5) {
                @Override protected void onClick() {
                    super.onClick();
                    ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                    int level = loadout != null && id.equals(loadout.trinketId)
                            ? loadout.trinketLevel : 0;
                    LedgerFlow.trinketCandidateId(id);
                    LedgerFlow.trinketCandidateLevel(level);
                    LedgerFlow.trinketPickerStage(1);
                    reload();
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
                LedgerFlow.resetTrinketPicker();
                LedgerTransitions.turn(LedgerTrinketScene.this,
                        right.paper, LedgerBuildScene.class, false);
            }
        });
    }

    private void buildLevelList() {
        final String id = LedgerFlow.trinketCandidateId();
        if (!ReturningHeroItemCatalog.isSelectable(id, Kind.TRINKET)) {
            LedgerFlow.trinketPickerStage(0);
            reload();
            return;
        }

        header(itemName(id), "选择归来时的炼金成长");
        float rowH = Math.min(21f, (right.body.height() - 2f) / 4f);
        float y = right.body.top + 1f;

        for (int level = 0; level <= 3; level++) {
            final int selectedLevel = level;
            int cost = ReturningHeroBuildCost.trinketAlchemyCost(id, level);
            int remaining = ReturningHeroBuildRules.TRINKET_ALCHEMY_RECONSTRUCTION_BUDGET - cost;
            boolean legal = cost >= 0 && remaining >= 0;
            String label = "+" + level + "  ·  累计 " + cost + "点  ·  "
                    + (remaining < 0 ? "额度不足" : "尚余 " + remaining);

            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 5) {
                @Override protected void onClick() {
                    super.onClick();
                    int c = ReturningHeroBuildCost.trinketAlchemyCost(id, selectedLevel);
                    if (c < 0 || c > ReturningHeroBuildRules.TRINKET_ALCHEMY_RECONSTRUCTION_BUDGET) {
                        LedgerTrinketScene.this.add(new WndMessage("这一等级超过可恢复的炼金投入。"));
                        return;
                    }
                    LedgerFlow.trinketCandidateLevel(selectedLevel);
                    reload();
                }
            };
            button.leftJustify = true;
            boolean selected = level == LedgerFlow.trinketCandidateLevel();
            button.textColor(!legal ? LedgerEnvironment.FADED_INK
                    : selected ? LedgerEnvironment.STAMP : LedgerEnvironment.INK);
            button.setRect(right.body.left + 5f, y,
                    right.body.width() - 10f, rowH - 0.5f);
            add(button);
            y += rowH;
        }

        footerSingle("写入饰品记录", new Runnable() {
            @Override public void run() {
                int level = LedgerFlow.trinketCandidateLevel();
                int cost = ReturningHeroBuildCost.trinketAlchemyCost(id, level);
                if (cost < 0 || cost > ReturningHeroBuildRules.TRINKET_ALCHEMY_RECONSTRUCTION_BUDGET) {
                    LedgerTrinketScene.this.add(new WndMessage("这一等级超过可恢复的炼金投入。"));
                    return;
                }
                ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                loadout.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
                loadout.trinketId = id;
                loadout.trinketLevel = level;
                LedgerAudio.write();
                LedgerFlow.resetTrinketPicker();
                LedgerTransitions.turn(LedgerTrinketScene.this,
                        right.paper, LedgerBuildScene.class, true);
            }
        }, true);
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
        return Math.max(label.bottom(), value.bottom()) + 10f;
    }

    private String itemName(String id) {
        Item item = ReturningHeroItemCatalog.newItem(id);
        return item == null ? "未知饰品" : Messages.titleCase(item.trueName());
    }

    private void reload() {
        PixelScene.noFade = true;
        Game.switchScene(LedgerTrinketScene.class);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
