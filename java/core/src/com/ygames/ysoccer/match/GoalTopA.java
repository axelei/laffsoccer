package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGraphics;

class GoalTopA extends Sprite {

    GoalTopA(GLGraphics glGraphics) {
        super(glGraphics);
        textureRegion = new TextureRegion(Assets.goalTopA);
        textureRegion.flip(false, true);
        x = -72;
        y = -Const.GOAL_LINE;
        offset = 50;
    }
}
