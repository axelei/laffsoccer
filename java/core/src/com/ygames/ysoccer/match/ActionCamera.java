package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

public class ActionCamera {

    // follow
    static final int CF_NONE = 0;
    static final int CF_BALL = 1;
    static final int CF_TARGET = 2;

    // speed
    static final int CS_NORMAL = 0;
    static final int CS_FAST = 1;
    static final int CS_WARP = 2;

    float x; // position
    float y;

    private float vx; // speed
    private float vy;

    private float offsetX;
    private float offsetY;

    private Renderer renderer;

    public ActionCamera(Renderer renderer) {
        this.renderer = renderer;
        x = 0.5f * (Const.PITCH_W - renderer.screenWidth / (renderer.zoom / 100.0f));
        y = 0;
    }

    void setOffset(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    // follow: CF_NONE, CF_BALL, CF_TARGET
    // speed: CS_NORMAL, CS_FAST, CS_WARP
    int updateX(int follow, int speed, float target_x, boolean limit) {

        switch (follow) {

            case CF_NONE:
                // do nothing
                break;

            case CF_BALL:
                Ball ball = renderer.ball;

                if (ball.v * Emath.cos(ball.a) != 0) {
                    // "remember" last direction of movement
                    offsetX = renderer.screenWidth / 20.0f * Emath.cos(ball.a);
                }

                // speed
                if (Math.abs(vx) > Math.abs(ball.v * Emath.cos(ball.a))) {
                    // decelerate
                    vx = (1 - 5.0f / Const.SECOND) * vx;
                } else {
                    // accelerate
                    vx = vx + (2.5f / Const.SECOND) * Math.signum(offsetX) * Math.abs(vx - ball.v * Emath.cos(ball.a));
                }

                x = x + vx / Const.SECOND;

                // near the point "ball+offset"
                if (Math.abs(ball.x + offsetX - (x - Const.CENTER_X + renderer.screenWidth / (2 * renderer.zoom / 100.0f))) >= 10) {
                    float f = ball.x + offsetX - (x - Const.CENTER_X + renderer.screenWidth / (2 * renderer.zoom / 100.0f));
                    x = x + (10.0f / Const.SECOND) * (1 + speed) * Math.signum(f) * (float) Math.sqrt(Math.abs(f));
                }
                break;

            case CF_TARGET:
                x = x + (10.0f / Const.SECOND)
                        * (1 + speed)
                        * Math.signum(target_x - (x - Const.CENTER_X + renderer.screenWidth / (2 * renderer.zoom / 100.0f)))
                        * (float) Math.sqrt(Math.abs(target_x - (x - Const.CENTER_X + renderer.screenWidth / (2 * renderer.zoom / 100.0f))));
                break;
        }

        // keep inside pitch
        float xmin = 0;
        float xmax = Const.PITCH_W - renderer.screenWidth / (renderer.zoom / 100.0f);

        if ((renderer.screenWidth / (renderer.zoom / 100.0f) < 1600) && limit) {
            xmin = Const.CENTER_X - Const.TOUCH_LINE - renderer.screenWidth / (16 * renderer.zoom / 100.0f);
            xmax = Const.CENTER_X + Const.TOUCH_LINE + renderer.screenWidth / (16 * renderer.zoom / 100.0f) - renderer.screenWidth / (renderer.zoom / 100.0f);
        }

        if (x < xmin) {
            x = xmin;
        }

        if (x > xmax) {
            x = xmax;
        }

        return Math.round(x);
    }

    // follow: CF_NONE, CF_BALL, CF_TARGET
    // speed: CS_NORMAL, CS_FAST, CS_WARP
    int updateY(int follow, int speed, float target_y, boolean limit) {

        switch (follow) {

            case CF_NONE:
                // do nothing;
                break;

            case CF_BALL:
                Ball ball = renderer.ball;

                if (ball.v * Emath.cos(ball.a) != 0) {
                    // "remember" last direction of movement
                    offsetY = renderer.screenHeight / 20.0f * Emath.sin(ball.a);
                }

                // speed
                if (Math.abs(vy) > Math.abs(ball.v * Emath.sin(ball.a))) {
                    // decelerate
                    vy = (1 - 5.0f / Const.SECOND) * vy;
                } else {
                    // accelerate
                    vy = vy + (2.5f / Const.SECOND) * Math.signum(offsetY) * Math.abs(vy - ball.v * Emath.sin(ball.a));
                }

                y = y + vy / Const.SECOND;

                // near the point "ball+offset"
                if (Math.abs(ball.y + offsetY - (y - Const.CENTER_Y + renderer.screenHeight / (2 * renderer.zoom / 100.0f))) >= 10) {
                    float f = ball.y + offsetY - (y - Const.CENTER_Y + renderer.screenHeight / (2 * renderer.zoom / 100.0f));
                    y = y + (10.0f / Const.SECOND) * (1 + speed) * Math.signum(f) * (float) Math.sqrt(Math.abs(f));
                }
                break;

            case CF_TARGET:
                y = y
                        + (10.0f / Const.SECOND)
                        * (1 + speed)
                        * Math.signum(target_y - (y - Const.CENTER_Y + renderer.screenHeight / (2 * renderer.zoom / 100.0f)))
                        * (float) Math.sqrt(Math.abs(target_y - (y - Const.CENTER_Y + renderer.screenHeight / (2 * renderer.zoom / 100.0f))));
                break;
        }

        // keep inside pitch
        float ymin = 0;
        float ymax = Const.PITCH_H - renderer.screenHeight / (renderer.zoom / 100.0f);

        if (limit) {
            ymin = Const.CENTER_Y - Const.GOAL_LINE - renderer.screenHeight / (4 * renderer.zoom / 100.0f);
            ymax = Const.CENTER_Y + Const.GOAL_LINE + renderer.screenHeight / (4 * renderer.zoom / 100.0f) - renderer.screenHeight / (renderer.zoom / 100.0f);
        }

        if (y < ymin) {
            y = ymin;
        }

        if (y > ymax) {
            y = ymax;
        }

        return Math.round(y);
    }
}
