package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Settings;

class BallSprite extends Sprite {

    Ball ball;

    BallSprite(GLGraphics glGraphics, Ball ball) {
        super(glGraphics);
        this.ball = ball;
    }

    @Override
    public void draw(int subframe) {
        Data d = ball.data[subframe];
        glGraphics.batch.draw(Assets.ball[d.fmx], d.x - Const.BALL_R, d.y - d.z - 2 - Const.BALL_R);

        if (Settings.showDevelopmentInfo) {
            Assets.font3.draw(glGraphics.batch,  d.x + "," + d.y + "," + d.z, d.x, d.y + 22, Font.Align.CENTER);
        }
    }

    public int getY(int subframe) {
        return ball.data[subframe].y;
    }
}
