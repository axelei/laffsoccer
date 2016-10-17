package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

class Mouse {

    Vector3 position = new Vector3();
    boolean button1;
    boolean button2;

    public void read(Camera camera) {
        position.x = Gdx.input.getX();
        position.y = Gdx.input.getY();
        position.z = 0;
        camera.unproject(position);

        button1 = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        button2 = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
    }
}
