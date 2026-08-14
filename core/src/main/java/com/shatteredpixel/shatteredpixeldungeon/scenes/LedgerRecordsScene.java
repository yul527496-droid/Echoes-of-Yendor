package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroProfile;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

import java.util.ArrayList;

/** First formal spread of the Echoes ledger. */
public class LedgerRecordsScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        LedgerPageGrid.Page left = LedgerEnvironment.leftGrid(book);
        LedgerPageGrid.Page right = LedgerEnvironment.rightGrid(book);
        ArrayList<GamesInProgress.Info> saves = GamesInProgress.checkAll();

        buildLedgerPage(left, saves);
        buildRecordsPage(right, saves);
        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildLedgerPage(LedgerPageGrid.Page page,
                                 ArrayList<GamesInProgress.Info> saves) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock title = text("遗迹下行者登记簿", 10,
                LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        RenderedTextBlock inn = text("晨溪镇 · 老鸦旅店", 6,
                LedgerEnvironment.FADED_INK, (int) w);
        inn.align(RenderedTextBlock.CENTER_ALIGN);
        inn.setPos(x + (w - inn.width()) / 2f, title.bottom() + 3f);
        add(inn);

        add(LedgerPageGrid.rule(x + 8f,
                Math.min(page.header.bottom - 2f, inn.bottom() + 5f),
                w - 16f, 0.24f));

        float bx = page.body.left + 4f;
        float bw = page.body.width() - 8f;
        float y = page.body.top + 7f;

        RenderedTextBlock section = text("名册摘要", 8,
                LedgerEnvironment.INK, (int) bw);
        section.setPos(bx, y);
        add(section);
        y = section.bottom() + 10f;

        y = addField(bx, bw, y, "登记地点", "晨溪镇 · 老鸦旅店");
        y = addField(bx, bw, y, "下行去向", "地下遗迹");

        int visibleRecords = Math.min(4, saves.size());
        y = addField(bx, bw, y, "册中人数", String.valueOf(visibleRecords));
        addField(bx, bw, y, "未归人数", String.valueOf(visibleRecords));

        LedgerStamp archiveMark = new LedgerStamp("老鸦旅店", 5);
        archiveMark.setRect(page.body.right - 58f, page.body.bottom - 25f, 52f, 18f);
        archiveMark.alpha(0.13f);
        add(archiveMark);

        add(LedgerPageGrid.rule(page.footer.left, page.footer.top + 1f,
                page.footer.width(), 0.18f));

        LedgerButton settings = new LedgerButton(Chrome.Type.BLANK, "设置", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerRecordsScene.this.add(new WndSettings());
            }
            @Override protected String hoverText() { return "调整游戏设置"; }
        };
        settings.textColor(LedgerEnvironment.FADED_INK);
        settings.setRect(page.footer.left, page.footer.top + 3f,
                page.footer.width() / 2f - 2f, page.footer.height() - 3f);
        add(settings);

        LedgerButton about = new LedgerButton(Chrome.Type.BLANK, "制作信息", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerAudio.leave();
                Game.switchScene(AboutScene.class);
            }
            @Override protected String hoverText() { return "查看制作信息"; }
        };
        about.textColor(LedgerEnvironment.FADED_INK);
        about.setRect(page.footer.left + page.footer.width() / 2f + 2f,
                page.footer.top + 3f, page.footer.width() / 2f - 2f,
                page.footer.height() - 3f);
        add(about);
    }

    private float addField(float x, float width, float y,
                           String labelValue, String fieldValue) {
        RenderedTextBlock label = text(labelValue, 6,
                LedgerEnvironment.FADED_INK, 34);
        label.setPos(x, y);
        add(label);

        RenderedTextBlock value = text(fieldValue, 7,
                LedgerEnvironment.INK, (int) width - 39);
        value.setPos(x + 39f, y - 1f);
        add(value);
        return Math.max(label.bottom(), value.bottom()) + 9f;
    }

    private void buildRecordsPage(LedgerPageGrid.Page page,
                                  ArrayList<GamesInProgress.Info> saves) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock title = text("现存记录", 10,
                LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top + 1f);
        add(title);

        RenderedTextBlock subtitle = text("仍留在册上的名字", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        subtitle.align(RenderedTextBlock.CENTER_ALIGN);
        subtitle.setPos(x + (w - subtitle.width()) / 2f, title.bottom() + 3f);
        add(subtitle);

        add(LedgerPageGrid.rule(x + 8f,
                Math.min(page.header.bottom - 2f, subtitle.bottom() + 5f),
                w - 16f, 0.24f));

        int count = Math.min(4, saves.size());
        if (count == 0) {
            buildEmptyRecords(page);
        } else {
            float rowH = Math.min(32f, (page.body.height() - 3f) / count);
            float y = page.body.top + 2f;

            for (int i = 0; i < count; i++) {
                GamesInProgress.Info info = saves.get(i);
                buildRecordRow(page, info, y, rowH, i);
                y += rowH;
            }
        }

        add(LedgerPageGrid.rule(page.footer.left, page.footer.top + 1f,
                page.footer.width(), 0.18f));

        LedgerButton newRecord = new LedgerButton(Chrome.Type.BLANK,
                "登记新的下行者", 7) {
            @Override protected void onClick() {
                super.onClick();
                if (GamesInProgress.firstEmpty() < 0) return;
                LedgerFlow.resetDraft();
                LedgerTransitions.turn(LedgerRecordsScene.this, page.paper,
                        LedgerHeroScene.class, true);
            }
            @Override protected String hoverText() { return "在名册中写下新的名字"; }
        };
        newRecord.textColor(LedgerEnvironment.STAMP);
        newRecord.setRect(page.footer.left, page.footer.top + 3f,
                page.footer.width(), page.footer.height() - 4f);
        add(newRecord);
    }

    private void buildEmptyRecords(LedgerPageGrid.Page page) {
        RenderedTextBlock emptyTitle = text("尚无登记", 8,
                LedgerEnvironment.INK, (int) page.body.width());
        emptyTitle.align(RenderedTextBlock.CENTER_ALIGN);
        emptyTitle.setPos(page.body.left +
                        (page.body.width() - emptyTitle.width()) / 2f,
                page.body.top + page.body.height() * 0.34f);
        add(emptyTitle);

        RenderedTextBlock emptyNote = text("这页还没有留下名字。", 6,
                LedgerEnvironment.FADED_INK, (int) page.body.width() - 8);
        emptyNote.align(RenderedTextBlock.CENTER_ALIGN);
        emptyNote.setPos(page.body.left +
                        (page.body.width() - emptyNote.width()) / 2f,
                emptyTitle.bottom() + 7f);
        add(emptyNote);
    }

    private void buildRecordRow(LedgerPageGrid.Page page,
                                GamesInProgress.Info info,
                                float rowY, float rowH, int index) {
        final int slot = info.slot;
        final ReturningHeroProfile profile = ReturningHeroProfile.loadFromSlot(slot);
        final String heroName = profile.name == null || profile.name.trim().isEmpty()
                ? "无名者" : profile.name;
        final float restAlpha = index % 2 == 0 ? 0.032f : 0.016f;

        final ColorBlock rowShade = new ColorBlock(page.body.width(),
                Math.max(1f, rowH - 2f), 0xFF7A5634);
        rowShade.x = page.body.left;
        rowShade.y = rowY;
        rowShade.alpha(restAlpha);
        add(rowShade);

        LedgerButton open = new LedgerButton(Chrome.Type.BLANK, "", 5) {
            @Override protected void onPointerDown() {
                super.onPointerDown();
                rowShade.alpha(0.10f);
            }
            @Override protected void onPointerUp() {
                rowShade.alpha(restAlpha);
                super.onPointerUp();
            }
            @Override protected void onClick() {
                super.onClick();
                GamesInProgress.curSlot = slot;
                InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                LedgerAudio.leave();
                Game.switchScene(InterlevelScene.class);
            }
            @Override protected String hoverText() { return "继续「" + heroName + "」"; }
        };
        open.setRect(page.body.left, rowY, page.body.width(), rowH - 2f);
        add(open);

        Image avatar = HeroSprite.avatar(info.heroClass, info.armorTier);
        float avatarScale = Math.min(1.12f,
                Math.max(0.76f, (rowH - 8f) / avatar.height));
        avatar.scale.set(avatarScale);
        avatar.x = page.body.left + 5f;
        avatar.y = rowY + (rowH - avatar.height()) / 2f - 1f;
        add(avatar);

        float textX = page.body.left + 27f;
        float textW = page.body.width() - 68f;

        RenderedTextBlock name = text(heroName, 7,
                LedgerEnvironment.INK, (int) textW);
        name.setPos(textX, rowY + 4f);
        add(name);

        RenderedTextBlock meta = text(
                Messages.titleCase(info.heroClass.title()) + " · Lv." + info.level,
                6, LedgerEnvironment.FADED_INK, (int) textW);
        meta.setPos(textX, rowY + 17f);
        add(meta);

        LedgerStamp stamp = new LedgerStamp("未归", 5);
        stamp.setRect(page.body.right - 31f, rowY + 3f, 27f, 13f);
        stamp.alpha(0.86f);
        add(stamp);

        LedgerButton erase = new LedgerButton(Chrome.Type.BLANK, "划去", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerRecordsScene.this.add(new WndOptions(
                        "划去记录",
                        "要从名册中划去「" + heroName + "」吗？\n这会同时删除对应存档。",
                        "划去", "取消") {
                    @Override protected void onSelect(int selected) {
                        if (selected == 0) {
                            LedgerAudio.erase();
                            Dungeon.deleteGame(slot, true);
                            Game.switchScene(LedgerRecordsScene.class);
                        }
                    }
                });
            }
            @Override protected String hoverText() { return "从名册中划去这条记录"; }
        };
        erase.textColor(LedgerEnvironment.STAMP);
        erase.setRect(page.body.right - 28f,
                rowY + rowH - 10f, 23f, 8f);
        add(erase);
    }

    private RenderedTextBlock text(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
