/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroProfile;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.RectF;

import java.util.ArrayList;

/**
 * Echoes of Yendor's front door: the Morningcreek inn's old expedition ledger.
 * New games are registered as "未归" entries; existing saves are continued from the same book.
 */
public class LedgerScene extends PixelScene {

    private static final String LEDGER_BG = "interfaces/echoes/ledger_open.png";
    private static final int INK = 0x3B2A1E;
    private static final int FADED_INK = 0x6F5B48;
    private static final int STAMP = 0x922E2E;

    private static ReturningHeroProfile draft = new ReturningHeroProfile();

    @Override
    public void create() {
        super.create();
        uiCamera.visible = false;

        addLedgerBackground();

        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF safe = getCommonInsets();
        float usableW = w - safe.left - safe.right;
        float pageGap = Math.max(6, usableW * 0.025f);
        float bookW = Math.min(usableW - 10, 360);
        float pageW = (bookW - pageGap) / 2f;
        float bookX = safe.left + (usableW - bookW) / 2f;
        float top = safe.top + Math.max(10, (h - safe.top - safe.bottom) * 0.13f);

        RenderedTextBlock title = inkText("ECHOES OF YENDOR", 13, (int)pageW - 10);
        title.setPos(bookX + (pageW - title.width()) / 2f, top);
        add(title);

        RenderedTextBlock subtitle = inkText("晨溪镇 · 老鸦旅店\n遗迹下行者登记簿", 7, (int)pageW - 12);
        subtitle.setPos(bookX + (pageW - subtitle.width()) / 2f, title.bottom() + 5);
        add(subtitle);

        RenderedTextBlock note = inkText(
                "有人写下姓名、理想与去处。\n有人回来，也有人永远停在这一页。", 6, (int)pageW - 18);
        note.hardlight(FADED_INK);
        note.setPos(bookX + 9, subtitle.bottom() + 12);
        add(note);

        addSideButtons(bookX + 7, top + Math.max(86, pageW * 0.76f), pageW - 14);

        float rightX = bookX + pageW + pageGap;
        RenderedTextBlock recordsTitle = inkText("现存记录", 10, (int)pageW - 10);
        recordsTitle.setPos(rightX + (pageW - recordsTitle.width()) / 2f, top);
        add(recordsTitle);

        ArrayList<GamesInProgress.Info> saves = GamesInProgress.checkAll();
        float y = recordsTitle.bottom() + 7;
        if (saves.isEmpty()) {
            RenderedTextBlock empty = inkText("这一册尚没有属于你的记录。", 6, (int)pageW - 18);
            empty.hardlight(FADED_INK);
            empty.setPos(rightX + 9, y + 7);
            add(empty);
        } else {
            int shown = 0;
            for (GamesInProgress.Info info : saves) {
                if (shown >= 4) break;
                ReturningHeroProfile profile = ReturningHeroProfile.loadFromSlot(info.slot);
                String label = profile.name + "   ·   " + Messages.titleCase(info.heroClass.title()) + "\n未归   ·   Lv." + info.level;
                final int slot = info.slot;
                StyledButton record = ledgerButton(label) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        GamesInProgress.curSlot = slot;
                        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                        Game.switchScene(InterlevelScene.class);
                    }
                };
                record.setRect(rightX + 6, y, pageW - 12, 31);
                add(record);
                y = record.bottom() + 4;
                shown++;
            }
        }

        StyledButton newRecord = ledgerButton("＋  登记新的下行者") {
            @Override
            protected void onClick() {
                super.onClick();
                if (GamesInProgress.firstEmpty() < 0) {
                    LedgerScene.this.add(new WndMessage("名册已经写满了。请先整理一条旧记录。"));
                    return;
                }
                draft = new ReturningHeroProfile();
                Game.switchScene(RegistrationScene.class);
            }
        };
        newRecord.setRect(rightX + 6, Math.min(h - safe.bottom - 29, y + 7), pageW - 12, 23);
        add(newRecord);

        fadeIn();
    }

    private void addSideButtons(float x, float y, float width) {
        StyledButton settings = ledgerButton("设置") {
            @Override
            protected void onClick() {
                super.onClick();
                LedgerScene.this.add(new WndSettings());
            }
        };
        settings.setRect(x, y, width, 19);
        add(settings);

        StyledButton credits = ledgerButton("制作与授权") {
            @Override
            protected void onClick() {
                super.onClick();
                Game.switchScene(AboutScene.class);
            }
        };
        credits.setRect(x, settings.bottom() + 4, width, 19);
        add(credits);

        if (DeviceCompat.isDesktop()) {
            StyledButton quit = ledgerButton("合上名册并退出") {
                @Override
                protected void onClick() {
                    super.onClick();
                    Game.instance.finish();
                }
            };
            quit.setRect(x, credits.bottom() + 4, width, 19);
            add(quit);
        }
    }

    private void addLedgerBackground() {
        int screenW = Camera.main.width;
        int screenH = Camera.main.height;
        add(new ColorBlock(screenW, screenH, 0xFF17110D));

        Image bg = new Image(LEDGER_BG);
        float scale = Math.max(screenW / bg.width, screenH / bg.height);
        bg.scale.set(scale);
        bg.x = (screenW - bg.width()) / 2f;
        bg.y = (screenH - bg.height()) / 2f;
        PixelScene.align(bg);
        add(bg);
    }

    private RenderedTextBlock inkText(String text, int size, int maxWidth) {
        RenderedTextBlock block = PixelScene.renderTextBlock(text, size);
        block.maxWidth(maxWidth);
        block.hardlight(INK);
        return block;
    }

    private StyledButton ledgerButton(String text) {
        StyledButton button = new StyledButton(Chrome.Type.TOAST_WHITE, text);
        button.textColor(INK);
        return button;
    }

    /** Fill one empty page before descending into the remembered past. */
    public static class RegistrationScene extends PixelScene {

        @Override
        public void create() {
            super.create();
            uiCamera.visible = false;

            int screenW = Camera.main.width;
            int screenH = Camera.main.height;
            add(new ColorBlock(screenW, screenH, 0xFF17110D));
            Image bg = new Image(LEDGER_BG);
            float scale = Math.max(screenW / bg.width, screenH / bg.height);
            bg.scale.set(scale);
            bg.x = (screenW - bg.width()) / 2f;
            bg.y = (screenH - bg.height()) / 2f;
            PixelScene.align(bg);
            add(bg);

            RectF safe = getCommonInsets();
            float usableW = screenW - safe.left - safe.right;
            float formW = Math.min(usableW - 24, 250);
            float formX = safe.left + (usableW - formW) / 2f;
            float y = safe.top + 14;

            RenderedTextBlock header = PixelScene.renderTextBlock("遗迹下行者登记", 12);
            header.hardlight(INK);
            header.setPos(formX + (formW - header.width()) / 2f, y);
            add(header);
            y = header.bottom() + 8;

            StyledButton name = fieldButton("姓名", draft.name) {
                @Override
                protected void onClick() {
                    super.onClick();
                    RegistrationScene.this.add(new WndTextInput(
                            "登记姓名",
                            "写下当年走进遗迹时留下的名字。",
                            draft.name.equals("无名者") ? "" : draft.name,
                            20,
                            false,
                            "写入名册",
                            "取消") {
                        @Override
                        public void onSelect(boolean positive, String text) {
                            if (positive && text != null && !text.trim().isEmpty()) {
                                draft.name = text.trim();
                                Game.switchScene(RegistrationScene.class);
                            }
                        }
                    });
                }
            };
            name.setRect(formX, y, formW, 22);
            add(name);
            y = name.bottom() + 3;

            StyledButton heroClass = fieldButton("理想职业", Messages.titleCase(draft.heroClass.title())) {
                @Override
                protected void onClick() {
                    super.onClick();
                    HeroClass[] classes = HeroClass.values();
                    String[] labels = new String[classes.length];
                    for (int i = 0; i < classes.length; i++) labels[i] = Messages.titleCase(classes[i].title());
                    RegistrationScene.this.add(new WndOptions(
                            "理想职业",
                            "这是当年登记在名册上的愿望，也是归还者真正拥有的英雄单位。",
                            labels) {
                        @Override
                        protected void onSelect(int index) {
                            draft.heroClass = HeroClass.values()[index];
                            draft.resetDependentChoices();
                            Game.switchScene(RegistrationScene.class);
                        }
                    });
                }
            };
            heroClass.setRect(formX, y, formW, 22);
            add(heroClass);
            y = heroClass.bottom() + 3;

            StyledButton weapon = fieldButton("惯用兵器", draft.weaponName()) {
                @Override
                protected void onClick() {
                    super.onClick();
                    RegistrationScene.this.add(new WndOptions(
                            "惯用兵器",
                            "不是把第一部存档伪装成精确导入，而是记录归来时最有代表性的战斗风格。",
                            draft.weaponOptions()) {
                        @Override
                        protected void onSelect(int index) {
                            draft.weaponIndex = index;
                            Game.switchScene(RegistrationScene.class);
                        }
                    });
                }
            };
            weapon.setRect(formX, y, formW, 22);
            add(weapon);
            y = weapon.bottom() + 3;

            StyledButton subclass = fieldButton("后来专精", Messages.titleCase(draft.subClass().title())) {
                @Override
                protected void onClick() {
                    super.onClick();
                    HeroSubClass[] subclasses = draft.heroClass.subClasses();
                    String[] labels = new String[subclasses.length];
                    for (int i = 0; i < subclasses.length; i++) labels[i] = Messages.titleCase(subclasses[i].title());
                    RegistrationScene.this.add(new WndOptions(
                            "后来专精",
                            "这不是新的职业系统，而是沿用原版英雄在地下城中形成的专精。",
                            labels) {
                        @Override
                        protected void onSelect(int index) {
                            draft.subclassIndex = index;
                            Game.switchScene(RegistrationScene.class);
                        }
                    });
                }
            };
            subclass.setRect(formX, y, formW, 22);
            add(subclass);
            y = subclass.bottom() + 3;

            StyledButton ability = fieldButton("最终战技", draft.armorAbility().name()) {
                @Override
                protected void onClick() {
                    super.onClick();
                    ArmorAbility[] abilities = draft.heroClass.armorAbilities();
                    String[] labels = new String[abilities.length];
                    for (int i = 0; i < abilities.length; i++) labels[i] = abilities[i].name();
                    RegistrationScene.this.add(new WndOptions(
                            "最终战技",
                            "记录你在第一部终局阶段掌握的英雄护甲能力。",
                            labels) {
                        @Override
                        protected void onSelect(int index) {
                            draft.abilityIndex = index;
                            Game.switchScene(RegistrationScene.class);
                        }
                    });
                }
            };
            ability.setRect(formX, y, formW, 22);
            add(ability);
            y = ability.bottom() + 3;

            StyledButton growth = fieldButton("成长记录", draft.growthPreset.title) {
                @Override
                protected void onClick() {
                    super.onClick();
                    String[] labels = new String[]{"均衡记录", "进攻记录", "生存记录"};
                    RegistrationScene.this.add(new WndOptions(
                            "成长记录",
                            "原版 Lv.30 的 29 个标准天赋点仍按四个层级分别计算。当前先提供三套起点；逐项加减的详细页会在这套名册 UI 稳定后接入。",
                            labels) {
                        @Override
                        protected void onSelect(int index) {
                            draft.growthPreset = ReturningHeroProfile.GrowthPreset.values()[index];
                            Game.switchScene(RegistrationScene.class);
                        }
                    });
                }
            };
            growth.setRect(formX, y, formW, 22);
            add(growth);
            y = growth.bottom() + 7;

            RenderedTextBlock destination = PixelScene.renderTextBlock("目的地：地下遗迹", 7);
            destination.hardlight(FADED_INK);
            destination.setPos(formX + 3, y);
            add(destination);
            y = destination.bottom() + 4;

            ColorBlock stampBorder = new ColorBlock(58, 21, 0x55922E2E);
            stampBorder.setPos(formX + formW - 61, y - 1);
            add(stampBorder);
            RenderedTextBlock stamp = PixelScene.renderTextBlock("未  归", 13);
            stamp.hardlight(STAMP);
            stamp.setPos(stampBorder.x + (stampBorder.width - stamp.width()) / 2f,
                    stampBorder.y + (stampBorder.height - stamp.height()) / 2f);
            add(stamp);

            StyledButton confirm = new StyledButton(Chrome.Type.TOAST_WHITE, "盖下「未归」并登记") {
                @Override
                protected void onClick() {
                    super.onClick();
                    if (draft.name == null || draft.name.trim().isEmpty() || draft.name.equals("无名者")) {
                        RegistrationScene.this.add(new WndMessage("至少先在名册上留下一个名字。"));
                        return;
                    }
                    SequelGame.start(draft);
                }
            };
            confirm.textColor(STAMP);
            confirm.setRect(formX, Math.min(screenH - safe.bottom - 27, y + 26), formW, 22);
            add(confirm);

            StyledButton back = fieldButton("返回名册", "") {
                @Override
                protected void onClick() {
                    super.onClick();
                    Game.switchScene(LedgerScene.class);
                }
            };
            back.setRect(formX, confirm.bottom() + 3, formW, 18);
            add(back);

            fadeIn();
        }

        private StyledButton fieldButton(String field, String value) {
            StyledButton button = new StyledButton(Chrome.Type.TOAST_WHITE,
                    value == null || value.isEmpty() ? field : field + "：" + value);
            button.textColor(INK);
            return button;
        }
    }
}
