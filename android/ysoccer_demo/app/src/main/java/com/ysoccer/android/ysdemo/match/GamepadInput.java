package com.ysoccer.android.ysdemo.match;

import android.annotation.TargetApi;
import android.view.KeyEvent;

import com.ysoccer.android.framework.Input;

@TargetApi(12)
public class GamepadInput extends InputDevice {

    Input input;
    boolean isLeft, isRight, isUp, isDown;

    public GamepadInput(Input input) {
        setType(ID_JOYSTICK);
        this.input = input;
    }

    public void _read() {
        isLeft = input.isButtonPressed(KeyEvent.KEYCODE_DPAD_LEFT);
        isRight = input.isButtonPressed(KeyEvent.KEYCODE_DPAD_RIGHT);
        x0 = isLeft ? -1 : isRight ? 1 : 0;

        isUp = input.isButtonPressed(KeyEvent.KEYCODE_DPAD_UP);
        isDown = input.isButtonPressed(KeyEvent.KEYCODE_DPAD_DOWN);
        y0 = isUp ? -1 : isDown ? 1 : 0;

        fire10 = input.isButtonPressed(KeyEvent.KEYCODE_BUTTON_1);
        fire20 = input.isButtonPressed(KeyEvent.KEYCODE_BUTTON_B);
    }

}
