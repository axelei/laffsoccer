package com.ygames.ysoccer.framework;

public abstract class InputDeviceConfig {

    public InputDevice.Type type;

    InputDeviceConfig(InputDevice.Type type) {
        this.type = type;
    }
}
