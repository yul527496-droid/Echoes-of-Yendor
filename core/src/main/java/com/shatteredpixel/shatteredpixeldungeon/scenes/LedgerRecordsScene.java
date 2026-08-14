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
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

import java.util.ArrayList;

/** The ledger's stable home page for both empty and existing save lists. */
public class LedgerRecordsScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        RectF left = LedgerEnvironment.leftPage(book);
        RectF right = LedgerEnvironment.rightPage(book);

        float leftX = left.left + 5f;
        float rightX = right.left + 5f;
        float leftW = left.width() - 10f;
        float rightW = right.width() - 10f;
        float top = left.top + 5f;

        // Left page: identity and atmosphere first. English is a quiet imprint,
        // not the dominant headline fighting with Chinese text.
        RenderedTextBlock imprint = text("ECHOES OF YENDOR", 5,
                LedgerEnvironment.FADED_INK, (int) leftW);
        imprint.align(RenderedTextBlock.CENTER_ALIGN);
        imprint.setPos(leftX + (leftW - imprint.width()) / 2f, top);
        add(imprint);

        RenderedTextBlock ledgerName = text("遗迹下行者登记簿", 8,
                LedgerEnvironment.INK, (int) leftW);
        ledgerName.align(RenderedTextBlock.CENTER_ALIGN);
        ledgerName.setPos(leftX + (leftW - ledgerName.width()) / 2f, imprint.bottom() + 5f);
        add(ledgerName);

        RenderedTextBlock inn = text("晨溪镇 · 老鸦旅店", 5,
                LedgerEnvironment.FADED_INK, (int) leftW);
        inn.align(RenderedTextBlock.CENTER_ALIGN);
        inn.setPos(leftX + (leftW - inn.width()) / 2f, ledgerName.bottom() + 3f);
        add(inn);

        ColorBlock leftRule = rule(leftW * 0.62f);
        leftRule.x = leftX + (leftW - leftRule.width()) / 2f;
        leftRule.y = inn.bottom() + 6f;
        add(leftRule);

        RenderedTextBlock note = text(
                "名字、理想与去向留在这里。\n有些页，后来再也没人补写。",
                5, LedgerEnvironment.FADED_INK, (int) leftW - 8);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(leftX + (leftW - note.width()) / 2f, leftRule.y + 7f);
        add(note);

        LedgerButton settings = new LedgerButton(Chrome.Type.BLANK, "设置", 5) {
            @Override
            protected void onClick() {
                super.onClick();
                LedgerRecordsScene.this.add(new WndSettings());
            }
        };
        settings.textColor(LedgerEnvironment.FADED_INK);
        settings.setRect(leftX + 2f, left.bottom - 16f, leftW / 2f - 3f, 12f);
        add(settings);

        LedgerButton about = new LedgerButton(Chrome.Type.BLANK, "制作信息", 5) {
            @Override
            protected void onClick() {
                super.onClick();
                Game.switchScene(AboutScene.class);
            }
        };
        about.textColor(LedgerEnvironment.FADED_INK);
        about.setRect(leftX + leftW / 2f + 1f, left.bottom - 16f, leftW / 2f - 3f, 12f);
        add(about);

        // Right page: compact archive rows. Everything stays inside the paper.
        RenderedTextBlock recordsTitle = text("现存记录", 8,
                LedgerEnvironment.INK, (int) rightW);
        recordsTitle.align(RenderedTextBlock.CENTER_ALIGN);
        recordsTitle.setPos(rightX + (rightW - recordsTitle.width()) / 2f, top);
        add(recordsTitle);

        RenderedTextBlock recordsNote = text("翻回已经写过的那一页", 5,
                LedgerEnvironment.FADED_INK, (int) rightW);
        recordsNote.align(RenderedTextBlock.CENTER_ALIGN);
        recordsNote.setPos(rightX + (rightW - recordsNote.width()) / 2f,
                recordsTitle.bottom() + 3f);
        add(recordsNote);

        float y = recordsNote.bottom() + 6f;
        float footerY = right.bottom - 16f;
        ArrayList<GamesInProgress.Info> saves = GamesInProgress.checkAll();

        if (saves.isEmpty()) {
            RenderedTextBlock empty = text("这一册还没有属于你的名字。", 5,
                    LedgerEnvironment.FADED_INK, (int) rightW - 8);
            empty.align(RenderedTextBlock.CENTER_ALIGN);
            empty.setPos(rightX + (rightW - empty.width()) / 2f, y + 10f);
            add(empty);
        } else {
            int shown = 0;
            for (GamesInProgress.Info info : saves) {
                if (shown >= 4 || y + 14f > footerY) break;

                final int slot = info.slot;
                final ReturningHeroProfile profile = ReturningHeroProfile.loadFromSlot(slot);
                String heroName = profile.name == null || profile.name.trim().isEmpty()
                        ? "无名者" : profile.name;
                String label = heroName + "  ·  "
                        + Messages.titleCase(info.heroClass.title()) + "  Lv." + info.level;

                LedgerButton record = new LedgerButton(Chrome.Type.BLANK, label, 5) {
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
                record.setRect(rightX + 1f, y, rightW - 29f, 12f);
                add(record);

                RenderedTextBlock stamp = text("未归", 5, LedgerEnvironment.STAMP, 22);
                stamp.setPos(rightX + rightW - stamp.width() - 2f, y);
                add(stamp);

                LedgerButton erase = new LedgerButton(Chrome.Type.BLANK, "删除", 4) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        LedgerRecordsScene.this.add(new WndOptions(
                                "删除记录",
                                "要从名册中移除「" + heroName + "」吗？\n这会同时删除对应存档。",
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
                erase.setRect(rightX + rightW - 23f, y + 6f, 22f, 8f);
                add(erase);

                ColorBlock rowRule = rule(rightW - 2f);
                rowRule.x = rightX + 1f;
                rowRule.y = y + 14f;
                rowRule.alpha(0.34f);
                add(rowRule);

                y += 15f;
                shown++;
            }
        }

        LedgerButton newRecord = new LedgerButton(Chrome.Type.BLANK, "＋ 登记新的下行者", 5) {
            @Override
            protected void onClick() {
                super.onClick();
                if (GamesInProgress.firstEmpty() < 0) return;
                LedgerFlow.resetDraft();
                Game.switchScene(LedgerHeroScene.class);
            }
        };
        newRecord.textColor(LedgerEnvironment.INK);
        newRecord.setRect(rightX + 2f, footerY, rightW - 4f, 12f);
        add(newRecord);

        fadeIn();
    }

    private ColorBlock rule(float width) {
        ColorBlock line = new ColorBlock(width, 1f, 0xFF000000 | LedgerEnvironment.RULE);
        line.alpha(0.48f);
        return line;
    }

    private RenderedTextBlock text(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
