package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.ysdemo.Assets;

public class GoalTopA extends Sprite {

    public GoalTopA(GLGraphics glGraphics) {
        super(glGraphics);
        x = -72;
        y = -Const.GOAL_LINE;
        offset = 50;
    }

    @Override
    public void draw(int subframe) {
        texture = Assets.goalTopA;
        super.draw(subframe);
    }
}
