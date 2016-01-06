package com.ygames.ysoccer.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.ArrayList;
import java.util.List;

public class InputButton extends Button {

    private String entryString;
    private int entryLimit;
    private int backspaceWait;
    private List<Boolean> keyStatus;

    public InputButton() {
        backspaceWait = 40;
        keyStatus = new ArrayList<Boolean>();
        for (int i = 0; i < 256; i++) {
            keyStatus.add(i, false);
        }
    }

    private void setEntryMode(boolean newValue) {
        if (!entryMode && newValue) {
            for (int i = 0; i < 256; i++) {
                keyStatus.set(i, Gdx.input.isKeyPressed(i));
            }
            entryString = text;
        }
        if (entryMode && !newValue) {
            if (!text.equals(entryString)) {
                changed = true;
            }
            text = entryString;
        }
        entryMode = newValue;
    }

    public void setEntryLimit(int entryLimit) {
        this.entryLimit = entryLimit;
    }

    @Override
    public String getText() {
        if (entryMode) {
            return entryString + "_";
        } else {
            return text;
        }
    }

    @Override
    public void update() {
        if (!entryMode) {
            return;
        }

        if (backspaceWait > 0) {
            backspaceWait -= 1;
        }

        for (int i = 0; i < 256; i++) {
            boolean isPressed = Gdx.input.isKeyPressed(i);
            if (isPressed && !keyStatus.get(i)) {
                switch (i) {
                    case Input.Keys.ESCAPE:
                        // disable entry mode without applying changes
                        entryMode = false;
                        break;

                    case Input.Keys.BACKSPACE:
                        if (backspaceWait == 0) {
                            entryString = entryString.substring(0, Math.max(entryString.length() - 1, 0));
                            backspaceWait = 4;
                        }
                        break;

                    case Input.Keys.ENTER:
                        setEntryMode(false);
                        break;

                    default:
                        // get ascii chars
                        String s = Input.Keys.toString(i);
                        if ((s.length() == 1) && (entryString.length() < entryLimit)) {
                            entryString += s.toUpperCase();
                        }
                }
            }
            keyStatus.set(i, isPressed);
        }
    }

    @Override
    public void onFire1Down() {
        setEntryMode(true);
    }
}
