package com.ygames.ysoccer.framework;

public class KeyboardConfig extends InputDeviceConfig {

    public int keyLeft;
    public int keyRight;
    public int keyUp;
    public int keyDown;

    public int button1;
    public int button2;

    public KeyboardConfig() {
        super(InputDevice.Type.KEYBOARD);
    }

    KeyboardConfig(int keyLeft, int keyRight, int keyUp, int keyDown, int button1, int button2) {
        super(InputDevice.Type.KEYBOARD);
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyUp = keyUp;
        this.keyDown = keyDown;
        this.button1 = button1;
        this.button2 = button2;
    }
}
