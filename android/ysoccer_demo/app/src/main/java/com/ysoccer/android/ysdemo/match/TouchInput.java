package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.Input.TouchEvent;
import com.ysoccer.android.framework.gl.Camera2D;
import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.framework.math.Vector2;

import java.util.List;

public class TouchInput extends InputDevice {

    static final int TOUCHCAM_WIDTH = 1024;
    static final int TOUCHCAM_HEIGHT = 576;

    static final int JOYSTICK_R = 64;
    private static final int JOYSTICK_BASE_X = 30 + JOYSTICK_R;
    private static final int JOYSTICK_BASE_Y = TOUCHCAM_HEIGHT - JOYSTICK_R - 30;

    static Vector2 joystickCurrentPos;
    private static Vector2 joystickVelocity;
    private static Vector2 joystickTargetPos;

    static int BUTTON_R = 64;
    static int BUTTON_X = TOUCHCAM_WIDTH - BUTTON_R - 30;
    static int BUTTON_Y = TOUCHCAM_HEIGHT - BUTTON_R - 30;

    private Camera2D touchCam;
    private Vector2 touchPoint;
    private Vector2 joystickDirection;
    public List<TouchEvent> touchEvents;
    private boolean joystickHold;

    private boolean f1;
    private boolean f2;
    private boolean v;
    private int a;

    public TouchInput(GLGraphics glGraphics) {
        setType(ID_TOUCH);
        touchCam = new Camera2D(glGraphics, TOUCHCAM_WIDTH, TOUCHCAM_HEIGHT);
        touchPoint = new Vector2();
        joystickDirection = new Vector2();
        joystickHold = true;

        joystickCurrentPos = new Vector2(JOYSTICK_BASE_X, JOYSTICK_BASE_Y);
        joystickTargetPos = new Vector2(JOYSTICK_BASE_X, JOYSTICK_BASE_Y);
        joystickVelocity = new Vector2();
    }

    public void _read() {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);

            touchPoint.set(event.x, event.y);

            touchCam.touchToWorld(touchPoint);

            if (touchPoint.x < TOUCHCAM_WIDTH / 2) {
                // Joystick
                float touchJoystickDist;
                switch (event.type) {
                    case TouchEvent.TOUCH_DOWN:
                        touchJoystickDist = touchPoint.dist(joystickCurrentPos);
                        v = false;
                        if (touchJoystickDist < JOYSTICK_R) {
                            joystickHold = true;
                            if (touchJoystickDist > 0.25f * JOYSTICK_R) {
                                joystickDirection.set(joystickCurrentPos);
                                joystickDirection.sub(touchPoint);
                                joystickDirection.rotate(180);
                                a = Math.round(joystickDirection.angle());
                                v = true;
                            }
                        }
                        break;

                    case TouchEvent.TOUCH_DRAGGED:
                        if (!joystickHold) continue;

                        touchJoystickDist = touchPoint.dist(joystickCurrentPos);
                        joystickDirection.set(joystickCurrentPos);
                        joystickDirection.sub(touchPoint);

                        if (touchJoystickDist > 2 * JOYSTICK_R) {
                            joystickDirection.setV(JOYSTICK_R);
                            joystickTargetPos.set(touchPoint).add(joystickDirection);
                            if (joystickTargetPos.x < JOYSTICK_R) {
                                joystickTargetPos.setX(JOYSTICK_R);
                            }
                            if (joystickTargetPos.y > JOYSTICK_BASE_Y) {
                                joystickTargetPos.setY(JOYSTICK_BASE_Y);
                            }
                        }

                        v = false;
                        if (touchJoystickDist > 0.25f * JOYSTICK_R) {
                            joystickDirection.rotate(180);
                            a = Math.round(joystickDirection.angle());
                            v = true;
                        }
                        break;

                    case TouchEvent.TOUCH_UP:
                        joystickHold = false;
                        v = false;
                        break;
                }
            } else {
                float touchButtonDist;
                switch (event.type) {
                    case TouchEvent.TOUCH_DOWN:
                        touchButtonDist = Emath.dist(touchPoint.x, touchPoint.y, BUTTON_X, BUTTON_Y);
                        f1 = touchButtonDist < BUTTON_R;
                        break;

                    case TouchEvent.TOUCH_DRAGGED:
                        touchButtonDist = Emath.dist(touchPoint.x, touchPoint.y, BUTTON_X, BUTTON_Y);
                        f1 = touchButtonDist < 3 * BUTTON_R;
                        break;

                    case TouchEvent.TOUCH_UP:
                        f1 = false;
                        break;
                }
            }
        }
        x0 = v ? Math.round(Emath.cos((a / 45.0f) * 45)) : 0;
        y0 = v ? Math.round(Emath.sin((a / 45.0f) * 45)) : 0;
        fire10 = f1;
        fire20 = f2;

        // move joystick
        if (!joystickHold) {
            joystickTargetPos.set(JOYSTICK_BASE_X, JOYSTICK_BASE_Y);
        }
        joystickVelocity.set(joystickTargetPos);
        joystickVelocity.sub(joystickCurrentPos);
        if (joystickVelocity.len() > 1) {
            if (joystickVelocity.len() > 8) {
                joystickVelocity.setV(8);
            }
            joystickCurrentPos.add(joystickVelocity);
        }
    }
}
