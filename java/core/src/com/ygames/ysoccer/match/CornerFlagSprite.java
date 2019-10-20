package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLGraphics;

class CornerFlagSprite extends Sprite {

    private SceneSettings sceneSettings;

    CornerFlagSprite(GLGraphics glGraphics, SceneSettings sceneSettings, int sideX, int sideY) {
        super(glGraphics);
        this.sceneSettings = sceneSettings;
        x = sideX * Const.TOUCH_LINE;
        y = sideY * Const.GOAL_LINE;
    }

    @Override
    public void draw(int subframe) {
        int frameX = 2;
        int frameY = 1;
        if (sceneSettings.wind.speed > 0) {
            frameX = 2 * (1 + sceneSettings.wind.dirX);
            frameX += ((subframe / GLGame.SUBFRAMES) >> (4 - sceneSettings.wind.speed)) % 2;
            frameY = 1 + sceneSettings.wind.dirY;
        }
        glGraphics.batch.draw(Assets.cornerFlags[frameX][frameY], x - 22, y - 35);
    }

    void drawShadow(int subframe, SpriteBatch batch) {
        int frameX = 2;
        int frameY = 1;
        if (sceneSettings.wind.speed > 0) {
            frameX = 2 * (1 + sceneSettings.wind.dirX);
            frameX += ((subframe / GLGame.SUBFRAMES) >> (4 - sceneSettings.wind.speed)) % 2;
            frameY = 1 + sceneSettings.wind.dirY;
        }
        batch.draw(Assets.cornerFlagsShadows[frameX][frameY][0], x - 12, y + 1);
        if (sceneSettings.time == MatchSettings.Time.NIGHT) {
            batch.draw(Assets.cornerFlagsShadows[frameX][frameY][1], x - 28, y + 1);
            batch.draw(Assets.cornerFlagsShadows[frameX][frameY][2], x - 28, y - 10);
            batch.draw(Assets.cornerFlagsShadows[frameX][frameY][3], x - 12, y - 10);
        }
    }
}
