/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.badlogic.gdx.graphics.Pixmap;

/** Small embedded Chapter 1 sprite sheets so the vertical slice has its own surface cast. */
final class EchoesSurfaceSpriteArt {

    static final int FRAME = 16;
    static final int FRAMES = 4;

    enum Kind { FARMER, DONKEY_CART, BIRD, WOLF }

    private EchoesSurfaceSpriteArt() {}

    static Pixmap sheet(Kind kind) {
        Pixmap p = new Pixmap(FRAME * FRAMES, FRAME, Pixmap.Format.RGBA8888);
        for (int i = 0; i < FRAMES; i++) {
            switch (kind) {
                case FARMER: farmer(p, i, i >= 2 ? i - 2 : 0); break;
                case DONKEY_CART: donkey(p, i, i >= 2 ? i - 2 : 0); break;
                case BIRD: bird(p, i, i); break;
                case WOLF: wolf(p, i, i); break;
            }
        }
        return p;
    }

    private static void rect(Pixmap p, int frame, int x, int y, int w, int h, int rgba) {
        p.setColor(rgba);
        p.fillRectangle(frame * FRAME + x, y, w, h);
    }

    private static void px(Pixmap p, int frame, int x, int y, int rgba) {
        p.drawPixel(frame * FRAME + x, y, rgba);
    }

    private static void farmer(Pixmap p, int f, int step) {
        final int OUT = 0x261A13FF;
        final int HAT = 0x8B6938FF;
        final int SKIN = 0xC48A5AFF;
        final int SHIRT = 0x6F7E48FF;
        final int COAT = 0x80613CFF;
        final int BOOT = 0x39271CFF;
        final int BEARD = 0xD2C2A1FF;

        rect(p,f,4,1,8,2,OUT); rect(p,f,3,2,10,2,HAT); rect(p,f,5,4,6,5,OUT);
        rect(p,f,6,4,4,4,SKIN); px(p,f,9,5,OUT); rect(p,f,5,7,6,2,BEARD);
        rect(p,f,4,9,8,5,OUT); rect(p,f,5,9,6,2,SHIRT); rect(p,f,5,11,6,3,COAT);
        rect(p,f,3,10,2,3,COAT); rect(p,f,11,10,2,3,COAT);
        if (step == 0) { rect(p,f,5,14,2,2,BOOT); rect(p,f,9,14,2,2,BOOT); }
        else { rect(p,f,4,14,3,2,BOOT); rect(p,f,10,14,2,2,BOOT); }
    }

    private static void donkey(Pixmap p, int f, int step) {
        final int OUT = 0x241C18FF;
        final int FUR = 0x81766AFF;
        final int MUZZLE = 0xB6A896FF;
        final int CART = 0x85552FFF;
        final int METAL = 0xC59B48FF;

        rect(p,f,2,6,8,6,OUT); rect(p,f,3,6,7,5,FUR);
        rect(p,f,8,3,5,6,OUT); rect(p,f,8,4,4,4,FUR); rect(p,f,10,6,3,2,MUZZLE);
        px(p,f,9,4,OUT); px(p,f,9,2,FUR); px(p,f,11,2,FUR);
        rect(p,f,0,8,3,5,OUT); rect(p,f,0,9,2,3,CART); px(p,f,1,13,METAL);
        if (step == 0) { rect(p,f,3,11,2,4,OUT); rect(p,f,7,11,2,4,OUT); }
        else { rect(p,f,2,11,2,4,OUT); rect(p,f,8,11,2,4,OUT); }
        px(p,f,13,6,OUT); px(p,f,14,5,OUT);
    }

    private static void bird(Pixmap p, int f, int phase) {
        final int OUT = 0x17191BFF;
        final int FEATHER = 0x4A5156FF;
        final int BELLY = 0x7A807CFF;
        final int BEAK = 0xC79C47FF;
        int lift = (phase == 1 || phase == 3) ? 1 : 0;
        rect(p,f,5,6-lift,6,5,OUT); rect(p,f,6,6-lift,4,4,FEATHER);
        rect(p,f,8,7-lift,2,3,BELLY); px(p,f,9,6-lift,0xE7E2CFFF);
        px(p,f,11,8-lift,BEAK); px(p,f,12,8-lift,BEAK);
        if (phase >= 2) { rect(p,f,2,4,4,2,FEATHER); rect(p,f,3,11,4,2,FEATHER); }
        else { rect(p,f,3,8,3,2,FEATHER); }
        px(p,f,6,11-lift,OUT); px(p,f,9,11-lift,OUT);
    }

    private static void wolf(Pixmap p, int f, int phase) {
        final int OUT = 0x1F2022FF;
        final int FUR = 0x666A6BFF;
        final int LIGHT = 0x92918BFF;
        final int EYE = 0xC9A64BFF;
        int crouch = phase == 3 ? 1 : 0;
        rect(p,f,3,6+crouch,9,6,OUT); rect(p,f,4,6+crouch,8,5,FUR);
        rect(p,f,9,4+crouch,5,5,OUT); rect(p,f,10,5+crouch,4,3,FUR);
        px(p,f,10,3+crouch,OUT); px(p,f,12,3+crouch,OUT); px(p,f,12,5+crouch,EYE);
        rect(p,f,12,7+crouch,3,2,LIGHT); px(p,f,15,8+crouch,OUT);
        px(p,f,2,7+crouch,OUT); px(p,f,1,6+crouch,OUT); px(p,f,0,5+crouch,OUT);
        if (phase % 2 == 0) { rect(p,f,4,11+crouch,2,4-crouch,OUT); rect(p,f,10,11+crouch,2,4-crouch,OUT); }
        else { rect(p,f,3,11+crouch,2,4-crouch,OUT); rect(p,f,11,11+crouch,2,4-crouch,OUT); }
    }
}
