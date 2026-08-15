/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TrainingRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrainingDummy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrainingMentor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TrainingTarget;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

/**
 * Complete state machine for the optional pre-dungeon memory tutorial.
 *
 * The controller deliberately observes real game state instead of simulating it:
 * items use the normal inventory/equipment code, ScrollOfIdentify performs the
 * normal item selection, wands use normal targeting, and the final enemies are
 * ordinary mobs with deliberately softened stats.
 */
public class TrainingTutorialController implements Bundlable {

    public enum Stage {
        INTRO,
        MOVE_TO_MENTOR,
        EQUIPMENT,
        DUMMY,
        TURN_LESSON,
        HEALING,
        IDENTIFY_RING,
        ARTIFACT,
        WAND,
        FINAL_TRIAL,
        EXIT,
        MOUTH,
        COMPLETE
    }

    private static final String STAGE = "stage";
    private static final String EQUIPMENT_INFO_SEEN = "equipment_info_seen";
    private static final String ARTIFACT_INFO_SEEN = "artifact_info_seen";
    private static final String CONTROLLED_DAMAGE_APPLIED = "controlled_damage_applied";
    private static final String HP_AFTER_DAMAGE = "hp_after_damage";
    private static final String WAND_HIT = "wand_hit";
    private static final String FINAL_SPAWNED = "final_spawned";

    private static final float CAMERA_PAN_INTENSITY = 3.5f;
    private static final int DIALOGUE_Y_OFFSET = 34;

    private static final int EQUIPMENT_WEAPON_X = 15;
    private static final int EQUIPMENT_WEAPON_Y = 16;
    private static final int EQUIPMENT_ARMOR_X = 16;
    private static final int EQUIPMENT_ARMOR_Y = 17;

    private static final int RING_X = 16;
    private static final int RING_Y = 11;
    private static final int SCROLL_X = 17;
    private static final int SCROLL_Y = 11;
    private static final int ARTIFACT_X = 17;
    private static final int ARTIFACT_Y = 12;
    private static final int WAND_X = 18;
    private static final int WAND_Y = 11;

    private static final int FINAL_RAT_A_X = 11;
    private static final int FINAL_RAT_A_Y = 7;
    private static final int FINAL_RAT_B_X = 14;
    private static final int FINAL_RAT_B_Y = 7;

    private transient TrainingGroundLevel level;
    private Stage stage = Stage.INTRO;

    private boolean equipmentInfoSeen;
    private boolean artifactInfoSeen;
    private boolean controlledDamageApplied;
    private int hpAfterDamage;
    private boolean wandHit;
    private boolean finalSpawned;

    // Render/UI state is intentionally transient. If a save is restored while a
    // blocking beat was open, the current stage simply presents it again.
    private transient boolean windowOpen;
    private transient Stage objectiveSyncedFor;

    public TrainingTutorialController() {
    }

    public void bind(TrainingGroundLevel level) {
        this.level = level;
        this.windowOpen = false;
        this.objectiveSyncedFor = null;
    }

    public Stage stage() {
        return stage;
    }

    /** Called by the mentor's normal actor tick. */
    public void tick(TrainingMentor mentor) {
        if (!validSession()) return;

        syncObjective();

        switch (stage) {
            case INTRO:
                if (!windowOpen) showIntro(mentor);
                break;

            case MOVE_TO_MENTOR:
                if (level.distance(mentor.pos, Dungeon.hero.pos) <= 2) {
                    enterEquipment(mentor);
                }
                break;

            case EQUIPMENT:
                ensureEquipmentItems();
                if (equipmentComplete()) enterDummy();
                break;

            case DUMMY:
                armDummy(true);
                break;

            case TURN_LESSON:
                if (!windowOpen) showTurnLesson();
                break;

            case HEALING:
                ensureHealingPotion();
                if (healingComplete()) enterIdentifyRing();
                break;

            case IDENTIFY_RING:
                ensureRingAndScroll();
                if (ringComplete()) enterArtifact();
                break;

            case ARTIFACT:
                ensureArtifact();
                if (artifactComplete()) enterWand();
                break;

            case WAND:
                ensureWand();
                armTargets(true);
                if (wandComplete()) enterFinalTrial();
                break;

            case FINAL_TRIAL:
                ensureFinalEnemies();
                if (finalTrialComplete()) enterExit();
                break;

            case MOUTH:
                if (!windowOpen) showMouthBeat();
                break;

            case EXIT:
            case COMPLETE:
            default:
                break;
        }
    }

    public void onMentorInteract(TrainingMentor mentor) {
        if (!validSession()) return;

        if (stage == Stage.INTRO && !windowOpen) {
            showIntro(mentor);
        } else if (stage == Stage.MOVE_TO_MENTOR) {
            enterEquipment(mentor);
        } else if (!windowOpen) {
            // Once the guided exchange is over, clicking the mentor gives a short
            // reminder without creating another mandatory dialogue chain.
            Game.runOnRenderThread(() -> GameScene.show(new WndOptions(
                    "老冒险者",
                    reminderForStage(),
                    "知道了"
            )));
        }
    }

    /** Called from WndInfoItem whenever the player opens a real item description. */
    public void onItemInspected(Item item) {
        if (item == null) return;

        if (stage == Stage.EQUIPMENT
                && (item instanceof WornShortsword || item instanceof ClothArmor)) {
            equipmentInfoSeen = true;
        } else if (stage == Stage.ARTIFACT && item instanceof TalismanOfForesight) {
            artifactInfoSeen = true;
        }
    }

    public static void notifyItemInspected(Item item) {
        if (Dungeon.level instanceof TrainingGroundLevel) {
            ((TrainingGroundLevel) Dungeon.level).tutorial().onItemInspected(item);
        }
    }

    /** Called by the training dummy after a real Hero melee attack reaches damage(). */
    public void onDummyHit() {
        if (stage != Stage.DUMMY) return;
        stage = Stage.TURN_LESSON;
        objectiveSyncedFor = null;
        syncObjective();
    }

    /** Called by a training target after a real Wand reaches damage(). */
    public void onTargetHit(Object source) {
        if (stage == Stage.WAND && source instanceof Wand) {
            wandHit = true;
        }
    }

    /** Invoked by the north REGULAR_EXIT transition instead of descending. */
    public void onDungeonMouth() {
        if (stage == Stage.EXIT) {
            stage = Stage.MOUTH;
            objectiveSyncedFor = null;
            syncObjective();
            showMouthBeat();
        } else if (stage != Stage.MOUTH && stage != Stage.COMPLETE) {
            GLog.w("还没完。先把训练场上的东西过一遍。");
        }
    }

    private void showIntro(TrainingMentor mentor) {
        if (windowOpen) return;
        windowOpen = true;

        Game.runOnRenderThread(() -> {
            if (!validSession()) {
                windowOpen = false;
                return;
            }

            if (mentor != null && mentor.sprite != null) {
                mentor.sprite.turnTo(mentor.pos, Dungeon.hero.pos);
                Camera.main.panTo(mentor.sprite.center(), CAMERA_PAN_INTENSITY);
            }

            WndOptions intro = lockedDialogue(
                    "老冒险者",
                    "老人抬起手，朝你招了两下。\n\n"
                            + "「新来的，先别往石阶那边走。过来。」\n\n"
                            + "他看了眼北边的入口，又看回你。\n\n"
                            + "「第一次下去？那就更别急。下面可没人等你站着翻半天行囊。」\n\n"
                            + "「过来。剑、甲、药，还有那些乱七八糟的东西，至少先认全。花不了几分钟。」\n\n"
                            + "移动：点击地面，或使用方向键 / WASD。",
                    "过去看看",
                    () -> {
                        stage = Stage.MOVE_TO_MENTOR;
                        objectiveSyncedFor = null;
                        windowOpen = false;
                        returnCameraToHero();
                        syncObjective();
                    }
            );
            GameScene.show(intro);
            intro.offset(0, DIALOGUE_Y_OFFSET);
            intro.boundOffsetWithMargin(4);
        });
    }

    private void enterEquipment(TrainingMentor mentor) {
        if (stage != Stage.MOVE_TO_MENTOR) return;

        stage = Stage.EQUIPMENT;
        objectiveSyncedFor = null;
        if (mentor != null && mentor.sprite != null) mentor.sprite.turnTo(mentor.pos, Dungeon.hero.pos);
        ensureEquipmentItems();
        syncObjective();
    }

    private boolean equipmentComplete() {
        Hero hero = Dungeon.hero;
        return equipmentInfoSeen
                && hero.belongings.weapon instanceof WornShortsword
                && hero.belongings.armor instanceof ClothArmor;
    }

    private void enterDummy() {
        stage = Stage.DUMMY;
        objectiveSyncedFor = null;
        armDummy(true);
        syncObjective();
    }

    private void showTurnLesson() {
        if (windowOpen) return;
        windowOpen = true;
        Game.runOnRenderThread(() -> {
            if (!validSession()) {
                windowOpen = false;
                return;
            }

            TrainingDummy dummy = findMob(TrainingDummy.class);
            if (dummy != null && dummy.sprite != null) {
                Camera.main.panTo(dummy.sprite.center(), CAMERA_PAN_INTENSITY);
            }

            WndOptions lesson = lockedDialogue(
                    "老冒险者",
                    "「停。」\n\n"
                            + "老人用鞋尖敲了敲木人的底座。\n\n"
                            + "「刚才那一下，用掉了你一个回合。真的敌人不会像木头一样站着给你想。」\n\n"
                            + "「走一步、挥一次剑、喝药、换东西——大多都得花时间。你动，它们也会动。」\n\n"
                            + "「先想，再动。记住这个，比记住哪把剑伤害高有用。」",
                    "记住了",
                    this::finishTurnLesson
            );
            GameScene.show(lesson);
            lesson.offset(0, DIALOGUE_Y_OFFSET);
            lesson.boundOffsetWithMargin(4);
        });
    }

    private void finishTurnLesson() {
        armDummy(false);

        if (!controlledDamageApplied && Dungeon.hero != null) {
            controlledDamageApplied = true;
            int damage = Math.min(6, Math.max(1, Dungeon.hero.HP - 1));
            Dungeon.hero.damage(damage, TrainingDummy.class);
            hpAfterDamage = Dungeon.hero.HP;
        }

        stage = Stage.HEALING;
        objectiveSyncedFor = null;
        windowOpen = false;
        ensureHealingPotion();
        returnCameraToHero();
        syncObjective();
    }

    private boolean healingComplete() {
        if (!controlledDamageApplied) return false;

        boolean potionStillExists = itemExists(PotionOfHealing.class);
        return !potionStillExists
                && (Dungeon.hero.buff(Healing.class) != null || Dungeon.hero.HP > hpAfterDamage);
    }

    private void enterIdentifyRing() {
        stage = Stage.IDENTIFY_RING;
        objectiveSyncedFor = null;
        ensureRingAndScroll();
        syncObjective();
    }

    private boolean ringComplete() {
        RingOfAccuracy ring = findItem(RingOfAccuracy.class);
        return ring != null && ring.isIdentified() && ring.isEquipped(Dungeon.hero);
    }

    private void enterArtifact() {
        stage = Stage.ARTIFACT;
        objectiveSyncedFor = null;
        ensureArtifact();
        syncObjective();
    }

    private boolean artifactComplete() {
        TalismanOfForesight artifact = findItem(TalismanOfForesight.class);
        return artifact != null && Dungeon.hero.belongings.contains(artifact) && artifactInfoSeen;
    }

    private void enterWand() {
        stage = Stage.WAND;
        objectiveSyncedFor = null;
        ensureWand();
        armTargets(true);
        syncObjective();
    }

    private boolean wandComplete() {
        WandOfMagicMissile wand = findItem(WandOfMagicMissile.class);
        return wand != null && Dungeon.quickslot.getSlot(wand) != -1 && wandHit;
    }

    private void enterFinalTrial() {
        stage = Stage.FINAL_TRIAL;
        objectiveSyncedFor = null;
        armTargets(false);
        ensureFinalEnemies();
        syncObjective();
    }

    private boolean finalTrialComplete() {
        if (!finalSpawned) return false;
        for (Mob mob : level.mobs) {
            if (mob instanceof TrainingRat && mob.isAlive()) return false;
        }
        return true;
    }

    private void enterExit() {
        stage = Stage.EXIT;
        objectiveSyncedFor = null;
        syncObjective();
    }

    private void showMouthBeat() {
        if (windowOpen || stage != Stage.MOUTH) return;
        windowOpen = true;

        Game.runOnRenderThread(() -> {
            if (!validSession()) {
                windowOpen = false;
                return;
            }

            Camera.main.panTo(
                    DungeonTilemap.tileCenterToWorld(level.dungeonMouthCell()),
                    CAMERA_PAN_INTENSITY
            );

            WndOptions finalBeat = lockedDialogue(
                    "地下城入口",
                    "石阶下面没有光。\n\n"
                            + "老人这一次没有再拦你。\n\n"
                            + "「看不懂的东西就先看说明；打不过就退；血不够就别硬撑。」\n\n"
                            + "他朝入口偏了偏头。\n\n"
                            + "「剩下的，活着回来再学。去吧。」",
                    "下去",
                    () -> {
                        stage = Stage.COMPLETE;
                        objectiveSyncedFor = null;
                        windowOpen = false;
                        GameScene.trainingObjective(null);
                        SequelGame.finishTrainingPreview();
                    }
            );
            GameScene.show(finalBeat);
            finalBeat.offset(0, DIALOGUE_Y_OFFSET);
            finalBeat.boundOffsetWithMargin(4);
        });
    }

    private void ensureEquipmentItems() {
        ensureItem(WornShortsword.class, () -> (Item) new WornShortsword().identify(),
                level.cellAt(EQUIPMENT_WEAPON_X, EQUIPMENT_WEAPON_Y));
        ensureItem(ClothArmor.class, () -> (Item) new ClothArmor().identify(),
                level.cellAt(EQUIPMENT_ARMOR_X, EQUIPMENT_ARMOR_Y));
    }

    private void ensureHealingPotion() {
        ensureItem(PotionOfHealing.class, () -> (Item) new PotionOfHealing().identify(), Dungeon.hero.pos);
    }

    private void ensureRingAndScroll() {
        ensureItem(RingOfAccuracy.class, RingOfAccuracy::new, level.cellAt(RING_X, RING_Y));
        ensureItem(ScrollOfIdentify.class, () -> (Item) new ScrollOfIdentify().identify(),
                level.cellAt(SCROLL_X, SCROLL_Y));
    }

    private void ensureArtifact() {
        ensureItem(TalismanOfForesight.class, () -> (Item) new TalismanOfForesight().identify(),
                level.cellAt(ARTIFACT_X, ARTIFACT_Y));
    }

    private void ensureWand() {
        ensureItem(WandOfMagicMissile.class, () -> (Item) new WandOfMagicMissile().identify(),
                level.cellAt(WAND_X, WAND_Y));
    }

    private interface ItemFactory {
        Item create();
    }

    private <T extends Item> void ensureItem(Class<T> type, ItemFactory factory, int cell) {
        if (!itemExists(type)) {
            Heap heap = level.drop(factory.create(), cell);
            if (heap.sprite != null) heap.sprite.drop();
        }
    }

    private boolean itemExists(Class<? extends Item> type) {
        return findItem(type) != null;
    }

    @SuppressWarnings("unchecked")
    private <T extends Item> T findItem(Class<T> type) {
        if (Dungeon.hero != null) {
            T held = Dungeon.hero.belongings.getItem(type);
            if (held != null) return held;
        }

        if (level != null && level.heaps != null) {
            for (Heap heap : level.heaps.valueList()) {
                for (Item item : heap.items) {
                    if (type.isInstance(item)) return (T) item;
                }
            }
        }
        return null;
    }

    private void armDummy(boolean armed) {
        TrainingDummy dummy = findMob(TrainingDummy.class);
        if (dummy != null) dummy.setArmed(armed);
    }

    private void armTargets(boolean armed) {
        if (level == null) return;
        for (Mob mob : level.mobs) {
            if (mob instanceof TrainingTarget) ((TrainingTarget) mob).setArmed(armed);
        }
    }

    private <T extends Mob> T findMob(Class<T> type) {
        if (level == null) return null;
        for (Mob mob : level.mobs) {
            if (type.isInstance(mob)) return type.cast(mob);
        }
        return null;
    }

    private void ensureFinalEnemies() {
        if (finalSpawned || level == null) return;
        finalSpawned = true;
        spawnTrainingRat(FINAL_RAT_A_X, FINAL_RAT_A_Y);
        spawnTrainingRat(FINAL_RAT_B_X, FINAL_RAT_B_Y);
    }

    private void spawnTrainingRat(int x, int y) {
        TrainingRat rat = new TrainingRat();
        rat.pos = level.cellAt(x, y);
        GameScene.add(rat);
    }

    private String objectiveForStage() {
        switch (stage) {
            case MOVE_TO_MENTOR:
                return "老冒险者：「过来，先别往石阶那边走。」\n目标：走到老冒险者身边";
            case EQUIPMENT:
                return "老冒险者：「那边一把旧剑，一件布甲。拿上。点开东西先看清楚，再往身上套。」\n目标：查看物品说明，并装备旧剑和布甲";
            case DUMMY:
                return "老冒险者：「行。现在去碰那个木人。砍一下就够。」\n目标：用装备好的武器攻击训练木人一次";
            case HEALING:
                return "木人的横杆弹回来，结结实实撞在你肩上。\n老冒险者：「现在知道血条为什么不是装饰了。把药喝了。」\n目标：拾取并使用治疗药剂";
            case IDENTIFY_RING:
                return "老冒险者：「地牢里的东西可不都肯报名字。先看看戒指，再用卷轴把它认出来。」\n目标：鉴定并装备那枚戒指";
            case ARTIFACT:
                return "老冒险者：「戒指至少还讲点规矩。神器就没那么客气了。别猜，拿起来看说明。」\n目标：拾取神器，并在行囊里打开它的说明";
            case WAND:
                return "老冒险者：「把法杖放进快捷栏。真打起来，你不会想每次都翻行囊。」\n目标：法杖放入快捷栏，并从快捷栏命中任意训练靶";
            case FINAL_TRIAL:
                return "老冒险者：「够了。现在我不告诉你按什么。那边两只老鼠，自己处理。」\n目标：击败练习区里的两只老鼠";
            case EXIT:
                return "老冒险者：「行了。下面不会有人替你把东西摆好。」\n目标：走上北面的地下城石阶";
            case TURN_LESSON:
            case INTRO:
            case MOUTH:
            case COMPLETE:
            default:
                return null;
        }
    }

    private String reminderForStage() {
        switch (stage) {
            case EQUIPMENT: return "「剑和甲就在旁边。点开看清楚，再装备。」";
            case DUMMY: return "「木人。砍一下，别跟它较劲。」";
            case HEALING: return "「先把血补回来。活着比省一瓶药重要。」";
            case IDENTIFY_RING: return "「未知的东西别靠猜。卷轴是拿来用的。」";
            case ARTIFACT: return "「神器各有各的规矩。先看说明。」";
            case WAND: return "「快捷栏，然后打靶。别隔着行囊施法。」";
            case FINAL_TRIAL: return "「两只老鼠。自己来。」";
            case EXIT: return "「去吧。石阶就在北边。」";
            default: return "「按刚才说的做。」";
        }
    }

    private void syncObjective() {
        if (objectiveSyncedFor == stage) return;
        objectiveSyncedFor = stage;
        final String text = objectiveForStage();
        Game.runOnRenderThread(() -> GameScene.trainingObjective(text));
    }

    private void returnCameraToHero() {
        if (Camera.main != null && Dungeon.hero != null && Dungeon.hero.sprite != null) {
            Camera.main.panTo(Dungeon.hero.sprite.center(), CAMERA_PAN_INTENSITY);
        }
    }

    private WndOptions lockedDialogue(String title, String message, String button, Runnable confirm) {
        return new WndOptions(title, message, button) {
            @Override
            protected void onSelect(int index) {
                confirm.run();
            }

            @Override
            public void onBackPressed() {
                // Important tutorial beats must be acknowledged explicitly.
            }
        };
    }

    private boolean validSession() {
        return level != null && Dungeon.level == level && Dungeon.hero != null && Dungeon.hero.isAlive();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(STAGE, stage.name());
        bundle.put(EQUIPMENT_INFO_SEEN, equipmentInfoSeen);
        bundle.put(ARTIFACT_INFO_SEEN, artifactInfoSeen);
        bundle.put(CONTROLLED_DAMAGE_APPLIED, controlledDamageApplied);
        bundle.put(HP_AFTER_DAMAGE, hpAfterDamage);
        bundle.put(WAND_HIT, wandHit);
        bundle.put(FINAL_SPAWNED, finalSpawned);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        String saved = bundle.getString(STAGE);
        try {
            stage = saved == null || saved.isEmpty() ? Stage.INTRO : Stage.valueOf(saved);
        } catch (IllegalArgumentException ignored) {
            stage = Stage.INTRO;
        }
        equipmentInfoSeen = bundle.getBoolean(EQUIPMENT_INFO_SEEN);
        artifactInfoSeen = bundle.getBoolean(ARTIFACT_INFO_SEEN);
        controlledDamageApplied = bundle.getBoolean(CONTROLLED_DAMAGE_APPLIED);
        hpAfterDamage = bundle.getInt(HP_AFTER_DAMAGE);
        wandHit = bundle.getBoolean(WAND_HIT);
        finalSpawned = bundle.getBoolean(FINAL_SPAWNED);
        windowOpen = false;
        objectiveSyncedFor = null;
    }
}
