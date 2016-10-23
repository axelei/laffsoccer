package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.ysdemo.Assets;

public class GoalTopB extends Sprite {

    public GoalTopB(GLGraphics glGraphics) {
        super(glGraphics);
        x = -68;
        y = -661;
        offset = 11;
    }

    @Override
    public void draw(int subframe) {
        texture = Assets.goalTopB;
        super.draw(subframe);
    }
}
