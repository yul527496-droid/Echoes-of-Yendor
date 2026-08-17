/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegionAreaLevel;
import com.shatteredpixel.shatteredpixeldungeon.ui.TaskGuidanceToast;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

/**
 * Formal semi-open-region state for Echoes of Yendor.
 *
 * This deliberately runs in parallel to legacy SequelState. It is not a second ordered
 * phase enum: independent evidence, conclusions, memories, leads, world changes, place
 * knowledge, travel nodes, shortcuts and time can advance without imposing one global
 * story counter.
 */
public class RegionState extends Buff {

    public enum Area {
        MORNINGCREEK_TOWN,
        OLD_CROW_INN,
        ACT_ONE_RETURN
    }

    public enum Knowledge {
        UNKNOWN,
        HEARD_OF,
        DISCOVERED
    }

    public enum TimeBand {
        MORNING,
        AFTERNOON,
        EVENING,
        NIGHT
    }

    public enum EvidenceState {
        UNKNOWN,
        FOUND,
        VERIFIED
    }

    public enum ConclusionState {
        LOCKED,
        AVAILABLE,
        CONFIRMED
    }

    public enum WorldState {
        DEFAULT,
        CHANGED
    }

    public enum MemoryState {
        HIDDEN,
        TRACE,
        RECOVERED
    }

    public enum LeadState {
        UNKNOWN,
        AVAILABLE,
        ACTIVE,
        RESOLVED
    }

    public enum Evidence {
        FOUR_NAMES_REGISTRY,
        INN_FIXED_PARTY_RECORD,
        WAREHOUSE_PARTY_RECORD,
        CONTRACT_PARTY_RECORD,
        BRAN_STRUCTURE_TRACE,
        EILEEN_BODY_MEMORY,
        MAEVE_PATH_MARKS,
        DM300_SPATIAL_REVIEW,
        YOG_AFTERBATTLE_SURVIVORS
    }

    public enum Conclusion {
        FOUR_PERSON_EXPEDITION,
        FIXED_PARTY,
        FOUR_SURVIVED_YOG
    }

    public enum Memory {
        EILEEN_CARE,
        BRAN_INFLUENCE,
        MAEVE_FUTURE,
        DM300_FIGHT,
        YOG_AFTERBATTLE
    }

    public enum Lead {
        RAVENFEATHER_REGISTRY,
        OLD_CROW_INN,
        RIVER_WAREHOUSES,
        CONTRACT_ARCHIVE,
        OLD_POSSESSIONS_ROOM,
        DEEP_EVIDENCE
    }

    public enum Location {
        SOUTH_GATE,
        SOUTH_CARAVAN_APRON,
        CENTRAL_MARKET,
        MARKET_WELL,
        OLD_CROW_INN,
        OLD_CROW_REAR_YARD,
        STONE_BRIDGE,
        RAVENFEATHER_TOWER,
        RAVENFEATHER_REGISTRY,
        ARCHIVE_COURT,
        EAST_CLINIC,
        HERB_GARDEN,
        RIVER_WAREHOUSES,
        WAREHOUSE_LOADING_LANE,
        RIVER_LANDING,
        BLACKSMITH,
        PUBLIC_STABLES,
        EAST_BACK_ALLEY,
        NORTH_GATE,
        HUNTERS_ROAD_MOUTH,
        RIVERSIDE_ROAD_MOUTH,
        NORTH_MILL_PATH_MOUTH,
        SURFACE_ENTRANCE,
        OLD_KINGS_ROAD_SHRINE,
        OLD_MILL,
        ABANDONED_EXPEDITION_CAMP,
        MORNINGCREEK
    }

    public enum TravelNode {
        SURFACE_ENTRANCE,
        OLD_KINGS_ROAD_SHRINE,
        MORNINGCREEK_SOUTH_GATE,
        OLD_CROW_INN,
        RAVENFEATHER_REGISTRY,
        MORNINGCREEK_NORTH_GATE,
        OLD_MILL
    }

    public enum Shortcut {
        HUNTERS_ROAD,
        RIVERSIDE_ROAD,
        NORTH_MILL_PATH
    }

    public enum ShortcutState {
        UNKNOWN,
        FOUND,
        OPEN
    }

    private static final String TIME = "region_time";
    private static final String LOCATION_KNOWLEDGE = "region_location_knowledge";
    private static final String EVIDENCE = "region_evidence";
    private static final String CONCLUSIONS = "region_conclusions";
    private static final String WORLD = "region_world";
    private static final String MEMORIES = "region_memories";
    private static final String LEADS = "region_leads";
    private static final String TRAVEL = "region_travel";
    private static final String SHORTCUTS = "region_shortcuts";
    private static final String FORMAL_ACT_ONE = "region_formal_act_one";
    private static final String VISITED_PREFIX = "region_visited_";
    private static final String MAPPED_PREFIX = "region_mapped_";

    private static volatile boolean hudSyncPending;

    private int time = TimeBand.AFTERNOON.ordinal();
    private int[] locationKnowledge = new int[Location.values().length];
    private int[] evidence = new int[Evidence.values().length];
    private int[] conclusions = new int[Conclusion.values().length];
    private int[] world = new int[Location.values().length];
    private int[] memories = new int[Memory.values().length];
    private int[] leads = new int[Lead.values().length];
    private int[] travel = new int[TravelNode.values().length];
    private int[] shortcuts = new int[Shortcut.values().length];
    private int[][] visitedByArea = new int[Area.values().length][];
    private int[][] mappedByArea = new int[Area.values().length][];
    private boolean formalActOne;

    public RegionState() {
        for (int i = 0; i < shortcuts.length; i++) shortcuts[i] = ShortcutState.UNKNOWN.ordinal();
    }

    public static RegionState get() {
        if (Dungeon.hero == null) return null;
        RegionState state = Dungeon.hero.buff(RegionState.class);
        if (state == null) state = Buff.affect(Dungeon.hero, RegionState.class);
        state.seedPrototypeKnowledge();
        return state;
    }

    public static RegionState current() {
        return Dungeon.hero == null ? null : Dungeon.hero.buff(RegionState.class);
    }

    /** Formal Act 1 starts without the old Region prototype's pre-seeded Inn/Registry leads. */
    public void beginFormalActOne() {
        formalActOne = true;
        locationKnowledge[Location.OLD_CROW_INN.ordinal()] = Knowledge.UNKNOWN.ordinal();
        locationKnowledge[Location.RAVENFEATHER_REGISTRY.ordinal()] = Knowledge.UNKNOWN.ordinal();
        leads[Lead.OLD_CROW_INN.ordinal()] = LeadState.UNKNOWN.ordinal();
        leads[Lead.RAVENFEATHER_REGISTRY.ordinal()] = LeadState.UNKNOWN.ordinal();
    }

    private void seedPrototypeKnowledge() {
        if (formalActOne) return;
        // Archived prototype/testing behavior remains intact outside the formal Act 1 start.
        hear(Location.OLD_CROW_INN);
        hear(Location.RAVENFEATHER_REGISTRY);
        setLead(Lead.OLD_CROW_INN, LeadState.AVAILABLE);
        setLead(Lead.RAVENFEATHER_REGISTRY, LeadState.AVAILABLE);
    }

    public Knowledge knowledge(Location location) {
        if (location == null) return Knowledge.UNKNOWN;
        return enumAt(Knowledge.values(), locationKnowledge, location.ordinal(), Knowledge.UNKNOWN);
    }

    public boolean hear(Location location) {
        if (location == null || knowledge(location) != Knowledge.UNKNOWN) return false;
        locationKnowledge[location.ordinal()] = Knowledge.HEARD_OF.ordinal();
        return true;
    }

    public boolean discover(Location location) {
        if (location == null || knowledge(location) == Knowledge.DISCOVERED) return false;
        locationKnowledge[location.ordinal()] = Knowledge.DISCOVERED.ordinal();
        unlockInfrastructureFor(location);
        return true;
    }

    private void unlockInfrastructureFor(Location location) {
        switch (location) {
            case SOUTH_GATE:
                unlockTravel(TravelNode.MORNINGCREEK_SOUTH_GATE);
                break;
            case OLD_CROW_INN:
                unlockTravel(TravelNode.OLD_CROW_INN);
                break;
            case RAVENFEATHER_REGISTRY:
                unlockTravel(TravelNode.RAVENFEATHER_REGISTRY);
                break;
            case NORTH_GATE:
                unlockTravel(TravelNode.MORNINGCREEK_NORTH_GATE);
                break;
            case SURFACE_ENTRANCE:
                unlockTravel(TravelNode.SURFACE_ENTRANCE);
                break;
            case OLD_KINGS_ROAD_SHRINE:
                unlockTravel(TravelNode.OLD_KINGS_ROAD_SHRINE);
                break;
            case OLD_MILL:
                unlockTravel(TravelNode.OLD_MILL);
                break;
            case HUNTERS_ROAD_MOUTH:
                setShortcut(Shortcut.HUNTERS_ROAD, ShortcutState.FOUND);
                break;
            case RIVERSIDE_ROAD_MOUTH:
                setShortcut(Shortcut.RIVERSIDE_ROAD, ShortcutState.FOUND);
                break;
            case NORTH_MILL_PATH_MOUTH:
                setShortcut(Shortcut.NORTH_MILL_PATH, ShortcutState.FOUND);
                break;
        }
    }

    public EvidenceState evidence(Evidence key) {
        return enumAt(EvidenceState.values(), evidence, key.ordinal(), EvidenceState.UNKNOWN);
    }

    public void setEvidence(Evidence key, EvidenceState state) {
        if (key != null && state != null) evidence[key.ordinal()] = state.ordinal();
        refreshDerivedConclusions();
    }

    public ConclusionState conclusion(Conclusion key) {
        return enumAt(ConclusionState.values(), conclusions, key.ordinal(), ConclusionState.LOCKED);
    }

    public void confirmConclusion(Conclusion key) {
        if (key != null && conclusion(key) == ConclusionState.AVAILABLE) {
            conclusions[key.ordinal()] = ConclusionState.CONFIRMED.ordinal();
        }
    }

    private void refreshDerivedConclusions() {
        if (evidence(Evidence.FOUR_NAMES_REGISTRY) == EvidenceState.VERIFIED) {
            makeAvailable(Conclusion.FOUR_PERSON_EXPEDITION);
        }

        int fixedPartyProofs = 0;
        if (evidence(Evidence.INN_FIXED_PARTY_RECORD) == EvidenceState.VERIFIED) fixedPartyProofs++;
        if (evidence(Evidence.WAREHOUSE_PARTY_RECORD) == EvidenceState.VERIFIED) fixedPartyProofs++;
        if (evidence(Evidence.CONTRACT_PARTY_RECORD) == EvidenceState.VERIFIED) fixedPartyProofs++;
        if (fixedPartyProofs >= 2) makeAvailable(Conclusion.FIXED_PARTY);

        if (evidence(Evidence.YOG_AFTERBATTLE_SURVIVORS) == EvidenceState.VERIFIED) {
            makeAvailable(Conclusion.FOUR_SURVIVED_YOG);
        }
    }

    private void makeAvailable(Conclusion key) {
        if (conclusion(key) == ConclusionState.LOCKED) {
            conclusions[key.ordinal()] = ConclusionState.AVAILABLE.ordinal();
        }
    }

    public MemoryState memory(Memory key) {
        return enumAt(MemoryState.values(), memories, key.ordinal(), MemoryState.HIDDEN);
    }

    public void setMemory(Memory key, MemoryState state) {
        if (key != null && state != null) memories[key.ordinal()] = state.ordinal();
    }

    public LeadState lead(Lead key) {
        return enumAt(LeadState.values(), leads, key.ordinal(), LeadState.UNKNOWN);
    }

    public void setLead(Lead key, LeadState state) {
        if (key != null && state != null && state.ordinal() > leads[key.ordinal()]) {
            leads[key.ordinal()] = state.ordinal();
        }
    }

    public WorldState worldState(Location key) {
        return enumAt(WorldState.values(), world, key.ordinal(), WorldState.DEFAULT);
    }

    public void changeWorldState(Location key) {
        if (key != null) world[key.ordinal()] = WorldState.CHANGED.ordinal();
    }

    public TimeBand timeBand() {
        int safe = Math.max(0, Math.min(TimeBand.values().length - 1, time));
        return TimeBand.values()[safe];
    }

    public void advanceTime(int bands) {
        if (bands <= 0) return;
        time = Math.floorMod(time + bands, TimeBand.values().length);
        syncHud();
    }

    public boolean travelUnlocked(TravelNode node) {
        return node != null && travel[node.ordinal()] != 0;
    }

    public void unlockTravel(TravelNode node) {
        if (node != null) travel[node.ordinal()] = 1;
    }

    public ShortcutState shortcut(Shortcut shortcut) {
        return enumAt(ShortcutState.values(), shortcuts, shortcut.ordinal(), ShortcutState.UNKNOWN);
    }

    public void setShortcut(Shortcut shortcut, ShortcutState state) {
        if (shortcut == null || state == null) return;
        if (state.ordinal() > shortcuts[shortcut.ordinal()]) shortcuts[shortcut.ordinal()] = state.ordinal();
    }

    /** Captures authored-area map knowledge before a fixed Level object is discarded. */
    public void captureExploration(Level level) {
        if (!(level instanceof RegionAreaLevel)) return;
        Area area = ((RegionAreaLevel) level).regionArea();
        visitedByArea[area.ordinal()] = trueCells(level.visited);
        mappedByArea[area.ordinal()] = trueCells(level.mapped);
    }

    /** Restores previously learned geometry into a newly created authored area. */
    public void restoreExploration(Level level) {
        if (!(level instanceof RegionAreaLevel)) return;
        Area area = ((RegionAreaLevel) level).regionArea();
        restoreCells(level.visited, visitedByArea[area.ordinal()]);
        restoreCells(level.mapped, mappedByArea[area.ordinal()]);
    }

    private void updatePoiKnowledge() {
        if (!(Dungeon.level instanceof RegionAreaLevel) || Dungeon.hero == null) return;
        RegionAreaLevel area = (RegionAreaLevel) Dungeon.level;
        RegionPoi[] pois = area.regionPois();
        if (pois == null) return;

        for (RegionPoi poi : pois) {
            if (poi == null || poi.location == null || knowledge(poi.location) == Knowledge.DISCOVERED) continue;
            int cell = poi.exactCell(Dungeon.level.width());
            if (cell < 0 || cell >= Dungeon.level.length()) continue;
            if (Dungeon.level.distance(Dungeon.hero.pos, cell) <= poi.discoveryRadius) {
                discover(poi.location);
            }
        }
    }

    public String objectiveText() {
        if (Dungeon.level instanceof RegionAreaLevel) {
            Area area = ((RegionAreaLevel) Dungeon.level).regionArea();
            if (area == Area.ACT_ONE_RETURN) {
                ActOneReturnState scene = ActOneReturnState.current();
                return scene == null ? "离开地下城旧址" : scene.objectiveText();
            }
            if (area == Area.OLD_CROW_INN) {
                return "空间勘察：检查旅店公共层布局，并从正门返回镇区";
            }
            return "空间勘察：自由探索晨溪镇 · 点击小地图查看区域地图";
        }
        return "";
    }

    public void syncHud() {
        if (hudSyncPending) return;
        final String objective = objectiveText();
        hudSyncPending = true;
        Game.runOnRenderThread(() -> {
            hudSyncPending = false;
            TaskGuidanceToast.showObjective(objective);
        });
    }

    @Override
    public boolean act() {
        if (Dungeon.hero != null && Dungeon.level instanceof RegionAreaLevel) {
            updatePoiKnowledge();
            syncHud();
        }
        spend(TICK);
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        captureExploration(Dungeon.level);
        bundle.put(TIME, time);
        bundle.put(LOCATION_KNOWLEDGE, locationKnowledge);
        bundle.put(EVIDENCE, evidence);
        bundle.put(CONCLUSIONS, conclusions);
        bundle.put(WORLD, world);
        bundle.put(MEMORIES, memories);
        bundle.put(LEADS, leads);
        bundle.put(TRAVEL, travel);
        bundle.put(SHORTCUTS, shortcuts);
        bundle.put(FORMAL_ACT_ONE, formalActOne);
        for (Area area : Area.values()) {
            int i = area.ordinal();
            bundle.put(VISITED_PREFIX + area.name(), visitedByArea[i] == null ? new int[0] : visitedByArea[i]);
            bundle.put(MAPPED_PREFIX + area.name(), mappedByArea[i] == null ? new int[0] : mappedByArea[i]);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        time = bundle.contains(TIME) ? bundle.getInt(TIME) : TimeBand.AFTERNOON.ordinal();
        locationKnowledge = normalized(bundle.getIntArray(LOCATION_KNOWLEDGE), Location.values().length);
        evidence = normalized(bundle.getIntArray(EVIDENCE), Evidence.values().length);
        conclusions = normalized(bundle.getIntArray(CONCLUSIONS), Conclusion.values().length);
        world = normalized(bundle.getIntArray(WORLD), Location.values().length);
        memories = normalized(bundle.getIntArray(MEMORIES), Memory.values().length);
        leads = normalized(bundle.getIntArray(LEADS), Lead.values().length);
        travel = normalized(bundle.getIntArray(TRAVEL), TravelNode.values().length);
        shortcuts = normalized(bundle.getIntArray(SHORTCUTS), Shortcut.values().length);
        formalActOne = bundle.getBoolean(FORMAL_ACT_ONE);
        for (Area area : Area.values()) {
            int i = area.ordinal();
            visitedByArea[i] = bundle.getIntArray(VISITED_PREFIX + area.name());
            mappedByArea[i] = bundle.getIntArray(MAPPED_PREFIX + area.name());
        }
        seedPrototypeKnowledge();
        refreshDerivedConclusions();
    }

    private static int[] normalized(int[] stored, int length) {
        int[] result = new int[length];
        if (stored != null) System.arraycopy(stored, 0, result, 0, Math.min(stored.length, result.length));
        return result;
    }

    private static int[] trueCells(boolean[] values) {
        if (values == null) return new int[0];
        int count = 0;
        for (boolean value : values) if (value) count++;
        int[] result = new int[count];
        int j = 0;
        for (int i = 0; i < values.length; i++) if (values[i]) result[j++] = i;
        return result;
    }

    private static void restoreCells(boolean[] target, int[] cells) {
        if (target == null || cells == null) return;
        for (int cell : cells) if (cell >= 0 && cell < target.length) target[cell] = true;
    }

    private static <T extends Enum<T>> T enumAt(T[] values, int[] stored, int index, T fallback) {
        if (stored == null || index < 0 || index >= stored.length) return fallback;
        int ordinal = stored[index];
        if (ordinal < 0 || ordinal >= values.length) return fallback;
        return values[ordinal];
    }
}
