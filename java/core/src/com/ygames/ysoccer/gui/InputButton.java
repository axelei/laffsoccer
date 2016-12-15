package com.ygames.ysoccer.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

import java.awt.event.KeyEvent;

public class InputButton extends Button {

    private String entryString;
    private int entryLimit;
    private String inputFilter;
    private InputProcessor inputProcessor;

    protected InputButton() {
        inputProcessor = new TextInputProcessor();
    }

    private void setEntryMode(boolean newValue, boolean applyChanges) {
        if (!entryMode && newValue) {
            Gdx.input.setInputProcessor(inputProcessor);
            entryString = text;
        }
        if (entryMode && !newValue) {
            if (applyChanges) {
                if (!text.equals(entryString)) {
                    text = entryString;
                    onChanged();
                }
            }
            Gdx.input.setInputProcessor(null);
        }
        entryMode = newValue;
    }

    protected void setEntryLimit(int entryLimit) {
        this.entryLimit = entryLimit;
    }

    protected void setInputFilter(String inputFilter) {
        this.inputFilter = inputFilter;
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
    public void onFire1Down() {
        setEntryMode(true, false);
    }

    @Override
    public void onDeselect() {
        setEntryMode(false, true);
    }

    public void onChanged() {
    }

    private class TextInputProcessor extends InputAdapter {

        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.ENTER:
                    setEntryMode(false, true);
                    break;

                case Input.Keys.ESCAPE:
                    setEntryMode(false, false);
                    break;
            }
            return true;
        }

        public boolean keyTyped(char character) {
            if (isPrintableChar(character) && entryString.length() < entryLimit) {
                entryString += charToString(character);
            } else {
                switch (character) {
                    case 8: // BACKSPACE
                        entryString = entryString.substring(0, Math.max(entryString.length() - 1, 0));
                        break;
                }
            }
            return true;
        }
    }

    private String charToString(char character) {
        String s = ("" + character).toUpperCase();
        if (inputFilter != null && !s.matches(inputFilter)) {
            return "";
        }
        return s;
    }

    private boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }
}
