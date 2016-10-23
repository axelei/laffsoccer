package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.gl.SpriteBatcher;
import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.impl.GLGraphics;
import com.ysoccer.android.ysdemo.Assets;

public class FlagpostSprite extends Sprite {

    Match match;

    public FlagpostSprite(GLGraphics glGraphics, Match match, int sideX,
                          int sideY) {
        super(glGraphics);
        this.match = match;
        x = sideX * Const.TOUCH_LINE;
        y = sideY * Const.GOAL_LINE;
    }

    @Override
    public void draw(int subframe) {
        int frameX = 2;
        int frameY = 1;
        if (match.settings.wind.speed > 0) {
            frameX = 2 * (1 + match.settings.wind.dirX);
            frameX += ((subframe / GLGame.SUBFRAMES) >> (4 - match.settings.wind.speed)) % 2;
            frameY = 1 + match.settings.wind.dirY;
        }
        glGraphics.drawTextureRect(Assets.flagposts, x - 21, y - 34,
                42 * frameX, 84 * frameY, 42, 36);
    }

    public void drawShadow(int subframe, SpriteBatcher batcher) {
        int frameX = 2;
        int frameY = 1;
        if (match.settings.wind.speed > 0) {
            frameX = 2 * (1 + match.settings.wind.dirX);
            frameX += ((subframe / GLGame.SUBFRAMES) >> (4 - match.settings.wind.speed)) % 2;
            frameY = 1 + match.settings.wind.dirY;
        }
        batcher.drawSprite(x - 12, y + 1, 42, 12,
                Assets.flagpostShadowFrames[frameX][frameY][0]);
        if (match.settings.time == Time.NIGHT) {
            batcher.drawSprite(x - 28, y + 1, 42, 12,
                    Assets.flagpostShadowFrames[frameX][frameY][1]);
            batcher.drawSprite(x - 28, y - 10, 42, 12,
                    Assets.flagpostShadowFrames[frameX][frameY][2]);
            batcher.drawSprite(x - 12, y - 10, 42, 12,
                    Assets.flagpostShadowFrames[frameX][frameY][3]);
        }
    }
}
