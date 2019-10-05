package com.ygames.ysoccer.framework;

public class JoystickConfig extends InputDeviceConfig {

    public String name;
    public int xAxis = -1;
    public int yAxis = -1;
    public int button1 = -1;
    public int button2 = -1;

    public JoystickConfig() {
        super(InputDevice.Type.JOYSTICK);
    }

    public JoystickConfig(String name) {
        super(InputDevice.Type.JOYSTICK);
        this.name = name;
    }

    public boolean isConfigured() {
        return xAxis != -1 && yAxis != -1 && button1 != -1 && button2 != -1;
    }

    public void reset() {
        xAxis = -1;
        yAxis = -1;
        button1 = -1;
        button2 = -1;
    }
}
