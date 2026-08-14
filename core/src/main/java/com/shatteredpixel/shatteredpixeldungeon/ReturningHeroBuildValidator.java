/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.ReturningHeroItemCatalog.Kind;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Rules-only validation for returning-hero builds and presets. */
public final class ReturningHeroBuildValidator {

    public enum Problem {
        RULESET_MISMATCH,
        INVALID_PRIMARY_WEAPON,
        INVALID_ARMOR,
        MAGE_IMBUEMENT_REQUIRED,
        MAGE_IMBUEMENT_ON_NON_MAGE,
        INVALID_CARRIED_WAND,
        INVALID_ARTIFACT_SLOT,
        CLASS_ARTIFACT_SLOT_IS_DERIVED,
        INVALID_MISC_SLOT,
        INVALID_RING_SLOT,
        DUPLICATE_RINGS,
        COMBINED_RING_LEVEL_TOO_HIGH,
        DUPLICATE_ARTIFACTS,
        ARTIFACT_GROWTH_TOO_HIGH,
        SECOND_ARTIFACT_TOO_HIGH_WITH_CLASS_ARTIFACT,
        INVALID_TRINKET,
        EQUIPMENT_BUDGET_EXCEEDED
    }

    public static final class Result {
        private final List<Problem> problems;

        private Result(List<Problem> problems) {
            this.problems = Collections.unmodifiableList(problems);
        }

        public boolean isValid() {
            return problems.isEmpty();
        }

        public List<Problem> problems() {
            return problems;
        }
    }

    private ReturningHeroBuildValidator() {
        // Utility class.
    }

    public static Result validate(ReturningHeroProfile profile) {
        if (profile == null) {
            List<Problem> problems = new ArrayList<>();
            problems.add(Problem.RULESET_MISMATCH);
            return new Result(problems);
        }
        return validate(profile.heroClass, profile.loadout);
    }

    public static Result validate(ReturningHeroPreset preset) {
        List<Problem> problems = new ArrayList<>();
        if (preset == null || preset.rulesetVersion != ReturningHeroBuildRules.RULESET_VERSION) {
            problems.add(Problem.RULESET_MISMATCH);
            return new Result(problems);
        }
        appendProblems(problems, preset.heroClass, preset.loadout);
        return new Result(problems);
    }

    public static Result validate(HeroClass heroClass, ReturningHeroLoadout loadout) {
        List<Problem> problems = new ArrayList<>();
        appendProblems(problems, heroClass, loadout);
        return new Result(problems);
    }

    private static void appendProblems(List<Problem> problems,
                                       HeroClass heroClass,
                                       ReturningHeroLoadout loadout) {
        if (heroClass == null || loadout == null
                || loadout.rulesetVersion != ReturningHeroBuildRules.RULESET_VERSION) {
            problems.add(Problem.RULESET_MISMATCH);
            return;
        }

        if (loadout.primaryWeaponId != null
                && ReturningHeroBuildCost.weaponCost(
                loadout.primaryWeaponId, loadout.primaryWeaponLevel) < 0) {
            problems.add(Problem.INVALID_PRIMARY_WEAPON);
        }

        if (loadout.armorId != null
                && ReturningHeroBuildCost.armorCost(loadout.armorId, loadout.armorLevel) < 0) {
            problems.add(Problem.INVALID_ARMOR);
        }

        if (heroClass == HeroClass.MAGE) {
            if (!ReturningHeroItemCatalog.isSelectable(
                    loadout.mageStaffImbuementId, Kind.WAND)) {
                problems.add(Problem.MAGE_IMBUEMENT_REQUIRED);
            }
        } else if (loadout.mageStaffImbuementId != null) {
            problems.add(Problem.MAGE_IMBUEMENT_ON_NON_MAGE);
        }

        if (loadout.carriedWandId != null) {
            if (ReturningHeroBuildCost.carriedWandCost(
                    loadout.carriedWandId, loadout.carriedWandLevel) < 0) {
                problems.add(Problem.INVALID_CARRIED_WAND);
            }
        } else if (loadout.carriedWandLevel != 0) {
            problems.add(Problem.INVALID_CARRIED_WAND);
        }

        boolean hasClassArtifact = ReturningHeroHeritage.hasClassArtifact(heroClass);
        if (hasClassArtifact) {
            if (loadout.artifactSlotId != null || loadout.artifactSlotLevel != 0) {
                problems.add(Problem.CLASS_ARTIFACT_SLOT_IS_DERIVED);
            }
        } else if (loadout.artifactSlotId != null) {
            if (!ReturningHeroItemCatalog.isSelectable(loadout.artifactSlotId, Kind.ARTIFACT)
                    || loadout.artifactSlotLevel < 0
                    || loadout.artifactSlotLevel
                    > ReturningHeroBuildRules.ORDINARY_ARTIFACT_GROWTH_BUDGET) {
                problems.add(Problem.INVALID_ARTIFACT_SLOT);
            }
        } else if (loadout.artifactSlotLevel != 0) {
            problems.add(Problem.INVALID_ARTIFACT_SLOT);
        }

        switch (loadout.miscSlotKind) {
            case EMPTY:
                if (loadout.miscSlotId != null || loadout.miscSlotLevel != 0) {
                    problems.add(Problem.INVALID_MISC_SLOT);
                }
                break;
            case RING:
                if (ReturningHeroBuildCost.ringCost(
                        loadout.miscSlotId, loadout.miscSlotLevel) < 0) {
                    problems.add(Problem.INVALID_MISC_SLOT);
                }
                break;
            case ARTIFACT:
                if (!ReturningHeroItemCatalog.isSelectable(loadout.miscSlotId, Kind.ARTIFACT)
                        || loadout.miscSlotLevel < 0
                        || loadout.miscSlotLevel
                        > ReturningHeroBuildRules.ORDINARY_ARTIFACT_GROWTH_BUDGET) {
                    problems.add(Problem.INVALID_MISC_SLOT);
                }
                break;
            default:
                problems.add(Problem.INVALID_MISC_SLOT);
        }

        if (loadout.ringSlotId != null) {
            if (ReturningHeroBuildCost.ringCost(
                    loadout.ringSlotId, loadout.ringSlotLevel) < 0) {
                problems.add(Problem.INVALID_RING_SLOT);
            }
        } else if (loadout.ringSlotLevel != 0) {
            problems.add(Problem.INVALID_RING_SLOT);
        }

        if (loadout.miscIsRing() && loadout.ringSlotId != null) {
            if (loadout.miscSlotId.equals(loadout.ringSlotId)) {
                problems.add(Problem.DUPLICATE_RINGS);
            }
            if (loadout.miscSlotLevel + loadout.ringSlotLevel
                    > ReturningHeroBuildRules.MAX_COMBINED_RING_LEVEL) {
                problems.add(Problem.COMBINED_RING_LEVEL_TOO_HIGH);
            }
        }

        if (loadout.miscIsArtifact()) {
            if (hasClassArtifact) {
                if (loadout.miscSlotLevel
                        > ReturningHeroBuildRules.MAX_SECOND_ARTIFACT_LEVEL_WITH_CLASS_ARTIFACT) {
                    problems.add(Problem.SECOND_ARTIFACT_TOO_HIGH_WITH_CLASS_ARTIFACT);
                }
            } else {
                if (loadout.artifactSlotId != null
                        && loadout.artifactSlotId.equals(loadout.miscSlotId)) {
                    problems.add(Problem.DUPLICATE_ARTIFACTS);
                }
                if (ReturningHeroBuildCost.ordinaryArtifactGrowthSpent(heroClass, loadout)
                        > ReturningHeroBuildRules.ORDINARY_ARTIFACT_GROWTH_BUDGET) {
                    problems.add(Problem.ARTIFACT_GROWTH_TOO_HIGH);
                }
            }
        }

        if (loadout.trinketId != null) {
            if (!ReturningHeroItemCatalog.isSelectable(loadout.trinketId, Kind.TRINKET)
                    || loadout.trinketLevel < 0 || loadout.trinketLevel > 3) {
                problems.add(Problem.INVALID_TRINKET);
            }
        } else if (loadout.trinketLevel != 0) {
            problems.add(Problem.INVALID_TRINKET);
        }

        int equipmentSpent = ReturningHeroBuildCost.equipmentSpent(heroClass, loadout);
        if (equipmentSpent < 0
                || equipmentSpent > ReturningHeroBuildRules.EQUIPMENT_RECONSTRUCTION_BUDGET) {
            problems.add(Problem.EQUIPMENT_BUDGET_EXCEEDED);
        }
    }
}
