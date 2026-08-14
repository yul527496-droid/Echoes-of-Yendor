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
        buildRegister(right);
        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildEntry(LedgerPageGrid.Page page) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock title = t("下行登记", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        RenderedTextBlock note = t("晨溪镇 · 老鸦旅店", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.14f,
                Math.min(page.header.bottom - 1f, note.bottom() + 4f), w * 0.72f, 0.36f));

        float bx = page.body.left + 3f;
        float bw = page.body.width() - 6f;
        float y = page.body.top + 4f;

        Image hero = new Image(LedgerFlow.draft().heroClass.spritesheet(), 0, 90, 12, 15);
        hero.scale.set(1.75f);
        hero.x = bx + 2f;
        hero.y = y;
        add(hero);

        float ix = hero.x + hero.width() + 7f;
        RenderedTextBlock classLabel = t("职业", 5, LedgerEnvironment.FADED_INK,
                (int) (page.body.right - ix));
        classLabel.setPos(ix, y);
        add(classLabel);

        RenderedTextBlock classValue = t(Messages.titleCase(LedgerFlow.draft().heroClass.title()),
                7, LedgerEnvironment.INK, (int) (page.body.right - ix));
        classValue.setPos(ix, classLabel.bottom() + 2f);
        add(classValue);

        y = Math.max(hero.y + hero.height(), classValue.bottom()) + 8f;
        add(LedgerPageGrid.rule(bx, y, bw, 0.24f));
        y += 5f;

        RenderedTextBlock nameLabel = t("姓名", 5, LedgerEnvironment.FADED_INK, 20);
        nameLabel.setPos(bx, y + 2f);
        add(nameLabel);

        String cur = LedgerFlow.draft().name;
        boolean unnamed = cur == null || cur.trim().isEmpty() || cur.equals("无名者");
        LedgerButton name = new LedgerButton(Chrome.Type.BLANK,
                unnamed ? "写下姓名" : cur, 6) {
            @Override protected void onClick() {
                super.onClick();
                String existing = LedgerFlow.draft().name;
                LedgerRegistrationScene.this.add(new WndTextInput(
                        "登记姓名",
                        "请报姓名。",
                        existing == null || existing.equals("无名者") ? "" : existing,
                        20, false, "写入", "取消") {
                    @Override public void onSelect(boolean ok, String value) {
                        if (ok && value != null && !value.trim().isEmpty()) {
                            LedgerFlow.draft().name = value.trim();
                            LedgerAudio.write();
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
        name.setRect(bx + 22f, y - 1f, bw - 22f, 13f);
        add(name);

        y += 17f;
        y = field(page, bx, bw, y, "去向", "地下遗迹");
        field(page, bx, bw, y, "归期", "未定");

        add(LedgerPageGrid.rule(page.footer.left, page.footer.top + 1f,
                page.footer.width(), 0.22f));

        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回职业栏", 5) {
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

    private float field(LedgerPageGrid.Page page, float x, float width, float y,
                        String labelText, String valueText) {
        RenderedTextBlock label = t(labelText, 5, LedgerEnvironment.FADED_INK, 20);
        label.setPos(x, y);
        add(label);
        RenderedTextBlock value = t(valueText, 6, LedgerEnvironment.INK, (int) width - 23);
        value.setPos(x + 23f, y - 1f);
        add(value);
        return Math.max(label.bottom(), value.bottom()) + 9f;
    }

    private void buildRegister(LedgerPageGrid.Page page) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock title = t("随身登记", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        RenderedTextBlock note = t("下行前核对", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.12f,
                Math.min(page.header.bottom - 1f, note.bottom() + 4f), w * 0.76f, 0.36f));

        float bx = page.body.left + 5f;
        float bw = page.body.width() - 10f;
        float y = page.body.top + 7f;
        String name = LedgerFlow.draft().name;
        if (name == null || name.trim().isEmpty() || name.equals("无名者")) name = "尚未填写";

        y = registerField(bx, bw, y, "姓名", name);
        y = registerField(bx, bw, y, "职业",
                Messages.titleCase(LedgerFlow.draft().heroClass.title()));
        y = registerField(bx, bw, y, "去向", "地下遗迹");
        y = registerField(bx, bw, y, "所习专精", "归来后补录");
        registerField(bx, bw, y, "英雄战技", "归来后补录");

        LedgerStamp keep = new LedgerStamp("旅店留档", 5);
        keep.setRect(page.body.right - 58f, page.body.bottom - 25f, 52f, 18f);
        keep.alpha(0.13f);
        add(keep);

        add(LedgerPageGrid.rule(page.footer.left, page.footer.top + 1f,
                page.footer.width(), 0.28f));

        LedgerButton next = new LedgerButton(Chrome.Type.BLANK, "补写归来专精与战技", 6) {
            @Override protected void onClick() {
                super.onClick();
                String n = LedgerFlow.draft().name;
                if (n == null || n.trim().isEmpty() || n.equals("无名者")) {
                    LedgerRegistrationScene.this.add(new WndMessage("请先留下姓名。"));
                    return;
                }
                LedgerFlow.heroPathReturnToBuild(false);
                LedgerTransitions.turn(LedgerRegistrationScene.this,
                        page.paper, LedgerHeroPathScene.class, true);
            }
        };
        next.textColor(LedgerEnvironment.INK);
        next.setRect(page.footer.left, page.footer.top + 3f,
                page.footer.width(), page.footer.height() - 3f);
        add(next);
    }

    private float registerField(float x, float width, float y,
                                String labelText, String valueText) {
        RenderedTextBlock label = t(labelText, 5, LedgerEnvironment.FADED_INK, 34);
        label.setPos(x, y);
        add(label);
        RenderedTextBlock value = t(valueText, 6, LedgerEnvironment.INK, (int) width - 39);
        value.setPos(x + 39f, y - 1f);
        add(value);
        return Math.max(label.bottom(), value.bottom()) + 10f;
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
