/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Entry;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Kind;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;

/** Small, always-available newcomer explanations beside Ledger choices. */
final class LedgerHelp {

    private LedgerHelp() {
    }

    static LedgerButton heroClass(final PixelScene scene, final HeroClass heroClass) {
        return button(scene, "查看" + Messages.titleCase(heroClass.title()) + "说明", new Runnable() {
            @Override public void run() {
                show(scene,
                        Messages.titleCase(heroClass.title()),
                        heroClass.desc(),
                        "职业决定基础战斗风格和前两阶天赋；专精与英雄战技会在后面单独选择。");
            }
        });
    }

    static LedgerButton subClass(final PixelScene scene, final HeroSubClass subClass) {
        return button(scene, "查看" + Messages.titleCase(subClass.title()) + "说明", new Runnable() {
            @Override public void run() {
                show(scene,
                        Messages.titleCase(subClass.title()),
                        subClass.desc(),
                        "专精主要决定第三阶天赋池。改选专精时，其他阶的天赋记录不会被整页清掉。");
            }
        });
    }

    static LedgerButton ability(final PixelScene scene, final ArmorAbility ability) {
        return button(scene, "查看" + ability.name() + "说明", new Runnable() {
            @Override public void run() {
                show(scene,
                        ability.name(),
                        ability.desc(),
                        "英雄战技决定第四阶天赋方向；实战中它由英雄护甲的充能驱动。");
            }
        });
    }

    static LedgerButton talent(final PixelScene scene, final Talent talent) {
        return button(scene, "查看" + talent.title() + "说明", new Runnable() {
            @Override public void run() {
                show(scene,
                        talent.title(),
                        talent.desc(),
                        "这里是在重建通关时的天赋记录；这一项最多可以投入 "
                                + talent.maxPoints() + " 点。");
            }
        });
    }

    static LedgerButton item(final PixelScene scene, final String itemId) {
        Item preview = ReturningHeroItemCatalog.newItem(itemId);
        final String hover = preview == null ? "查看装备说明"
                : "查看" + Messages.titleCase(preview.trueName()) + "说明";
        return button(scene, hover, new Runnable() {
            @Override public void run() {
                showItem(scene, itemId);
            }
        });
    }

    private static LedgerButton button(final PixelScene scene,
                                       final String hoverText,
                                       final Runnable action) {
        LedgerButton help = new LedgerButton(Chrome.Type.BLANK, "?", 6) {
            @Override protected void onClick() {
                super.onClick();
                action.run();
            }

            @Override protected String hoverText() {
                return hoverText;
            }
        };
        help.textColor(LedgerEnvironment.STAMP);
        return help;
    }

    private static void showItem(PixelScene scene, String itemId) {
        Entry entry = ReturningHeroItemCatalog.byId(itemId);
        Item item = ReturningHeroItemCatalog.newItem(itemId);
        if (entry == null || item == null) {
            show(scene, "说明", "这条记录暂时没有可读取的原版物品说明。", "可以继续选择，它不会影响登记流程。");
            return;
        }

        // Do not mutate the real game's discovery catalog just because the player
        // opened help during reconstruction. identify(false) only makes this fresh
        // throwaway preview instance disclose its complete original information.
        item.identify(false);
        show(scene,
                Messages.titleCase(item.trueName()),
                item.info(),
                newcomerLine(entry.kind));
    }

    private static String newcomerLine(Kind kind) {
        switch (kind) {
            case MELEE_WEAPON:
                return "先看阶次、力量需求和特殊特性；这里登记的是归来时真正惯用的主武器。";
            case ARMOR:
                return "护甲要同时看防御与力量需求；力量不足时，沉重装备往往会拖累行动与闪避。";
            case WAND:
                return "法杖靠充能施法，不同法杖的瞄准方式、射程和效果差别很大。";
            case RING:
                return "戒指提供持续被动效果；强化等级越高，对应效果通常越强。";
            case ARTIFACT:
                return "神器各有独立机制和成长方式，不能只把它当成另一件单纯加数值的装备。";
            case TRINKET:
                return "饰物更多是在改变概率、生成或规则，不一定直接提高面板战斗数值。";
            default:
                return "不确定时先看原版说明，再决定是否把它写进归来记录。";
        }
    }

    private static void show(PixelScene scene, String title, String original, String newcomer) {
        String body = original == null ? "" : original;
        body += "\n\n_给新人的一句话_\n" + newcomer;
        scene.add(new WndOptions(title, body, "知道了"));
    }
}
