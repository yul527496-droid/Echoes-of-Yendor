/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;

/** Project-owned Act 1 Return props, authored exclusively on SPD's native 16px grid. */
public class EchoesReturnPropTilemap extends CustomTilemap {

    public static final int BEDROLL_A=0, BEDROLL_B=1, BEDROLL_C=2, BEDROLL_D=3;
    public static final int DEAD_FIRE=4, COOK_POT=5, TRAVEL_BAG=6, CRATE=7;
    public static final int FADED_TENT=8, CHECKLIST=9, MAINTENANCE_TOOLS=10, CLOTH_STRIP=11;
    public static final int MILESTONE=12, LOW_WALL=13, FIELD_EDGE=14, SACK=15;
    public static final int FARM_TOOL=16, CART_WOOD=17, OFFERING_BOWL=18, FOUR_RECESSES=19;
    public static final int ROOTS=20, STUMP=21;

    private static final String TEX="environment/echoes/act1_return/return_props_v1.png";
    private static final int TEX_WIDTH=256;
    private static final String KIND="kind";
    private int kind;
    private int tx,ty,tw=1,th=1;

    public EchoesReturnPropTilemap(){ configure(); }
    public EchoesReturnPropTilemap(int kind){ this.kind=kind; configure(); }

    private void configure(){
        tw=th=1;
        switch(kind){
            case BEDROLL_A: tx=0; ty=0; break; case BEDROLL_B: tx=1; ty=0; break;
            case BEDROLL_C: tx=2; ty=0; break; case BEDROLL_D: tx=3; ty=0; break;
            case DEAD_FIRE: tx=4; ty=0; break; case COOK_POT: tx=5; ty=0; break;
            case TRAVEL_BAG: tx=6; ty=0; break; case CRATE: tx=7; ty=0; break;
            case FADED_TENT: tx=8; ty=0; tw=2; th=2; break;
            case CHECKLIST: tx=10; ty=0; break; case MAINTENANCE_TOOLS: tx=11; ty=0; break;
            case CLOTH_STRIP: tx=12; ty=0; break; case MILESTONE: tx=13; ty=0; break;
            case LOW_WALL: tx=14; ty=0; break; case FIELD_EDGE: tx=15; ty=0; break;
            case SACK: tx=0; ty=1; break; case FARM_TOOL: tx=1; ty=1; break;
            case CART_WOOD: tx=2; ty=1; break; case OFFERING_BOWL: tx=3; ty=1; break;
            case FOUR_RECESSES: tx=4; ty=1; tw=2; break;
            case ROOTS: tx=6; ty=1; break; case STUMP: tx=7; ty=1; break;
            default: kind=BEDROLL_A; tx=0; ty=0; break;
        }
        texture=TEX; tileW=tw; tileH=th;
    }

    @Override public Tilemap create(){
        configure();
        Tilemap result=super.create();
        result.map(mapSimpleImage(tx,ty,TEX_WIDTH),tileW);
        return result;
    }
    @Override public void storeInBundle(Bundle b){ super.storeInBundle(b); b.put(KIND,kind); }
    @Override public void restoreFromBundle(Bundle b){ super.restoreFromBundle(b); kind=b.getInt(KIND); configure(); }
}
