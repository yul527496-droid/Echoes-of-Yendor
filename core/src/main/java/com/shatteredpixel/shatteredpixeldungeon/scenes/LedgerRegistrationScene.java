package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class LedgerRegistrationScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        LedgerPageGrid.Page left = LedgerEnvironment.leftGrid(book);
        LedgerPageGrid.Page right = LedgerEnvironment.rightGrid(book);

        buildEntry(left);
        buildNotes(right);
        fadeIn();
    }

    private void buildEntry(LedgerPageGrid.Page page) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock title = t("遗迹下行者登记", 8, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        RenderedTextBlock note = t("出发前记录", 4, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.14f,
                Math.min(page.header.bottom - 1f, note.bottom() + 4f), w * 0.72f, 0.36f));

        float bx = page.body.left + 2f;
        float bw = page.body.width() - 4f;
        float y = page.body.top + 4f;

        Image hero = new Image(LedgerFlow.draft().heroClass.spritesheet(), 0, 90, 12, 15);
        hero.scale.set(1.65f);
        hero.x = bx + 2f;
        hero.y = y;
        add(hero);

        float ix = hero.x + hero.width() + 7f;
        RenderedTextBlock classLabel = t("理想职业", 4, LedgerEnvironment.FADED_INK,
                (int) (page.body.right - ix));
        classLabel.setPos(ix, y);
        add(classLabel);

        RenderedTextBlock classValue = t(Messages.titleCase(LedgerFlow.draft().heroClass.title()),
                6, LedgerEnvironment.INK, (int) (page.body.right - ix));
        classValue.setPos(ix, classLabel.bottom() + 2f);
        add(classValue);

        y = Math.max(hero.y + hero.height(), classValue.bottom()) + 8f;
        add(LedgerPageGrid.rule(bx, y, bw, 0.24f));
        y += 5f;

        RenderedTextBlock nameLabel = t("姓名", 4, LedgerEnvironment.FADED_INK, 20);
        nameLabel.setPos(bx, y + 2f);
        add(nameLabel);

        String cur = LedgerFlow.draft().name;
        boolean unnamed = cur == null || cur.trim().isEmpty() || cur.equals("无名者");
        LedgerButton name = new LedgerButton(Chrome.Type.BLANK,
                unnamed ? "点击写下姓名" : cur, 5) {
            @Override protected void onClick() {
                super.onClick();
                String existing = LedgerFlow.draft().name;
                LedgerRegistrationScene.this.add(new WndTextInput(
                        "登记姓名",
                        "写下当年进入遗迹前留下的名字。",
                        existing == null || existing.equals("无名者") ? "" : existing,
                        20, false, "写入名册", "取消") {
                    @Override public void onSelect(boolean ok, String value) {
                        if (ok && value != null && !value.trim().isEmpty()) {
                            LedgerFlow.draft().name = value.trim();
                            PixelScene.noFade = true;
                            Game.switchScene(LedgerRegistrationScene.class);
                        }
                    }
                });
            }
            @Override protected String hoverText() { return "修改登记姓名"; }
        };
        name.leftJustify = true;
        name.textColor(unnamed ? LedgerEnvironment.FADED_INK : LedgerEnvironment.INK);
        name.setRect(bx + 22f, y - 1f, bw - 22f, 12f);
        add(name);

        y += 15f;
        RenderedTextBlock routeLabel = t("去向", 4, LedgerEnvironment.FADED_INK, 20);
        routeLabel.setPos(bx, y);
        add(routeLabel);
        RenderedTextBlock routeValue = t("地下遗迹", 5, LedgerEnvironment.INK, (int) bw - 22);
        routeValue.setPos(bx + 22f, y - 1f);
        add(routeValue);

        RenderedTextBlock signed = t("本人于下行前登记。", 4,
                LedgerEnvironment.FADED_INK, (int) bw);
        signed.setPos(bx, page.body.bottom - signed.height() - 3f);
        add(signed);

        add(LedgerPageGrid.rule(page.footer.left, page.footer.top + 1f,
                page.footer.width(), 0.22f));

        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "重新选择身份", 4) {
            @Override protected void onClick() {
                super.onClick();
                LedgerTransitions.turn(LedgerRegistrationScene.this,
                        page.paper, LedgerHeroScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(page.footer.left, page.footer.top + 3f,
                page.footer.width(), page.footer.height() - 3f);
        add(back);
    }

    private void buildNotes(LedgerPageGrid.Page page) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock title = t("登记原则", 8, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        RenderedTextBlock note = t("老板娘只记录当时能知道的事", 4,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.10f,
                Math.min(page.header.bottom - 1f, note.bottom() + 4f), w * 0.80f, 0.36f));

        float bx = page.body.left + 3f;
        float bw = page.body.width() - 6f;
        RenderedTextBlock rules = t(
                "写入这一页\n姓名\n理想职业\n惯用兵器\n去向\n\n不写入这一页\n后来专精\n最终战技\n归来后的经历",
                5, LedgerEnvironment.FADED_INK, (int) bw);
        rules.setPos(bx, page.body.top + 4f);
        add(rules);

        RenderedTextBlock warning = t("后来的事，留给归来者自己补上。", 4,
                LedgerEnvironment.STAMP, (int) bw);
        warning.setPos(bx, page.body.bottom - warning.height() - 3f);
        warning.alpha(0.78f);
        add(warning);

        add(LedgerPageGrid.rule(page.footer.left, page.footer.top + 1f,
                page.footer.width(), 0.28f));

        LedgerButton next = new LedgerButton(Chrome.Type.BLANK, "继续填写惯用兵器", 5) {
            @Override protected void onClick() {
                super.onClick();
                String n = LedgerFlow.draft().name;
                if (n == null || n.trim().isEmpty() || n.equals("无名者")) {
                    LedgerRegistrationScene.this.add(new WndMessage("先在名册上留下一个名字。"));
                    return;
                }
                LedgerTransitions.turn(LedgerRegistrationScene.this,
                        page.paper, LedgerWeaponScene.class, true);
            }
        };
        next.textColor(LedgerEnvironment.INK);
        next.setRect(page.footer.left, page.footer.top + 3f,
                page.footer.width(), page.footer.height() - 3f);
        add(next);
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
