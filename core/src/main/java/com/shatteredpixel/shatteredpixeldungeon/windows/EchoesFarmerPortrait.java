/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.noosa.Image;

/** Small embedded portrait so the first story NPC is not a blown-up map sprite. */
final class EchoesFarmerPortrait {

    private static final int W = 42;
    private static final int H = 54;

    private EchoesFarmerPortrait() {
    }

    static Image image(WndDialogueStage.Portrait expression) {
        Pixmap p = new Pixmap(W, H, Pixmap.Format.RGBA8888);

        // Coat and shoulders.
        fill(p, 7, 36, 28, 18, 0x53432DFF);
        fill(p, 3, 43, 36, 11, 0x443624FF);
        fill(p, 9, 37, 24, 4, 0x73593AFF);
        fill(p, 17, 38, 8, 16, 0x30271DFF);

        // Neck, ears, face and nose.
        fill(p, 15, 31, 12, 9, 0xB77A50FF);
        fill(p, 10, 13, 22, 23, 0xC58A5EFF);
        fill(p, 8, 21, 4, 8, 0xA76D49FF);
        fill(p, 31, 21, 4, 8, 0xA76D49FF);
        fill(p, 31, 24, 4, 3, 0xD39A6EFF);

        // Sun-reddened cheeks and face shadow.
        fill(p, 11, 27, 6, 4, 0xB66B50FF);
        fill(p, 26, 27, 5, 4, 0xB66B50FF);
        fill(p, 10, 31, 22, 5, 0xA96E49FF);

        // Hair and hat.
        fill(p, 9, 10, 24, 7, 0x6A4A2BFF);
        fill(p, 5, 8, 32, 5, 0x806039FF);
        fill(p, 11, 3, 21, 7, 0x72522FFF);
        fill(p, 15, 2, 13, 2, 0x94734AFF);
        fill(p, 8, 12, 5, 6, 0xB9A17DFF);
        fill(p, 30, 12, 4, 6, 0xB9A17DFF);

        // Beard gives the portrait a readable silhouette at phone scale.
        fill(p, 12, 28, 19, 11, 0xC1B18FFF);
        fill(p, 15, 36, 13, 5, 0xA99574FF);
        fill(p, 10, 31, 3, 4, 0xD4C4A2FF);
        fill(p, 30, 31, 3, 4, 0x8F8067FF);

        // Brows / eyes vary by the five story states.
        int eye = expression == WndDialogueStage.Portrait.FARMER_FIXATED ? 0xE7D8AFFF : 0x2A211AFF;
        switch (expression) {
            case FARMER_WARM:
                pixel(p, 15, 21, eye); pixel(p, 26, 21, eye);
                fill(p, 16, 26, 10, 2, 0x6F4034FF);
                pixel(p, 17, 28, 0x6F4034FF); pixel(p, 25, 28, 0x6F4034FF);
                break;
            case FARMER_CONFUSED:
                fill(p, 13, 18, 6, 2, 0x5A3A28FF);
                fill(p, 24, 17, 6, 2, 0x5A3A28FF);
                fill(p, 15, 21, 2, 2, eye); fill(p, 26, 21, 2, 2, eye);
                fill(p, 18, 28, 8, 2, 0x70453AFF);
                break;
            case FARMER_FIXATED:
                fill(p, 13, 18, 6, 2, 0x3C2C22FF);
                fill(p, 24, 18, 6, 2, 0x3C2C22FF);
                fill(p, 14, 21, 4, 3, eye); fill(p, 25, 21, 4, 3, eye);
                pixel(p, 16, 22, 0x251E1AFF); pixel(p, 27, 22, 0x251E1AFF);
                fill(p, 18, 28, 8, 2, 0x5A342DFF);
                break;
            case FARMER_SHAKEN:
                fill(p, 13, 18, 6, 2, 0x6B5745FF);
                fill(p, 24, 18, 6, 2, 0x6B5745FF);
                pixel(p, 15, 22, eye); pixel(p, 26, 22, eye);
                fill(p, 18, 29, 8, 1, 0x6E4941FF);
                fill(p, 20, 31, 4, 1, 0x6E4941FF);
                break;
            case FARMER_NEUTRAL:
            default:
                fill(p, 13, 19, 6, 2, 0x5A3A28FF);
                fill(p, 24, 19, 6, 2, 0x5A3A28FF);
                pixel(p, 15, 22, eye); pixel(p, 26, 22, eye);
                fill(p, 18, 28, 8, 2, 0x70453AFF);
                break;
        }

        return new Image(p);
    }

    private static void fill(Pixmap p, int x, int y, int w, int h, int rgba) {
        p.setColor(rgba);
        p.fillRectangle(x, y, w, h);
    }

    private static void pixel(Pixmap p, int x, int y, int rgba) {
        p.drawPixel(x, y, rgba);
    }
}
