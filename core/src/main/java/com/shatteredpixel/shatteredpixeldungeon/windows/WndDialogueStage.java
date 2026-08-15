/*
 * Echoes of Yendor modifications Copyright (C) 2026
 * Licensed under GPL-3.0-or-later with the rest of the project.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;

import java.util.ArrayList;

/**
 * Bottom-anchored story dialogue stage. It keeps the current map visible,
 * gives important speakers a dedicated portrait column, and embeds choices
 * in the same scene instead of opening a second WndOptions popup.
 */
public class WndDialogueStage extends Window {

    public enum Portrait {
        NONE,
        HERO,
        FARMER_NEUTRAL,
        FARMER_WARM,
        FARMER_CONFUSED,
        FARMER_FIXATED,
        FARMER_SHAKEN
    }

    public interface ChoiceListener {
        void onSelect(int index);
    }

    private static final int MARGIN = 4;
    private static final int PORTRAIT_W_L = 48;
    private static final int PORTRAIT_W_P = 38;
    private static final int OPTION_H = 13;

    private final Callback onAdvance;
    private final ChoiceListener onChoice;
    private final boolean hasChoices;

    private final String fullMessage;
    private final RenderedTextBlock body;
    private final float charsPerSecond;
    private float revealProgress;
    private int revealedChars;
    private boolean revealComplete;

    private final ArrayList<RedButton> optionButtons = new ArrayList<>();
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

        shadow.am = 0.18f;

        boolean landscape = PixelScene.landscape();
        int screenW = PixelScene.uiCamera.width;
        int stageW = Math.min(landscape ? 236 : 129, screenW - 6);
        int portraitW = portrait == Portrait.NONE ? 0 : (landscape ? PORTRAIT_W_L : PORTRAIT_W_P);
        boolean heroRight = portrait == Portrait.HERO;

        int textLeft = MARGIN + (portraitW > 0 && !heroRight ? portraitW + MARGIN : 0);
        int textRight = stageW - MARGIN - (portraitW > 0 && heroRight ? portraitW + MARGIN : 0);
        int textW = Math.max(58, textRight - textLeft);

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
                RedButton option = new RedButton("", 6) {
                    @Override
                    protected void onClick() {
                        if (!revealComplete) {
                            revealAll();
                        } else {
                            choose(index);
                        }
                    }
                };
                option.multiline = true;
                option.setRect(textLeft, y, textW, OPTION_H);
                option.enable(false);
                optionButtons.add(option);
                add(option);
                y += OPTION_H + 1;
            }
            refreshOptionLabels();
        } else {
            RedButton advance = new RedButton("▼", 6) {
                @Override
                protected void onClick() {
                    advance();
                }
            };
            advance.setRect(textRight - 18, y, 18, 12);
            add(advance);
            y += 13;
        }

        int stageH = Math.max(landscape ? 58 : 72, (int)y + MARGIN);
        if (portraitW > 0) addPortrait(portrait, heroRight, stageW, stageH, portraitW);

        resize(stageW, stageH);

        // Window is centered by default; shift this one down into a JRPG-style stage.
        int offsetY = Math.max(0, (PixelScene.uiCamera.height - stageH) / 2 - 3);
        offset(0, offsetY);

        if (fullMessage.isEmpty()) {
            revealAll();
        } else {
            // Layout was measured using the complete message, so revealing text never makes
            // the stage or its buttons jump around.
            body.text("");
        }
    }

    @Override
    public void update() {
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

    private void addPortrait(Portrait type, boolean right, int stageW, int stageH, int portraitW) {
        Image image;
        if (type == Portrait.HERO) {
            image = new Image(heroSplash());
            int crop = Math.min(image.texture.width, Math.round(image.texture.height * 0.80f));
            int left = Math.max(0, (image.texture.width - crop) / 2);
            int top = Math.max(0, Math.min(image.texture.height - crop,
                    Math.round(image.texture.height * 0.03f)));
            image.frame(left, top, crop, crop);
        } else {
            image = EchoesFarmerPortrait.image(type);
        }

        float target = Math.min(portraitW, stageH - 8);
        float scale = target / Math.max(image.width, image.height);
        image.scale.set(scale);

        float visualW = image.width * scale;
        float visualH = image.height * scale;
        image.x = right ? stageW - MARGIN - visualW : MARGIN;
        image.y = stageH - MARGIN - visualH;
        add(image);
    }

    private String heroSplash() {
        if (Dungeon.hero == null || Dungeon.hero.heroClass == null) return Assets.Splashes.WARRIOR;
        HeroClass heroClass = Dungeon.hero.heroClass;
        switch (heroClass) {
            case MAGE: return Assets.Splashes.MAGE;
            case ROGUE: return Assets.Splashes.ROGUE;
            case HUNTRESS: return Assets.Splashes.HUNTRESS;
            case DUELIST: return Assets.Splashes.DUELIST;
            case CLERIC: return Assets.Splashes.CLERIC;
            case WARRIOR:
            default: return Assets.Splashes.WARRIOR;
        }
    }

    private void revealAll() {
        if (revealComplete) return;
        revealComplete = true;
        revealedChars = fullMessage.length();
        revealProgress = revealedChars;
        body.text(fullMessage);
        for (RedButton option : optionButtons) option.enable(true);
        refreshOptionLabels();
    }

    private void advance() {
        if (!revealComplete) {
            revealAll();
            return;
        }
        hide();
        if (onAdvance != null) onAdvance.call();
    }

    private void choose(int index) {
        if (!revealComplete) {
            revealAll();
            return;
        }
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
            optionButtons.get(i).text((i == selectedOption ? "› " : "  ") + optionLabels.get(i));
        }
    }

    @Override
    public boolean onSignal(KeyEvent event) {
        if (event.pressed) {
            GameAction action = KeyBindings.getActionForKey(event);

            if (hasChoices) {
                if (!revealComplete && (action == SPDAction.WAIT_OR_PICKUP
                        || action == SPDAction.TAG_ATTACK || action == SPDAction.TAG_LOOT)) {
                    revealAll();
                    return true;
                }
                if (revealComplete) {
                    if (action == SPDAction.N || action == SPDAction.W) {
                        moveSelection(-1);
                        return true;
                    }
                    if (action == SPDAction.S || action == SPDAction.E) {
                        moveSelection(1);
                        return true;
                    }
                    if (action == SPDAction.WAIT_OR_PICKUP
                            || action == SPDAction.TAG_ATTACK || action == SPDAction.TAG_LOOT) {
                        choose(selectedOption);
                        return true;
                    }
                }
                if (action == SPDAction.BACK) return true;
            } else if (action == SPDAction.WAIT_OR_PICKUP
                    || action == SPDAction.TAG_ATTACK || action == SPDAction.TAG_LOOT
                    || action == SPDAction.WAIT) {
                advance();
                return true;
            }
        }
        return super.onSignal(event);
    }

    @Override
    public void onBackPressed() {
        if (!hasChoices) {
            advance();
        } else if (!revealComplete) {
            revealAll();
        }
    }
}
