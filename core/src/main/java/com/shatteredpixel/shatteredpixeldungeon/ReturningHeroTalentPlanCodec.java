/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import java.util.Map;

/** Compact settings serialization for returning-hero talent allocations. */
public final class ReturningHeroTalentPlanCodec {

    private ReturningHeroTalentPlanCodec() {
        // Utility class.
    }

    public static String encode(ReturningHeroTalentPlan value) {
        ReturningHeroTalentPlan plan = value == null ? new ReturningHeroTalentPlan() : value;
        StringBuilder out = new StringBuilder(512);
        out.append(plan.rulesetVersion);
        for (Map.Entry<String, Integer> entry : plan.entries().entrySet()) {
            if (entry.getKey() == null || entry.getKey().isEmpty()) continue;
            int points = entry.getValue() == null ? 0 : entry.getValue();
            if (points <= 0) continue;
            out.append('|').append(entry.getKey()).append(':').append(points);
        }
        return out.toString();
    }

    public static ReturningHeroTalentPlan decode(String encoded) {
        ReturningHeroTalentPlan fallback = new ReturningHeroTalentPlan();
        if (encoded == null || encoded.isEmpty()) return fallback;

        String[] fields = encoded.split("\\|", -1);
        if (fields.length == 0) return fallback;

        ReturningHeroTalentPlan plan = new ReturningHeroTalentPlan();
        plan.rulesetVersion = integer(fields[0], 0);
        for (int i = 1; i < fields.length; i++) {
            int split = fields[i].lastIndexOf(':');
            if (split <= 0 || split >= fields[i].length() - 1) continue;
            String talentId = fields[i].substring(0, split);
            int points = integer(fields[i].substring(split + 1), 0);
            if (points > 0) plan.set(talentId, points);
        }
        return plan;
    }

    private static int integer(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }
}
