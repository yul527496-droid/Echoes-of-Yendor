/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.SurfaceVillager;
import com.watabou.noosa.TextureFilm;

/** Eight distinct Morningcreek residents using stable per-NPC visual variants. */
public class SurfaceVillagerSprite extends MobSprite {

    private static final String TEXTURE = "sprites/echoes_ch1_character_sprites_v1.png";
    private static final int FIRST_RESIDENT_ROW = 7;

    public SurfaceVillagerSprite() {
        super();
        configure(0);
    }

    @Override
    public void linkVisuals(Char ch) {
        super.linkVisuals(ch);
        int variant = ch instanceof SurfaceVillager ? ((SurfaceVillager) ch).artVariant() : 0;
        configure(variant);
    }

    private void configure(int variant) {
        variant = Math.max(0, Math.min(7, variant));
        texture(TEXTURE);
        TextureFilm film = new TextureFilm(texture, 16, 16);
        int first = (FIRST_RESIDENT_ROW + variant) * 8;

        idle = new Animation(3, true);
        idle.frames(film, first, first, first + 1, first);

        run = new Animation(8, true);
        run.frames(film, first + 2, first + 3, first + 4, first + 5);

        operate = new Animation(5, false);
        operate.frames(film, first + 6, first + 7);

        attack = idle.clone();
        die = new Animation(20, false);
        die.frames(film, first);
        idle();
    }
}
