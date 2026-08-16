/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.RegionMapSettings;
import com.shatteredpixel.shatteredpixeldungeon.SequelState;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekMainStreetLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

/** North-up explored-region map drawn from Level's visited/mapped/FOV knowledge only. */
public class WndRegionMap extends Window {

    private static final String PIXEL="interfaces/echoes/minimap_pixel.png";
    private static final int WIDTH=220, HEIGHT=194, MARGIN=8;
    private static final int VIEW_X=10, VIEW_Y=27, VIEW_W=200, VIEW_H=112;
    private static final int HERO=0xF2D36B, WATER=0x4B93A0, WALL=0x3E5940,
            ROAD=0xA78B62, GRASS=0x6F9954, EXIT=0xE7D9A0, TARGET=0xD64D9C;

    private final int zoom;

    public WndRegionMap(){
        super();
        zoom=RegionMapSettings.zoom();
        resize(WIDTH,HEIGHT);

        RenderedTextBlock title=PixelScene.renderTextBlock("区域地图   N ↑",9);
        title.hardlight(TITLE_COLOR); title.setPos(MARGIN,MARGIN); add(title);

        Level level=Dungeon.level;
        if(level==null||Dungeon.hero==null){
            RenderedTextBlock none=PixelScene.renderTextBlock("暂无区域数据",7); none.setPos(MARGIN,32); add(none); return;
        }

        drawMap(level);
        buildZoomControls();

        SequelState state=Dungeon.hero.buff(SequelState.class);
        String objectiveText=state==null?"":state.objectiveText();
        RenderedTextBlock legend=PixelScene.renderTextBlock("金：你   紫：目标   蓝：水   米：道路/出口\n"+objectiveText,6);
        legend.maxWidth(WIDTH-MARGIN*2);
        legend.setPos(MARGIN,166); add(legend);
    }

    private void drawMap(Level level){
        int w=level.width(), h=level.height();
        int fit=Math.max(1,Math.min(3,Math.min(VIEW_W/Math.max(1,w),VIEW_H/Math.max(1,h))));
        int cell=Math.max(1,fit*zoom);

        int heroX=Dungeon.hero.pos%w;
        int heroY=Dungeon.hero.pos/w;
        float centerX=VIEW_X+VIEW_W/2f;
        float centerY=VIEW_Y+VIEW_H/2f;
        float originX=zoom<=1 ? VIEW_X+(VIEW_W-w*cell)/2f : centerX-(heroX+0.5f)*cell;
        float originY=zoom<=1 ? VIEW_Y+(VIEW_H-h*cell)/2f : centerY-(heroY+0.5f)*cell;
        int objective=objectiveCell(level);

        for(int y=0;y<h;y++) for(int x=0;x<w;x++){
            int c=x+y*w;
            boolean fov=known(level.heroFOV,c), visited=known(level.visited,c), mapped=known(level.mapped,c);
            if(c!=Dungeon.hero.pos&&!fov&&!visited&&!mapped) continue;

            float pxX=originX+x*cell;
            float pxY=originY+y*cell;
            if(pxX+cell<=VIEW_X||pxX>=VIEW_X+VIEW_W||pxY+cell<=VIEW_Y||pxY>=VIEW_Y+VIEW_H) continue;

            int color;
            if(c==Dungeon.hero.pos) color=HERO;
            else if(c==objective) color=TARGET;
            else if(level.map[c]==Terrain.WATER) color=WATER;
            else if(level.map[c]==Terrain.WALL) color=WALL;
            else if(level.map[c]==Terrain.ENTRANCE||level.map[c]==Terrain.EXIT) color=EXIT;
            else if(level.map[c]==Terrain.EMPTY||level.map[c]==Terrain.EMPTY_SP||level.map[c]==Terrain.EMPTY_DECO) color=ROAD;
            else color=GRASS;

            Image px=new Image(PIXEL);
            px.hardlight(color);
            px.scale.set(cell,cell);
            if(!fov&&c!=Dungeon.hero.pos) px.alpha(visited?0.68f:0.40f);
            px.x=pxX; px.y=pxY; add(px);
        }
    }

    private void buildZoomControls(){
        RedButton minus=new RedButton("－",8){
            @Override protected void onClick(){
                super.onClick();
                setZoom(zoom-1);
            }
        };
        minus.setRect(69,143,28,18);
        minus.enable(zoom>1);
        add(minus);

        RenderedTextBlock scale=PixelScene.renderTextBlock(zoom+"×",8);
        scale.hardlight(TITLE_COLOR);
        scale.setPos((WIDTH-scale.width())/2f,148);
        add(scale);

        RedButton plus=new RedButton("+",8){
            @Override protected void onClick(){
                super.onClick();
                setZoom(zoom+1);
            }
        };
        plus.setRect(123,143,28,18);
        plus.enable(zoom<4);
        add(plus);
    }

    private void setZoom(int value){
        int next=Math.max(1,Math.min(4,value));
        if(next==zoom) return;
        RegionMapSettings.zoom(next);
        hide();
        GameScene.show(new WndRegionMap());
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
