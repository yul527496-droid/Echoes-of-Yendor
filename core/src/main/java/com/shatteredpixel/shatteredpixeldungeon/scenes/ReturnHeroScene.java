/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Echoes of Yendor modifications Copyright (C) 2026
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SequelGame;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.utils.RectF;

/** Lightweight sequel entry flow kept separate from Shattered's original hero-select scene. */
public class ReturnHeroScene extends PixelScene {

    @Override
    public void create() {
        super.create();

        Dungeon.hero = null;
        GamesInProgress.selectedClass = null;
        uiCamera.visible = false;

        int screenW = Camera.main.width;
        int screenH = Camera.main.height;
        RectF insets = getCommonInsets();

        add(new TitleBackground(screenW, screenH));
        add(new ColorBlock(screenW, screenH, 0x66000000));

        float usableW = screenW - insets.left - insets.right;

        RenderedTextBlock title = PixelScene.renderTextBlock("Choose the Returning Hero", 14);
        title.maxWidth((int)Math.min(usableW - 16, 220));
        title.setPos(insets.left + (usableW - title.width()) / 2f, insets.top + 18);
        add(title);

        RenderedTextBlock intro = PixelScene.renderTextBlock(
                "Who carried the Amulet of Yendor out of the dungeon?\n\n"
                        + "This is the hero whose story continues in Echoes of Yendor.", 7);
        intro.maxWidth((int)Math.min(usableW - 24, 200));
        intro.setPos(insets.left + (usableW - intro.width()) / 2f, title.bottom() + 10);
        add(intro);

        HeroClass[] classes = HeroClass.values();
        float gap = 4;
        float buttonW = Math.min(88, (usableW - gap - 12) / 2f);
        float buttonH = 22;
        float totalW = buttonW * 2 + gap;
        float startX = insets.left + (usableW - totalW) / 2f;
        float startY = intro.bottom() + 14;

        for (int i = 0; i < classes.length; i++) {
            final HeroClass heroClass = classes[i];
            StyledButton button = new StyledButton(
                    Chrome.Type.GREY_BUTTON_TR,
                    Messages.titleCase(heroClass.title())) {
                @Override
                protected void onClick() {
                    super.onClick();
                    GamesInProgress.selectedClass = heroClass;
                    Game.switchScene(ReturnPrologueScene.class);
                }
            };

            int row = i / 2;
            int col = i % 2;
            button.setRect(startX + col * (buttonW + gap), startY + row * (buttonH + gap), buttonW, buttonH);
            add(button);
        }

        RenderedTextBlock footer = PixelScene.renderTextBlock("Echoes of Yendor  •  development build", 6);
        footer.setPos(insets.left + (usableW - footer.width()) / 2f,
                Math.min(screenH - insets.bottom - footer.height() - 8, startY + 3 * (buttonH + gap) + 10));
        add(footer);

        fadeIn();
    }

    public static class ReturnPrologueScene extends PixelScene {

        @Override
        public void create() {
            super.create();

            uiCamera.visible = false;

            int screenW = Camera.main.width;
            int screenH = Camera.main.height;
            RectF insets = getCommonInsets();
            float usableW = screenW - insets.left - insets.right;

            add(new TitleBackground(screenW, screenH));
            add(new ColorBlock(screenW, screenH, 0x77000000));

            HeroClass selected = GamesInProgress.selectedClass;
            String heroName = selected == null ? "Hero" : Messages.titleCase(selected.title());

            RenderedTextBlock title = PixelScene.renderTextBlock("The Returning Hero", 14);
            title.setPos(insets.left + (usableW - title.width()) / 2f, insets.top + 28);
            add(title);

            RenderedTextBlock text = PixelScene.renderTextBlock(
                    heroName + " has survived the dungeon.\n\n"
                            + "Behind lies Yog-Dzewa and a road paved with monsters. "
                            + "Ahead, beyond the final stair, waits the surface — and the Amulet of Yendor is coming with you.", 7);
            text.maxWidth((int)Math.min(usableW - 28, 210));
            text.setPos(insets.left + (usableW - text.width()) / 2f, title.bottom() + 18);
            add(text);

            StyledButton continueButton = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "Climb the final stair") {
                @Override
                protected void onClick() {
                    super.onClick();
                    SequelGame.start();
                }
            };
            continueButton.setSize(Math.min(130, usableW - 28), 22);
            continueButton.setPos(insets.left + (usableW - continueButton.width()) / 2f, text.bottom() + 22);
            add(continueButton);

            StyledButton backButton = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "Choose another hero") {
                @Override
                protected void onClick() {
                    super.onClick();
                    Game.switchScene(ReturnHeroScene.class);
                }
            };
            backButton.setSize(Math.min(110, usableW - 28), 20);
            backButton.setPos(insets.left + (usableW - backButton.width()) / 2f, continueButton.bottom() + 5);
            add(backButton);

            fadeIn();
        }
    }
}
