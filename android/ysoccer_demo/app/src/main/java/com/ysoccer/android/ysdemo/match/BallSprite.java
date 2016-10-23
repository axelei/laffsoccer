package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.ysdemo.Assets;

public class BallSprite extends Sprite {

    Ball ball;

    public BallSprite(GLGraphics glGraphics, Ball ball) {
        super(glGraphics);
        this.ball = ball;
    }

    @Override
    public void draw(int subframe) {
        Data d = ball.data[subframe];
        glGraphics.drawTextureRect(Assets.ball, d.x + 1 - Const.BALL_R, d.y
                - d.z - 2 - Const.BALL_R, 8 * d.fmx, 0, 8, 8);
    }

    public int getY(int subframe) {
        return ball.data[subframe].y;
    }

}
