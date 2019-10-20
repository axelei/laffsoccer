package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGraphics;

import static com.ygames.ysoccer.match.Const.JUMPER_H;
import static com.ygames.ysoccer.match.Const.JUMPER_X;
import static com.ygames.ysoccer.match.Const.JUMPER_Y;

class JumperSprite extends Sprite {

    JumperSprite(GLGraphics glGraphics, int xSide, int ySide) {
        super(glGraphics);
        textureRegion = new TextureRegion(Assets.jumper);
        textureRegion.flip(false, true);
        x = xSide * JUMPER_X -1;
        y = ySide * JUMPER_Y - JUMPER_H -1;
    }

    @Override
    public int getY(int subframe) {
        return y + JUMPER_H;
    }
}
