/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Player-controlled talent allocation for a reconstructed returning hero.
 *
 * Talent enum names are stored as stable reconstruction IDs.  The plan does not
 * decide which talents are legal; ReturningHeroTalentRules derives that from
 * the selected class, subclass and armor ability using the original SPD talent
 * initialization methods.
 */
public class ReturningHeroTalentPlan {

    public int rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;

    private final LinkedHashMap<String, Integer> points = new LinkedHashMap<>();

    public ReturningHeroTalentPlan() {
    }

    public ReturningHeroTalentPlan(ReturningHeroTalentPlan other) {
        if (other == null) return;
        rulesetVersion = other.rulesetVersion;
        points.putAll(other.points);
    }

    public ReturningHeroTalentPlan copy() {
        return new ReturningHeroTalentPlan(this);
    }

    public int pointsIn(Talent talent) {
        return talent == null ? 0 : pointsIn(talent.name());
    }

    public int pointsIn(String talentId) {
        Integer value = talentId == null ? null : points.get(talentId);
        return value == null ? 0 : Math.max(0, value);
    }

    public void set(Talent talent, int value) {
        if (talent == null) return;
        set(talent.name(), value);
    }

    public void set(String talentId, int value) {
        if (talentId == null || talentId.isEmpty()) return;
        if (value <= 0) {
            points.remove(talentId);
        } else {
            points.put(talentId, value);
        }
    }

    public void clear() {
        rulesetVersion = ReturningHeroBuildRules.RULESET_VERSION;
        points.clear();
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public Map<String, Integer> entries() {
        return Collections.unmodifiableMap(points);
    }
}
