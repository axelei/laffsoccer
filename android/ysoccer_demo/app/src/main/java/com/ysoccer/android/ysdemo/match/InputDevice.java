package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;

abstract public class InputDevice {
    protected final int ID_COMPUTER = 0;
    protected final int ID_KEYBOARD = 1;
    protected final int ID_JOYSTICK = 2;
    protected final int ID_TOUCH = 3;

    private int type; // ID_COMPUTER, ID_KEYBOARD, ID_JOYSTICK
    private int port;

    // new values
    int x0, y0;
    boolean fire10, fire20;
    boolean value;
    int angle;
    boolean isAvailable;

    // valid values
    public int x1;
    public int y1;
    boolean fire11;
    boolean fire21;

    // old values
    private int x2, y2;

    public void readInput() {

        // update input buffer
        x2 = x1;
        x1 = x0;
        y2 = y1;
        y1 = y0;
        fire11 = fire10;
        fire21 = fire20;

        // read new values
        _read();

        // clean spikes: safe values are x1, y1
        if ((x1 != x0) && (x1 != x2)) {
            x1 = x2;
        }
        if ((y1 != y0) && (y1 != y2)) {
            y1 = y2;
        }

        value = (x1 != 0) || (y1 != 0);
        angle = Math.round(Emath.aTan2(y1, x1));

    }

    abstract void _read();

    public boolean fire1Down() {
        return (fire10) && (!fire11);
    }

    public boolean fire1Up() {
        return (!fire10) && (fire11);
    }

    public boolean fire2Down() {
        return (fire20) && (!fire21);
    }

    public boolean fire2Up() {
        return (!fire20) && (fire21);
    }

    protected void setType(int type) {
        this.type = type;
    }

    public String toString() {
        String s = "";
        switch (type) {
            case ID_COMPUTER:
                s = "Computer";
                break;
            case ID_KEYBOARD:
                s = "Keyboard " + port;
                break;
            case ID_JOYSTICK:
                s = "Joystick " + port;
                break;
        }
        s += "\t is available: " + isAvailable;
        return s;
    }

}
