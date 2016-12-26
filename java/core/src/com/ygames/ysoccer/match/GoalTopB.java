package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGraphics;

public class GoalTopB extends Sprite {

    public GoalTopB(GLGraphics glGraphics) {
        super(glGraphics);
        textureRegion = new TextureRegion(Assets.goalTopB);
        textureRegion.flip(false, true);
        x = -68;
        y = -661;
        offset = 11;
    }
}
