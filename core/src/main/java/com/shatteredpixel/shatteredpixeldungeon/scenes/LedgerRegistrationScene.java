package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class LedgerRegistrationScene extends PixelScene {

    @Override
    public void create() {
        super.create();
        uiCamera.visible = false;
        LedgerEnvironment.addOpenBook(this);

        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF safe = getCommonInsets();
        float usableW = w - safe.left - safe.right;
        float bookW = Math.min(usableW - 10, 360);
        float gap = Math.max(7, bookW * 0.035f);
        float pageW = (bookW - gap) / 2f;
        float bookX = safe.left + (usableW - bookW) / 2f;
        float leftX = bookX;
        float rightX = bookX + pageW + gap;
        float top = safe.top + Math.max(10, (h - safe.top - safe.bottom) * 0.12f);

        RenderedTextBlock header = text("遗迹下行者登记", 10, LedgerEnvironment.INK, (int) pageW - 14);
        header.setPos(leftX + (pageW - header.width()) / 2f, top);
        add(header);

        Image hero = new Image(LedgerFlow.draft().heroClass.spritesheet(), 0, 90, 12, 15);
        hero.scale.set(2f);
        hero.x = leftX + 12;
        hero.y = header.bottom() + 12;
        add(hero);

        RenderedTextBlock idealLabel = text("理想职业", 6, LedgerEnvironment.FADED_INK, 80);
        idealLabel.setPos(hero.right() + 8, hero.y + 1);
        add(idealLabel);

        RenderedTextBlock ideal = text(Messages.titleCase(LedgerFlow.draft().heroClass.title()), 8,
                LedgerEnvironment.INK, 90);
        ideal.setPos(hero.right() + 8, idealLabel.bottom() + 2);
        add(ideal);

        float formY = hero.y + 42;
        ColorBlock line1 = new ColorBlock(pageW - 20, 1, 0x553B2A1E);
        line1.x = leftX + 10;
        line1.y = formY;
        add(line1);

        RenderedTextBlock nameLabel = text("姓名", 6, LedgerEnvironment.FADED_INK, 40);
        nameLabel.setPos(leftX + 11, formY + 7);
        add(nameLabel);

        String currentName = LedgerFlow.draft().name;
        boolean unnamed = currentName == null || currentName.trim().isEmpty() || currentName.equals("无名者");
        StyledButton name = new StyledButton(Chrome.Type.BLANK,
                unnamed ? "点击这里写下姓名" : currentName, 8) {
            @Override
            protected void onClick() {
                super.onClick();
                LedgerRegistrationScene.this.add(new WndTextInput(
                        "登记姓名", "写下当年进入遗迹前留下的名字。",
                        LedgerFlow.draft().name.equals("无名者") ? "" : LedgerFlow.draft().name,
                        20, false, "写入名册", "取消") {
                    @Override
                    public void onSelect(boolean ok, String value) {
                        if (ok && value != null && !value.trim().isEmpty()) {
                            LedgerFlow.draft().name = value.trim();
                            Game.switchScene(LedgerRegistrationScene.class);
                        }
                    }
                });
            }
        };
        name.leftJustify = true;
        name.textColor(unnamed ? LedgerEnvironment.FADED_INK : LedgerEnvironment.INK);
        name.setRect(leftX + 48, formY + 1, pageW - 60, 22);
        add(name);

        ColorBlock line2 = new ColorBlock(pageW - 20, 1, 0x553B2A1E);
        line2.x = leftX + 10;
        line2.y = formY + 30;
        add(line2);

        RenderedTextBlock destinationLabel = text("去向", 6, LedgerEnvironment.FADED_INK, 40);
        destinationLabel.setPos(leftX + 11, line2.y + 7);
        add(destinationLabel);

        RenderedTextBlock destination = text("地下遗迹", 8, LedgerEnvironment.INK, 90);
        destination.setPos(leftX + 50, line2.y + 6);
        add(destination);

        RenderedTextBlock rightHeader = text("登记说明", 10, LedgerEnvironment.INK, (int) pageW - 14);
        rightHeader.setPos(rightX + (pageW - rightHeader.width()) / 2f, top);
        add(rightHeader);

        RenderedTextBlock note = text(
                "这本名册只记录下行之前能够知道的事。\n\n姓名。\n理想职业。\n惯用兵器。\n去向。\n\n后来专精、最终战技与天赋，\n都不属于这一页。",
                6, LedgerEnvironment.FADED_INK, (int) pageW - 24);
        note.setPos(rightX + 12, rightHeader.bottom() + 12);
        add(note);

        StyledButton next = new StyledButton(Chrome.Type.TOAST_WHITE, "继续填写惯用兵器 ›", 7) {
            @Override
            protected void onClick() {
                super.onClick();
                String n = LedgerFlow.draft().name;
                if (n == null || n.trim().isEmpty() || n.equals("无名者")) {
                    LedgerRegistrationScene.this.add(new WndMessage("先在名册上留下一个名字。"));
                    return;
                }
                Game.switchScene(LedgerWeaponScene.class);
            }
        };
        next.textColor(LedgerEnvironment.INK);
        next.setRect(rightX + 9, h - safe.bottom - 31, pageW - 18, 21);
        add(next);

        StyledButton back = new StyledButton(Chrome.Type.BLANK, "‹ 重新选择身份", 6) {
            @Override
            protected void onClick() {
                super.onClick();
                Game.switchScene(LedgerHeroScene.class);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(leftX + 9, h - safe.bottom - 25, pageW - 18, 17);
        add(back);

        fadeIn();
    }

    private RenderedTextBlock text(String value, int size, int color, int width) {
        RenderedTextBlock block = PixelScene.renderTextBlock(value, size);
        block.maxWidth(width);
        block.hardlight(color);
        return block;
    }
}
