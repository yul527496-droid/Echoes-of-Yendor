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
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
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
 * The first screen is the inn ledger itself. The existing SPD save-slot and
 * six-hero selection scenes remain responsible for mature game logic; this scene
 * provides the sequel's narrative shell and entry flow.
 */
public class TitleScene extends PixelScene {

    private static final String LEDGER_CLOSED = "interfaces/echoes/ledger_closed.png";
    private static final String LEDGER_OPEN = "interfaces/echoes/ledger_open.png";

    // ColorBlock uses ARGB. RenderedTextBlock.hardlight uses RGB.
    private static final int DESK = 0xFF5A3B29;
    private static final int DESK_SEAM = 0xFF352219;
    private static final int INK = 0x2A211B;
    private static final int INK_SOFT = 0x5B5047;
    private static final int GOLD = 0xE8C98D;
    private static final int CREAM = 0xF3E2BD;
    private static final int STAMP_RED = 0x8E2B2B;

    private static boolean returnToOpenLedger;

    private Image closedBook;
    private Image openBook;
    private PointerArea bookHotArea;
    private Group openUI;

    private RenderedTextBlock title;
    private RenderedTextBlock subtitle;
    private RenderedTextBlock hint;

    private float left;
    private float top;
    private float contentW;
    private float contentH;
    private float openScale;

    private boolean opened;
    private boolean animating;

    @Override
    public void create() {
        super.create();

        Music.INSTANCE.playTracks(
                new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
                new float[]{1f, 1f}, false);

        uiCamera.visible = false;

        RectF insets = getCommonInsets();
        left = insets.left;
        top = insets.top;
        contentW = Camera.main.width - insets.left - insets.right;
        contentH = Camera.main.height - insets.top - insets.bottom;

        createDesk();
        createBooks();
        createIntro();
        createOpenUI();

        if (DeviceCompat.isDesktop()) {
            ExitButton exit = new ExitButton();
            exit.setPos(left + contentW - exit.width(), top);
            add(exit);
        }

        if (returnToOpenLedger) showOpenImmediately();

        fadeIn();
    }

    private void createDesk() {
        add(new ColorBlock(Camera.main.width, Camera.main.height, DESK));

        int plank = landscape() ? 34 : 42;
        for (int y = 0; y < Camera.main.height; y += plank) {
            ColorBlock seam = new ColorBlock(Camera.main.width, 1, DESK_SEAM);
            seam.y = y;
            add(seam);
        }
    }

    private void createBooks() {
        closedBook = new Image(LEDGER_CLOSED);
        float closedScale = Math.min(
                contentW * (landscape() ? 0.48f : 0.56f) / closedBook.width,
                contentH * 0.52f / closedBook.height);
        closedScale = Math.max(1f, Math.min(closedScale, 2.25f));
        closedBook.scale.set(closedScale);
        center(closedBook, -2f);
        add(closedBook);

        openBook = new Image(LEDGER_OPEN);
        openScale = Math.min(
                (contentW - 12f) / openBook.width,
                (contentH - (landscape() ? 30f : 54f)) / openBook.height);
        // Open ledger is 160px wide while portrait PixelScene can be about 135px.
        openScale = Math.max(0.72f, Math.min(openScale, 2.1f));
        openBook.scale.set(openScale);
        center(openBook, landscape() ? 1f : 7f);
        openBook.visible = false;
        openBook.am = 0f;
        add(openBook);

        bookHotArea = new PointerArea(closedBook) {
            @Override
            protected void onClick(PointerEvent event) {
                openLedger();
            }

            @Override
            protected void onPointerDown(PointerEvent event) {
                closedBook.brightness(0.86f);
            }

            @Override
            protected void onPointerUp(PointerEvent event) {
                closedBook.resetColor();
            }

            @Override
            protected void onHoverStart(PointerEvent event) {
                closedBook.brightness(1.12f);
            }

            @Override
            protected void onHoverEnd(PointerEvent event) {
                closedBook.resetColor();
            }
        };
        add(bookHotArea);
    }

    private void createIntro() {
        title = renderTextBlock("ECHOES OF YENDOR", landscape() ? 14 : 12);
        title.hardlight(GOLD);
        title.setPos(left + (contentW - title.width()) / 2f, top + 10f);
        add(title);

        subtitle = renderTextBlock("遗迹下行者登记簿", 8);
        subtitle.hardlight(CREAM);
        subtitle.setPos(left + (contentW - subtitle.width()) / 2f, title.bottom() + 3f);
        add(subtitle);

        hint = renderTextBlock("点击登记簿", 7);
        hint.hardlight(CREAM);
        hint.setPos(
                left + (contentW - hint.width()) / 2f,
                Math.min(top + contentH - 24f, closedBook.y + closedBook.height() + 10f));
        add(hint);
    }

    private void createOpenUI() {
        openUI = new Group();
        openUI.visible = false;
        openUI.active = false;
        add(openUI);

        float pageLeft = openBook.x + 12f * openScale;
        float pageRight = openBook.x + 86f * openScale;
        float pageTop = openBook.y + 10f * openScale;
        float pageWidth = 62f * openScale;

        int headingSize = openScale >= 1.55f ? 8 : 6;
        int bodySize = openScale >= 1.55f ? 7 : 6;
        int buttonSize = openScale >= 1.2f ? 7 : 6;

        RenderedTextBlock leftHeading = renderTextBlock("遗迹下行者登记簿", headingSize);
        leftHeading.hardlight(INK);
        leftHeading.setPos(pageLeft, pageTop);
        openUI.add(leftHeading);

        RenderedTextBlock story = renderTextBlock(bodySize);
        story.text(
                "这里记录着踏入地下遗迹的冒险者们。\n有人再也没有归来，\n有人带着荣耀与故事返回。",
                Math.max(36, (int) pageWidth));
        story.hardlight(INK_SOFT);
        story.setPos(pageLeft, leftHeading.bottom() + 5f);
        openUI.add(story);

        RenderedTextBlock rightHeading = renderTextBlock("登记记录", headingSize);
        rightHeading.hardlight(INK);
        rightHeading.setPos(pageRight, pageTop);
        openUI.add(rightHeading);

        ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();
        RenderedTextBlock status = renderTextBlock(bodySize);
        if (games.isEmpty()) {
            status.text("尚无下行者记录。", Math.max(36, (int) pageWidth));
        } else if (games.size() >= GamesInProgress.MAX_SLOTS) {
            status.text("已有 " + games.size() + " 份记录。\n名册已满，请先整理旧记录。",
                    Math.max(36, (int) pageWidth));
        } else {
            status.text("已有 " + games.size() + " 份记录。", Math.max(36, (int) pageWidth));
        }
        status.hardlight(INK_SOFT);
        status.setPos(pageRight, rightHeading.bottom() + 5f);
        openUI.add(status);

        float buttonW = Math.max(40f, pageWidth - 4f);
        float buttonY = status.bottom() + 5f;

        if (!games.isEmpty()) {
            LedgerButton records = new LedgerButton("整理登记记录", buttonSize) {
                @Override
                protected void onClick() {
                    Sample.INSTANCE.play(Assets.Sounds.CLICK);
                    returnToOpenLedger = true;
                    ShatteredPixelDungeon.switchNoFade(StartScene.class);
                }
            };
            records.setRect(pageRight, buttonY, buttonW, 14f);
            openUI.add(records);
            buttonY = records.bottom() + 3f;
        }

        if (games.size() < GamesInProgress.MAX_SLOTS) {
            LedgerButton register = new LedgerButton("登记新的下行者", buttonSize) {
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
            register.setRect(pageRight, buttonY, buttonW, 14f);
            openUI.add(register);
        }
    }

    private void openLedger() {
        if (opened || animating) return;

        animating = true;
        bookHotArea.active = false;
        openBook.visible = true;
        openBook.am = 0f;
        Sample.INSTANCE.play(Assets.Sounds.READ);

        add(new Tweener(this, 0.24f) {
            @Override
            protected void updateValues(float progress) {
                closedBook.am = 1f - progress;
                openBook.am = progress;
                title.alpha(1f - progress);
                subtitle.alpha(1f - progress);
                hint.alpha(1f - progress);
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                closedBook.visible = false;
                title.visible = subtitle.visible = hint.visible = false;
                openBook.am = 1f;
                openUI.visible = true;
                openUI.active = true;
                opened = true;
                animating = false;
            }
        });
    }

    private void closeLedger() {
        if (!opened || animating) return;

        animating = true;
        returnToOpenLedger = false;
        openUI.visible = false;
        openUI.active = false;

        closedBook.visible = true;
        closedBook.am = 0f;
        title.visible = subtitle.visible = hint.visible = true;
        title.alpha(0f);
        subtitle.alpha(0f);
        hint.alpha(0f);
        Sample.INSTANCE.play(Assets.Sounds.READ);

        add(new Tweener(this, 0.22f) {
            @Override
            protected void updateValues(float progress) {
                openBook.am = 1f - progress;
                closedBook.am = progress;
                title.alpha(progress);
                subtitle.alpha(progress);
                hint.alpha(progress);
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                openBook.visible = false;
                closedBook.am = 1f;
                bookHotArea.active = true;
                opened = false;
                animating = false;
            }
        });
    }

    private void showOpenImmediately() {
        opened = true;
        closedBook.visible = false;
        closedBook.am = 0f;
        bookHotArea.active = false;
        openBook.visible = true;
        openBook.am = 1f;
        title.visible = subtitle.visible = hint.visible = false;
        openUI.visible = true;
        openUI.active = true;
    }

    private void center(Image image, float yOffset) {
        image.x = left + (contentW - image.width()) / 2f;
        image.y = top + (contentH - image.height()) / 2f + yOffset;
        align(image);
    }

    @Override
    protected void onBackPressed() {
        if (animating) return;
        if (opened) closeLedger();
        else super.onBackPressed();
    }

    private static class LedgerButton extends Button {

        private final RenderedTextBlock label;
        private final ColorBlock underline;

        LedgerButton(String text, int fontSize) {
            super();
            label = PixelScene.renderTextBlock(text, fontSize);
            label.hardlight(INK);
            add(label);

            underline = new ColorBlock(1f, 1f, 0xFF000000 | STAMP_RED);
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

            float lineW = Math.min(width - 6f, Math.max(24f, label.width() + 4f));
            underline.size(lineW, 1f);
            underline.x = x + (width - lineW) / 2f;
            underline.y = Math.min(y + height - 2f, label.bottom() + 1f);
        }
    }
}
