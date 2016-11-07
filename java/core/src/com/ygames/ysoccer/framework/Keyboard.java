package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;

class Keyboard extends InputDevice {

    private KeyboardConfig config;

    Keyboard(int port, KeyboardConfig config) {
        super(Type.KEYBOARD, port);
        this.config = config;
    }

    protected void read() {
        x0 = (Gdx.input.isKeyPressed(config.keyLeft) ? -1 : 0) + (Gdx.input.isKeyPressed(config.keyRight) ? 1 : 0);
        y0 = (Gdx.input.isKeyPressed(config.keyUp) ? -1 : 0) + (Gdx.input.isKeyPressed(config.keyDown) ? 1 : 0);
        fire10 = Gdx.input.isKeyPressed(config.button1);
        fire20 = Gdx.input.isKeyPressed(config.button2);
    }
}
