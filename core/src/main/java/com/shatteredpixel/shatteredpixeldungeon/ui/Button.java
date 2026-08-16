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
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;

public class Button extends Component {

    public static float longClick = 0.5f;
    protected PointerArea hotArea;
    protected Tooltip hoverTip;
    protected static Button pressedButton;
    protected float pressTime;
    protected boolean clickReady;

    @Override
    protected void createChildren() {
        hotArea = new PointerArea(0, 0, 0, 0) {
            @Override protected void onPointerDown(PointerEvent event) {
                pressedButton = Button.this;
                pressTime = 0;
                clickReady = true;
                Button.this.onPointerDown();
            }
            @Override protected void onPointerUp(PointerEvent event) {
                if (pressedButton == Button.this) pressedButton = null;
                else clickReady = false;
                Button.this.onPointerUp();
            }
            @Override protected void onClick(PointerEvent event) {
                if (!clickReady) return;
                killTooltip();
                switch (event.button) {
                    case PointerEvent.RIGHT: Button.this.onRightClick(); break;
                    case PointerEvent.MIDDLE: Button.this.onMiddleClick(); break;
                    case PointerEvent.LEFT:
                    default: Button.this.onClick(); break;
                }
            }
            @Override protected void onHoverStart(PointerEvent event) {
                Button.this.onPointerHoverStart();
                String text = hoverText();
                if (text != null) {
                    int key = 0;
                    if (keyAction() != null) key = KeyBindings.getFirstKeyForAction(keyAction(), ControllerHandler.controllerActive);
                    if (key == 0 && secondaryTooltipAction() != null) key = KeyBindings.getFirstKeyForAction(secondaryTooltipAction(), ControllerHandler.controllerActive);
                    if (key != 0) text += " _(" + KeyBindings.getKeyName(key) + ")_";
                    hoverTip = new Tooltip(Button.this, text, 80);
                    Button.this.parent.addToFront(hoverTip);
                    hoverTip.camera = camera();
                    alignTooltip(hoverTip);
                }
            }
            @Override protected void onHoverEnd(PointerEvent event) {
                Button.this.onPointerHoverEnd();
                killTooltip();
            }
        };
        add(hotArea);

        KeyEvent.addKeyListener(keyListener = new Signal.Listener<KeyEvent>() {
            @Override public boolean onSignal(KeyEvent event) {
                if (active && KeyBindings.getActionForKey(event) == keyAction()) {
                    if (event.pressed) {
                        pressedButton = Button.this;
                        pressTime = 0;
                        clickReady = true;
                        Button.this.onPointerDown();
                    } else {
                        Button.this.onPointerUp();
                        if (pressedButton == Button.this) {
                            pressedButton = null;
                            if (clickReady) onClick();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private Signal.Listener<KeyEvent> keyListener;

    public GameAction keyAction(){ return null; }
    public GameAction secondaryTooltipAction(){ return null; }

    @Override
    public void update() {
        super.update();
        hotArea.active = visible;
        if (pressedButton == this && (pressTime += Game.elapsed) >= longClick) {
            pressedButton = null;
            if (onLongClick()) {
                hotArea.reset();
                clickReady = false;
                onPointerUp();
                if (SPDSettings.vibration()) Game.vibrate(50);
            }
        }
    }

    protected void onPointerDown() {}
    protected void onPointerUp() {}
    protected void onPointerHoverStart() {}
    protected void onPointerHoverEnd() {}
    protected void onClick() {}
    protected void onRightClick() {}
    protected void onMiddleClick() {}
    protected boolean onLongClick() { return false; }
    protected String hoverText() { return null; }

    private void alignTooltip(Tooltip tip){
        tip.setPos(x, y-tip.height()-1);
        Camera cam = camera();
        if (tip.right() > (cam.width+cam.scroll.x)) tip.setPos(tip.left() - (tip.right() - (cam.width+cam.scroll.x)), tip.top());
        if (tip.top() < 0) tip.setPos(tip.left(), bottom()+1);
    }

    public void killTooltip(){
        if (hoverTip != null){
            hoverTip.killAndErase();
            hoverTip = null;
        }
    }

    @Override protected void layout() {
        hotArea.x = x;
        hotArea.y = y;
        hotArea.width = width;
        hotArea.height = height;
    }

    @Override public synchronized void destroy () {
        super.destroy();
        KeyEvent.removeKeyListener(keyListener);
        killTooltip();
    }

    public void givePointerPriority(){ hotArea.givePointerPriority(); }
}
