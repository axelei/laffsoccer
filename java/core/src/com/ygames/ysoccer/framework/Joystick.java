package com.ygames.ysoccer.framework;

import com.badlogic.gdx.controllers.Controller;

import static java.lang.Math.round;

class Joystick extends InputDevice {

    private Controller controller;

    Joystick(Controller controller, int port) {
        super(Type.JOYSTICK, port);
        this.controller = controller;
    }

    protected void read() {
        x0 = round(this.controller.getAxis(0));
        y0 = round(this.controller.getAxis(1));
        fire10 = this.controller.getButton(0);
        fire20 = this.controller.getButton(1);
    }
}
