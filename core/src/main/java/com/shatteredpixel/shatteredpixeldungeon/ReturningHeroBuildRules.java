/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

/**
 * Central tuning values for reconstructing a hero who already completed the
 * original dungeon adventure.  Keep these values out of scene code so balance
 * changes can be made without rewriting the ledger UI.
 */
public final class ReturningHeroBuildRules {

    /** Increment whenever stored presets need to be revalidated. */
    public static final int RULESET_VERSION = 3;

    /** Shared budget for ordinary equipment reconstruction. */
    public static final int EQUIPMENT_RECONSTRUCTION_BUDGET = 15;

    /** Ring limits used by the reconstruction flow, not by normal gameplay. */
    public static final int MAX_RING_LEVEL = 3;
    public static final int MAX_COMBINED_RING_LEVEL = 4;

    /** Ordinary artifacts use their own growth budget. */
    public static final int ORDINARY_ARTIFACT_GROWTH_BUDGET = 8;
    public static final int MAX_SECOND_ARTIFACT_LEVEL_WITH_CLASS_ARTIFACT = 4;

    /** Class artifacts are career-defining heritage and return fully grown. */
    public static final int CLASS_ARTIFACT_RETURN_LEVEL = 10;

    /**
     * Echoes treats +7 as the returning Mage's completed staff state.  At this
     * level an imbued wand reaches the original 10-charge cap once the staff's
     * extra charge is applied.  This is a reconstruction rule, not a change to
     * the original MagesStaff upgrade mechanics.
     */
    public static final int MAGES_STAFF_RETURN_LEVEL = 7;

    /** Trinkets keep their independent alchemy economy. */
    public static final int TRINKET_ALCHEMY_RECONSTRUCTION_BUDGET = 25;

    /** Ordinary carried wands are optional extras, separate from the Mage staff. */
    public static final int CARRIED_WAND_BASE_COST = 2;
    public static final int MAX_CARRIED_WAND_LEVEL = 5;

    private ReturningHeroBuildRules() {
        // Utility class.
    }
}
