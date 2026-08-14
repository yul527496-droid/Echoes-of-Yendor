/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;

/** Compact settings serialization for stable-ID returning-hero loadouts. */
public final class ReturningHeroLoadoutCodec {

    private static final String SEP = "|";
    private static final int FIELD_COUNT = 17;

    private ReturningHeroLoadoutCodec() {
        // Utility class.
    }

    public static String encode(ReturningHeroLoadout value) {
        ReturningHeroLoadout loadout = value == null ? new ReturningHeroLoadout() : value;
        StringBuilder out = new StringBuilder(256);
        append(out, Integer.toString(loadout.rulesetVersion));
        append(out, loadout.primaryWeaponId);
        append(out, Integer.toString(loadout.primaryWeaponLevel));
        append(out, loadout.armorId);
        append(out, Integer.toString(loadout.armorLevel));
        append(out, loadout.mageStaffImbuementId);
        append(out, loadout.carriedWandId);
        append(out, Integer.toString(loadout.carriedWandLevel));
        append(out, loadout.artifactSlotId);
        append(out, Integer.toString(loadout.artifactSlotLevel));
        append(out, loadout.miscSlotKind.name());
        append(out, loadout.miscSlotId);
        append(out, Integer.toString(loadout.miscSlotLevel));
        append(out, loadout.ringSlotId);
        append(out, Integer.toString(loadout.ringSlotLevel));
        append(out, loadout.trinketId);
        append(out, Integer.toString(loadout.trinketLevel));
        return out.toString();
    }

    public static ReturningHeroLoadout decode(String encoded, HeroClass heroClass) {
        ReturningHeroLoadout fallback = new ReturningHeroLoadout();
        fallback.resetForClass(heroClass);
        if (encoded == null || encoded.isEmpty()) return fallback;

        String[] fields = encoded.split("\\|", -1);
        if (fields.length != FIELD_COUNT) return fallback;

        ReturningHeroLoadout loadout = new ReturningHeroLoadout();
        loadout.rulesetVersion = integer(fields[0], 0);
        loadout.primaryWeaponId = nullable(fields[1]);
        loadout.primaryWeaponLevel = integer(fields[2], 0);
        loadout.armorId = nullable(fields[3]);
        loadout.armorLevel = integer(fields[4], 0);
        loadout.mageStaffImbuementId = nullable(fields[5]);
        loadout.carriedWandId = nullable(fields[6]);
        loadout.carriedWandLevel = integer(fields[7], 0);
        loadout.artifactSlotId = nullable(fields[8]);
        loadout.artifactSlotLevel = integer(fields[9], 0);
        loadout.miscSlotKind = miscKind(fields[10]);
        loadout.miscSlotId = nullable(fields[11]);
        loadout.miscSlotLevel = integer(fields[12], 0);
        loadout.ringSlotId = nullable(fields[13]);
        loadout.ringSlotLevel = integer(fields[14], 0);
        loadout.trinketId = nullable(fields[15]);
        loadout.trinketLevel = integer(fields[16], 0);
        return loadout;
    }

    private static void append(StringBuilder out, String value) {
        if (out.length() > 0) out.append(SEP);
        if (value != null) out.append(value);
    }

    private static String nullable(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static int integer(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static ReturningHeroLoadout.MiscSlotKind miscKind(String value) {
        try {
            return ReturningHeroLoadout.MiscSlotKind.valueOf(value);
        } catch (RuntimeException ignored) {
            return ReturningHeroLoadout.MiscSlotKind.EMPTY;
        }
    }
}
