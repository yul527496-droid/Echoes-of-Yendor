/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.TrainingGroundLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.TrainingTutorialController;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrainingMentorSprite;

/**
 * The veteran NPC itself stays deliberately small. Tutorial sequencing belongs
 * to TrainingTutorialController, not to the character class.
 */
public class TrainingMentor extends NPC {

    {
        spriteClass = TrainingMentorSprite.class;
        // The mentor gets the first chance to establish the opening camera beat
        // before a newly-created hero can act.
        actPriority = HERO_PRIO + 1;
    }

    @Override
    protected boolean act() {
        if (Dungeon.level instanceof TrainingGroundLevel && Dungeon.hero != null) {
            TrainingGroundLevel training = (TrainingGroundLevel) Dungeon.level;
            TrainingTutorialController tutorial = training.tutorial();
            tutorial.tick(this);

            // The controller creates the healing bottle on the hero's tile after
            // the mandatory turn lesson. Immediately move that one bottle to the
            // first free adjacent cell below/in front of the hero and use the
            // mature ItemSprite throw animation so it reads as the mentor tossing
            // help over, not as an item materialising from a chest.
            if (tutorial.stage() == TrainingTutorialController.Stage.HEALING) {
                moveHealingPotionInFront(training);
            }
        }
        spend(TICK);
        return true;
    }

    private void moveHealingPotionInFront(TrainingGroundLevel training) {
        Heap source = training.heaps.get(Dungeon.hero.pos);
        if (source == null || source.items == null || source.items.isEmpty()) return;

        Item potion = null;
        for (Item item : source.items) {
            if (item instanceof PotionOfHealing) {
                potion = item;
                break;
            }
        }
        if (potion == null) return;

        int target = healingDropCell(training);
        if (target == Dungeon.hero.pos) return;

        source.remove(potion);
        Heap dropped = training.drop(potion, target);
        if (dropped.sprite != null) {
            dropped.sprite.drop(pos);
        }
    }

    private int healingDropCell(TrainingGroundLevel training) {
        int hero = Dungeon.hero.pos;
        int width = training.width();

        // Screen-down is visually "in front" in this raised top-down perspective.
        // If that tile is occupied, fall back to the other three adjacent cells.
        int[] candidates = new int[]{hero + width, hero + 1, hero - 1, hero - width};
        int heroX = hero % width;

        for (int cell : candidates) {
            if (cell < 0 || cell >= training.map.length) continue;
            if (Math.abs((cell % width) - heroX) > 1) continue;
            if (!training.passable[cell] || training.solid[cell]) continue;
            if (Actor.findChar(cell) != null) continue;
            if (training.heaps.get(cell) != null) continue;
            return cell;
        }

        return hero;
    }

    @Override
    public boolean interact(Char c) {
        if (sprite != null) sprite.turnTo(pos, c.pos);
        if (c == Dungeon.hero && Dungeon.level instanceof TrainingGroundLevel) {
            ((TrainingGroundLevel) Dungeon.level).tutorial().onMentorInteract(this);
        }
        return true;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public String name() {
        return "老冒险者";
    }

    @Override
    public String description() {
        return "一位守在地下城入口营地附近的老练冒险者。灰发、旧围巾和磨损的装备都说明他在这里待了很久。";
    }
}
