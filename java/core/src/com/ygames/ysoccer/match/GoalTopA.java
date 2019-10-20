package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGraphics;

import static com.ygames.ysoccer.match.Const.GOAL_LINE;

class GoalTopA extends Sprite {

    GoalTopA(GLGraphics glGraphics) {
        super(glGraphics);
        textureRegion = new TextureRegion(Assets.goalTopA);
        textureRegion.flip(false, true);
        x = -73;
        y = -691;
    }

    @Override
    public int getY(int subframe) {
        return -GOAL_LINE;
    }
}
