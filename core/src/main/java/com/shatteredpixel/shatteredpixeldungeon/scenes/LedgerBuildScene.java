package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.LedgerFlow;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildCost;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroBuildRules;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroHeritage;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog;
import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroLoadout;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Image;

import java.util.Map;

/** Central directory for reconstructing the returning hero's final build. */
public class LedgerBuildScene extends PixelScene {

    private LedgerPageGrid.Page left;
    private LedgerPageGrid.Page right;

    @Override
    public void create() {
        super.create();

        Image book = LedgerEnvironment.addOpenBook(this);
        left = LedgerEnvironment.leftGrid(book);
        right = LedgerEnvironment.rightGrid(book);

        buildSummary();
        buildDirectory();
        LedgerTransitions.revealIfPending(this, left.paper, right.paper);
        fadeIn();
    }

    private void buildSummary() {
        float x = left.header.left;
        float w = left.header.width();

        RenderedTextBlock title = t("归来者旧录", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, left.header.top);
        add(title);

        RenderedTextBlock note = t("按本人回忆补齐", 5, LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.12f,
                Math.min(left.header.bottom - 1f, note.bottom() + 4f), w * 0.76f, 0.36f));

        float bx = left.body.left + 5f;
        float bw = left.body.width() - 10f;
        float y = left.body.top + 6f;

        y = field(bx, bw, y, "姓名", LedgerFlow.draft().name);
        y = field(bx, bw, y, "职业",
                Messages.titleCase(LedgerFlow.draft().heroClass.title()));
        y = field(bx, bw, y, "惯用兵器", weaponStatus());
        y = field(bx, bw, y, "护甲", armorStatus());

        int spent = ReturningHeroBuildCost.equipmentSpent(
                LedgerFlow.draft().heroClass, LedgerFlow.draft().loadout);
        int remaining = ReturningHeroBuildCost.equipmentRemaining(
                LedgerFlow.draft().heroClass, LedgerFlow.draft().loadout);
        String budget = spent < 0 || remaining < 0
                ? "待重新核对"
                : spent + " / " + ReturningHeroBuildRules.EQUIPMENT_RECONSTRUCTION_BUDGET
                + "（余 " + remaining + "）";
        field(bx, bw, y, "装备重建值", budget);

        add(LedgerPageGrid.rule(left.footer.left, left.footer.top + 1f,
                left.footer.width(), 0.22f));
        LedgerButton back = new LedgerButton(Chrome.Type.BLANK, "返回登记页", 5) {
            @Override protected void onClick() {
                super.onClick();
                LedgerTransitions.turn(LedgerBuildScene.this,
                        left.paper, LedgerRegistrationScene.class, false);
            }
        };
        back.textColor(LedgerEnvironment.FADED_INK);
        back.setRect(left.footer.left, left.footer.top + 3f,
                left.footer.width(), left.footer.height() - 3f);
        add(back);
    }

    private void buildDirectory() {
        float x = right.header.left;
        float w = right.header.width();

        RenderedTextBlock title = t("旧行装", 9, LedgerEnvironment.INK, (int) w);
        title.align(RenderedTextBlock.CENTER_ALIGN);
        title.setPos(x + (w - title.width()) / 2f, right.header.top);
        add(title);

        RenderedTextBlock note = t("逐栏核对，不必一次写完", 5,
                LedgerEnvironment.FADED_INK, (int) w);
        note.align(RenderedTextBlock.CENTER_ALIGN);
        note.setPos(x + (w - note.width()) / 2f, title.bottom() + 3f);
        add(note);
        add(LedgerPageGrid.rule(x + w * 0.10f,
                Math.min(right.header.bottom - 1f, note.bottom() + 4f), w * 0.80f, 0.36f));

        float rowH = Math.min(14.2f, (right.body.height() - 2f) / 7f);
        float y = right.body.top + 1f;

        y = directoryButton(y, rowH, "惯用兵器", weaponStatus(), true, new Runnable() {
            @Override public void run() {
                LedgerFlow.resetWeaponPicker();
                LedgerTransitions.turn(LedgerBuildScene.this,
                        right.paper, LedgerWeaponScene.class, false);
            }
        });
        y = directoryButton(y, rowH, "护甲", armorStatus(), true, new Runnable() {
            @Override public void run() {
                LedgerFlow.resetArmorPicker();
                LedgerTransitions.turn(LedgerBuildScene.this,
                        right.paper, LedgerArmorScene.class, true);
            }
        });
        y = directoryButton(y, rowH, "法杖", wandStatus(), true, new Runnable() {
            @Override public void run() {
                LedgerFlow.resetWandPicker();
                LedgerTransitions.turn(LedgerBuildScene.this,
                        right.paper, LedgerWandScene.class, true);
            }
        });
        y = directoryButton(y, rowH, "神器与戒指", accessoryStatus(), true, new Runnable() {
            @Override public void run() {
                LedgerFlow.resetAccessoryPicker();
                LedgerTransitions.turn(LedgerBuildScene.this,
                        right.paper, LedgerAccessoryScene.class, true);
            }
        });
        y = directoryButton(y, rowH, "随身饰品", trinketStatus(), true, new Runnable() {
            @Override public void run() {
                LedgerFlow.resetTrinketPicker();
                LedgerTransitions.turn(LedgerBuildScene.this,
                        right.paper, LedgerTrinketScene.class, true);
            }
        });
        y = directoryButton(y, rowH, "天赋", talentStatus(), true, new Runnable() {
            @Override public void run() {
                LedgerFlow.resetTalentPicker();
                LedgerTransitions.turn(LedgerBuildScene.this,
                        right.paper, LedgerTalentScene.class, true);
            }
        });
        directoryButton(y, rowH, "构筑预设", "八处留档位", false, null);

        add(LedgerPageGrid.rule(right.footer.left, right.footer.top + 1f,
                right.footer.width(), 0.28f));
        LedgerButton seal = new LedgerButton(Chrome.Type.BLANK, "按现有记录留印", 6) {
            @Override protected void onClick() {
                super.onClick();
                ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
                boolean hasWeapon = LedgerFlow.draft().heroClass == HeroClass.MAGE
                        || (loadout != null && loadout.primaryWeaponId != null);
                boolean hasArmor = loadout != null && loadout.armorId != null;
                if (!hasWeapon || !hasArmor) {
                    LedgerBuildScene.this.add(new WndMessage("惯用兵器与护甲尚未核对完整。"));
                    return;
                }
                LedgerTransitions.turn(LedgerBuildScene.this,
                        right.paper, LedgerSealScene.class, true);
            }
        };
        seal.textColor(LedgerEnvironment.STAMP);
        seal.setRect(right.footer.left, right.footer.top + 3f,
                right.footer.width(), right.footer.height() - 3f);
        add(seal);
    }

    private float directoryButton(float y, float rowH, String label,
                                  String status, boolean ready, final Runnable action) {
        String text = label + "  ·  " + status;
        LedgerButton button = new LedgerButton(Chrome.Type.BLANK, text, 5) {
            @Override protected void onClick() {
                super.onClick();
                if (action != null) {
                    action.run();
                } else {
                    LedgerBuildScene.this.add(new WndMessage("这一栏还没有补写。"));
                }
            }
        };
        button.leftJustify = true;
        button.textColor(ready ? LedgerEnvironment.INK : LedgerEnvironment.FADED_INK);
        button.setRect(right.body.left + 4f, y,
                right.body.width() - 8f, rowH - 0.5f);
        add(button);
        return y + rowH;
    }

    private float field(float x, float width, float y, String labelText, String valueText) {
        RenderedTextBlock label = t(labelText, 5, LedgerEnvironment.FADED_INK, 42);
        label.setPos(x, y);
        add(label);
        RenderedTextBlock value = t(valueText == null ? "—" : valueText,
                6, LedgerEnvironment.INK, (int) width - 47);
        value.setPos(x + 47f, y - 1f);
        add(value);
        return Math.max(label.bottom(), value.bottom()) + 9f;
    }

    private String weaponStatus() {
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        if (loadout != null && loadout.primaryWeaponId != null) {
            return itemName(loadout.primaryWeaponId) + " +" + loadout.primaryWeaponLevel;
        }
        if (LedgerFlow.draft().heroClass == HeroClass.MAGE) {
            return "法师魔杖 +" + ReturningHeroBuildRules.MAGES_STAFF_RETURN_LEVEL;
        }
        return "未填写";
    }

    private String armorStatus() {
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        if (loadout == null || loadout.armorId == null) return "未填写";
        return itemName(loadout.armorId) + " +" + loadout.armorLevel;
    }

    private String wandStatus() {
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        if (loadout == null) return "未填写";
        if (LedgerFlow.draft().heroClass == HeroClass.MAGE
                && loadout.mageStaffImbuementId != null) {
            String text = itemName(loadout.mageStaffImbuementId) + "灌注";
            if (loadout.carriedWandId != null) text += " / 另携一杖";
            return text;
        }
        if (loadout.carriedWandId != null) {
            return itemName(loadout.carriedWandId) + " +" + loadout.carriedWandLevel;
        }
        return "未携带";
    }

    private String accessoryStatus() {
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        if (loadout == null) return "未填写";
        int count = 0;
        if (ReturningHeroHeritage.hasClassArtifact(LedgerFlow.draft().heroClass)
                || loadout.artifactSlotId != null) count++;
        if (loadout.miscSlotId != null) count++;
        if (loadout.ringSlotId != null) count++;
        return count == 0 ? "未填写" : "已录 " + count + " 格";
    }

    private String trinketStatus() {
        ReturningHeroLoadout loadout = LedgerFlow.draft().loadout;
        if (loadout == null || loadout.trinketId == null) return "未携带";
        return itemName(loadout.trinketId) + " +" + loadout.trinketLevel;
    }

    private String talentStatus() {
        if (LedgerFlow.draft().talentPlan == null
                || LedgerFlow.draft().talentPlan.isEmpty()) return "未填写";
        int total = 0;
        for (Map.Entry<String, Integer> entry : LedgerFlow.draft().talentPlan.entries().entrySet()) {
            total += Math.max(0, entry.getValue());
        }
        return "已分配 " + total + " 点";
    }

    private String itemName(String id) {
        Item item = ReturningHeroItemCatalog.newItem(id);
        return item == null ? "未知" : Messages.titleCase(item.trueName());
    }

    private RenderedTextBlock t(String value, int size, int color, int width) {
        return LedgerUI.text(value, size, color, width);
    }
}
