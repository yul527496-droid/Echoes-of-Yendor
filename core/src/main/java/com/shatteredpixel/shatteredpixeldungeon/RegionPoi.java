/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon;

/**
 * Authored point-of-interest metadata shared by RegionState and Area Map rendering.
 *
 * A POI deliberately stores two map anchors. The heard-of anchor is a coarse district
 * hint and must never be treated as the exact GPS position. Once the location is
 * physically discovered, Area Map may use the exact anchor instead.
 */
public final class RegionPoi {

    public enum Category {
        LANDMARK,
        CIVIC,
        SERVICE,
        TRAVEL,
        INVESTIGATION,
        SHORTCUT,
        AMBIENT
    }

    public final RegionState.Location location;
    public final Category category;
    public final int x;
    public final int y;
    public final int heardX;
    public final int heardY;
    public final int discoveryRadius;
    public final String name;
    public final String heardDescription;
    public final String discoveredDescription;

    public RegionPoi(RegionState.Location location,
                     Category category,
                     int x,
                     int y,
                     int heardX,
                     int heardY,
                     int discoveryRadius,
                     String name,
                     String heardDescription,
                     String discoveredDescription) {
        this.location = location;
        this.category = category;
        this.x = x;
        this.y = y;
        this.heardX = heardX;
        this.heardY = heardY;
        this.discoveryRadius = Math.max(1, discoveryRadius);
        this.name = name;
        this.heardDescription = heardDescription;
        this.discoveredDescription = discoveredDescription;
    }

    public int exactCell(int width) {
        return x + y * width;
    }

    public int heardCell(int width) {
        return heardX + heardY * width;
    }
}
