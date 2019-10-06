package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

class Mouse {

    final Vector3 position = new Vector3();
    boolean buttonLeft;
    boolean buttonRight;
    boolean enabled;

    Mouse() {
        enable();
    }

    void enable() {
        enabled = true;
        Gdx.graphics.setCursor(Assets.customCursor);
    }

    void disable() {
        enabled = false;
        Gdx.graphics.setCursor(Assets.hiddenCursor);
    }

    public void read(Camera camera) {
        position.x = Gdx.input.getX();
        position.y = Gdx.input.getY();
        position.z = 0;
        camera.unproject(position);

        buttonLeft = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        buttonRight = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
    }

    boolean isActioned() {
        return Gdx.input.isButtonPressed(Input.Buttons.LEFT)
                || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
                || Gdx.input.getDeltaX() != 0
                || Gdx.input.getDeltaY() != 0;
    }
}
