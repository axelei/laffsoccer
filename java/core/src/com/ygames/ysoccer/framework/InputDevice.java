package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.math.Emath;

public abstract class InputDevice {

    protected final int ID_COMPUTER = 0;
    protected final int ID_KEYBOARD = 1;
    protected final int ID_JOYSTICK = 2;

    private int type; // ID_COMPUTER, ID_KEYBOARD, ID_JOYSTICK

    // new values
    protected int x0, y0;
    protected boolean fire10, fire20;

    // valid values
    public int x1, y1;
    public boolean fire11, fire21;
    public boolean value;
    public int angle;

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
        angle = Math.round(Emath.aTan2(y1, x1));
    }

    abstract void read();

    protected void setType(int type) {
        this.type = type;
    }
}
