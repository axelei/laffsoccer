package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLGraphics;

class CornerFlagSprite extends Sprite {

    private MatchSettings matchSettings;

    CornerFlagSprite(GLGraphics glGraphics, MatchSettings matchSettings, int sideX, int sideY) {
        super(glGraphics);
        this.matchSettings = matchSettings;
        x = sideX * Const.TOUCH_LINE;
        y = sideY * Const.GOAL_LINE;
    }

    @Override
    public void draw(int subframe) {
        int frameX = 2;
        int frameY = 1;
        if (matchSettings.wind.speed > 0) {
            frameX = 2 * (1 + matchSettings.wind.dirX);
            frameX += ((subframe / GLGame.SUBFRAMES) >> (4 - matchSettings.wind.speed)) % 2;
            frameY = 1 + matchSettings.wind.dirY;
        }
        glGraphics.batch.draw(Assets.cornerFlags[frameX][frameY], x - 21, y - 34);
    }

    void drawShadow(int subframe, SpriteBatch batch) {
        int frameX = 2;
        int frameY = 1;
        if (matchSettings.wind.speed > 0) {
            frameX = 2 * (1 + matchSettings.wind.dirX);
            frameX += ((subframe / GLGame.SUBFRAMES) >> (4 - matchSettings.wind.speed)) % 2;
            frameY = 1 + matchSettings.wind.dirY;
        }
        batch.draw(Assets.cornerFlagsShadows[frameX][frameY][0], x - 12, y + 1);
        if (matchSettings.time == MatchSettings.Time.NIGHT) {
            batch.draw(Assets.cornerFlagsShadows[frameX][frameY][1], x - 28, y + 1);
            batch.draw(Assets.cornerFlagsShadows[frameX][frameY][2], x - 28, y - 10);
            batch.draw(Assets.cornerFlagsShadows[frameX][frameY][3], x - 12, y - 10);
        }
    }
}
