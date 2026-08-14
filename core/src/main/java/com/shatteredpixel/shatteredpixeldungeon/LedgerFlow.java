package com.shatteredpixel.shatteredpixeldungeon;

public final class LedgerFlow {
    private static ReturningHeroProfile draft = new ReturningHeroProfile();

    private static int weaponTier = 2;
    private static int weaponPickerStage = 0;
    private static String weaponCandidateId;
    private static int weaponCandidateLevel;

    private static int armorPickerStage = 0;
    private static String armorCandidateId;
    private static int armorCandidateLevel;

    private static int choicePage = 0;
    private static int wandPickerStage = 0;
    private static String wandCandidateId;
    private static int wandCandidateLevel;

    private static int accessoryPickerStage = 0;
    private static int accessoryTarget = 0;
    private static String accessoryCandidateId;
    private static int accessoryCandidateLevel;

    private static int trinketPickerStage = 0;
    private static String trinketCandidateId;
    private static int trinketCandidateLevel;

    private static int talentTier = 0;
    private static boolean heroPathReturnToBuild;
    private static boolean talentReturnToBuild;

    private LedgerFlow() {}

    public static ReturningHeroProfile draft() { return draft; }

    public static ReturningHeroProfile resetDraft() {
        draft = new ReturningHeroProfile();
        resetWeaponPicker();
        resetArmorPicker();
        resetWandPicker();
        resetAccessoryPicker();
        resetTrinketPicker();
        resetTalentPicker();
        heroPathReturnToBuild = false;
        talentReturnToBuild = false;
        return draft;
    }

    /** Transient picker state only; never serialized into a character build. */
    public static int weaponTier() { return weaponTier; }
    public static void weaponTier(int value) { weaponTier = Math.max(1, Math.min(value, 5)); }

    /** 0=tier list, 1=weapon list, 2=upgrade level. */
    public static int weaponPickerStage() { return weaponPickerStage; }
    public static void weaponPickerStage(int value) { weaponPickerStage = Math.max(0, Math.min(value, 2)); }
    public static String weaponCandidateId() { return weaponCandidateId; }
    public static void weaponCandidateId(String value) { weaponCandidateId = value; }
    public static int weaponCandidateLevel() { return weaponCandidateLevel; }
    public static void weaponCandidateLevel(int value) { weaponCandidateLevel = Math.max(0, Math.min(value, 7)); }

    public static void resetWeaponPicker() {
        weaponTier = 2;
        weaponPickerStage = 0;
        weaponCandidateId = null;
        weaponCandidateLevel = 0;
    }

    /** 0=armor list, 1=upgrade level. */
    public static int armorPickerStage() { return armorPickerStage; }
    public static void armorPickerStage(int value) { armorPickerStage = Math.max(0, Math.min(value, 1)); }
    public static String armorCandidateId() { return armorCandidateId; }
    public static void armorCandidateId(String value) { armorCandidateId = value; }
    public static int armorCandidateLevel() { return armorCandidateLevel; }
    public static void armorCandidateLevel(int value) { armorCandidateLevel = Math.max(0, Math.min(value, 6)); }

    public static void resetArmorPicker() {
        armorPickerStage = 0;
        armorCandidateId = null;
        armorCandidateLevel = 0;
    }

    /** Shared transient page index for long item lists. */
    public static int choicePage() { return choicePage; }
    public static void choicePage(int value) { choicePage = Math.max(0, value); }

    /** 0=wand overview, 1=staff imbuement list, 2=carried wand list, 3=carried wand level. */
    public static int wandPickerStage() { return wandPickerStage; }
    public static void wandPickerStage(int value) { wandPickerStage = Math.max(0, Math.min(value, 3)); }
    public static String wandCandidateId() { return wandCandidateId; }
    public static void wandCandidateId(String value) { wandCandidateId = value; }
    public static int wandCandidateLevel() { return wandCandidateLevel; }
    public static void wandCandidateLevel(int value) {
        wandCandidateLevel = Math.max(0, Math.min(value, ReturningHeroBuildRules.MAX_CARRIED_WAND_LEVEL));
    }

    public static void resetWandPicker() {
        choicePage = 0;
        wandPickerStage = 0;
        wandCandidateId = null;
        wandCandidateLevel = 0;
    }

    /**
     * Accessory picker stages: 0=three-slot overview, 1=item list, 2=level list.
     * Targets: 0=artifact slot, 1=misc artifact, 2=misc ring, 3=ring slot.
     */
    public static int accessoryPickerStage() { return accessoryPickerStage; }
    public static void accessoryPickerStage(int value) { accessoryPickerStage = Math.max(0, Math.min(value, 2)); }
    public static int accessoryTarget() { return accessoryTarget; }
    public static void accessoryTarget(int value) { accessoryTarget = Math.max(0, Math.min(value, 3)); }
    public static String accessoryCandidateId() { return accessoryCandidateId; }
    public static void accessoryCandidateId(String value) { accessoryCandidateId = value; }
    public static int accessoryCandidateLevel() { return accessoryCandidateLevel; }
    public static void accessoryCandidateLevel(int value) { accessoryCandidateLevel = Math.max(0, Math.min(value, 10)); }

    public static void resetAccessoryPicker() {
        choicePage = 0;
        accessoryPickerStage = 0;
        accessoryTarget = 0;
        accessoryCandidateId = null;
        accessoryCandidateLevel = 0;
    }

    /** 0=trinket list, 1=trinket level. */
    public static int trinketPickerStage() { return trinketPickerStage; }
    public static void trinketPickerStage(int value) { trinketPickerStage = Math.max(0, Math.min(value, 1)); }
    public static String trinketCandidateId() { return trinketCandidateId; }
    public static void trinketCandidateId(String value) { trinketCandidateId = value; }
    public static int trinketCandidateLevel() { return trinketCandidateLevel; }
    public static void trinketCandidateLevel(int value) { trinketCandidateLevel = Math.max(0, Math.min(value, 3)); }

    public static void resetTrinketPicker() {
        choicePage = 0;
        trinketPickerStage = 0;
        trinketCandidateId = null;
        trinketCandidateLevel = 0;
    }

    /** 0=talent tier overview, 1..4=editing that tier. */
    public static int talentTier() { return talentTier; }
    public static void talentTier(int value) { talentTier = Math.max(0, Math.min(value, 4)); }
    public static void resetTalentPicker() { talentTier = 0; }

    /** True when the subclass/ability picker was opened from the build directory. */
    public static boolean heroPathReturnToBuild() { return heroPathReturnToBuild; }
    public static void heroPathReturnToBuild(boolean value) { heroPathReturnToBuild = value; }

    /** True when leaving the talent ledger should return to the build directory. */
    public static boolean talentReturnToBuild() { return talentReturnToBuild; }
    public static void talentReturnToBuild(boolean value) { talentReturnToBuild = value; }
}
