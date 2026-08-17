/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;

import java.util.ArrayList;

/** Compact JRPG-style story stage with native-pixel portraits and restrained choices. */
public class WndDialogueStage extends Window {

    public enum Portrait {
        NONE,
        /** Legacy neutral alias retained so older story beats remain source-compatible. */
        HERO,
        HERO_NEUTRAL,
        HERO_ALERT,
        HERO_CONCERNED,
        FARMER_NEUTRAL,
        FARMER_WARM,
        FARMER_CONFUSED,
        FARMER_FIXATED,
        FARMER_SHAKEN,
        INNKEEPER_NEUTRAL,
        INNKEEPER_ATTENTIVE,
        INNKEEPER_SERIOUS
    }

    public interface ChoiceListener { void onSelect(int index); }

    private static final int MARGIN = 4;
    private static final int PORTRAIT_W_L = 42;
    private static final int PORTRAIT_W_P = 36;
    private static final int OPTION_H = 11;
    private static final int CHOICE_TEXT = 0xDDD2BD;
    private static final int CHOICE_ACTIVE = 0xF1D6A4;
    private static final int CHOICE_PRESS = 0xFFF0CF;

    private final Callback onAdvance;
    private final ChoiceListener onChoice;
    private final boolean hasChoices;
    private final String fullMessage;
    private final RenderedTextBlock body;
    private final float charsPerSecond;
    private float revealProgress;
    private int revealedChars;
    private boolean revealComplete;

    private final ArrayList<DialogueChoiceButton> optionButtons = new ArrayList<>();
    private final ArrayList<String> optionLabels = new ArrayList<>();
    private int selectedOption;

    public WndDialogueStage(String speaker, String message, Portrait portrait, Callback onAdvance) {
        this(speaker, message, portrait, onAdvance, null, new String[0]);
    }

    public WndDialogueStage(String speaker, String message, Portrait portrait,
                            ChoiceListener onChoice, String... choices) {
        this(speaker, message, portrait, null, onChoice, choices);
    }

    private WndDialogueStage(String speaker, String message, Portrait portrait,
                             Callback onAdvance, ChoiceListener onChoice, String... choices) {
        super();
        this.onAdvance = onAdvance;
        this.onChoice = onChoice;
        this.hasChoices = choices != null && choices.length > 0;
        this.fullMessage = message == null ? "" : message;
        this.charsPerSecond = portrait == Portrait.FARMER_FIXATED ? 22f : 34f;

        shadow.am = 0.16f;

        boolean landscape = PixelScene.landscape();
        int screenW = PixelScene.uiCamera.width;
        int stageW = Math.min(landscape ? 204 : 124, screenW - 8);
        int portraitW = portrait == Portrait.NONE ? 0 : (landscape ? PORTRAIT_W_L : PORTRAIT_W_P);
        boolean heroRight = isHero(portrait);

        int textLeft = MARGIN + (portraitW > 0 && !heroRight ? portraitW + MARGIN : 0);
        int textRight = stageW - MARGIN - (portraitW > 0 && heroRight ? portraitW + MARGIN : 0);
        int textW = Math.max(56, textRight - textLeft);
        float y = MARGIN;

        if (speaker != null && !speaker.isEmpty()) {
            RenderedTextBlock title = PixelScene.renderTextBlock(speaker, 7);
            title.hardlight(TITLE_COLOR);
            title.maxWidth(textW);
            title.setPos(textLeft, y);
            add(title);
            y = title.bottom() + 2;
        }

        body = PixelScene.renderTextBlock(fullMessage, 6);
        body.maxWidth(textW);
        body.setPos(textLeft, y);
        add(body);
        y = body.bottom() + 3;

        if (hasChoices) {
            for (int i = 0; i < choices.length; i++) {
                final int index = i;
                optionLabels.add(choices[i]);
                DialogueChoiceButton option = new DialogueChoiceButton() {
                    @Override protected void onClick() {
                        if (!revealComplete) revealAll();
                        else choose(index);
                    }
                };
                option.setRect(textLeft, y, textW, OPTION_H);
                option.enable(false);
                optionButtons.add(option);
                add(option);
                y += OPTION_H + 1;
            }
            refreshOptionLabels();
        } else {
            DialogueChoiceButton advance = new DialogueChoiceButton() {
                @Override protected void onClick() { advance(); }
            };
            advance.text("v");
            advance.setRect(textRight - 17, y, 17, 10);
            add(advance);
            y += 11;
        }

        int stageH = Math.max(landscape ? 52 : 66, (int)y + MARGIN);
        if (portraitW > 0) addPortrait(portrait, heroRight, stageW, stageH, portraitW);
        resize(stageW, stageH);

        int offsetY = Math.max(0, (PixelScene.uiCamera.height - stageH) / 2 - 3);
        offset(0, offsetY);

        if (fullMessage.isEmpty()) revealAll();
        else body.text("");
    }

    @Override public void update() {
        super.update();
        if (revealComplete) return;
        revealProgress += Game.elapsed * charsPerSecond;
        int target = Math.min(fullMessage.length(), (int)revealProgress);
        if (target > revealedChars) {
            revealedChars = target;
            body.text(fullMessage.substring(0, revealedChars));
            if (revealedChars >= fullMessage.length()) revealAll();
        }
    }

    private static boolean isHero(Portrait portrait) {
        return portrait == Portrait.HERO || portrait == Portrait.HERO_NEUTRAL
                || portrait == Portrait.HERO_ALERT || portrait == Portrait.HERO_CONCERNED;
    }

    private static EchoesDialoguePortraits.HeroExpression heroExpression(Portrait portrait) {
        if (portrait == Portrait.HERO_ALERT) return EchoesDialoguePortraits.HeroExpression.ALERT;
        if (portrait == Portrait.HERO_CONCERNED) return EchoesDialoguePortraits.HeroExpression.CONCERNED;
        return EchoesDialoguePortraits.HeroExpression.NEUTRAL;
    }

    private void addPortrait(Portrait type, boolean right, int stageW, int stageH, int portraitW) {
        Image image = isHero(type)
                ? EchoesDialoguePortraits.hero(Dungeon.hero == null ? null : Dungeon.hero.heroClass,
                        heroExpression(type))
                : EchoesDialoguePortraits.image(type);
        float target = Math.min(portraitW, stageH - 8);
        float scale = target / Math.max(image.width, image.height);
        image.scale.set(scale);
        float visualW = image.width * scale;
        float visualH = image.height * scale;
        image.x = right ? stageW - MARGIN - visualW : MARGIN;
        image.y = stageH - MARGIN - visualH;
        add(image);
    }

    private void revealAll() {
        if (revealComplete) return;
        revealComplete = true;
        revealedChars = fullMessage.length();
        revealProgress = revealedChars;
        body.text(fullMessage);
        for (DialogueChoiceButton option : optionButtons) option.enable(true);
        refreshOptionLabels();
    }

    private void advance() {
        if (!revealComplete) { revealAll(); return; }
        hide();
        if (onAdvance != null) onAdvance.call();
    }

    private void choose(int index) {
        if (!revealComplete) { revealAll(); return; }
        hide();
        if (onChoice != null) onChoice.onSelect(index);
    }

    private void moveSelection(int delta) {
        if (!hasChoices || optionButtons.isEmpty()) return;
        selectedOption = (selectedOption + delta + optionButtons.size()) % optionButtons.size();
        refreshOptionLabels();
    }

    private void refreshOptionLabels() {
        for (int i = 0; i < optionButtons.size(); i++) {
            DialogueChoiceButton button = optionButtons.get(i);
            boolean selected = i == selectedOption;
            button.setSelected(selected);
            // ASCII-only marker: avoids the replacement-glyph problem seen with the old U+203A glyph.
            button.text((selected ? "> " : "  ") + optionLabels.get(i));
        }
    }

    @Override public boolean onSignal(KeyEvent event) {
        if (event.pressed) {
            GameAction action = KeyBindings.getActionForKey(event);
            if (hasChoices) {
                if (!revealComplete && (action == SPDAction.WAIT_OR_PICKUP
                        || action == SPDAction.TAG_ATTACK || action == SPDAction.TAG_LOOT)) {
                    revealAll(); return true;
                }
                if (revealComplete) {
                    if (action == SPDAction.N || action == SPDAction.W) { moveSelection(-1); return true; }
                    if (action == SPDAction.S || action == SPDAction.E) { moveSelection(1); return true; }
                    if (action == SPDAction.WAIT_OR_PICKUP || action == SPDAction.TAG_ATTACK || action == SPDAction.TAG_LOOT) {
                        choose(selectedOption); return true;
                    }
                }
                if (action == SPDAction.BACK) return true;
            } else if (action == SPDAction.WAIT_OR_PICKUP || action == SPDAction.TAG_ATTACK
                    || action == SPDAction.TAG_LOOT || action == SPDAction.WAIT) {
                advance(); return true;
            }
        }
        return super.onSignal(event);
    }

    @Override public void onBackPressed() {
        if (!hasChoices) advance();
        else if (!revealComplete) revealAll();
    }

    private static class DialogueChoiceButton extends StyledButton {
        private boolean selected;
        private boolean hovered;

        DialogueChoiceButton() {
            super(Chrome.Type.GREY_BUTTON_TR, "", 6);
            multiline = true;
            leftJustify = true;
            applyState();
        }

        void setSelected(boolean selected) {
            this.selected = selected;
            applyState();
        }

        @Override protected void onPointerHoverStart() {
            hovered = true;
            applyState();
        }

        @Override protected void onPointerHoverEnd() {
            hovered = false;
            applyState();
        }

        @Override protected void onPointerDown() {
            super.onPointerDown();
            bg.hardlight(0x8A4E42);
            text.hardlight(CHOICE_PRESS);
        }

        @Override protected void onPointerUp() {
            super.onPointerUp();
            applyState();
        }

        @Override public void enable(boolean value) {
            super.enable(value);
            applyState();
        }

        private void applyState() {
            if (bg == null || text == null) return;
            bg.resetColor();
            text.resetColor();
            if (!active) {
                bg.alpha(0.35f);
                text.alpha(0.35f);
                return;
            }
            bg.alpha(selected || hovered ? 0.90f : 0.62f);
            if (selected || hovered) {
                bg.hardlight(0x6F5544);
                text.hardlight(CHOICE_ACTIVE);
            } else {
                text.hardlight(CHOICE_TEXT);
            }
        }
    }
}
