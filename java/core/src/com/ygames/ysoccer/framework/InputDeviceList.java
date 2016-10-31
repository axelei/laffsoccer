package com.ygames.ysoccer.framework;

import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;

public class InputDeviceList extends ArrayList<InputDevice> {

    public void setAvailability(boolean n) {
        for (InputDevice inputDevice : this) {
            inputDevice.available = n;
        }
    }

    public InputDevice assignFirstAvailable() {
        for (InputDevice inputDevice : this) {
            if (inputDevice.available) {
                inputDevice.available = false;
                return inputDevice;
            }
        }
        return null;
    }

    public InputDevice rotateAvailable(InputDevice inputDevice, int n) {
        int index = this.indexOf(inputDevice);
        inputDevice.available = true;
        do {
            index = Emath.rotate(index, 0, this.size() - 1, n);
            inputDevice = this.get(index);
        } while (!inputDevice.available);

        inputDevice.available = false;
        return inputDevice;
    }
}
