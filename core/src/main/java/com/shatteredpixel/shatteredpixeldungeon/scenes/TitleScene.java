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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.RectF;

import java.util.ArrayList;

/**
 * Echoes of Yendor title flow.
 *
 * The title screen is intentionally treated as an object in the world rather than
 * a conventional menu: the player first sees the closed inn ledger, clicks it,
 * and then uses the opened ledger to access existing records or register a new
 * dungeon delver. The original save-slot and six-hero selection scenes remain
 * responsible for their mature game logic.
 */
public class TitleScene extends PixelScene {

    private static final String LEDGER_CLOSED = "interfaces/echoes/ledger_closed.png";
    private static final String LEDGER_OPEN = "interfaces/echoes/ledger_open.png";

    private static final int DESK_BASE = 0xFF5A3B29;
    private static final int DESK_SEAM = 0xFF352219;
    private static final int DESK_GRAIN = 0xFF74513A;
    private static final int INK = 0xFF2A211B;
    private static final int INK_SOFT = 0xFF5B5047;
    private static final int GOLD = 0xFFE8C98D;
    private static final int CREAM = 0xFFF3E2BD;
    private static final int STAMP_RED = 0xFF8E2B2B;

    // Keeps the book open when the player returns from the save list / hero picker.
    // A fresh process still starts with the cover closed.
    private static boolean returnToOpenLedger = false;

    private Image ledgerClosed;
    private Image ledgerOpen;
    private PointerArea ledgerHotArea;
    private Group ledgerUI;

    private RenderedTextBlock title;
    private RenderedTextBlock subtitle;
    private RenderedTextBlock hint;

    private boolean opened;
    private boolean animating;

    private RectF insets;
    private float contentLeft;
    private float contentTop;
    private float contentWidth;
    private float contentHeight;

    private float closedScale;
    private float openScale;

    @Override
    public void create() {
        super.create();

        Music.INSTANCE.playTracks(
                new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
                new float[]{1, 1},
                false);

        uiCamera.visible = false;

        insets = getCommonInsets();
        contentLeft = insets.left;
        contentTop = insets.top;
        contentWidth = Camera.main.width - insets.left - insets.right;
        contentHeight = Camera.main.height - insets.top - insets.bottom;

        createDeskBackground();
        createLedgerImages();
        createIntroText();
        createLedgerUI();
        createUtilityButtons();

        if (returnToOpenLedger) {
            showOpenImmediately();
        }

        fadeIn();
    }

    /**
     * Temporary code-built desk. This gives the existing transparent ledger art a
     * readable home without reusing SPD's dungeon title background. It will later
     * be replaced by the dedicated candlelit desk texture.
     */
    private void createDeskBackground() {
        ColorBlock base = new ColorBlock(Camera.main.width, Camera.main.height, DESK_BASE);
        add(base);

        int plankHeight = landscape() ? 34 : 42;
        for (int y = 0; y < Camera.main.height; y += plankHeight) {
            ColorBlock seam = new ColorBlock(Camera.main.width, 1, DESK_SEAM);
            seam.y = y;
            add(seam);

            if (y + 4 < Camera.main.height) {
                ColorBlock grain = new ColorBlock(Camera.main.width, 1, DESK_GRAIN);
                grain.y = y + 4;
                grain.alpha(0.28f);
                add(grain);
            }
        }
    }

    private void createLedgerImages() {
        ledgerClosed = new Image(LEDGER_CLOSED);
        closedScale = Math.min(
                (contentWidth * (landscape() ? 0.48f : 0.56f)) / ledgerClosed.width,
                (contentHeight * 0.52f) / ledgerClosed.height);
        closedScale = Math.max(1f, Math.min(closedScale, 2.25f));
        ledgerClosed.scale.set(closedScale);
        centerInContent(ledgerClosed, -2f);
        add(ledgerClosed);

        ledgerOpen = new Image(LEDGER_OPEN);
        openScale = Math.min(
                (contentWidth - 12f) / ledgerOpen.width,
                (contentHeight - (landscape() ? 30f : 54f)) / ledgerOpen.height);
        openScale = Math.max(1f, Math.min(openScale, 2.1f));
        ledgerOpen.scale.set(openScale);
        centerInContent(ledgerOpen, landscape() ? 1f : 7f);
        ledgerOpen.visible = false;
        ledgerOpen.am = 0f;
        add(ledgerOpen);

        ledgerHotArea = new PointerArea(ledgerClosed) {
            @Override
            protected void onClick(PointerEvent event) {
                openLedger();
            }

            @Override
            protected void onPointerDown(PointerEvent event) {
                ledgerClosed.brightness(0.86f);
            }

            @Override
            protected void onPointerUp(PointerEvent event) {
                ledgerClosed.resetColor();
            }

            @Override
            protected void onHoverStart(PointerEvent event) {
                ledgerClosed.brightness(1.12f);
            }

            @Override
            protected void onHoverEnd(PointerEvent event) {
                ledgerClosed.resetColor();
            }
        };
        add(ledgerHotArea);
    }

    private void createIntroText() {
        title = PixelScene.renderTextBlock("ECHOES OF YENDOR", landscape() ? 14 : 12);
        title.hardlight(GOLD);
        title.setPos(contentLeft + (contentWidth - title.width()) / 2f, contentTop + 10f);
        add(title);

        subtitle = PixelScene.renderTextBlock("遗迹下行者登记簿", 8);
        subtitle.hardlight(CREAM);
        subtitle.setPos(contentLeft + (contentWidth - subtitle.width()) / 2f, title.bottom() + 3f);
        add(subtitle);

        hint = PixelScene.renderTextBlock("点击登记簿", 7);
        hint.hardlight(CREAM);
        hint.setPos(
                contentLeft + (contentWidth - hint.width()) / 2f,
                Math.min(contentTop + contentHeight - hint.height() - 22f, ledgerClosed.y + ledgerClosed.height() + 10f));
        add(hint);
    }

    private void createLedgerUI() {
        ledgerUI = new Group();
        ledgerUI.visible = false;
        ledgerUI.active = false;
        add(ledgerUI);

        float s = openScale;
        float leftX = ledgerOpen.x + 12f * s;
        float rightX = ledgerOpen.x + 86f * s;
        float pageTop = ledgerOpen.y + 10f * s;
        float pageWidth = 62f * s;

        int headingSize = openScale >= 1.65f ? 8 : 7;
        int bodySize = openScale >= 1.65f ? 7 : 6;

        RenderedTextBlock leftHeading = PixelScene.renderTextBlock("遗迹下行者登记簿", headingSize);
        leftHeading.hardlight(INK);
        leftHeading.setPos(leftX, pageTop);
        ledgerUI.add(leftHeading);

        RenderedTextBlock body = PixelScene.renderTextBlock(bodySize);
        body.text("这里记录着踏入地下遗迹的冒险者们。\n有人再也没有归来，\n有人带着荣耀与故事返回。", Math.max(42, (int) pageWidth));
        body.hardlight(INK_SOFT);
        body.setPos(leftX, leftHeading.bottom() + 5f);
        ledgerUI.add(body);

        RenderedTextBlock rightHeading = PixelScene.renderTextBlock("登记记录", headingSize);
        rightHeading.hardlight(INK);
        rightHeading.setPos(rightX, pageTop);
        ledgerUI.add(rightHeading);

        ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();

        RenderedTextBlock status = PixelScene.renderTextBlock(bodySize);
        if (games.isEmpty()) {
            status.text("尚无下行者记录。", Math.max(42, (int) pageWidth));
        } else if (games.size() >= GamesInProgress.MAX_SLOTS) {
            status.text("已有 " + games.size() + " 份记录。\n名册已满，请先整理旧记录。", Math.max(42, (int) pageWidth));
        } else {
            status.text("已有 " + games.size() + " 份记录。", Math.max(42, (int) pageWidth));
        }
        status.hardlight(INK_SOFT);
        status.setPos(rightX, rightHeading.bottom() + 5f);
        ledgerUI.add(status);

        float buttonWidth = Math.max(48f, pageWidth - 4f);
        float buttonY = status.bottom() + 5f;

        if (!games.isEmpty()) {
            LedgerButton records = new LedgerButton("查看 / 整理登记记录") {
                @Override
                protected void onClick() {
                    Sample.INSTANCE.play(Assets.Sounds.CLICK);
                    returnToOpenLedger = true;
                    ShatteredPixelDungeon.switchNoFade(StartScene.class);
                }
            };
            records.setRect(rightX, buttonY, buttonWidth, 14f);
            ledgerUI.add(records);
            buttonY = records.bottom() + 3f;
        }

        if (games.size() < GamesInProgress.MAX_SLOTS) {
            LedgerButton register = new LedgerButton("登记新的下行者") {
                @Override
                protected void onClick() {
                    Sample.INSTANCE.play(Assets.Sounds.CLICK);
                    returnToOpenLedger = true;
                    Dungeon.daily = Dungeon.dailyReplay = false;
                    GamesInProgress.selectedClass = null;
                    GamesInProgress.curSlot = GamesInProgress.firstEmpty();
                    ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
                }
            };
            register.setRect(rightX, buttonY, buttonWidth, 14f);
            ledgerUI.add(register);
        }
    }

    private void createUtilityButtons() {
        IconButton settings = new IconButton(Icons.get(Icons.PREFS)) {
            @Override
            protected void onClick() {
                Sample.INSTANCE.play(Assets.Sounds.CLICK);
                TitleScene.this.add(new WndSettings());
            }
        };
        settings.setRect(
                contentLeft + contentWidth - 38f,
                contentTop + contentHeight - 18f,
                16f,
                16f);
        add(settings);

        IconButton about = new IconButton(Icons.get(Icons.SHPX)) {
            @Override
            protected void onClick() {
                Sample.INSTANCE.play(Assets.Sounds.CLICK);
                returnToOpenLedger = opened;
                ShatteredPixelDungeon.switchScene(AboutScene.class);
            }
        };
        about.setRect(
                contentLeft + contentWidth - 20f,
                contentTop + contentHeight - 18f,
                16f,
                16f);
        add(about);

        RenderedTextBlock version = PixelScene.renderTextBlock("v" + Game.version, 6);
        version.hardlight(0xFFCFB98E);
        version.setPos(contentLeft + 4f, contentTop + contentHeight - version.height() - 5f);
        add(version);

        if (DeviceCompat.isDesktop()) {
            ExitButton exit = new ExitButton();
            exit.setPos(contentLeft + contentWidth - exit.width(), contentTop);
            add(exit);
        }
    }

    private void openLedger() {
        if (opened || animating) return;

        animating = true;
        ledgerHotArea.active = false;
        ledgerOpen.visible = true;
        ledgerOpen.am = 0f;
        Sample.INSTANCE.play(Assets.Sounds.READ);

        add(new Tweener(this, 0.24f) {
            @Override
            protected void updateValues(float progress) {
                ledgerClosed.am = 1f - progress;
                ledgerOpen.am = progress;
                title.alpha(1f - progress);
                subtitle.alpha(1f - progress);
                hint.alpha(1f - progress);
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                ledgerClosed.visible = false;
                ledgerClosed.am = 0f;
                title.visible = false;
                subtitle.visible = false;
                hint.visible = false;
                ledgerOpen.am = 1f;
                ledgerUI.visible = true;
                ledgerUI.active = true;
                opened = true;
                animating = false;
            }
        });
    }

    private void closeLedger() {
        if (!opened || animating) return;

        animating = true;
        returnToOpenLedger = false;
        ledgerUI.visible = false;
        ledgerUI.active = false;

        ledgerClosed.visible = true;
        ledgerClosed.am = 0f;
        title.visible = true;
        subtitle.visible = true;
        hint.visible = true;
        title.alpha(0f);
        subtitle.alpha(0f);
        hint.alpha(0f);
        Sample.INSTANCE.play(Assets.Sounds.READ);

        add(new Tweener(this, 0.22f) {
            @Override
            protected void updateValues(float progress) {
                ledgerOpen.am = 1f - progress;
                ledgerClosed.am = progress;
                title.alpha(progress);
                subtitle.alpha(progress);
                hint.alpha(progress);
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                ledgerOpen.visible = false;
                ledgerOpen.am = 0f;
                ledgerClosed.am = 1f;
                ledgerHotArea.active = true;
                opened = false;
                animating = false;
            }
        });
    }

    private void showOpenImmediately() {
        opened = true;
        animating = false;

        ledgerClosed.visible = false;
        ledgerClosed.am = 0f;
        ledgerHotArea.active = false;

        ledgerOpen.visible = true;
        ledgerOpen.am = 1f;

        title.visible = false;
        subtitle.visible = false;
        hint.visible = false;

        ledgerUI.visible = true;
        ledgerUI.active = true;
    }

    private void centerInContent(Image image, float yOffset) {
        image.x = contentLeft + (contentWidth - image.width()) / 2f;
        image.y = contentTop + (contentHeight - image.height()) / 2f + yOffset;
        align(image);
    }

    @Override
    protected void onBackPressed() {
        if (animating) return;
        if (opened) {
            closeLedger();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * A parchment-native action: no chrome panel, just ink text and a red rule.
     * This avoids dropping SPD's grey menu buttons on top of the ledger art.
     */
    private static class LedgerButton extends Button {

        private final RenderedTextBlock label;
        private final ColorBlock underline;

        LedgerButton(String text) {
            super();
            label = PixelScene.renderTextBlock(text, 7);
            label.hardlight(INK);
            add(label);

            underline = new ColorBlock(1f, 1f, STAMP_RED);
            underline.alpha(0.38f);
            add(underline);
        }

        @Override
        protected void onPointerDown() {
            label.hardlight(STAMP_RED);
            underline.alpha(0.95f);
        }

        @Override
        protected void onPointerUp() {
            label.hardlight(INK);
            underline.alpha(0.38f);
        }

        @Override
        protected void layout() {
            super.layout();
            if (label == null || underline == null) return;

            label.setPos(
                    x + (width - label.width()) / 2f,
                    y + (height - label.height()) / 2f - 1f);

            float lineWidth = Math.min(width - 6f, Math.max(24f, label.width() + 4f));
            underline.size(lineWidth, 1f);
            underline.x = x + (width - lineWidth) / 2f;
            underline.y = Math.min(y + height - 2f, label.bottom() + 1f);
        }
    }
}
