/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ActOneReturnState;
import com.shatteredpixel.shatteredpixeldungeon.ChapterOneAudio;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.ActOneReturnLevel;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;

/** Near-silent, exact-text opening for Act 1 Scene 1. */
public class ActOneOpeningScene extends PixelScene {

    // Highest-level canon: do not alter characters, order, or add text here.
    private static final String[] LINES = {
            "我成功了。",
            "我杀死了古神。",
            "我带着 Yendor 回来了。",
            "……",
            "我独自一人。"
    };

    // A little breathing space between beats, with a longer hold on the final statement.
    private static final float FIRST_AT = 0.80f;
    private static final float STEP = 1.15f;
    private static final float FINAL_HOLD = 1.55f;

    private RenderedTextBlock text;
    private float elapsed;
    private int visibleLines;
    private boolean leaving;

    @Override
    public void create() {
        super.create();
        ChapterOneAudio.stopAmbience();

        ColorBlock black = new ColorBlock(uiCamera.width, uiCamera.height, 0xFF000000);
        black.camera = uiCamera;
        add(black);

        text = renderTextBlock("", 9);
        text.setLedgerPixelFont(true);
        text.maxWidth(Math.max(120, uiCamera.width - 36));
        text.lineSpacing(6f);
        text.hardlight(0xF0EEE8);
        text.camera = uiCamera;
        add(text);

        // A loaded scene should never replay this sequence. The transition is still delayed
        // until create() so there is no partially initialized GameScene in between.
        ActOneReturnState state = ActOneReturnState.get();
        if (state != null && state.openingShown) finish();
    }

    @Override
    public void update() {
        super.update();
        if (leaving) return;

        elapsed += Game.elapsed;
        int shouldShow = 0;
        if (elapsed >= FIRST_AT) {
            shouldShow = Math.min(LINES.length,
                    1 + (int) ((elapsed - FIRST_AT) / STEP));
        }
        if (shouldShow != visibleLines) {
            visibleLines = shouldShow;
            rebuildText();
        }

        float doneAt = FIRST_AT + STEP * (LINES.length - 1) + FINAL_HOLD;
        if (elapsed >= doneAt) finish();
    }

    private void rebuildText() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < visibleLines; i++) {
            if (i > 0) out.append('\n');
            out.append(LINES[i]);
        }
        text.text(out.toString());
        text.setPos((uiCamera.width - text.width()) / 2f,
                (uiCamera.height - text.height()) / 2f);
        align(text);
    }

    private void finish() {
        if (leaving) return;
        leaving = true;
        ActOneReturnState state = ActOneReturnState.get();
        if (state != null) state.openingShown = true;

        ActOneReturnLevel level = new ActOneReturnLevel();
        level.create();
        SequelTransitionScene.enter(level, -1);
    }
}
