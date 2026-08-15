package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildRules;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroTalentPlan;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroTalentRules;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/** Free allocation of the original four talent tiers for the returning hero. */
public class LedgerTalentScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();
        ensurePlanVersion();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildSummary();
        if (LedgerFlow.talentTier() == 0) buildTierOverview();
        else buildTierEditor(LedgerFlow.talentTier());

        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void ensurePlanVersion() {
        if (LedgerFlow.draft().talentPlan == null) {
            LedgerFlow.draft().talentPlan = new ReturningHeroTalentPlan();
        }
        // v6 changed artifact validation, not the talent economy itself.
        LedgerFlow.draft().talentPlan.rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
    }

    private void buildSummary() {
        float x = left.header.left;
        float w = left.header.width();
        RenderedTextBlock title = t("天赋旧录", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("照通关时的四阶天赋补写", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.13f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f), w * 0.74f, 0.36f));

        float bx = left.body.left + 5f;
        float bw = left.body.width() - 10f;
        float y = left.body.top + 7f;
        for (int tier = 1; tier <= Talent.MAX_TALENT_TIERS; tier++) {
            int spent = spent(tier);
            int budget = ReturningHeroTalentRules.tierBudget(tier);
            y = field(bx, bw, y, "第" + chineseTier(tier) + "阶",
                    spent + " / " + budget + (spent == budget ? "  已满" : ""));
        }

        boolean complete = isComplete();
        RenderedTextBlock status = t(complete ? "四阶记录已完整。" : "仍有天赋点尚未分配。",
                5, complete ? LedgerEnvironment.INK : LedgerEnvironment.FADED_INK,
                (int) bw);
        status.setPos(bx, Math.min(y + 3f, left.body.bottom - status.height() - 4f));
        add(status);

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));
        LedgerButton back = new LedgerButton(Chrome.Type.BLANK,
                LedgerFlow.talentReturnToBuild() ? "返回旧行装" : "返回专精战技", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerFlow.resetTalentPicker();
                returnFromTalent(left.paper);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildTierOverview() {
        header("自由天赋", "标准通关基线：5 / 6 / 8 / 10");
        float rowH = Math.min(22f, (right.body.height() - 4f) / 4f);
        float y = right.body.top + 2f;
        for (int tier = 1; tier <= Talent.MAX_TALENT_TIERS; tier++) {
            final int selectedTier = tier;
            int spent = spent(tier);
            int budget = ReturningHeroTalentRules.tierBudget(tier);
            String text = "第" + chineseTier(tier) + "阶天赋  ·  " + spent + " / " + budget;
            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, text, 6) {
                @Override protected void onClick() {
                    super.onClick();
                    LedgerFlow.talentTier(selectedTier);
                    reload();
                }
            };
            button.leftJustify = true;
            button.textColor(spent == budget ? LedgerEnvironment.INK : LedgerEnvironment.FADED_INK);
            button.setRect(right.body.left + 5f, y,
                    right.body.width() - 10f, rowH - 1f);
            add(button);
            y += rowH;
        }

        if (LedgerFlow.talentReturnToBuild()) {
            footerSingle("返回旧行装", new Runnable() {
                @Override public void run() {
                    LedgerFlow.resetTalentPicker();
                    LedgerTransitions.turn(LedgerTalentScene.this,
                            right.paper, LedgerBuildScene.class, false);
                }
            }, false);
        } else {
            footerTwo("返回专精战技", new Runnable() {
                @Override public void run() {
                    LedgerFlow.resetTalentPicker();
                    LedgerTransitions.turn(LedgerTalentScene.this,
                            right.paper, LedgerHeroPathScene.class, false);
                }
            }, "继续旧行装", new Runnable() {
                @Override public void run() {
                    if (!isComplete()) {
                        LedgerTalentScene.this.add(new WndMessage("请先把四阶天赋点全部分配完。"));
                        return;
                    }
                    LedgerFlow.resetTalentPicker();
                    LedgerTransitions.turn(LedgerTalentScene.this,
                            right.paper, LedgerBuildScene.class, true);
                }
            });
        }
    }

    private void buildTierEditor(final int tier) {
        ArrayList<LinkedHashMap<Talent, Integer>> legal = ReturningHeroTalentRules.legalTiers(
                LedgerFlow.draft().heroClass,
                LedgerFlow.draft().subClass(),
                LedgerFlow.draft().armorAbility());
        final LinkedHashMap<Talent, Integer> talents = legal.get(tier - 1);
        int remaining = ReturningHeroTalentRules.remaining(
                LedgerFlow.draft().talentPlan, tier,
                LedgerFlow.draft().heroClass,
                LedgerFlow.draft().subClass(),
                LedgerFlow.draft().armorAbility());
        header("第" + chineseTier(tier) + "阶天赋", "尚余 " + remaining + " 点");

        float rowH = Math.min(17f,
                (right.body.height() - 2f) / Math.max(1, talents.size()));
        float y = right.body.top + 1f;
        for (final Talent talent : talents.keySet()) {
            int points = LedgerFlow.draft().talentPlan.pointsIn(talent);
            int max = talent.maxPoints();
            float rowX = right.body.left + 4f;
            float rowW = right.body.width() - 8f;
            float sideW = 18f;
            float helpW = 13f;
            float helpGap = 1f;

            // ASCII '-' is intentional. The typographic U+2212 used previously
            // is absent from some Fusion Pixel builds and rendered as a missing
            // glyph on the actual Ledger screen.
            LedgerButton minus = new LedgerButton(Chrome.Type.BLANK, "-", 7) {
                @Override protected void onClick() {
                    super.onClick();
                    int current = LedgerFlow.draft().talentPlan.pointsIn(talent);
                    if (current > 0) {
                        LedgerFlow.draft().talentPlan.set(talent, current - 1);
                        LedgerAudio.write();
                        reload();
                    }
                }
            };
            minus.textColor(points > 0 ? LedgerEnvironment.INK : LedgerEnvironment.FADED_INK);
            minus.setRect(rowX, y, sideW, rowH - 0.5f);
            add(minus);

            float helpX = rowX + rowW - sideW - helpW - helpGap;
            float nameW = helpX - (rowX + sideW + 2f) - 2f;
            RenderedTextBlock name = t(talent.title() + "  " + points + "/" + max,
                    5, LedgerEnvironment.INK, Math.max(20, (int) nameW));
            name.setPos(rowX + sideW + 2f,
                    y + Math.max(1f, (rowH - name.height()) * 0.5f));
            add(name);

            LedgerButton help = LedgerHelp.talent(this, talent);
            help.setRect(helpX, y, helpW, rowH - 0.5f);
            add(help);

            LedgerButton plus = new LedgerButton(Chrome.Type.BLANK, "+", 7) {
                @Override protected void onClick() {
                    super.onClick();
                    int current = LedgerFlow.draft().talentPlan.pointsIn(talent);
                    int rem = ReturningHeroTalentRules.remaining(
                            LedgerFlow.draft().talentPlan, tier,
                            LedgerFlow.draft().heroClass,
                            LedgerFlow.draft().subClass(),
                            LedgerFlow.draft().armorAbility());
                    if (current >= talent.maxPoints()) return;
                    if (rem <= 0) {
                        LedgerTalentScene.this.add(new WndMessage("这一阶的天赋点已经分配完了。"));
                        return;
                    }
                    LedgerFlow.draft().talentPlan.set(talent, current + 1);
                    LedgerAudio.write();
                    reload();
                }
            };
            plus.textColor(points < max && remaining > 0
                    ? LedgerEnvironment.INK : LedgerEnvironment.FADED_INK);
            plus.setRect(rowX + rowW - sideW, y, sideW, rowH - 0.5f);
            add(plus);
            y += rowH;
        }

        footerTwo("返回阶层", new Runnable() {
            @Override public void run() {
                LedgerFlow.talentTier(0);
                reload();
            }
        }, "清空本阶", new Runnable() {
            @Override public void run() {
                for (Talent talent : talents.keySet()) {
                    LedgerFlow.draft().talentPlan.set(talent, 0);
                }
                LedgerAudio.erase();
                reload();
            }
        });
    }

    private boolean isComplete() {
        return ReturningHeroTalentRules.isComplete(
                LedgerFlow.draft().talentPlan,
                LedgerFlow.draft().heroClass,
                LedgerFlow.draft().subClass(),
                LedgerFlow.draft().armorAbility());
    }

    private void returnFromTalent(RectF paper) {
        Class<? extends PixelScene> target = LedgerFlow.talentReturnToBuild()
                ? LedgerBuildScene.class : LedgerHeroPathScene.class;
        LedgerTransitions.turn(LedgerTalentScene.this, paper, target, false);
    }

    private int spent(int tier) {
        return ReturningHeroTalentRules.spent(
                LedgerFlow.draft().talentPlan, tier,
                LedgerFlow.draft().heroClass,
                LedgerFlow.draft().subClass(),
                LedgerFlow.draft().armorAbility());
    }

    private void footerTwo(String leftText, final Runnable leftAction,
                           String rightText, final Runnable rightAction) {
        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.28f));
        float y = right.footer.top + 3f;
        float h = right.footer.height() - 3f;
        float half = right.footer.width() / 2f;

        LedgerButton leftButton = new LedgerButton(Chrome.Type.BLANK, leftText, 5) {
            @Override protected void onClick() {
                super.onClick();
                leftAction.run();
            }
        };
        leftButton.textColor(LedgerEnvironment.FADED_INK);
        leftButton.setRect(right.footer.left, y, half, h);
        add(leftButton);

        LedgerButton rightButton = new LedgerButton(Chrome.Type.BLANK, rightText, 5) {
            @Override protected void onClick() {
                super.onClick();
                rightAction.run();
            }
        };
        rightButton.textColor(LedgerEnvironment.FADED_INK);
        rightButton.setRect(right.footer.left + half, y, half, h);
        add(rightButton);
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

    private String chineseTier(int tier) {
        switch (tier) {
            case 1: return "一";
            case 2: return "二";
            case 3: return "三";
            case 4: return "四";
            default: return Integer.toString(tier);
        }
    }

    private void reload() {
        PixelScene.noFade = true;
        Game.switchScene(LedgerTalentScene.class);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
