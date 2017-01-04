package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.Input.TouchEvent;
import com.ysoccer.android.framework.gl.Camera2D;
import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.framework.math.Vector2;

import java.util.List;

public class TouchInput extends InputDevice {

    Camera2D guiCam;
    Vector2 touchPoint;
    Vector2 oldTouchPoint;
    private boolean inverted;
    public List<TouchEvent> touchEvents;

    private boolean f1;
    private boolean f2;
    private boolean v;
    private int a;

    public TouchInput(GLGraphics glGraphics) {
        setType(ID_TOUCH);
        guiCam = new Camera2D(glGraphics, 1280, 800);
        touchPoint = new Vector2();
        oldTouchPoint = new Vector2();
        this.inverted = false;
    }

    public void _read() {
        int len = touchEvents.size();
        if (len > 0) {
            f1 = false;
            f2 = false;
            v = false;
        }
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);

            touchPoint.set(event.x, event.y);

            guiCam.touchToWorld(touchPoint);

            if (inverted ? (touchPoint.x > 640) : (touchPoint.x < 640)) {
                if (touchPoint.y > 80) {
                    f1 = (event.type != TouchEvent.TOUCH_UP);
                } else {
                    f2 = (event.type != TouchEvent.TOUCH_UP);
                }
            } else {
                v = false;
                if (event.type == TouchEvent.TOUCH_DRAGGED) {
                    v = true;
                    oldTouchPoint.sub(touchPoint);
                    if (oldTouchPoint.v > 2) {
                        a = Math.round((180.0f + oldTouchPoint.a) / 45.0f) * 45;
                    }
                }
                oldTouchPoint.set(touchPoint);
            }
        }
        x0 = v ? Math.round(Emath.cos((a / 45.0f) * 45)) : 0;
        y0 = v ? Math.round(Emath.sin((a / 45.0f) * 45)) : 0;
        fire10 = f1;
        fire20 = f2;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
}
