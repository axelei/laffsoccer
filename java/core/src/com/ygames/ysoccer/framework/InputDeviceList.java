package com.ygames.ysoccer.framework;

import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;

public class InputDeviceList extends ArrayList<InputDevice> {

    public void setAvailability(boolean n) {
        for (InputDevice inputDevice : this) {
            inputDevice.available = n;
        }
    }

    public int getAvailabilityCount() {
        int n = 0;
        for (InputDevice inputDevice : this) {
            if (inputDevice.available) n += 1;
        }
        return n;
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

    public InputDevice assignNextAvailable(InputDevice current) {
        int start = indexOf(current);
        if (start == -1) {
            throw new GdxRuntimeException("item not found");
        }

        int len = size();
        for (int i = start + 1; i < len; i++) {
            InputDevice next = get(i);
            if (next.available) {
                current.setAvailable(true);
                next.setAvailable(false);
                return next;
            }
        }
        return null;
    }

    public InputDevice rotateAvailable(InputDevice inputDevice, int n) {
        int index = this.indexOf(inputDevice);
        if (index == -1) {
            throw new GdxRuntimeException("item not found");
        }
        inputDevice.available = true;
        do {
            index = EMath.rotate(index, 0, this.size() - 1, n);
            inputDevice = this.get(index);
        } while (!inputDevice.available);

        inputDevice.available = false;
        return inputDevice;
    }
}
