package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Input;

public abstract class InputDevice {

    public enum Type {COMPUTER, KEYBOARD, JOYSTICK}

    public final Type type;
    public final int port;
    public boolean available;

    // new values
    public int x0, y0;
    public boolean fire10, fire20;

    // valid values
    public int x1, y1;
    public boolean fire11, fire21;
    public boolean value;
    public int angle;

    InputDevice(Type type, int port) {
        this.type = type;
        this.port = port;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void update() {

        // update input buffer
        int x2 = x1;
        x1 = x0;
        int y2 = y1;
        y1 = y0;
        fire11 = fire10;
        fire21 = fire20;

        // read new values
        read();

        // clean spikes: safe values are x1, y1
        if ((x1 != x0) && (x1 != x2)) {
            x1 = x2;
        }
        if ((y1 != y0) && (y1 != y2)) {
            y1 = y2;
        }

        value = (x1 != 0) || (y1 != 0);
        angle = Math.round(EMath.aTan2(y1, x1));
    }

    abstract void read();

    public boolean fire1Down() {
        return fire10 && !fire11;
    }

    public boolean fire1Up() {
        return !fire10 && fire11;
    }

    public boolean fire2Down() {
        return fire20 && !fire21;
    }

    public boolean xReleased() {
        return (x1 == 0) && (x0 != 0);
    }

    public boolean yMoved() {
        return (y1 != 0) && (y0 == 0);
    }

    public static String keyDescription(int keyCode) {
        return Input.Keys.toString(keyCode).toUpperCase();
    }
}
