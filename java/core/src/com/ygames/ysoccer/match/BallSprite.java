package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GlGraphics;

class BallSprite extends Sprite {

    Ball ball;

    BallSprite(GlGraphics glGraphics, Ball ball) {
        super(glGraphics);
        this.ball = ball;
    }

    @Override
    public void draw(int subframe) {
        Data d = ball.data[subframe];
        glGraphics.batch.draw(Assets.ball[d.fmx], d.x + 1 - Const.BALL_R, d.y - d.z - 2 - Const.BALL_R);
    }

    public int getY(int subframe) {
        return ball.data[subframe].y;
    }
}
