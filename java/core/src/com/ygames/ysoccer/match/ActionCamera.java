package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.math.Emath;

public class ActionCamera {

    enum Mode {
        STILL,
        FOLLOW_BALL,
        REACH_TARGET
    }

    enum SpeedMode {
        NORMAL,
        FAST,
        WARP
    }

    private SpeedMode speedMode;

    float x; // position
    float y;

    private float vx; // speed
    private float vy;

    private float offsetX;
    private float offsetY;

    private boolean xLimited;
    private boolean yLimited;

    private Vector2 target;

    private Renderer renderer;

    public ActionCamera(Renderer renderer) {
        this.renderer = renderer;
        x = 0.5f * (Const.PITCH_W - renderer.screenWidth / (renderer.zoom / 100.0f));
        y = 0;
        speedMode = SpeedMode.NORMAL;
        target = new Vector2();
    }

    public void setSpeedMode(SpeedMode speedMode) {
        this.speedMode = speedMode;
    }

    void setOffset(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    void setLimited(boolean xLimited, boolean yLimited) {
        this.xLimited = xLimited;
        this.yLimited = yLimited;
    }

    void setTarget(float x, float y) {
        target.set(x, y);
    }

    void update(Mode mode) {

        switch (mode) {

            case STILL:
                // do nothing
                break;

            case FOLLOW_BALL:
                Ball ball = renderer.ball;

                // "remember" last direction of movement
                if (ball.v * Emath.cos(ball.a) != 0) {
                    offsetX = renderer.screenWidth / 20.0f * Emath.cos(ball.a);
                }
                if (ball.v * Emath.sin(ball.a) != 0) {
                    offsetY = renderer.screenHeight / 20.0f * Emath.sin(ball.a);
                }

                // speed
                if (Math.abs(vx) > Math.abs(ball.v * Emath.cos(ball.a))) {
                    // decelerate
                    vx = (1 - 5.0f / Const.SECOND) * vx;
                } else {
                    // accelerate
                    vx = vx + (2.5f / Const.SECOND) * Math.signum(offsetX) * Math.abs(vx - ball.v * Emath.cos(ball.a));
                }
                if (Math.abs(vy) > Math.abs(ball.v * Emath.sin(ball.a))) {
                    // decelerate
                    vy = (1 - 5.0f / Const.SECOND) * vy;
                } else {
                    // accelerate
                    vy = vy + (2.5f / Const.SECOND) * Math.signum(offsetY) * Math.abs(vy - ball.v * Emath.sin(ball.a));
                }

                x = x + vx / Const.SECOND;
                y = y + vy / Const.SECOND;

                // near the point "ball+offset"
                if (Math.abs(ball.x + offsetX - (x - Const.CENTER_X + renderer.screenWidth / (2 * renderer.zoom / 100.0f))) >= 10) {
                    float f = ball.x + offsetX - (x - Const.CENTER_X + renderer.screenWidth / (2 * renderer.zoom / 100.0f));
                    x = x + (10.0f / Const.SECOND) * (1 + speedMode.ordinal()) * Math.signum(f) * (float) Math.sqrt(Math.abs(f));
                }
                if (Math.abs(ball.y + offsetY - (y - Const.CENTER_Y + renderer.screenHeight / (2 * renderer.zoom / 100.0f))) >= 10) {
                    float f = ball.y + offsetY - (y - Const.CENTER_Y + renderer.screenHeight / (2 * renderer.zoom / 100.0f));
                    y = y + (10.0f / Const.SECOND) * (1 + speedMode.ordinal()) * Math.signum(f) * (float) Math.sqrt(Math.abs(f));
                }
                break;

            case REACH_TARGET:
                x = x + (10.0f / Const.SECOND)
                        * (1 + speedMode.ordinal())
                        * Math.signum(target.x - (x - Const.CENTER_X + renderer.screenWidth / (2 * renderer.zoom / 100.0f)))
                        * (float) Math.sqrt(Math.abs(target.x - (x - Const.CENTER_X + renderer.screenWidth / (2 * renderer.zoom / 100.0f))));
                y = y
                        + (10.0f / Const.SECOND)
                        * (1 + speedMode.ordinal())
                        * Math.signum(target.y - (y - Const.CENTER_Y + renderer.screenHeight / (2 * renderer.zoom / 100.0f)))
                        * (float) Math.sqrt(Math.abs(target.y - (y - Const.CENTER_Y + renderer.screenHeight / (2 * renderer.zoom / 100.0f))));
                break;
        }

        // keep inside pitch
        float xmin = 0;
        float xmax = Const.PITCH_W - renderer.screenWidth / (renderer.zoom / 100.0f);
        float ymin = 0;
        float ymax = Const.PITCH_H - renderer.screenHeight / (renderer.zoom / 100.0f);

        if (xLimited && (renderer.screenWidth / (renderer.zoom / 100.0f) < 1600)) {
            xmin = Const.CENTER_X - Const.TOUCH_LINE - renderer.screenWidth / (16 * renderer.zoom / 100.0f);
            xmax = Const.CENTER_X + Const.TOUCH_LINE + renderer.screenWidth / (16 * renderer.zoom / 100.0f) - renderer.screenWidth / (renderer.zoom / 100.0f);
        }
        if (yLimited) {
            ymin = Const.CENTER_Y - Const.GOAL_LINE - renderer.screenHeight / (4 * renderer.zoom / 100.0f);
            ymax = Const.CENTER_Y + Const.GOAL_LINE + renderer.screenHeight / (4 * renderer.zoom / 100.0f) - renderer.screenHeight / (renderer.zoom / 100.0f);
        }

        if (x < xmin) {
            x = xmin;
        }

        if (x > xmax) {
            x = xmax;
        }

        if (y < ymin) {
            y = ymin;
        }

        if (y > ymax) {
            y = ymax;
        }
    }
}
