package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;

public class Keyboard extends InputDevice {

    private int keyLeft, keyRight, keyUp, keyDown;
    private int button1, button2;

    protected void read() {
        x0 = (Gdx.input.isKeyPressed(keyLeft) ? -1 : 0) + (Gdx.input.isKeyPressed(keyRight) ? 1 : 0);
        y0 = (Gdx.input.isKeyPressed(keyUp) ? -1 : 0) + (Gdx.input.isKeyPressed(keyDown) ? 1 : 0);
        fire10 = Gdx.input.isKeyPressed(button1);
        fire20 = Gdx.input.isKeyPressed(button2);
    }

    public void setKeys(int keyLeft, int keyRight, int keyUp, int keyDown, int button1, int button2) {
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyUp = keyUp;
        this.keyDown = keyDown;
        this.button1 = button1;
        this.button2 = button2;
    }
}
