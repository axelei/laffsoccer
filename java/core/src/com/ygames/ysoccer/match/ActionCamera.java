package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.Const.CENTER_X;
import static com.ygames.ysoccer.match.Const.CENTER_Y;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.PITCH_H;
import static com.ygames.ysoccer.match.Const.PITCH_W;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Const.TOUCH_LINE;

class ActionCamera {

    enum Mode {
        STILL,
        FOLLOW_BALL,
        REACH_TARGET
    }

    enum Speed {
        NORMAL,
        FAST,
        WARP
    }

    private Mode mode;
    private Speed speed;

    float x; // position
    float y;

    private float width;
    private float height;

    private float dx;
    private float dy;

    private float vx;
    private float vy;

    private float offsetX;
    private float offsetY;

    private boolean xLimited;
    private boolean yLimited;

    private final Vector2 target;
    private float targetDistance;

    private final Ball ball;

    ActionCamera(Ball ball) {
        this.ball = ball;
        speed = Speed.NORMAL;
        target = new Vector2();
    }

    Mode getMode() {
        return mode;
    }

    ActionCamera setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    Speed getSpeed() {
        return speed;
    }

    ActionCamera setSpeed(Speed speed) {
        this.speed = speed;
        return this;
    }

    ActionCamera setOffset(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
        return this;
    }

    ActionCamera setLimited(boolean xLimited, boolean yLimited) {
        this.xLimited = xLimited;
        this.yLimited = yLimited;
        return this;
    }

    ActionCamera setTarget(float x, float y) {
        target.set(x, y);
        return this;
    }

    ActionCamera setTarget(Vector2 t) {
        target.set(t);
        return this;
    }

    Vector2 getCurrentTarget() {
        Vector2 t = new Vector2();

        switch (mode) {
            case STILL:
                t.set(x - dx, y - dy);
                break;

            case FOLLOW_BALL:
                t.set(ball.x + offsetX, ball.y + offsetY);
                break;

            case REACH_TARGET:
                t.set(target);
                break;
        }

        clampTarget(t);

        return t;
    }

    private void clampTarget(Vector2 t) {
        float txMin = -CENTER_X + width / 2;
        float txMax = -CENTER_X + PITCH_W - width / 2;
        float tyMin = -CENTER_Y + height / 2;
        float tyMax = -CENTER_Y + PITCH_H - height / 2;

        if (xLimited && width < 1600) {
            txMin = -TOUCH_LINE - width / 16 + width / 2;
            txMax = +TOUCH_LINE + width / 16 - width / 2;
        }
        if (yLimited) {
            tyMin = -GOAL_LINE - height / 4 + height / 2;
            tyMax = +GOAL_LINE + height / 4 - height / 2;
        }

        t.x = EMath.clamp(t.x, txMin, txMax);
        t.y = EMath.clamp(t.y, tyMin, tyMax);
    }

    void setScreenParameters(int screenWidth, int screenHeight, int zoom) {
        x += width / 2;
        y += height / 2;
        width = screenWidth / (zoom / 100.0f);
        height = screenHeight / (zoom / 100.0f);
        x -= width / 2;
        y -= height / 2;
        dx = CENTER_X - width / 2;
        dy = CENTER_Y - height / 2;
    }

    public float getTargetDistance() {
        return targetDistance;
    }

    void update() {

        switch (mode) {

            case STILL:
                // do nothing
                break;

            case FOLLOW_BALL:
                // "remember" last direction of movement
                if (Math.abs(ball.v * EMath.cos(ball.a)) > 1) {
                    offsetX = width / 20.0f * EMath.cos(ball.a);
                }
                if (Math.abs(ball.v * EMath.sin(ball.a)) > 1) {
                    offsetY = height / 20.0f * EMath.sin(ball.a);
                }

                // speed
                if (Math.abs(vx) > Math.abs(ball.v * EMath.cos(ball.a))) {
                    // decelerate
                    vx = (1 - 5.0f / SECOND) * vx;
                } else {
                    // accelerate
                    vx = vx + (2.5f / SECOND) * Math.signum(offsetX) * Math.abs(vx - ball.v * EMath.cos(ball.a));
                }
                if (Math.abs(vy) > Math.abs(ball.v * EMath.sin(ball.a))) {
                    // decelerate
                    vy = (1 - 5.0f / SECOND) * vy;
                } else {
                    // accelerate
                    vy = vy + (2.5f / SECOND) * Math.signum(offsetY) * Math.abs(vy - ball.v * EMath.sin(ball.a));
                }

                x = x + vx / SECOND;
                y = y + vy / SECOND;

                // near the point "ball+offset"
                if (Math.abs(ball.x + offsetX - (x - dx)) >= 10) {
                    float f = ball.x + offsetX - (x - dx);
                    x = x + (10.0f / SECOND) * (1 + speed.ordinal()) * Math.signum(f) * (float) Math.sqrt(Math.abs(f));
                }
                if (Math.abs(ball.y + offsetY - (y - dy)) >= 10) {
                    float f = ball.y + offsetY - (y - dy);
                    y = y + (10.0f / SECOND) * (1 + speed.ordinal()) * Math.signum(f) * (float) Math.sqrt(Math.abs(f));
                }
                break;

            case REACH_TARGET:
                float x0 = target.x - (x - dx);
                float y0 = target.y - (y - dy);
                float a = EMath.aTan2(y0, x0);
                targetDistance = EMath.sqrt(Math.abs(x0) + Math.abs(y0));
                x += (10.0f / SECOND) * (1 + speed.ordinal()) * targetDistance * EMath.cos(a);
                y += (10.0f / SECOND) * (1 + speed.ordinal()) * targetDistance * EMath.sin(a);
                break;
        }

        // keep inside pitch
        float xMin = 0;
        float xMax = PITCH_W - width;
        float yMin = 0;
        float yMax = PITCH_H - height;

        if (xLimited && width < 1600) {
            xMin = CENTER_X - TOUCH_LINE - width / 16;
            xMax = CENTER_X + TOUCH_LINE + width / 16 - width;
        }
        if (yLimited) {
            yMin = CENTER_Y - GOAL_LINE - height / 4;
            yMax = CENTER_Y + GOAL_LINE + height / 4 - height;
        }

        if (x < xMin) {
            x = xMin;
        }

        if (x > xMax) {
            x = xMax;
        }

        if (y < yMin) {
            y = yMin;
        }

        if (y > yMax) {
            y = yMax;
        }
    }
}
