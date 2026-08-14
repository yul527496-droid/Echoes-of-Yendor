/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.*;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.*;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stable IDs for equipment that may appear in the returning-hero reconstruction.
 *
 * IDs are intentionally owned by Echoes instead of being derived from localized
 * names, enum ordinals, or UI array positions.  This lets saved presets survive
 * menu reordering and localization changes.
 */
public final class ReturningHeroItemCatalog {

    public enum Kind {
        MELEE_WEAPON,
        ARMOR,
        WAND,
        RING,
        ARTIFACT,
        TRINKET
    }

    public static final class Entry {
        public final String id;
        public final Kind kind;
        public final int tier;
        public final Class<? extends Item> itemClass;
        public final boolean selectable;

        private Entry(String id, Kind kind, int tier,
                      Class<? extends Item> itemClass, boolean selectable) {
            this.id = id;
            this.kind = kind;
            this.tier = tier;
            this.itemClass = itemClass;
            this.selectable = selectable;
        }
    }

    private static final Map<String, Entry> BY_ID = new LinkedHashMap<>();
    private static final Map<Class<? extends Item>, Entry> BY_CLASS = new LinkedHashMap<>();
    private static final Map<Kind, List<Entry>> BY_KIND = new LinkedHashMap<>();

    static {
        for (Kind kind : Kind.values()) BY_KIND.put(kind, new ArrayList<Entry>());

        // T1 is career heritage. It is catalogued for stable identity, but is
        // not offered in the normal returning-weapon picker.
        add("weapon.t1.worn_shortsword", Kind.MELEE_WEAPON, 1, WornShortsword.class, false);
        add("weapon.t1.mages_staff", Kind.MELEE_WEAPON, 1, MagesStaff.class, false);
        add("weapon.t1.dagger", Kind.MELEE_WEAPON, 1, Dagger.class, false);
        add("weapon.t1.gloves", Kind.MELEE_WEAPON, 1, Gloves.class, false);
        add("weapon.t1.rapier", Kind.MELEE_WEAPON, 1, Rapier.class, false);
        add("weapon.t1.cudgel", Kind.MELEE_WEAPON, 1, Cudgel.class, false);

        add("weapon.t2.shortsword", Kind.MELEE_WEAPON, 2, Shortsword.class, true);
        add("weapon.t2.hand_axe", Kind.MELEE_WEAPON, 2, HandAxe.class, true);
        add("weapon.t2.spear", Kind.MELEE_WEAPON, 2, Spear.class, true);
        add("weapon.t2.quarterstaff", Kind.MELEE_WEAPON, 2, Quarterstaff.class, true);
        add("weapon.t2.dirk", Kind.MELEE_WEAPON, 2, Dirk.class, true);
        add("weapon.t2.sickle", Kind.MELEE_WEAPON, 2, Sickle.class, true);

        add("weapon.t3.sword", Kind.MELEE_WEAPON, 3, Sword.class, true);
        add("weapon.t3.mace", Kind.MELEE_WEAPON, 3, Mace.class, true);
        add("weapon.t3.scimitar", Kind.MELEE_WEAPON, 3, Scimitar.class, true);
        add("weapon.t3.round_shield", Kind.MELEE_WEAPON, 3, RoundShield.class, true);
        add("weapon.t3.sai", Kind.MELEE_WEAPON, 3, Sai.class, true);
        add("weapon.t3.whip", Kind.MELEE_WEAPON, 3, Whip.class, true);

        add("weapon.t4.longsword", Kind.MELEE_WEAPON, 4, Longsword.class, true);
        add("weapon.t4.battle_axe", Kind.MELEE_WEAPON, 4, BattleAxe.class, true);
        add("weapon.t4.flail", Kind.MELEE_WEAPON, 4, Flail.class, true);
        add("weapon.t4.runic_blade", Kind.MELEE_WEAPON, 4, RunicBlade.class, true);
        add("weapon.t4.assassins_blade", Kind.MELEE_WEAPON, 4, AssassinsBlade.class, true);
        add("weapon.t4.crossbow", Kind.MELEE_WEAPON, 4, Crossbow.class, true);
        add("weapon.t4.katana", Kind.MELEE_WEAPON, 4, Katana.class, true);

        add("weapon.t5.greatsword", Kind.MELEE_WEAPON, 5, Greatsword.class, true);
        add("weapon.t5.war_hammer", Kind.MELEE_WEAPON, 5, WarHammer.class, true);
        add("weapon.t5.glaive", Kind.MELEE_WEAPON, 5, Glaive.class, true);
        add("weapon.t5.greataxe", Kind.MELEE_WEAPON, 5, Greataxe.class, true);
        add("weapon.t5.greatshield", Kind.MELEE_WEAPON, 5, Greatshield.class, true);
        add("weapon.t5.gauntlet", Kind.MELEE_WEAPON, 5, Gauntlet.class, true);
        add("weapon.t5.war_scythe", Kind.MELEE_WEAPON, 5, WarScythe.class, true);

        add("armor.t1.cloth", Kind.ARMOR, 1, ClothArmor.class, false);
        add("armor.t2.leather", Kind.ARMOR, 2, LeatherArmor.class, true);
        add("armor.t3.mail", Kind.ARMOR, 3, MailArmor.class, true);
        add("armor.t4.scale", Kind.ARMOR, 4, ScaleArmor.class, true);
        add("armor.t5.plate", Kind.ARMOR, 5, PlateArmor.class, true);

        add("wand.magic_missile", Kind.WAND, 0, WandOfMagicMissile.class, true);
        add("wand.lightning", Kind.WAND, 0, WandOfLightning.class, true);
        add("wand.disintegration", Kind.WAND, 0, WandOfDisintegration.class, true);
        add("wand.fireblast", Kind.WAND, 0, WandOfFireblast.class, true);
        add("wand.corrosion", Kind.WAND, 0, WandOfCorrosion.class, true);
        add("wand.blast_wave", Kind.WAND, 0, WandOfBlastWave.class, true);
        add("wand.living_earth", Kind.WAND, 0, WandOfLivingEarth.class, true);
        add("wand.frost", Kind.WAND, 0, WandOfFrost.class, true);
        add("wand.prismatic_light", Kind.WAND, 0, WandOfPrismaticLight.class, true);
        add("wand.warding", Kind.WAND, 0, WandOfWarding.class, true);
        add("wand.transfusion", Kind.WAND, 0, WandOfTransfusion.class, true);
        add("wand.corruption", Kind.WAND, 0, WandOfCorruption.class, true);
        add("wand.regrowth", Kind.WAND, 0, WandOfRegrowth.class, true);

        add("ring.accuracy", Kind.RING, 0, RingOfAccuracy.class, true);
        add("ring.arcana", Kind.RING, 0, RingOfArcana.class, true);
        add("ring.elements", Kind.RING, 0, RingOfElements.class, true);
        add("ring.energy", Kind.RING, 0, RingOfEnergy.class, true);
        add("ring.evasion", Kind.RING, 0, RingOfEvasion.class, true);
        add("ring.force", Kind.RING, 0, RingOfForce.class, true);
        add("ring.furor", Kind.RING, 0, RingOfFuror.class, true);
        add("ring.haste", Kind.RING, 0, RingOfHaste.class, true);
        add("ring.might", Kind.RING, 0, RingOfMight.class, true);
        add("ring.sharpshooting", Kind.RING, 0, RingOfSharpshooting.class, true);
        add("ring.tenacity", Kind.RING, 0, RingOfTenacity.class, true);
        add("ring.wealth", Kind.RING, 0, RingOfWealth.class, true);

        add("artifact.alchemists_toolkit", Kind.ARTIFACT, 0, AlchemistsToolkit.class, true);
        add("artifact.chalice_of_blood", Kind.ARTIFACT, 0, ChaliceOfBlood.class, true);
        add("artifact.cloak_of_shadows", Kind.ARTIFACT, 0, CloakOfShadows.class, false);
        add("artifact.dried_rose", Kind.ARTIFACT, 0, DriedRose.class, true);
        add("artifact.ethereal_chains", Kind.ARTIFACT, 0, EtherealChains.class, true);
        add("artifact.holy_tome", Kind.ARTIFACT, 0, HolyTome.class, false);
        add("artifact.horn_of_plenty", Kind.ARTIFACT, 0, HornOfPlenty.class, true);
        add("artifact.master_thieves_armband", Kind.ARTIFACT, 0, MasterThievesArmband.class, true);
        add("artifact.sandals_of_nature", Kind.ARTIFACT, 0, SandalsOfNature.class, true);
        add("artifact.skeleton_key", Kind.ARTIFACT, 0, SkeletonKey.class, true);
        add("artifact.talisman_of_foresight", Kind.ARTIFACT, 0, TalismanOfForesight.class, true);
        add("artifact.timekeepers_hourglass", Kind.ARTIFACT, 0, TimekeepersHourglass.class, true);
        add("artifact.unstable_spellbook", Kind.ARTIFACT, 0, UnstableSpellbook.class, true);

        add("trinket.rat_skull", Kind.TRINKET, 0, RatSkull.class, true);
        add("trinket.parchment_scrap", Kind.TRINKET, 0, ParchmentScrap.class, true);
        add("trinket.petrified_seed", Kind.TRINKET, 0, PetrifiedSeed.class, true);
        add("trinket.exotic_crystals", Kind.TRINKET, 0, ExoticCrystals.class, true);
        add("trinket.mossy_clump", Kind.TRINKET, 0, MossyClump.class, true);
        add("trinket.dimensional_sundial", Kind.TRINKET, 0, DimensionalSundial.class, true);
        add("trinket.thirteen_leaf_clover", Kind.TRINKET, 0, ThirteenLeafClover.class, true);
        add("trinket.trap_mechanism", Kind.TRINKET, 0, TrapMechanism.class, true);
        add("trinket.mimic_tooth", Kind.TRINKET, 0, MimicTooth.class, true);
        add("trinket.wondrous_resin", Kind.TRINKET, 0, WondrousResin.class, true);
        add("trinket.eye_of_newt", Kind.TRINKET, 0, EyeOfNewt.class, true);
        add("trinket.salt_cube", Kind.TRINKET, 0, SaltCube.class, true);
        add("trinket.vial_of_blood", Kind.TRINKET, 0, VialOfBlood.class, true);
        add("trinket.shard_of_oblivion", Kind.TRINKET, 0, ShardOfOblivion.class, true);
        add("trinket.chaotic_censer", Kind.TRINKET, 0, ChaoticCenser.class, true);
        add("trinket.ferret_tuft", Kind.TRINKET, 0, FerretTuft.class, true);
        add("trinket.cracked_spyglass", Kind.TRINKET, 0, CrackedSpyglass.class, true);

        for (Kind kind : Kind.values()) {
            BY_KIND.put(kind, Collections.unmodifiableList(BY_KIND.get(kind)));
        }
    }

    private ReturningHeroItemCatalog() {
        // Utility class.
    }

    private static void add(String id, Kind kind, int tier,
                            Class<? extends Item> itemClass, boolean selectable) {
        Entry entry = new Entry(id, kind, tier, itemClass, selectable);
        if (BY_ID.put(id, entry) != null) {
            throw new IllegalStateException("Duplicate returning item id: " + id);
        }
        if (BY_CLASS.put(itemClass, entry) != null) {
            throw new IllegalStateException("Duplicate returning item class: " + itemClass.getName());
        }
        BY_KIND.get(kind).add(entry);
    }

    public static Entry byId(String id) {
        return id == null ? null : BY_ID.get(id);
    }

    public static Entry byClass(Class<? extends Item> itemClass) {
        return itemClass == null ? null : BY_CLASS.get(itemClass);
    }

    public static String idForClass(Class<? extends Item> itemClass) {
        Entry entry = byClass(itemClass);
        return entry == null ? null : entry.id;
    }

    /** Creates a fresh item instance for a stable ID, or null for an unknown ID. */
    public static Item newItem(String id) {
        Entry entry = byId(id);
        return entry == null ? null : Reflection.newInstance(entry.itemClass);
    }

    public static List<Entry> entries(Kind kind) {
        List<Entry> entries = BY_KIND.get(kind);
        return entries == null ? Collections.<Entry>emptyList() : entries;
    }

    public static List<Entry> selectableEntries(Kind kind) {
        List<Entry> result = new ArrayList<>();
        for (Entry entry : entries(kind)) {
            if (entry.selectable) result.add(entry);
        }
        return result;
    }

    public static boolean isSelectable(String id, Kind kind) {
        Entry entry = byId(id);
        return entry != null && entry.selectable && entry.kind == kind;
    }
}
