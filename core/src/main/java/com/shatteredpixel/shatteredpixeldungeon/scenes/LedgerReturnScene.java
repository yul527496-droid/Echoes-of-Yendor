package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroProfile;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;

public class LedgerReturnScene extends PixelScene {

    private ColorBlock[] subMarks;
    private ColorBlock[] abilityMarks;
    private ColorBlock[] growthMarks;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();
        Image book = LedgerEnvironment.addOpenBook(this);
        LedgerPageGrid.Page left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);
        buildReturnedHero(left);
        buildConfiguration();
        fadeIn();
    }

    private void buildReturnedHero(LedgerPageGrid.Page page) {
        float x = page.header.left;
        float w = page.header.width();
        RenderedTextBlock title = t("名册记录止于此", 8, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);
        RenderedTextBlock note = t("下面属于真正归来的人", 4, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.12f,
                Math.min(page.header.bottom - 1f, note.bottom() + 4f), w * 0.76f, 0.34f));

        Image hero = new Image(LedgerFlow.draft().heroClass.spritesheet(), 0, 90, 12, 15);
        hero.scale.set(2.15f);
        hero.x = page.body.left + (page.body.width() - hero.width()) / 2f;
        hero.y = page.body.top + 7f;
        add(hero);

        RenderedTextBlock who = t(LedgerFlow.draft().name + "\n"
                        + Messages.titleCase(LedgerFlow.draft().heroClass.title()),
                6, LedgerEnvironment.INK, (int) page.body.width());
        who.align(RenderedTextBlock.CENTER_ALIGN);
        who.setPos(page.body.left + (page.body.width() - who.width()) / 2f,
                hero.y + hero.height() + 5f);
        add(who);

        RenderedTextBlock stamp = t("未归", 7, LedgerEnvironment.STAMP, (int) page.body.width());
        stamp.align(RenderedTextBlock.CENTER_ALIGN);
        stamp.setPos(page.body.left + (page.body.width() - stamp.width()) / 2f,
                Math.min(page.body.bottom - stamp.height() - 6f, who.bottom() + 7f));
        stamp.alpha(0.82f);
        add(stamp);

        RenderedTextBlock lead = t("老板娘的笔停在这里。\n之后的经历，由你来补完。", 4,
                LedgerEnvironment.FADED_INK, (int) page.body.width() - 6);
        lead.align(RenderedTextBlock.CENTER_ALIGN);
        lead.setPos(page.body.left + (page.body.width() - lead.width()) / 2f,
                page.body.bottom - lead.height() - 2f);
        add(lead);
    }

    private void buildConfiguration() {
        float x = right.header.left;
        float w = right.header.width();
        RenderedTextBlock title = t("归还者配置", 8, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, right.header.top);
        add(title);
        RenderedTextBlock note = t("补上地下冒险真正留下的结果", 4,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.08f,
                Math.min(right.header.bottom - 1f, note.bottom() + 4f), w * 0.84f, 0.36f));

        float sectionGap = 3f;
        float sectionH = (right.body.height() - sectionGap * 2f) / 3f;
        float y = right.body.top;

        HeroSubClass[] subs = LedgerFlow.draft().heroClass.subClasses();
        subMarks = section("后来专精", y, sectionH, names(subs),
                LedgerFlow.draft().subclassIndex, i -> {
                    LedgerFlow.draft().subclassIndex = i;
                    refresh(subMarks, i);
                });
        y += sectionH + sectionGap;

        ArmorAbility[] abilities = LedgerFlow.draft().heroClass.armorAbilities();
        abilityMarks = section("最终战技", y, sectionH, names(abilities),
                LedgerFlow.draft().abilityIndex, i -> {
                    LedgerFlow.draft().abilityIndex = i;
                    refresh(abilityMarks, i);
                });
        y += sectionH + sectionGap;

        ReturningHeroProfile.GrowthPreset[] growth = ReturningHeroProfile.GrowthPreset.values();
        String[] growthNames = new String[growth.length];
        for (int i = 0; i < growth.length; i++) growthNames[i] = growth[i].title;
        growthMarks = section("天赋倾向", y, sectionH, growthNames,
                LedgerFlow.draft().growthPreset.ordinal(), i -> {
                    LedgerFlow.draft().growthPreset = growth[i];
                    refresh(growthMarks, i);
                });

        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.30f));
        LedgerButton start = new LedgerButton(Chrome.Type.BLANK, "让这名归还者醒来", 5) {
            @Override protected void onClick() {
                super.onClick();
                SequelGame.start(LedgerFlow.draft());
            }
        };
        start.textColor(LedgerEnvironment.INK);
        start.setRect(right.footer.left, right.footer.top + 3f,
                right.footer.width(), right.footer.height() - 3f);
        add(start);
    }

    private interface Pick { void choose(int i); }

    private ColorBlock[] section(String label, float y, float h,
                                 String[] labels, int selected, Pick pick) {
        RenderedTextBlock sectionLabel = t(label, 4,
                LedgerEnvironment.FADED_INK, (int) right.body.width());
        sectionLabel.setPos(right.body.left, y);
        add(sectionLabel);
        float optionY = y + 7f;
        float optionH = Math.max(8f, h - 8f);
        int n = labels.length;
        float gap = 2f;
        float bw = (right.body.width() - gap * (n - 1)) / n;
        ColorBlock[] lines = new ColorBlock[n];
        for (int i = 0; i < n; i++) {
            final int choice = i;
            float bx = right.body.left + i * (bw + gap);
            LedgerButton button = new LedgerButton(Chrome.Type.BLANK, labels[i], 4) {
                @Override protected void onClick() {
                    super.onClick();
                    pick.choose(choice);
                }
            };
            button.multiline = true;
            button.setRect(bx, optionY, bw, optionH - 1f);
            button.textColor(LedgerEnvironment.INK);
            add(button);
            ColorBlock line = new ColorBlock(bw - 1f, 1f, 0xFF9B302C);
            line.x = bx;
            line.y = optionY + optionH - 1f;
            line.alpha(i == selected ? 0.80f : 0.10f);
            lines[i] = line;
            add(line);
        }
        return lines;
    }

    private void refresh(ColorBlock[] lines, int selected) {
        if (lines == null) return;
        for (int i = 0; i < lines.length; i++) lines[i].alpha(i == selected ? 0.80f : 0.10f);
    }

    private String[] names(HeroSubClass[] values) {
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) names[i] = Messages.titleCase(values[i].title());
        return names;
    }

    private String[] names(ArmorAbility[] values) {
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) names[i] = values[i].name();
        return names;
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
