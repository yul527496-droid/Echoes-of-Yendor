/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekMainStreetLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

/** North-up explored-region map drawn from Level's visited/mapped/FOV knowledge only. */
public class WndRegionMap extends Window {

    private static final String PIXEL="interfaces/echoes/minimap_pixel.png";
    private static final int WIDTH=220, HEIGHT=168, MARGIN=8;
    private static final int HERO=0xF2D36B, WATER=0x4B93A0, WALL=0x3E5940,
            ROAD=0xA78B62, GRASS=0x6F9954, EXIT=0xE7D9A0, TARGET=0xD64D9C;

    public WndRegionMap(){
        super();
        resize(WIDTH,HEIGHT);

        RenderedTextBlock title=PixelScene.renderTextBlock("区域地图   N ↑",9);
        title.hardlight(TITLE_COLOR); title.setPos(MARGIN,MARGIN); add(title);

        Level level=Dungeon.level;
        if(level==null||Dungeon.hero==null){
            RenderedTextBlock none=PixelScene.renderTextBlock("暂无区域数据",7); none.setPos(MARGIN,32); add(none); return;
        }

        int w=level.width(), h=level.height();
        int cell=Math.max(2,Math.min(3,Math.min((WIDTH-MARGIN*2)/Math.max(1,w),(112)/Math.max(1,h))));
        int mapW=w*cell, mapH=h*cell;
        int originX=(WIDTH-mapW)/2;
        int originY=25;
        int objective=objectiveCell(level);

        for(int y=0;y<h;y++) for(int x=0;x<w;x++){
            int c=x+y*w;
            boolean fov=known(level.heroFOV,c), visited=known(level.visited,c), mapped=known(level.mapped,c);
            if(c!=Dungeon.hero.pos&&!fov&&!visited&&!mapped) continue;
            int color;
            if(c==Dungeon.hero.pos) color=HERO;
            else if(c==objective) color=TARGET;
            else if(level.map[c]==Terrain.WATER) color=WATER;
            else if(level.map[c]==Terrain.WALL) color=WALL;
            else if(level.map[c]==Terrain.ENTRANCE||level.map[c]==Terrain.EXIT) color=EXIT;
            else if(level.map[c]==Terrain.EMPTY||level.map[c]==Terrain.EMPTY_SP||level.map[c]==Terrain.EMPTY_DECO) color=ROAD;
            else color=GRASS;
            Image px=new Image(PIXEL); px.hardlight(color); px.scale.set(cell,cell);
            if(!fov&&c!=Dungeon.hero.pos) px.alpha(visited?0.68f:0.40f);
            px.x=originX+x*cell; px.y=originY+y*cell; add(px);
        }

        SequelState state=Dungeon.hero.buff(SequelState.class);
        String objectiveText=state==null?"":state.objectiveText();
        RenderedTextBlock legend=PixelScene.renderTextBlock("金：你   紫：目标   蓝：水   米：道路/出口\n"+objectiveText,6);
        legend.maxWidth(WIDTH-MARGIN*2);
        legend.setPos(MARGIN,Math.min(HEIGHT-33,originY+mapH+5)); add(legend);
    }

    private static boolean known(boolean[] values,int cell){ return values!=null&&cell>=0&&cell<values.length&&values[cell]; }

    private static int objectiveCell(Level level){
        if(level instanceof SurfaceEntranceLevel) return ((SurfaceEntranceLevel)level).cell(SurfaceEntranceLevel.NORTH_X,SurfaceEntranceLevel.NORTH_Y);
        if(level instanceof OldKingsRoadLevel) return ((OldKingsRoadLevel)level).cell(OldKingsRoadLevel.NORTH_X,OldKingsRoadLevel.NORTH_Y);
        if(level instanceof MorningcreekOutskirtsLevel) return ((MorningcreekOutskirtsLevel)level).cell(MorningcreekOutskirtsLevel.NORTH_X,MorningcreekOutskirtsLevel.NORTH_Y);
        if(level instanceof MorningcreekMainStreetLevel) return ((MorningcreekMainStreetLevel)level).cell(MorningcreekMainStreetLevel.INN_X,MorningcreekMainStreetLevel.INN_Y);
        if(level instanceof OldCrowInnLevel) return ((OldCrowInnLevel)level).cell(OldCrowInnLevel.LEDGER_X,OldCrowInnLevel.LEDGER_Y);
        return -1;
    }
}
