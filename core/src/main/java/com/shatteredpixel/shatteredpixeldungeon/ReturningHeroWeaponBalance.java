/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

/**
 * Small, reconstruction-only corrections for melee weapons whose original
 * mechanics differ materially from the generic tier damage curve.
 *
 * The tier/upgrade tables in ReturningHeroBuildCost already carry most of the
 * price.  These values are therefore deliberately conservative: original SPD
 * weapons usually trade raw damage for accuracy, speed, reach or defence, so we
 * only charge when a weapon keeps a clear net advantage after that trade.  A
 * negative value is likewise used only where the generic tier table noticeably
 * over-prices the weapon before its unusual scaling catches up.
 *
 * This class never changes the weapon itself.  It only affects the cost of
 * reconstructing a past build in the Echoes ledger.
 */
public final class ReturningHeroWeaponBalance {

    private ReturningHeroWeaponBalance() {
        // Utility class.
    }

    public static int featureAdjustment(String itemId, int level) {
        if (itemId == null || level < 0) return 0;

        switch (itemId) {
            // T1 class heritage ---------------------------------------------------
            // Gloves attack at 2x speed. Their raw per-hit curve is heavily cut,
            // but sustained output and on-hit interactions pull ahead as upgrades
            // accumulate, so the late levels need a modest surcharge.
            case "weapon.t1.gloves":
                if (level >= 5) return 2;
                return level >= 2 ? 1 : 0;

            // Dagger, rapier and cudgel exchange raw damage for surprise,
            // defence or accuracy. The reduced T1 curve already prices that trade.
            case "weapon.t1.worn_shortsword":
            case "weapon.t1.dagger":
            case "weapon.t1.rapier":
            case "weapon.t1.cudgel":
                return 0;

            // T2 -----------------------------------------------------------------
            // Hand axe trades 20% max damage for +32% accuracy.  Once upgraded,
            // the unchanged damage scaling lets the accuracy advantage pull ahead.
            case "weapon.t2.hand_axe":
                return level >= 3 ? 1 : 0;

            // Spear: 2-tile reach and higher damage are paid for by 1.5x delay.
            // Quarterstaff: lower damage is paid back as +2 defence.
            // Dirk: lower damage is paid back by its stronger surprise rolls.
            // Sickle: high max damage is paid for by 0.68x accuracy.
            case "weapon.t2.spear":
            case "weapon.t2.quarterstaff":
            case "weapon.t2.dirk":
            case "weapon.t2.sickle":
                return 0;

            // T3 -----------------------------------------------------------------
            // Mace trades max damage for +28% accuracy; at higher upgrades its
            // normal scaling makes the exchange slightly favourable.
            case "weapon.t3.mace":
                return level >= 4 ? 1 : 0;

            // Scimitar's 1.25x attack speed overtakes its lower max damage early.
            case "weapon.t3.scimitar":
                return level >= 2 ? 1 : 0;

            // Sai has 2x attack speed with a damage curve tuned down by less than
            // half in expected sustained output, leaving a modest net advantage.
            case "weapon.t3.sai":
                return 1;

            // Whip sacrifices substantial damage for three-tile reach.  Reach is
            // independent tactical utility, so retain a small surcharge.
            case "weapon.t3.whip":
                return 1;

            // Round shield's large damage sacrifice already pays for its blocking.
            case "weapon.t3.round_shield":
                return 0;

            // T4 -----------------------------------------------------------------
            // Battle axe is close to the generic curve until very high upgrades.
            case "weapon.t4.battle_axe":
                return level >= 6 ? 1 : 0;

            // Flail's large damage is counterbalanced by 0.8x accuracy and the
            // inability to make surprise attacks.
            case "weapon.t4.flail":
                return 0;

            // Runic blade starts below a normal T4 and only catches it around +5.
            case "weapon.t4.runic_blade":
                return level <= 2 ? -1 : 0;

            // Assassin's blade exchanges normal damage for surprise reliability.
            case "weapon.t4.assassins_blade":
                return 0;

            // Crossbow is a weaker melee weapon but permanently upgrades darts;
            // this ranged synergy is useful beyond its own melee damage curve.
            case "weapon.t4.crossbow":
                return 1;

            // Katana exchanges normal damage for +3 defence.
            case "weapon.t4.katana":
                return 0;

            // T5 -----------------------------------------------------------------
            // War hammer's +20% accuracy broadly pays for its lower max damage.
            case "weapon.t5.war_hammer":
                return 0;

            // Glaive's 2-tile reach and high damage are balanced by 1.5x delay.
            case "weapon.t5.glaive":
                return 0;

            // Greataxe has a very large base-damage premium.  Its +2 strength
            // requirement matters in the original run, but matters much less to a
            // completed returning hero, so some of that premium must be priced.
            case "weapon.t5.greataxe":
                return level <= 2 ? 2 : 1;

            // Greatshield gives enormous defence but sacrifices enough damage that
            // the ordinary T5 reconstruction price already captures the trade.
            case "weapon.t5.greatshield":
                return 0;

            // Gauntlet retains a modest sustained-output edge while attacking at
            // 2x speed, which also improves several on-hit interactions.
            case "weapon.t5.gauntlet":
                return 1;

            // War scythe's high max damage is offset by 0.8x accuracy.
            case "weapon.t5.war_scythe":
                return 0;

            // Generic swords and any future entry default to the tier curve until
            // explicitly reviewed.  Unknown IDs are still rejected by BuildCost.
            default:
                return 0;
        }
    }
}
