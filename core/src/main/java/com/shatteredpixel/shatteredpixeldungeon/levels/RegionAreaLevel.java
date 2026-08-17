/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.RegionPoi;
import com.shatteredpixel.shatteredpixeldungeon.RegionState;

/**
 * Contract for authored semi-open RPG areas that participate in formal region systems.
 * Legacy sequel demo maps intentionally do not implement this interface.
 */
public interface RegionAreaLevel {

    RegionState.Area regionArea();

    String regionAreaName();

    RegionPoi[] regionPois();
}
