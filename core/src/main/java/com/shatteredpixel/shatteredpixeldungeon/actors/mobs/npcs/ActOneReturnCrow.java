/* Echoes of Yendor modifications Copyright (C) 2026 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.levels.ActOneReturnLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EchoesBirdSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

/** One ordinary crow used as sparse, readable visual guidance during the Yendor chase. */
public class ActOneReturnCrow extends NPC {

    private static final String STAGE = "return_crow_stage";
    private int stage;

    // Presentation-only state is intentionally transient. If a save interrupts a tween, reload
    // reconstructs the crow at its durable stage stop rather than serializing animation internals.
    private transient boolean visualFlight;
    private transient boolean departing;
    private transient int farTicks;
    private transient int lastAssistStage = -1;

    {
        spriteClass = EchoesBirdSprite.class;
        flying = true;
    }

    @Override
    protected boolean act() {
        ActOneReturnState state = ActOneReturnState.current();
        if (!(Dungeon.level instanceof ActOneReturnLevel)
                || state == null || !state.crowChaseActive || state.yendorRecovered) {
            destroyCrow();
            return true;
        }

        ActOneReturnLevel level = (ActOneReturnLevel) Dungeon.level;
        int[] stops = level.crowStops();
        stage = Math.max(0, Math.min(stage, stops.length - 1));

        if (visualFlight || departing) {
            // No waitUntil/sprite.isMoving coupling: the hero keeps normal turn control while the
            // Noosa tween finishes in presentation time.
            spend(TICK);
            return true;
        }

        if (!state.crowTheftPresented) {
            if (!(sprite instanceof EchoesBirdSprite)) {
                spend(TICK);
                return true;
            }
            presentInitialTheft(level, stops[0]);
            spend(TICK);
            return true;
        }

        int target = stops[stage];
        if (pos != target) {
            // Save migration / interrupted-tween recovery only. Normal stage travel never uses place().
            pos = target;
            if (sprite != null) sprite.place(pos);
        }

        int distance = Dungeon.level.distance(Dungeon.hero.pos, pos);
        if (distance <= 4 && stage < stops.length - 1) {
            flyToNextStop(stops);
        } else if (stage == stops.length - 1 && distance <= 3) {
            leaveShrine(level);
        } else {
            provideLostPlayerAssist(distance);
            if (Dungeon.level.heroFOV != null
                    && pos >= 0 && pos < Dungeon.level.heroFOV.length
                    && Dungeon.level.heroFOV[pos] && sprite != null) {
                sprite.turnTo(pos, Dungeon.hero.pos);
            }
        }

        spend(TICK);
        return true;
    }

    /**
     * Theft presentation: the already-spawned logical crow owns stop 0, while its sprite begins on
     * a nearby ordinary bush/perch, crosses the hero, then visibly lands beside the first old wall.
     */
    private void presentInitialTheft(ActOneReturnLevel level, int firstStop) {
        EchoesBirdSprite bird = (EchoesBirdSprite) sprite;
        final int perch = level.cell(68, 35);
        final int heroCell = Dungeon.hero.pos;
        pos = firstStop;
        visualFlight = true;
        bird.place(perch);

        ChapterOneAudio.playRaven();
        ChapterOneAudio.playRavenWings();
        bird.flyTo(perch, heroCell, 0.28f, () -> {
            if (!(Dungeon.level instanceof ActOneReturnLevel) || sprite == null) {
                visualFlight = false;
                return;
            }
            ChapterOneAudio.playRavenWings();
            ((EchoesBirdSprite) sprite).flyTo(heroCell, firstStop, 0.56f, () -> {
                visualFlight = false;
                ActOneReturnState current = ActOneReturnState.current();
                if (current != null) current.markCrowTheftPresented();
                Game.runOnRenderThread(() -> GameScene.show(new WndMessage(
                        "吊链从指间滑了出去。\n\n乌鸦衔着它，落向左侧林间。")));
            });
        });
    }

    private void flyToNextStop(int[] stops) {
        int from = pos;
        stage++;
        int next = stops[stage];
        pos = next; // durable logical state updates immediately; only the sprite interpolates.
        farTicks = 0;
        lastAssistStage = -1;

        ChapterOneAudio.playRavenWings();
        // One restrained mid-chase reminder, not a call at every waypoint.
        if (stage == 2) ChapterOneAudio.playRaven();

        if (sprite instanceof EchoesBirdSprite) {
            visualFlight = true;
            ((EchoesBirdSprite) sprite).flyTo(from, next, 0.58f, () -> visualFlight = false);
        }
    }

    private void leaveShrine(ActOneReturnLevel level) {
        departing = true;
        ChapterOneAudio.playRaven();
        ChapterOneAudio.playRavenWings();
        int from = pos;
        int away = level.cell(34, 17);
        if (sprite instanceof EchoesBirdSprite) {
            ((EchoesBirdSprite) sprite).flyTo(from, away, 0.62f, this::destroyCrow);
        } else {
            destroyCrow();
        }
    }

    /** A single distant call per stage can recover a chase the player has genuinely lost. */
    private void provideLostPlayerAssist(int distance) {
        if (distance <= 14) {
            farTicks = 0;
            return;
        }
        farTicks++;
        if (farTicks >= 10 && lastAssistStage != stage) {
            lastAssistStage = stage;
            farTicks = 0;
            ChapterOneAudio.playRavenDistant();
        }
    }

    private void destroyCrow() {
        departing = false;
        visualFlight = false;
        destroy();
        if (sprite != null) sprite.die();
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public String name() {
        return "乌鸦";
    }

    @Override
    public String description() {
        return "一只普通的乌鸦。只是从刚才开始，它似乎对 Yendor 的吊链格外在意。";
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STAGE, stage);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        stage = Math.max(0, bundle.getInt(STAGE));
        visualFlight = false;
        departing = false;
        farTicks = 0;
        lastAssistStage = -1;
    }
}
