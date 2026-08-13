package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroProfile;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.utils.RectF;

import java.util.ArrayList;

public class LedgerRecordsScene extends PixelScene {

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

        RenderedTextBlock title = text("ECHOES OF YENDOR", 11, LedgerEnvironment.INK, (int) pageW - 14);
        title.setPos(leftX + (pageW - title.width()) / 2f, top);
        add(title);

        RenderedTextBlock ledgerName = text("晨溪镇 · 老鸦旅店\n遗迹下行者登记簿", 7,
                LedgerEnvironment.FADED_INK, (int) pageW - 18);
        ledgerName.align(RenderedTextBlock.CENTER_ALIGN);
        ledgerName.setPos(leftX + (pageW - ledgerName.width()) / 2f, title.bottom() + 6);
        add(ledgerName);

        ColorBlock rule = new ColorBlock(pageW - 22, 1, 0x663B2A1E);
        rule.x = leftX + 11;
        rule.y = ledgerName.bottom() + 8;
        add(rule);

        RenderedTextBlock note = text("姓名写在这里。\n理想写在这里。\n去向也写在这里。\n\n但不是每一页都会等到第二笔。", 6,
                LedgerEnvironment.FADED_INK, (int) pageW - 24);
        note.setPos(leftX + 12, rule.y + 10);
        add(note);

        StyledButton settings = new StyledButton(Chrome.Type.BLANK, "设置", 6) {
            @Override
            protected void onClick() {
                super.onClick();
                LedgerRecordsScene.this.add(new WndSettings());
            }
        };
        settings.textColor(LedgerEnvironment.FADED_INK);
        settings.setRect(leftX + 9, h - safe.bottom - 42, (pageW - 22) / 2f, 17);
        add(settings);

        StyledButton about = new StyledButton(Chrome.Type.BLANK, "制作信息", 6) {
            @Override
            protected void onClick() {
                super.onClick();
                Game.switchScene(AboutScene.class);
            }
        };
        about.textColor(LedgerEnvironment.FADED_INK);
        about.setRect(settings.right() + 4, settings.top(), (pageW - 22) / 2f, 17);
        add(about);

        RenderedTextBlock recordsTitle = text("现存记录", 10, LedgerEnvironment.INK, (int) pageW - 14);
        recordsTitle.setPos(rightX + (pageW - recordsTitle.width()) / 2f, top);
        add(recordsTitle);

        RenderedTextBlock recordsNote = text("翻回已经写过的那一页。", 6,
                LedgerEnvironment.FADED_INK, (int) pageW - 18);
        recordsNote.setPos(rightX + (pageW - recordsNote.width()) / 2f, recordsTitle.bottom() + 5);
        add(recordsNote);

        final ArrayList<GamesInProgress.Info> saves = GamesInProgress.checkAll();
        float y = recordsNote.bottom() + 8;

        if (saves.isEmpty()) {
            RenderedTextBlock empty = text("这一册还没有属于你的名字。", 6,
                    LedgerEnvironment.FADED_INK, (int) pageW - 22);
            empty.setPos(rightX + 11, y + 8);
            add(empty);
            y = empty.bottom() + 18;
        } else {
            int shown = 0;
            for (GamesInProgress.Info info : saves) {
                if (shown >= 4) break;
                final int slot = info.slot;
                ReturningHeroProfile profile = ReturningHeroProfile.loadFromSlot(slot);
                String label = profile.name + "\n" + Messages.titleCase(info.heroClass.title()) + " · Lv." + info.level;

                StyledButton record = new StyledButton(Chrome.Type.BLANK, label, 7) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        GamesInProgress.curSlot = slot;
                        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                        Game.switchScene(InterlevelScene.class);
                    }
                };
                record.leftJustify = true;
                record.textColor(LedgerEnvironment.INK);
                record.setRect(rightX + 9, y, pageW - 18, 29);
                add(record);

                RenderedTextBlock stamp = text("未归", 7, LedgerEnvironment.STAMP, 36);
                stamp.setPos(record.right() - stamp.width() - 4, record.top() + 10);
                add(stamp);

                ColorBlock recordRule = new ColorBlock(pageW - 18, 1, 0x443B2A1E);
                recordRule.x = rightX + 9;
                recordRule.y = record.bottom();
                add(recordRule);

                y = record.bottom() + 4;
                shown++;
            }
        }

        StyledButton newRecord = new StyledButton(Chrome.Type.TOAST_WHITE, "＋  登记新的下行者", 7) {
            @Override
            protected void onClick() {
                super.onClick();
                if (GamesInProgress.firstEmpty() < 0) return;
                LedgerFlow.resetDraft();
                Game.switchScene(LedgerHeroScene.class);
            }
        };
        newRecord.textColor(LedgerEnvironment.INK);
        newRecord.setRect(rightX + 9, Math.min(h - safe.bottom - 29, y + 6), pageW - 18, 21);
        add(newRecord);

        fadeIn();
    }

    private RenderedTextBlock text(String value, int size, int color, int width) {
        RenderedTextBlock block = PixelScene.renderTextBlock(value, size);
        block.maxWidth(width);
        block.hardlight(color);
        return block;
    }
}
