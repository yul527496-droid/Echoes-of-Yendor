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

        RenderedTextBlock title = text("遗迹下行者登记簿", 10, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        RenderedTextBlock inn = text("晨溪镇 · 老鸦旅店", 6, LedgerEnvironment.FADED_INK, (int) w);
        inn.align(RenderedTextBlock.CENTER_ALIGN);
        inn.setPos(x + (w - inn.width()) / 2f, title.bottom() + 3f);
        add(inn);

        float bx = page.body.left + 5f;
        float bw = page.body.width() - 10f;
        float y = page.body.top + 8f;

        RenderedTextBlock note = text("名字、身份与去向，都留在这一册里。",
                6, LedgerEnvironment.FADED_INK, (int) bw);
        note.setPos(bx, y);
        add(note);

        y = note.bottom() + 12f;
        RenderedTextBlock fieldName = text("姓名", 6, LedgerEnvironment.FADED_INK, 24);
        fieldName.setPos(bx, y);
        add(fieldName);
        RenderedTextBlock fieldValue = text("——", 7, LedgerEnvironment.INK, (int) bw - 30);
        fieldValue.setPos(bx + 28f, y - 1f);
        add(fieldValue);

        y += 15f;
        RenderedTextBlock fieldRole = text("身份", 6, LedgerEnvironment.FADED_INK, 24);
        fieldRole.setPos(bx, y);
        add(fieldRole);
        RenderedTextBlock roleValue = text("——", 7, LedgerEnvironment.INK, (int) bw - 30);
        roleValue.setPos(bx + 28f, y - 1f);
        add(roleValue);

        y += 15f;
        RenderedTextBlock fieldDest = text("去向", 6, LedgerEnvironment.FADED_INK, 24);
        fieldDest.setPos(bx, y);
        add(fieldDest);
        RenderedTextBlock destValue = text("地下遗迹", 7, LedgerEnvironment.INK, (int) bw - 30);
        destValue.setPos(bx + 28f, y - 1f);
        add(destValue);

        RenderedTextBlock archiveMark = text("老鸦旅店", 7, LedgerEnvironment.STAMP, 50);
        archiveMark.alpha(0.13f);
        archiveMark.setPos(page.body.right - archiveMark.width() - 5f,
                page.body.bottom - archiveMark.height() - 8f);
        add(archiveMark);

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

    private void buildRecordsPage(LedgerPageGrid.Page page) {
        float x = page.header.left;
        float w = page.header.width();

        RenderedTextBlock title = text("现存记录", 10, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, page.header.top);
        add(title);

        ArrayList<GamesInProgress.Info> saves = GamesInProgress.checkAll();
        int count = Math.min(4, saves.size());

        if (count == 0) {
            RenderedTextBlock emptyTitle = text("尚无登记", 7, LedgerEnvironment.INK,
                    (int) page.body.width());
            emptyTitle.align(RenderedTextBlock.CENTER_ALIGN);
            emptyTitle.setPos(page.body.left + (page.body.width() - emptyTitle.width()) / 2f,
                    page.body.top + page.body.height() * 0.32f);
            add(emptyTitle);

            RenderedTextBlock emptyNote = text("这一册还没有属于你的名字。", 6,
                    LedgerEnvironment.FADED_INK, (int) page.body.width() - 8);
            emptyNote.align(RenderedTextBlock.CENTER_ALIGN);
            emptyNote.setPos(page.body.left + (page.body.width() - emptyNote.width()) / 2f,
                    emptyTitle.bottom() + 6f);
            add(emptyNote);
        } else {
            float rowH = Math.min(38f, (page.body.height() - 4f) / count);
            float y = page.body.top + 2f;

            for (int i = 0; i < count; i++) {
                GamesInProgress.Info info = saves.get(i);
                final int slot = info.slot;
                final ReturningHeroProfile profile = ReturningHeroProfile.loadFromSlot(slot);
                final String heroName = profile.name == null || profile.name.trim().isEmpty()
                        ? "无名者" : profile.name;
                final float rowY = y;
                final float restAlpha = i % 2 == 0 ? 0.035f : 0.018f;

                final ColorBlock rowShade = new ColorBlock(page.body.width(),
                        Math.max(1f, rowH - 2f), 0xFF7A5634);
                rowShade.x = page.body.left;
                rowShade.y = rowY;
                rowShade.alpha(restAlpha);
                add(rowShade);

                LedgerButton open = new LedgerButton(Chrome.Type.BLANK, "", 5) {
                    @Override protected void onPointerDown() {
                        super.onPointerDown();
                        rowShade.alpha(0.11f);
                    }
                    @Override protected void onPointerUp() {
                        rowShade.alpha(restAlpha);
                        super.onPointerUp();
                    }
                    @Override protected void onClick() {
                        super.onClick();
                        GamesInProgress.curSlot = slot;
                        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                        Game.switchScene(InterlevelScene.class);
                    }
                    @Override protected String hoverText() { return "继续「" + heroName + "」"; }
                };
                open.setRect(page.body.left, rowY, page.body.width(), rowH - 2f);
                add(open);

                Image avatar = HeroSprite.avatar(info.heroClass, info.armorTier);
                float avatarScale = Math.min(1.15f, Math.max(0.72f, (rowH - 8f) / avatar.height));
                avatar.scale.set(avatarScale);
                avatar.x = page.body.left + 5f;
                avatar.y = rowY + (rowH - avatar.height()) / 2f - 1f;
                add(avatar);

                RenderedTextBlock number = text(String.valueOf(slot), 7,
                        LedgerEnvironment.INK, 24);
                number.setPos(page.body.left + 22f, rowY + 4f);
                add(number);

                float textX = page.body.left + 42f;
                float textW = page.body.width() - 80f;
                RenderedTextBlock name = text(heroName, 7, LedgerEnvironment.INK, (int) textW);
                name.setPos(textX, rowY + 4f);
                add(name);

                RenderedTextBlock meta = text(Messages.titleCase(info.heroClass.title()) + " · Lv." + info.level,
                        5, LedgerEnvironment.FADED_INK, (int) textW);
                meta.setPos(textX, rowY + 18f);
                add(meta);

                Image stamp = LedgerEnvironment.tryLoad(LedgerEnvironment.STAMP_UNRETURNED);
                if (stamp != null) {
                    float stampScale = Math.min(0.78f, Math.max(0.52f, (rowH - 6f) / stamp.height));
                    stamp.scale.set(stampScale);
                    stamp.x = page.body.right - stamp.width() - 5f;
                    stamp.y = rowY + (rowH - stamp.height()) / 2f - 2f;
                    stamp.alpha(0.88f);
                    add(stamp);
                } else {
                    RenderedTextBlock stampText = text("未归", 6, LedgerEnvironment.STAMP, 22);
                    stampText.setPos(page.body.right - stampText.width() - 5f, rowY + 5f);
                    add(stampText);
                }

                LedgerButton erase = new LedgerButton(Chrome.Type.BLANK, "移除", 5) {
                    @Override protected void onClick() {
                        super.onClick();
                        LedgerRecordsScene.this.add(new WndOptions(
                                "移除记录",
                                "要从名册中移除「" + heroName + "」吗？\n这会同时删除对应存档。",
                                "移除", "取消") {
                            @Override protected void onSelect(int index) {
                                if (index == 0) {
                                    Dungeon.deleteGame(slot, true);
                                    Game.switchScene(LedgerRecordsScene.class);
                                }
                            }
                        });
                    }
                    @Override protected String hoverText() { return "从名册中移除这条记录"; }
                };
                erase.textColor(LedgerEnvironment.STAMP);
                erase.setRect(page.body.right - 27f, rowY + rowH - 10f, 22f, 8f);
                add(erase);

                y += rowH;
            }
        }

        LedgerButton newRecord = new LedgerButton(Chrome.Type.BLANK, "＋ 登记新的下行者", 6) {
            @Override protected void onClick() {
                super.onClick();
                if (GamesInProgress.firstEmpty() < 0) return;
                LedgerFlow.resetDraft();
                LedgerTransitions.turn(LedgerRecordsScene.this, page.paper,
                        LedgerHeroScene.class, true);
            }
            @Override protected String hoverText() { return "在名册中写下新的名字"; }
        };
        newRecord.textColor(LedgerEnvironment.INK);
        newRecord.setRect(page.footer.left, page.footer.top + 3f,
                page.footer.width(), page.footer.height() - 4f);
        add(newRecord);
    }

    private RenderedTextBlock text(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
