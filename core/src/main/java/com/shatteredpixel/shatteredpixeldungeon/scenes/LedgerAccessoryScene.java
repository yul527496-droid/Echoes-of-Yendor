package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroArtifactBalance;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildCost;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildRules;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildValidator;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroHeritage;
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

/** Reconstructs SPD's original artifact / misc / ring equipment slots. */
public class LedgerAccessoryScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildSummary();
        switch (LedgerFlow.accessoryPickerStage()) {
            case 1:
                buildItemList();
                break;
            case 2:
                buildLevelList();
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

        RenderedTextBlock title = t("佩物旧录", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("照旧日三个佩位核对", 5, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.13f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f), w * 0.74f, 0.36f));

        float bx = left.body.left + 5f;
        float bw = left.body.width() - 10f;
        float y = left.body.top + 7f;
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;

        y = field(bx, bw, y, "神器槽", artifactSlotStatus(loadout));
        y = field(bx, bw, y, "万用槽", miscSlotStatus(loadout));
        y = field(bx, bw, y, "戒指槽", ringSlotStatus(loadout));

        int artifactSpent = ReturningHeroBuildCost.ordinaryArtifactGrowthSpent(
                LedgerFlow.draft().heroClass, loadout);
        String artBudget = ReturningHeroHeritage.hasClassArtifact(LedgerFlow.draft().heroClass)
                ? "职业神器满级；第二神器最高 +"
                    + ReturningHeroBuildRules.MAX_SECOND_ARTIFACT_LEVEL_WITH_CLASS_ARTIFACT
                : artifactSpent + " / " + ReturningHeroBuildRules.ORDINARY_ARTIFACT_GROWTH_BUDGET;
        y = field(bx, bw, y, "神器成长", artBudget);

        int equipRemain = ReturningHeroBuildCost.equipmentRemaining(
                LedgerFlow.draft().heroClass, loadout);
        field(bx, bw, y, "装备值余量", equipRemain < 0 ? "待重新核对" : Integer.toString(equipRemain));

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));
        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回旧行装", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerFlow.resetAccessoryPicker();
                LedgerTransitions.turn(LedgerAccessoryScene.this,
                        left.paper, LedgerBuildScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildOverview() {
        header("三个佩位", "神器槽 / 万用槽 / 戒指槽");
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        float y = right.body.top + 3f;
        float rowH = Math.min(17f, (right.body.height() - 3f) / 6f);

        if (ReturningHeroHeritage.hasClassArtifact(LedgerFlow.draft().heroClass)) {
            String id = ReturningHeroItemCatalog.idForClass(
                    ReturningHeroHeritage.classArtifact(LedgerFlow.draft().heroClass));
            y = fixedRow(y, rowH, "神器槽 · " + itemName(id) + " +"
                    + ReturningHeroBuildRules.CLASS_ARTIFACT_RETURN_LEVEL, id);
        } else {
            y = actionRow(y, rowH, "神器槽 · " + artifactSlotStatus(loadout), new Runnable() {
                @Override public void run() { beginTarget(0); }
            });
        }

        y = actionRow(y, rowH, "万用槽（神器） · "
                + (loadout != null && loadout.miscIsArtifact() ? miscSlotStatus(loadout) : "选择"),
                new Runnable() {
                    @Override public void run() { beginTarget(1); }
                });

        y = actionRow(y, rowH, "万用槽（戒指） · "
                + (loadout != null && loadout.miscIsRing() ? miscSlotStatus(loadout) : "选择"),
                new Runnable() {
                    @Override public void run() { beginTarget(2); }
                });

        y = actionRow(y, rowH, "戒指槽 · " + ringSlotStatus(loadout), new Runnable() {
            @Override public void run() { beginTarget(3); }
        });

        if (loadout != null && loadout.miscSlotKind != ReturningHeroLoadout.MiscSlotKind.EMPTY) {
            y = clearRow(y, rowH, "划去万用槽记录", new Runnable() {
                @Override public void run() {
                    LedgerFlow.draft().loadout.clearMiscSlot();
                    LedgerAudio.erase();
                    reload();
                }
            });
        }

        boolean clearable = loadout != null && (loadout.ringSlotId != null
                || (!ReturningHeroHeritage.hasClassArtifact(LedgerFlow.draft().heroClass)
                && loadout.artifactSlotId != null));
        if (clearable) {
            clearRow(y, rowH, "清理一个专用槽请进入该栏重选", null);
        }

        footerSingle("返回旧行装", new Runnable() {
            @Override public void run() {
                LedgerFlow.resetAccessoryPicker();
                LedgerTransitions.turn(LedgerAccessoryScene.this,
                        right.paper, LedgerBuildScene.class, false);
            }
        }, false);
    }

    private void beginTarget(int target) {
        LedgerFlow.accessoryTarget(target);
        LedgerFlow.accessoryPickerStage(1);
        LedgerFlow.choicePage(0);
        LedgerFlow.accessoryCandidateId(null);
        LedgerFlow.accessoryCandidateLevel(0);
        reload();
    }

    private void buildItemList() {
        final int target = LedgerFlow.accessoryTarget();
        final boolean artifact = target == 0 || target == 1;
        List<Entry> entries = ReturningHeroItemCatalog.selectableEntries(
                artifact ? Kind.ARTIFACT : Kind.RING);
        int page = LedgerChoicePages.clampPage(LedgerFlow.choicePage(), entries.size());
        LedgerFlow.choicePage(page);
        int pages = LedgerChoicePages.pageCount(entries.size());

        header(targetTitle(target), "第 " + (page + 1) + " / " + pages + " 页");

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
            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 6) {
                @Override protected void onClick() {
                    super.onClick();
                    LedgerFlow.accessoryCandidateId(id);
                    LedgerFlow.accessoryCandidateLevel(currentLevelFor(target, id));
                    LedgerFlow.accessoryPickerStage(2);
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
                LedgerFlow.accessoryPickerStage(0);
                LedgerFlow.choicePage(0);
                reload();
            }
        });
    }

    private void buildLevelList() {
        final int target = LedgerFlow.accessoryTarget();
        final String id = LedgerFlow.accessoryCandidateId();
        final boolean artifact = target == 0 || target == 1;
        if (id == null || !ReturningHeroItemCatalog.isSelectable(
                id, artifact ? Kind.ARTIFACT : Kind.RING)) {
            LedgerFlow.accessoryPickerStage(1);
            reload();
            return;
        }

        header(itemName(id), artifact ? "选择可恢复的神器成长" : "选择戒指强化");

        if (artifact) {
            int max = artifactMax(target);
            List<Integer> levels = ReturningHeroArtifactBalance.legalVisibleLevels(id, max);
            float rowH = Math.min(17f,
                    (right.body.height() - 2f) / Math.max(1, levels.size()));
            float y = right.body.top + 1f;
            for (Integer value : levels) {
                final int level = value;
                boolean legal = previewValid(target, id, level);
                String label = "+" + level + "  ·  " + artifactBudgetPreview(target, id, level);
                LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 5) {
                    @Override protected void onClick() {
                        super.onClick();
                        if (!previewValid(target, id, level)) {
                            LedgerAccessoryScene.this.add(new WndMessage("这件神器与当前佩物记录冲突，或超过神器成长额度。"));
                            return;
                        }
                        LedgerFlow.accessoryCandidateLevel(level);
                        reload();
                    }
                };
                button.leftJustify = true;
                boolean selected = level == LedgerFlow.accessoryCandidateLevel();
                button.textColor(!legal ? LedgerEnvironment.FADED_INK
                        : selected ? LedgerEnvironment.STAMP : LedgerEnvironment.INK);
                button.setRect(right.body.left + 5f, y,
                        right.body.width() - 10f, rowH - 0.5f);
                add(button);
                y += rowH;
            }
        } else {
            float rowH = Math.min(20f, (right.body.height() - 2f) / 4f);
            float y = right.body.top + 1f;
            for (int level = 0; level <= ReturningHeroBuildRules.MAX_RING_LEVEL; level++) {
                final int selectedLevel = level;
                int remain = equipmentRemainingPreview(target, id, level);
                boolean legal = previewValid(target, id, level);
                String label = "+" + level + "  ·  本项 " + level + "点  ·  "
                        + (remain < 0 ? "额度不足" : "尚余 " + remain);
                LedgerButton button = new LedgerButton(Chrome.Type.BLANK, label, 5) {
                    @Override protected void onClick() {
                        super.onClick();
                        if (!previewValid(target, id, selectedLevel)) {
                            LedgerAccessoryScene.this.add(new WndMessage("戒指记录与当前槽位冲突，或超过强化上限。"));
                            return;
                        }
                        LedgerFlow.accessoryCandidateLevel(selectedLevel);
                        reload();
                    }
                };
                button.leftJustify = true;
                boolean selected = level == LedgerFlow.accessoryCandidateLevel();
                button.textColor(!legal ? LedgerEnvironment.FADED_INK
                        : selected ? LedgerEnvironment.STAMP : LedgerEnvironment.INK);
                button.setRect(right.body.left + 5f, y,
                        right.body.width() - 10f, rowH - 0.5f);
                add(button);
                y += rowH;
            }
        }

        footerSingle("写入这个佩位", new Runnable() {
            @Override public void run() {
                int level = LedgerFlow.accessoryCandidateLevel();
                if (!previewValid(target, id, level)) {
                    LedgerAccessoryScene.this.add(new WndMessage("这份佩物配置不符合当前重建规则。"));
                    return;
                }
                applyCandidate(target, id, level);
                LedgerAudio.write();
                LedgerFlow.accessoryPickerStage(0);
                LedgerFlow.choicePage(0);
                LedgerFlow.accessoryCandidateId(null);
                LedgerFlow.accessoryCandidateLevel(0);
                reload();
            }
        }, true);
    }

    private void applyCandidate(int target, String id, int level) {
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        loadout.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
        switch (target) {
            case 0:
                loadout.artifactSlotId = id;
                loadout.artifactSlotLevel = level;
                break;
            case 1:
                loadout.miscSlotKind = ReturningHeroLoadout.MiscSlotKind.ARTIFACT;
                loadout.miscSlotId = id;
                loadout.miscSlotLevel = level;
                break;
            case 2:
                loadout.miscSlotKind = ReturningHeroLoadout.MiscSlotKind.RING;
                loadout.miscSlotId = id;
                loadout.miscSlotLevel = level;
                break;
            case 3:
                loadout.ringSlotId = id;
                loadout.ringSlotLevel = level;
                break;
        }
    }

    private ReturningHeroLoadout preview(int target, String id, int level) {
        ReturningHeroLoadout base = LedgerFlow.draft().loadout;
        ReturningHeroLoadout preview = base == null ? new ReturningHeroLoadout() : base.copy();
        preview.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
        switch (target) {
            case 0:
                preview.artifactSlotId = id;
                preview.artifactSlotLevel = level;
                break;
            case 1:
                preview.miscSlotKind = ReturningHeroLoadout.MiscSlotKind.ARTIFACT;
                preview.miscSlotId = id;
                preview.miscSlotLevel = level;
                break;
            case 2:
                preview.miscSlotKind = ReturningHeroLoadout.MiscSlotKind.RING;
                preview.miscSlotId = id;
                preview.miscSlotLevel = level;
                break;
            case 3:
                preview.ringSlotId = id;
                preview.ringSlotLevel = level;
                break;
        }
        return preview;
    }

    private boolean previewValid(int target, String id, int level) {
        return ReturningHeroBuildValidator.validate(
                LedgerFlow.draft().heroClass, preview(target, id, level)).isValid();
    }

    private int equipmentRemainingPreview(int target, String id, int level) {
        return ReturningHeroBuildCost.equipmentRemaining(
                LedgerFlow.draft().heroClass, preview(target, id, level));
    }

    private String artifactBudgetPreview(int target, String id, int level) {
        ReturningHeroLoadout preview = preview(target, id, level);
        if (ReturningHeroHeritage.hasClassArtifact(LedgerFlow.draft().heroClass)) {
            return "第二神器上限 +"
                    + ReturningHeroBuildRules.MAX_SECOND_ARTIFACT_LEVEL_WITH_CLASS_ARTIFACT;
        }
        int spent = ReturningHeroBuildCost.ordinaryArtifactGrowthSpent(
                LedgerFlow.draft().heroClass, preview);
        return "成长 " + spent + " / " + ReturningHeroBuildRules.ORDINARY_ARTIFACT_GROWTH_BUDGET;
    }

    private int artifactMax(int target) {
        return target == 1 && ReturningHeroHeritage.hasClassArtifact(LedgerFlow.draft().heroClass)
                ? ReturningHeroBuildRules.MAX_SECOND_ARTIFACT_LEVEL_WITH_CLASS_ARTIFACT
                : ReturningHeroBuildRules.ORDINARY_ARTIFACT_GROWTH_BUDGET;
    }

    private int currentLevelFor(int target, String id) {
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        if (loadout == null) return 0;
        switch (target) {
            case 0:
                return id.equals(loadout.artifactSlotId) ? loadout.artifactSlotLevel : 0;
            case 1:
                return loadout.miscIsArtifact() && id.equals(loadout.miscSlotId)
                        ? loadout.miscSlotLevel : 0;
            case 2:
                return loadout.miscIsRing() && id.equals(loadout.miscSlotId)
                        ? loadout.miscSlotLevel : 0;
            case 3:
                return id.equals(loadout.ringSlotId) ? loadout.ringSlotLevel : 0;
            default:
                return 0;
        }
    }

    private String artifactSlotStatus(ReturningHeroLoadout loadout) {
        if (ReturningHeroHeritage.hasClassArtifact(LedgerFlow.draft().heroClass)) {
            String id = ReturningHeroItemCatalog.idForClass(
                    ReturningHeroHeritage.classArtifact(LedgerFlow.draft().heroClass));
            return itemName(id) + " +" + ReturningHeroBuildRules.CLASS_ARTIFACT_RETURN_LEVEL;
        }
        return loadout != null && loadout.artifactSlotId != null
                ? itemName(loadout.artifactSlotId) + " +" + loadout.artifactSlotLevel
                : "未填写";
    }

    private String miscSlotStatus(ReturningHeroLoadout loadout) {
        if (loadout == null || loadout.miscSlotId == null
                || loadout.miscSlotKind == ReturningHeroLoadout.MiscSlotKind.EMPTY) return "空";
        return itemName(loadout.miscSlotId) + " +" + loadout.miscSlotLevel;
    }

    private String ringSlotStatus(ReturningHeroLoadout loadout) {
        return loadout != null && loadout.ringSlotId != null
                ? itemName(loadout.ringSlotId) + " +" + loadout.ringSlotLevel
                : "未填写";
    }

    private String targetTitle(int target) {
        switch (target) {
            case 0: return "神器槽";
            case 1: return "万用槽 · 神器";
            case 2: return "万用槽 · 戒指";
            case 3: return "戒指槽";
            default: return "佩物";
        }
    }

    private float actionRow(float y, float rowH, String text, final Runnable action) {
        LedgerButton button = new LedgerButton(Chrome.Type.BLANK, text, 5) {
            @Override protected void onClick() {
                super.onClick();
                action.run();
            }
        };
        button.leftJustify = true;
        button.textColor(LedgerEnvironment.INK);
        button.setRect(right.body.left + 5f, y,
                right.body.width() - 10f, rowH - 0.5f);
        add(button);
        return y + rowH;
    }

    private float fixedRow(float y, float rowH, String text, String itemId) {
        float x = right.body.left + 5f;
        float w = right.body.width() - 10f;
        float helpW = 13f;
        RenderedTextBlock value = t(text, 5, LedgerEnvironment.INK,
                Math.max(20, (int) (w - helpW - 2f)));
        value.setPos(x, y + 4f);
        add(value);

        if (itemId != null) {
            LedgerButton help = LedgerHelp.item(this, itemId);
            help.setRect(x + w - helpW, y, helpW, rowH - 0.5f);
            add(help);
        }
        return y + rowH;
    }

    private float clearRow(float y, float rowH, String text, final Runnable action) {
        LedgerButton button = new LedgerButton(Chrome.Type.BLANK, text, 5) {
            @Override protected void onClick() {
                super.onClick();
                if (action != null) action.run();
            }
        };
        button.leftJustify = true;
        button.textColor(LedgerEnvironment.FADED_INK);
        button.setRect(right.body.left + 5f, y,
                right.body.width() - 10f, rowH - 0.5f);
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
        return item == null ? "未知" : Messages.titleCase(item.trueName());
    }

    private void reload() {
        PixelScene.noFade = true;
        Game.switchScene(LedgerAccessoryScene.class);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
