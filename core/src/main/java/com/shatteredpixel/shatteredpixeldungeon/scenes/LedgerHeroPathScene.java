package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroTalentRules;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Image;

/** Explicit returning-hero path step: subclass first, then armor ability. */
public class LedgerHeroPathScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;
    private LedgerButton[] subclassButtons;
    private LedgerButton[] abilityButtons;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildSubclassPage();
        buildAbilityPage();
        refreshSelections();

        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildSubclassPage() {
        header(left, "所习专精", "第三阶天赋由此决定");

        HeroSubClass[] subclasses = LedgerFlow.draft().heroClass.subClasses();
        subclassButtons = new LedgerButton[subclasses.length];

        float x = left.body.left + 5f;
        float w = left.body.width() - 10f;
        float gap = 4f;
        float rowH = Math.min(29f,
                (left.body.height() - gap * Math.max(0, subclasses.length - 1))
                        / Math.max(1, subclasses.length));
        float y = left.body.top + 2f;

        for (int i = 0; i < subclasses.length; i++) {
            final int index = i;
            HeroSubClass subClass = subclasses[i];
            LedgerButton button = new LedgerButton(Chrome.Type.BLANK,
                    Messages.titleCase(subClass.title()), 6) {
                @Override protected void onClick() {
                    super.onClick();
                    chooseSubclass(index);
                }
            };
            button.multiline = true;
            button.textColor(LedgerEnvironment.INK);
            button.setRect(x, y, w, rowH);
            subclassButtons[i] = button;
            add(button);
            y += rowH + gap;
        }

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));
        LedgerButton back = new LedgerButton(Chrome.Type.BLANK,
                LedgerFlow.heroPathReturnToBuild() ? "返回旧行装" : "返回登记页", 5) {
            @Override protected void onClick() {
                super.onClick();
                Class<? extends PixelScene> target = LedgerFlow.heroPathReturnToBuild()
                        ? LedgerBuildScene.class : LedgerRegistrationScene.class;
                LedgerTransitions.turn(LedgerHeroPathScene.this,
                        left.paper, target, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildAbilityPage() {
        header(right, "英雄战技", "第四阶天赋由此决定");

        ArmorAbility[] abilities = LedgerFlow.draft().heroClass.armorAbilities();
        abilityButtons = new LedgerButton[abilities.length];

        float x = right.body.left + 5f;
        float w = right.body.width() - 10f;
        float gap = 3f;
        float rowH = Math.min(23f,
                (right.body.height() - gap * Math.max(0, abilities.length - 1))
                        / Math.max(1, abilities.length));
        float y = right.body.top + 2f;

        for (int i = 0; i < abilities.length; i++) {
            final int index = i;
            LedgerButton button = new LedgerButton(Chrome.Type.BLANK,
                    abilities[i].name(), 6) {
                @Override protected void onClick() {
                    super.onClick();
                    chooseAbility(index);
                }
            };
            button.multiline = true;
            button.textColor(LedgerEnvironment.INK);
            button.setRect(x, y, w, rowH);
            abilityButtons[i] = button;
            add(button);
            y += rowH + gap;
        }

        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.28f));
        LedgerButton next = new LedgerButton(Chrome.Type.BLANK, "继续核对自由天赋", 6) {
            @Override protected void onClick() {
                super.onClick();
                LedgerFlow.resetTalentPicker();
                LedgerFlow.talentReturnToBuild(false);
                LedgerTransitions.turn(LedgerHeroPathScene.this,
                        right.paper, LedgerTalentScene.class, true);
            }
        };
        next.textColor(LedgerEnvironment.INK);
        next.setRect(right.footer.left, right.footer.top + 3f,
                right.footer.width(), right.footer.height() - 3f);
        add(next);
    }

    private void chooseSubclass(int index) {
        if (index == LedgerFlow.draft().subclassIndex) return;

        HeroClass heroClass = LedgerFlow.draft().heroClass;
        HeroSubClass oldSubClass = LedgerFlow.draft().subClass();
        ArmorAbility ability = LedgerFlow.draft().armorAbility();

        LedgerFlow.draft().subclassIndex = index;
        HeroSubClass newSubClass = LedgerFlow.draft().subClass();
        ReturningHeroTalentRules.reconcileChangedTier(
                LedgerFlow.draft().talentPlan, 3,
                heroClass, oldSubClass, ability,
                heroClass, newSubClass, ability);

        LedgerAudio.write();
        refreshSelections();
    }

    private void chooseAbility(int index) {
        if (index == LedgerFlow.draft().abilityIndex) return;

        HeroClass heroClass = LedgerFlow.draft().heroClass;
        HeroSubClass subClass = LedgerFlow.draft().subClass();
        ArmorAbility oldAbility = LedgerFlow.draft().armorAbility();

        LedgerFlow.draft().abilityIndex = index;
        ArmorAbility newAbility = LedgerFlow.draft().armorAbility();
        ReturningHeroTalentRules.reconcileChangedTier(
                LedgerFlow.draft().talentPlan, 4,
                heroClass, subClass, oldAbility,
                heroClass, subClass, newAbility);

        LedgerAudio.write();
        refreshSelections();
    }

    private void refreshSelections() {
        if (subclassButtons != null) {
            for (int i = 0; i < subclassButtons.length; i++) {
                subclassButtons[i].setSelected(i == LedgerFlow.draft().subclassIndex);
            }
        }
        if (abilityButtons != null) {
            for (int i = 0; i < abilityButtons.length; i++) {
                abilityButtons[i].setSelected(i == LedgerFlow.draft().abilityIndex);
            }
        }
    }

    private void header(LedgerPageGrid.Page page, String titleText, String noteText) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock title = t(titleText, 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        RenderedTextBlock note = t(noteText, 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.10f,
                Math.min(page.header.bottom - 1f, note.bottom() + 4f),
                w * 0.80f, 0.36f));
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
