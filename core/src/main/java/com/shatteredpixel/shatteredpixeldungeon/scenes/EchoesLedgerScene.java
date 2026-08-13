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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
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
import java.util.Base64;

/**
 * Echoes of Yendor's ledger-first title scene.
 *
 * The artwork is stored as base64 text only as a temporary repository transport
 * workaround. At runtime it is decoded into a normal Pixmap/Image.
 */
public class EchoesLedgerScene extends PixelScene {

    private static final String CLOSED_ART = "interfaces/echoes/ledger_closed_v2.b64";
    private static final String OPEN_ART = "interfaces/echoes/ledger_open_v2.b64";

    private static final int INK = 0x2B2119;
    private static final int INK_SOFT = 0x55483C;
    private static final int CREAM = 0xF4DEAE;
    private static final int STAMP_RED = 0x8C2929;

    private static boolean returnToOpenLedger;

    private Image closedArt;
    private Image openArt;
    private PointerArea openHotArea;
    private Group openUI;
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
        // The ledger deliberately uses the bundled Droid Sans generator so CJK
        // glyphs render even when the game's current language is not Chinese.
        boolean previousSystemFont = SPDSettings.systemFont();
        if (!previousSystemFont) SPDSettings.systemFont(true);
        super.create();
        if (!previousSystemFont) SPDSettings.systemFont(false);

        Music.INSTANCE.playTracks(
                new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
                new float[]{1f, 1f}, false);

        uiCamera.visible = false;

        RectF insets = getCommonInsets();
        left = insets.left;
        top = insets.top;
        contentW = Camera.main.width - insets.left - insets.right;
        contentH = Camera.main.height - insets.top - insets.bottom;

        createArtwork();
        createClosedHint();
        createOpenUI();

        if (DeviceCompat.isDesktop()) {
            ExitButton exit = new ExitButton();
            exit.setPos(left + contentW - exit.width(), top);
            add(exit);
        }

        if (returnToOpenLedger) showOpenImmediately();
        fadeIn();
    }

    private void createArtwork() {
        closedArt = loadBase64Image(CLOSED_ART);
        float closedScale = landscape()
                ? Math.max(contentW / closedArt.width, contentH / closedArt.height)
                : Math.min(contentW / closedArt.width, contentH / closedArt.height);
        closedArt.scale.set(closedScale);
        center(closedArt);
        add(closedArt);

        openArt = loadBase64Image(OPEN_ART);
        openScale = Math.min(contentW / openArt.width, contentH / openArt.height);
        openScale = Math.max(0.2f, openScale);
        openArt.scale.set(openScale);
        center(openArt);
        openArt.visible = false;
        openArt.am = 0f;
        add(openArt);

        // First pass: make the whole title artwork clickable so the interaction
        // stays reliable at every aspect ratio. We can tighten this to the book
        // bounds after the visual layout is approved.
        openHotArea = new PointerArea(left, top, contentW, contentH) {
            @Override
            protected void onClick(PointerEvent event) {
                openLedger();
            }

            @Override
            protected void onPointerDown(PointerEvent event) {
                closedArt.brightness(0.92f);
            }

            @Override
            protected void onPointerUp(PointerEvent event) {
                closedArt.resetColor();
            }
        };
        add(openHotArea);
    }

    private static Image loadBase64Image(String path) {
        String encoded = Gdx.files.internal(path).readString("UTF-8").replaceAll("\\s+", "");
        byte[] bytes = Base64.getDecoder().decode(encoded);
        Pixmap pixmap = new Pixmap(bytes, 0, bytes.length);
        return new Image(pixmap);
    }

    private void createClosedHint() {
        hint = ledgerText("点击或触碰名册以翻开它", landscape() ? 7 : 6);
        hint.hardlight(CREAM);
        hint.setPos(
                left + (contentW - hint.width()) / 2f,
                top + contentH - hint.height() - 8f);
        add(hint);
    }

    private void createOpenUI() {
        openUI = new Group();
        openUI.visible = false;
        openUI.active = false;
        add(openUI);

        // Coordinates are measured against the approved 480x270 ledger artwork.
        float pageLeft = openArt.x + 88f * openScale;
        float pageRight = openArt.x + 258f * openScale;
        float pageTop = openArt.y + 49f * openScale;
        float pageWidth = 132f * openScale;

        int headingSize = openScale >= 0.75f ? 9 : 7;
        int bodySize = openScale >= 0.75f ? 7 : 6;
        int buttonSize = openScale >= 0.7f ? 7 : 6;

        RenderedTextBlock leftHeading = ledgerText("遗迹下行者登记簿", headingSize);
        leftHeading.hardlight(INK);
        leftHeading.setPos(pageLeft, pageTop);
        openUI.add(leftHeading);

        RenderedTextBlock inn = ledgerText("晨溪镇 · 老鸦旅店", bodySize);
        inn.hardlight(INK_SOFT);
        inn.setPos(pageLeft, leftHeading.bottom() + 2f);
        openUI.add(inn);

        RenderedTextBlock story = ledgerText(bodySize);
        story.text(
                "这里记录着踏入地下遗迹的冒险者们。\n"
                        + "有人再也没有归来，\n"
                        + "有人带着荣耀与故事返回。",
                Math.max(58, (int) pageWidth));
        story.hardlight(INK_SOFT);
        story.setPos(pageLeft, inn.bottom() + 6f);
        openUI.add(story);

        RenderedTextBlock rightHeading = ledgerText("登记记录", headingSize);
        rightHeading.hardlight(INK);
        rightHeading.setPos(pageRight, pageTop);
        openUI.add(rightHeading);

        ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();
        RenderedTextBlock status = ledgerText(bodySize);
        if (games.isEmpty()) {
            status.text("尚无下行者记录。", Math.max(58, (int) pageWidth));
        } else if (games.size() >= GamesInProgress.MAX_SLOTS) {
            status.text(
                    "已有 " + games.size() + " 份记录。\n名册已满，请先整理旧记录。",
                    Math.max(58, (int) pageWidth));
        } else {
            status.text("已有 " + games.size() + " 份记录。", Math.max(58, (int) pageWidth));
        }
        status.hardlight(INK_SOFT);
        status.setPos(pageRight, rightHeading.bottom() + 7f);
        openUI.add(status);

        float buttonW = Math.max(58f, pageWidth);
        float buttonY = Math.max(status.bottom() + 8f, openArt.y + 138f * openScale);

        if (!games.isEmpty()) {
            LedgerButton records = new LedgerButton("整理登记记录", buttonSize) {
                @Override
                protected void onClick() {
                    Sample.INSTANCE.play(Assets.Sounds.CLICK);
                    returnToOpenLedger = true;
                    ShatteredPixelDungeon.switchNoFade(StartScene.class);
                }
            };
            records.setRect(pageRight, buttonY, buttonW, 16f);
            openUI.add(records);
            buttonY = records.bottom() + 4f;
        }

        if (games.size() < GamesInProgress.MAX_SLOTS) {
            LedgerButton register = new LedgerButton("＋ 登记新的下行者", buttonSize) {
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
            register.setRect(pageRight, buttonY, buttonW, 16f);
            openUI.add(register);
        }
    }

    /** Ledger text has no outline: dark glyphs stay crisp on bright parchment. */
    private static RenderedTextBlock ledgerText(String text, int size) {
        float realScale = DeviceCompat.getRealPixelScaleX();
        int fontScale = Math.max(1, Math.round(defaultZoom * realScale));
        RenderedTextBlock result = new RenderedTextBlock(text, size * fontScale, false);
        result.zoom(1f / fontScale);
        return result;
    }

    private static RenderedTextBlock ledgerText(int size) {
        return ledgerText("", size);
    }

    private void openLedger() {
        if (opened || animating) return;

        animating = true;
        openHotArea.active = false;
        openArt.visible = true;
        openArt.am = 0f;
        Sample.INSTANCE.play(Assets.Sounds.READ);

        add(new Tweener(this, 0.24f) {
            @Override
            protected void updateValues(float progress) {
                closedArt.am = 1f - progress;
                openArt.am = progress;
                hint.alpha(1f - progress);
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                closedArt.visible = false;
                hint.visible = false;
                openArt.am = 1f;
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

        closedArt.visible = true;
        closedArt.am = 0f;
        hint.visible = true;
        hint.alpha(0f);
        Sample.INSTANCE.play(Assets.Sounds.READ);

        add(new Tweener(this, 0.22f) {
            @Override
            protected void updateValues(float progress) {
                openArt.am = 1f - progress;
                closedArt.am = progress;
                hint.alpha(progress);
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                openArt.visible = false;
                closedArt.am = 1f;
                openHotArea.active = true;
                opened = false;
                animating = false;
            }
        });
    }

    private void showOpenImmediately() {
        opened = true;
        closedArt.visible = false;
        closedArt.am = 0f;
        openHotArea.active = false;
        openArt.visible = true;
        openArt.am = 1f;
        hint.visible = false;
        openUI.visible = true;
        openUI.active = true;
    }

    private void center(Image image) {
        image.x = left + (contentW - image.width()) / 2f;
        image.y = top + (contentH - image.height()) / 2f;
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
            label = ledgerText(text, fontSize);
            label.hardlight(INK);
            add(label);

            underline = new ColorBlock(1f, 1f, 0xFF000000 | STAMP_RED);
            underline.alpha(0.42f);
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
            underline.alpha(0.42f);
        }

        @Override
        protected void layout() {
            super.layout();
            if (label == null || underline == null) return;

            label.setPos(
                    x + (width - label.width()) / 2f,
                    y + (height - label.height()) / 2f - 1f);

            float lineW = Math.min(width - 6f, Math.max(30f, label.width() + 5f));
            underline.size(lineW, 1f);
            underline.x = x + (width - lineW) / 2f;
            underline.y = Math.min(y + height - 2f, label.bottom() + 1f);
        }
    }
}
