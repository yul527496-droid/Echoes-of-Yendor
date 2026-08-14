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

import java.util.ArrayList;

/** The ledger's stable home page for both empty and existing save lists. */
public class LedgerRecordsScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        LedgerPageGrid.Page left = LedgerEnvironment.leftGrid(book);
        LedgerPageGrid.Page right = LedgerEnvironment.rightGrid(book);

        buildIdentityPage(left);
        buildRecordsPage(right);

        fadeIn();
    }

    private void buildIdentityPage(LedgerPageGrid.Page page) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock imprint = text("ECHOES OF YENDOR", 4,
                LedgerEnvironment.FADED_INK, (int) w);
        imprint.align(RenderedTextBlock.CENTER_ALIGN);
        imprint.setPos(x + (w - imprint.width()) / 2f, page.header.top);
        add(imprint);

        RenderedTextBlock title = text("遗迹下行者登记簿", 7,
                LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, imprint.bottom() + 3f);
        add(title);

        RenderedTextBlock inn = text("晨溪镇 · 老鸦旅店", 4,
                LedgerEnvironment.FADED_INK, (int) w);
        inn.align(RenderedTextBlock.CENTER_ALIGN);
        inn.setPos(x + (w - inn.width()) / 2f, title.bottom() + 2f);
        add(inn);

        ColorBlock headerRule = LedgerPageGrid.rule(
                x + w * 0.12f,
                Math.min(page.header.bottom - 1f, inn.bottom() + 4f),
                w * 0.76f,
                0.42f);
        add(headerRule);

        float bx = page.body.left + 2f;
        float bw = page.body.width() - 4f;
        float y = page.body.top + 5f;

        RenderedTextBlock useLabel = text("用途", 4,
                LedgerEnvironment.FADED_INK, 22);
        useLabel.setPos(bx, y);
        add(useLabel);
        RenderedTextBlock useValue = text("遗迹下行者离店登记", 5,
                LedgerEnvironment.INK, (int) bw - 25);
        useValue.setPos(bx + 24f, y - 1f);
        add(useValue);

        y += 11f;
        RenderedTextBlock keepLabel = text("保管", 4,
                LedgerEnvironment.FADED_INK, 22);
        keepLabel.setPos(bx, y);
        add(keepLabel);
        RenderedTextBlock keepValue = text("老鸦旅店柜台内册", 5,
                LedgerEnvironment.INK, (int) bw - 25);
        keepValue.setPos(bx + 24f, y - 1f);
        add(keepValue);

        y += 15f;
        ColorBlock bodyRule = LedgerPageGrid.rule(bx, y, bw, 0.26f);
        add(bodyRule);

        RenderedTextBlock note = text(
                "名字、理想与去向留在这里。\n有些页，后来再也没人补写。",
                5,
                LedgerEnvironment.FADED_INK,
                (int) bw);
        note.setPos(bx, y + 6f);
        add(note);

        RenderedTextBlock archiveMark = text("内册", 9,
                LedgerEnvironment.STAMP, 30);
        archiveMark.alpha(0.10f);
        archiveMark.setPos(page.body.right - archiveMark.width() - 2f,
                page.body.bottom - archiveMark.height() - 4f);
        add(archiveMark);

        ColorBlock footerRule = LedgerPageGrid.rule(
                page.footer.left,
                page.footer.top + 1f,
                page.footer.width(),
                0.22f);
        add(footerRule);

        LedgerButton settings = new LedgerButton(Chrome.Type.BLANK, "设置", 4) {
            @Override
            protected void onClick() {
                super.onClick();
                LedgerRecordsScene.this.add(new WndSettings());
            }

            @Override
            protected String hoverText() {
                return "调整游戏设置";
            }
        };
        settings.textColor(LedgerEnvironment.FADED_INK);
        settings.setRect(page.footer.left, page.footer.top + 3f,
                page.footer.width() / 2f - 2f, page.footer.height() - 3f);
        add(settings);

        LedgerButton about = new LedgerButton(Chrome.Type.BLANK, "制作信息", 4) {
            @Override
            protected void onClick() {
                super.onClick();
                Game.switchScene(AboutScene.class);
            }

            @Override
            protected String hoverText() {
                return "查看制作信息";
            }
        };
        about.textColor(LedgerEnvironment.FADED_INK);
        about.setRect(page.footer.left + page.footer.width() / 2f + 2f,
                page.footer.top + 3f,
                page.footer.width() / 2f - 2f,
                page.footer.height() - 3f);
        add(about);
    }

    private void buildRecordsPage(LedgerPageGrid.Page page) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock title = text("现存记录", 8,
                LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        RenderedTextBlock note = text("翻回已经写过的那一页", 4,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);

        ColorBlock headerRule = LedgerPageGrid.rule(
                x + w * 0.08f,
                Math.min(page.header.bottom - 1f, note.bottom() + 4f),
                w * 0.84f,
                0.40f);
        add(headerRule);

        ArrayList<GamesInProgress.Info> saves = GamesInProgress.checkAll();
        int count = Math.min(4, saves.size());

        if (count == 0) {
            RenderedTextBlock emptyTitle = text("尚无登记", 6,
                    LedgerEnvironment.INK, (int) page.body.width());
            emptyTitle.align(RenderedTextBlock.CENTER_ALIGN);
            emptyTitle.setPos(page.body.left + (page.body.width() - emptyTitle.width()) / 2f,
                    page.body.top + page.body.height() * 0.28f);
            add(emptyTitle);

            RenderedTextBlock emptyNote = text("这一册还没有属于你的名字。", 4,
                    LedgerEnvironment.FADED_INK, (int) page.body.width() - 6);
            emptyNote.align(RenderedTextBlock.CENTER_ALIGN);
            emptyNote.setPos(page.body.left + (page.body.width() - emptyNote.width()) / 2f,
                    emptyTitle.bottom() + 5f);
            add(emptyNote);
        } else {
            float rowH = Math.min(17f, (page.body.height() - 3f) / count);
            float y = page.body.top + 1f;

            for (int i = 0; i < count; i++) {
                GamesInProgress.Info info = saves.get(i);
                final int slot = info.slot;
                final ReturningHeroProfile profile = ReturningHeroProfile.loadFromSlot(slot);
                final String heroName = profile.name == null || profile.name.trim().isEmpty()
                        ? "无名者" : profile.name;
                final float rowY = y;
                final float restAlpha = i % 2 == 0 ? 0.028f : 0.014f;

                final ColorBlock rowShade = new ColorBlock(
                        page.body.width(), Math.max(1f, rowH - 1f), 0xFF7A5634);
                rowShade.x = page.body.left;
                rowShade.y = rowY;
                rowShade.alpha(restAlpha);
                add(rowShade);

                final ColorBlock rowRule = LedgerPageGrid.rule(
                        page.body.left,
                        rowY + rowH - 1f,
                        page.body.width(),
                        0.28f);
                add(rowRule);

                LedgerButton open = new LedgerButton(Chrome.Type.BLANK, "", 4) {
                    @Override
                    protected void onPointerDown() {
                        super.onPointerDown();
                        rowShade.alpha(0.085f);
                        rowRule.alpha(0.62f);
                    }

                    @Override
                    protected void onPointerUp() {
                        rowShade.alpha(restAlpha);
                        rowRule.alpha(0.28f);
                        super.onPointerUp();
                    }

                    @Override
                    protected void onClick() {
                        super.onClick();
                        GamesInProgress.curSlot = slot;
                        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                        Game.switchScene(InterlevelScene.class);
                    }

                    @Override
                    protected String hoverText() {
                        return "继续「" + heroName + "」";
                    }
                };
                open.setRect(page.body.left, rowY,
                        page.body.width() - 20f, rowH - 1f);
                add(open);

                RenderedTextBlock index = text(String.format("%02d", i + 1), 4,
                        LedgerEnvironment.FADED_INK, 10);
                index.setPos(page.body.left + 1f, rowY + 2f);
                index.alpha(0.70f);
                add(index);

                float textX = page.body.left + 11f;
                float textW = page.body.width() - 34f;

                RenderedTextBlock name = text(heroName, 5,
                        LedgerEnvironment.INK, (int) textW);
                name.setPos(textX, rowY + 1f);
                add(name);

                RenderedTextBlock meta = text(
                        Messages.titleCase(info.heroClass.title()) + " · Lv." + info.level,
                        4,
                        LedgerEnvironment.FADED_INK,
                        (int) textW);
                meta.setPos(textX, rowY + Math.min(8f, rowH * 0.50f));
                add(meta);

                RenderedTextBlock stamp = text("未归", 5,
                        LedgerEnvironment.STAMP, 18);
                stamp.setPos(page.body.right - stamp.width() - 1f, rowY + 1f);
                add(stamp);

                LedgerButton erase = new LedgerButton(Chrome.Type.BLANK, "移除", 4) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        LedgerRecordsScene.this.add(new WndOptions(
                                "移除记录",
                                "要从名册中移除「" + heroName + "」吗？\n这会同时删除对应存档。",
                                "移除",
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

                    @Override
                    protected String hoverText() {
                        return "从名册中移除这条记录";
                    }
                };
                erase.textColor(LedgerEnvironment.STAMP);
                erase.setRect(page.body.right - 18f,
                        rowY + Math.max(7f, rowH - 8f),
                        17f,
                        7f);
                add(erase);

                y += rowH;
            }
        }

        ColorBlock footerRule = LedgerPageGrid.rule(
                page.footer.left,
                page.footer.top + 1f,
                page.footer.width(),
                0.28f);
        add(footerRule);

        LedgerButton newRecord = new LedgerButton(Chrome.Type.BLANK, "＋ 登记新的下行者  ›", 5) {
            @Override
            protected void onClick() {
                super.onClick();
                if (GamesInProgress.firstEmpty() < 0) return;
                LedgerFlow.resetDraft();
                LedgerTransitions.turn(LedgerRecordsScene.this,
                        page.paper, LedgerHeroScene.class, true);
            }

            @Override
            protected String hoverText() {
                return "在名册中写下新的名字";
            }
        };
        newRecord.textColor(LedgerEnvironment.INK);
        newRecord.setRect(page.footer.left,
                page.footer.top + 3f,
                page.footer.width(),
                page.footer.height() - 3f);
        add(newRecord);
    }

    private RenderedTextBlock text(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
