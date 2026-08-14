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
import com.watabou.utils.RectF;

public class LedgerReturnScene extends PixelScene {

    private ColorBlock[] subMarks;
    private ColorBlock[] abilityMarks;
    private ColorBlock[] growthMarks;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        RectF l = LedgerEnvironment.leftPage(book);
        RectF r = LedgerEnvironment.rightPage(book);
        float lx = l.left + 7;
        float lw = l.width() - 14;
        float rx = r.left + 7;
        float rw = r.width() - 14;
        float top = l.top + 7;

        RenderedTextBlock lh = t("名册记录止于此", 8, LedgerEnvironment.INK, (int) lw);
        lh.setPos(lx + (lw - lh.width()) / 2f, top);
        add(lh);

        RenderedTextBlock lead = t(
                "下面这些内容不是老板娘当年写下的。\n\n它们属于那个真正从地下归来的人。",
                5,
                LedgerEnvironment.FADED_INK,
                (int) lw);
        lead.setPos(lx + 2, lh.bottom() + 11);
        add(lead);

        Image hero = new Image(LedgerFlow.draft().heroClass.spritesheet(), 0, 90, 12, 15);
        hero.scale.set(2f);
        hero.x = lx + (lw - hero.width()) / 2f;
        hero.y = lead.bottom() + 12;
        add(hero);

        RenderedTextBlock who = t(
                LedgerFlow.draft().name + "\n" + Messages.titleCase(LedgerFlow.draft().heroClass.title()),
                6,
                LedgerEnvironment.INK,
                (int) lw);
        who.align(RenderedTextBlock.CENTER_ALIGN);
        who.setPos(lx + (lw - who.width()) / 2f, hero.y + hero.height() + 7);
        add(who);

        RenderedTextBlock stamp = t("未归", 7, LedgerEnvironment.STAMP, (int) lw);
        stamp.setPos(lx + (lw - stamp.width()) / 2f, Math.min(l.bottom - 29, who.bottom() + 9));
        add(stamp);

        RenderedTextBlock rh = t("归还者回忆", 8, LedgerEnvironment.INK, (int) rw);
        rh.setPos(rx + (rw - rh.width()) / 2f, top);
        add(rh);
        float y = rh.bottom() + 8;

        HeroSubClass[] subs = LedgerFlow.draft().heroClass.subClasses();
        RenderedTextBlock sl = t("后来专精", 5, LedgerEnvironment.FADED_INK, (int) rw);
        sl.setPos(rx, y);
        add(sl);
        y += 9;
        subMarks = row(rx, y, rw, names(subs), LedgerFlow.draft().subclassIndex, i -> {
            LedgerFlow.draft().subclassIndex = i;
            refresh(subMarks, i);
        });
        y += 22;

        ArmorAbility[] abilities = LedgerFlow.draft().heroClass.armorAbilities();
        RenderedTextBlock al = t("最终战技", 5, LedgerEnvironment.FADED_INK, (int) rw);
        al.setPos(rx, y);
        add(al);
        y += 9;
        abilityMarks = row(rx, y, rw, names(abilities), LedgerFlow.draft().abilityIndex, i -> {
            LedgerFlow.draft().abilityIndex = i;
            refresh(abilityMarks, i);
        });
        y += 22;

        ReturningHeroProfile.GrowthPreset[] growth = ReturningHeroProfile.GrowthPreset.values();
        RenderedTextBlock gl = t("天赋倾向", 5, LedgerEnvironment.FADED_INK, (int) rw);
        gl.setPos(rx, y);
        add(gl);
        y += 9;
        String[] growthNames = new String[growth.length];
        for (int i = 0; i < growth.length; i++) growthNames[i] = growth[i].title;
        growthMarks = row(rx, y, rw, growthNames, LedgerFlow.draft().growthPreset.ordinal(), i -> {
            LedgerFlow.draft().growthPreset = growth[i];
            refresh(growthMarks, i);
        });

        LedgerButton start = new LedgerButton(Chrome.Type.BLANK, "让这名归还者醒来  ›", 6) {
            @Override
            protected void onClick() {
                super.onClick();
                SequelGame.start(LedgerFlow.draft());
            }
        };
        start.textColor(LedgerEnvironment.INK);
        start.setRect(rx, r.bottom - 20, rw, 15);
        add(start);

        fadeIn();
    }

    private interface Pick {
        void choose(int i);
    }

    private ColorBlock[] row(float x, float y, float w, String[] labels, int selected, Pick pick) {
        int n = labels.length;
        float gap = 2;
        float bw = (w - gap * (n - 1)) / n;
        ColorBlock[] lines = new ColorBlock[n];
        for (int i = 0; i < n; i++) {
            final int choice = i;
            float bx = x + i * (bw + gap);
            LedgerButton b = new LedgerButton(Chrome.Type.BLANK, labels[i], 5) {
                @Override
                protected void onClick() {
                    super.onClick();
                    pick.choose(choice);
                }
            };
            b.setRect(bx, y, bw, 14);
            b.textColor(LedgerEnvironment.INK);
            add(b);

            ColorBlock line = new ColorBlock(bw, 1, 0x663B2A1E);
            line.x = bx;
            line.y = y + 15;
            line.alpha(i == selected ? 0.95f : 0.2f);
            lines[i] = line;
            add(line);
        }
        return lines;
    }

    private void refresh(ColorBlock[] lines, int selected) {
        if (lines == null) return;
        for (int i = 0; i < lines.length; i++) {
            lines[i].alpha(i == selected ? 0.95f : 0.2f);
        }
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
