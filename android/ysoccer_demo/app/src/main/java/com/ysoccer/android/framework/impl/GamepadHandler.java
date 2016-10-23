package com.ysoccer.android.framework.impl;

import android.annotation.TargetApi;
import android.view.InputDevice;
import android.view.View;
import android.view.View.OnKeyListener;

import com.ysoccer.android.framework.Input.KeyEvent;
import com.ysoccer.android.framework.Pool;
import com.ysoccer.android.framework.Pool.PoolObjectFactory;

import java.util.ArrayList;
import java.util.List;

@TargetApi(12)
public class GamepadHandler implements OnKeyListener {
    boolean[] pressedKeys = new boolean[256];
    Pool<KeyEvent> keyEventPool;
    List<KeyEvent> keyEventsBuffer = new ArrayList<KeyEvent>();
    List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();

    public GamepadHandler(View view) {
        PoolObjectFactory<KeyEvent> factory = new PoolObjectFactory<KeyEvent>() {
            public KeyEvent createObject() {
                return new KeyEvent();
            }
        };
        keyEventPool = new Pool<KeyEvent>(factory, 100);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    public boolean onKey(View v, int keyCode, android.view.KeyEvent event) {
        if (event.getAction() == android.view.KeyEvent.ACTION_MULTIPLE)
            return false;

        synchronized (this) {
            KeyEvent keyEvent = keyEventPool.newObject();
            keyEvent.keyCode = keyCode;
            keyEvent.keyChar = (char) event.getUnicodeChar();
            boolean isJoystick = (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK;
            boolean isGamepad = (event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD;
            if (isJoystick || isGamepad) {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                }
                if (event.getAction() == android.view.KeyEvent.ACTION_UP) {
                    keyEvent.type = KeyEvent.KEY_DOWN;
                    if (keyCode > 0 && keyCode < 255) {
                        pressedKeys[keyCode] = true;
                    }
                    keyEvent.type = KeyEvent.KEY_UP;
                    if (keyCode > 0 && keyCode < 255) {
                        pressedKeys[keyCode] = false;
                    }
                }
                keyEventsBuffer.add(keyEvent);
            }
        }
        return false;
    }

    public boolean isKeyPressed(int keyCode) {
        if (keyCode < 0 || keyCode > 255)
            return false;
        return pressedKeys[keyCode];
    }

    public List<KeyEvent> getKeyEvents() {
        synchronized (this) {
            int len = keyEvents.size();
            for (int i = 0; i < len; i++) {
                keyEventPool.free(keyEvents.get(i));
            }
            keyEvents.clear();
            keyEvents.addAll(keyEventsBuffer);
            keyEventsBuffer.clear();
            return keyEvents;
        }
    }
}
