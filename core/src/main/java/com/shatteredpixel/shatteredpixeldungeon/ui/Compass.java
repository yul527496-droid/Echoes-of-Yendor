/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekMainStreetLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MorningcreekOutskirtsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldCrowInnLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.OldKingsRoadLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SurfaceEntranceLevel;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.utils.PointF;

public class Compass extends Image {

	private static final float RAD_2_G	= 180f / 3.1415926f;
	private static final float RADIUS	= 12;
	
	private int cell;
	private PointF cellCenter;
	private final boolean directionHint;
	
	private PointF lastScroll = new PointF();
	
	public Compass( int cell ) {
		this(surfaceTargetOr(cell), isSurfaceStoryLevel());
	}

	/**
	 * directionHint=true keeps the compass visible for a story destination even when the
	 * exact cell has not been discovered. It reveals only a bearing, never a map marker.
	 */
	public Compass( int cell, boolean directionHint ) {
		
		super();
		copy( Icons.COMPASS.get() );
		origin.set( width / 2, RADIUS );
		
		this.cell = cell;
		this.directionHint = directionHint;
		cellCenter = cell >= 0 ? DungeonTilemap.tileCenterToWorld( cell ) : new PointF();
		visible = false;
	}

	private static boolean isSurfaceStoryLevel() {
		Level level = Dungeon.level;
		return level instanceof SurfaceEntranceLevel
				|| level instanceof OldKingsRoadLevel
				|| level instanceof MorningcreekOutskirtsLevel
				|| level instanceof MorningcreekMainStreetLevel
				|| level instanceof OldCrowInnLevel;
	}

	private static int surfaceTargetOr(int fallback) {
		Level level = Dungeon.level;
		if (level instanceof SurfaceEntranceLevel) {
			SurfaceEntranceLevel surface = (SurfaceEntranceLevel) level;
			return surface.cell(SurfaceEntranceLevel.NORTH_X, SurfaceEntranceLevel.NORTH_Y);
		}
		if (level instanceof OldKingsRoadLevel) {
			OldKingsRoadLevel road = (OldKingsRoadLevel) level;
			return road.cell(OldKingsRoadLevel.NORTH_X, OldKingsRoadLevel.NORTH_Y);
		}
		if (level instanceof MorningcreekOutskirtsLevel) {
			MorningcreekOutskirtsLevel outskirts = (MorningcreekOutskirtsLevel) level;
			return outskirts.cell(MorningcreekOutskirtsLevel.NORTH_X, MorningcreekOutskirtsLevel.NORTH_Y);
		}
		if (level instanceof MorningcreekMainStreetLevel) {
			MorningcreekMainStreetLevel town = (MorningcreekMainStreetLevel) level;
			return town.cell(MorningcreekMainStreetLevel.INN_X, MorningcreekMainStreetLevel.INN_Y);
		}
		if (level instanceof OldCrowInnLevel) {
			OldCrowInnLevel inn = (OldCrowInnLevel) level;
			return inn.cell(OldCrowInnLevel.LEDGER_X, OldCrowInnLevel.LEDGER_Y);
		}
		return fallback;
	}
	
	@Override
	public void update() {
		super.update();

		// Surface navigation already lives in the fixed minimap header (N + objective bearing).
		// Keep SPD's portrait compass for dungeon/non-surface play, but do not duplicate it here.
		if (isSurfaceStoryLevel()) {
			visible = false;
			return;
		}
		
		if (cell < 0 || cell >= Dungeon.level.length() || Dungeon.hero == null || cell == Dungeon.hero.pos){
			visible = false;
			return;
		}
		
		visible = directionHint || Dungeon.level.visited[cell] || Dungeon.level.mapped[cell];
		
		if (visible) {
			PointF scroll = Camera.main.scroll;
			if (!scroll.equals( lastScroll )) {
				lastScroll.set( scroll );
				PointF center = Camera.main.center().offset( scroll );
				angle = (float)Math.atan2( cellCenter.x - center.x, center.y - cellCenter.y ) * RAD_2_G;
			}
		}
	}
}
