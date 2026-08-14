package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroProfile;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

import java.util.ArrayList;

public class LedgerRecordsScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        RectF left = LedgerEnvironment.leftPage(book);
        RectF right = LedgerEnvironment.rightPage(book);
        float leftX = left.left + 6;
        float rightX = right.left + 6;
        float leftW = left.width() - 12;
        float rightW = right.width() - 12;
        float top = left.top + 6;

        RenderedTextBlock title = text("ECHOES OF YENDOR", 8, LedgerEnvironment.INK, (int) leftW);
        title.setPos(leftX + (leftW - title.width()) / 2f, top);
        add(title);

        RenderedTextBlock ledgerName = text("晨溪镇 · 老鸦旅店\n遗迹下行者登记簿", 6,
                LedgerEnvironment.FADED_INK, (int) leftW);
        ledgerName.align(RenderedTextBlock.CENTER_ALIGN);
        ledgerName.setPos(leftX + (leftW - ledgerName.width()) / 2f, title.bottom() + 5);
        add(ledgerName);

        RenderedTextBlock note = text("名字、理想与去向都留在这里。\n有些页，后来再也没人补写。", 5,
                LedgerEnvironment.FADED_INK, (int) leftW - 4);
        note.setPos(leftX + 2, ledgerName.bottom() + 12);
        add(note);

        LedgerButton settings = new LedgerButton(Chrome.Type.BLANK, "设置", 5) {
            @Override
            protected void onClick() {
                super.onClick();
                LedgerRecordsScene.this.add(new WndSettings());
            }
        };
        settings.textColor(LedgerEnvironment.FADED_INK);
        settings.setRect(leftX, left.bottom - 20, leftW / 2f - 2, 15);
        add(settings);

        LedgerButton about = new LedgerButton(Chrome.Type.BLANK, "制作信息", 5) {
            @Override
            protected void onClick() {
                super.onClick();
                Game.switchScene(AboutScene.class);
            }
        };
        about.textColor(LedgerEnvironment.FADED_INK);
        about.setRect(leftX + leftW / 2f + 2, left.bottom - 20, leftW / 2f - 2, 15);
        add(about);

        RenderedTextBlock recordsTitle = text("现存记录", 8, LedgerEnvironment.INK, (int) rightW);
        recordsTitle.setPos(rightX + (rightW - recordsTitle.width()) / 2f, top);
        add(recordsTitle);

        RenderedTextBlock recordsNote = text("翻回已经写过的那一页。", 5,
                LedgerEnvironment.FADED_INK, (int) rightW);
        recordsNote.setPos(rightX + (rightW - recordsNote.width()) / 2f, recordsTitle.bottom() + 4);
        add(recordsNote);

        ArrayList<GamesInProgress.Info> saves = GamesInProgress.checkAll();
        float y = recordsNote.bottom() + 7;

        if (saves.isEmpty()) {
            RenderedTextBlock empty = text("这一册还没有属于你的名字。", 5,
                    LedgerEnvironment.FADED_INK, (int) rightW - 4);
            empty.setPos(rightX + 2, y + 8);
            add(empty);
            y = empty.bottom() + 12;
        } else {
            int shown = 0;
            for (GamesInProgress.Info info : saves) {
                if (shown >= 4) break;

                final int slot = info.slot;
                final ReturningHeroProfile profile = ReturningHeroProfile.loadFromSlot(slot);
                String label = profile.name + "\n" + Messages.titleCase(info.heroClass.title()) + " · Lv." + info.level;

                LedgerButton record = new LedgerButton(Chrome.Type.BLANK, label, 6) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        GamesInProgress.curSlot = slot;
                        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                        Game.switchScene(InterlevelScene.class);
                    }
                };
                record.leftJustify = true;
                record.multiline = true;
                record.textColor(LedgerEnvironment.INK);
                record.setRect(rightX, y, rightW - 31, 25);
                add(record);

                RenderedTextBlock stamp = text("未归", 6, LedgerEnvironment.STAMP, 28);
                stamp.setPos(right.right - stamp.width() - 8, y + 3);
                add(stamp);

                LedgerButton erase = new LedgerButton(Chrome.Type.BLANK, "删除", 5) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        LedgerRecordsScene.this.add(new WndOptions(
                                "删除记录",
                                "要从名册中移除「" + profile.name + "」吗？\n这会同时删除对应存档。",
                                "删除",
                                "取消") {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    Dungeon.deleteGame(slot, true);
                                    Game.switchScene(LedgerRecordsScene.class);
                                }
                            }
                        });
                    }
                };
                erase.textColor(LedgerEnvironment.STAMP);
                erase.setRect(right.right - 34, y + 11, 27, 13);
                add(erase);

                y += 29;
                shown++;
            }
        }

        // Keep ledger actions inside the ledger visual language. The previous
        // TOAST_WHITE chrome was the large white bar visible in screenshots.
        LedgerButton newRecord = new LedgerButton(Chrome.Type.BLANK, "＋ 登记新的下行者", 6) {
            @Override
            protected void onClick() {
                super.onClick();
                if (GamesInProgress.firstEmpty() < 0) return;
                LedgerFlow.resetDraft();
                Game.switchScene(LedgerHeroScene.class);
            }
        };
        newRecord.textColor(LedgerEnvironment.INK);
        newRecord.setRect(rightX, Math.min(right.bottom - 20, y + 4), rightW, 16);
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
