package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGraphics;

class ObjectSprite extends Sprite {

    float alpha = 1.0f;
    float ratio = 1.0f;
    float rotation = 0f;

    ObjectSprite(GLGraphics glGraphics, TextureRegion textureRegion, int x, int y) {
        this(glGraphics, textureRegion, 1, x, y, 1, 0);
    }

    ObjectSprite(GLGraphics glGraphics, TextureRegion textureRegion, int x, int y, float rotation) {
        this(glGraphics, textureRegion, 1, x, y, 1, rotation);
    }

    ObjectSprite(GLGraphics glGraphics, TextureRegion textureRegion, float alpha, int x, int y, float ratio, float rotation) {
        super(glGraphics);
        this.textureRegion = textureRegion;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.priority = -1;
        this.ratio = ratio;
        this.alpha = alpha;
    }

    ObjectSprite(GLGraphics glGraphics) {
        super(glGraphics);
    }

    @Override
    public void draw(int subFrame) {

        int width = (int) (textureRegion.getRegionWidth() * ratio);
        int height = (int) (textureRegion.getRegionHeight() * ratio);
        
        if (alpha != 1f) {
            glGraphics.batch.setColor(0xFFFFFF, alpha);
        }
        glGraphics.batch.draw(textureRegion, x - width / 2.0f, y - height / 2.0f, width / 2.0f, height / 2.0f, width, height, ratio, ratio, rotation);
        if (alpha != 1f) {
            glGraphics.batch.setColor(0xFFFFFF, 1f);
        }
    }

    public static ObjectSprite mow(GLGraphics glGraphics, float x, float y, float angle) {
        return new ObjectSprite(glGraphics, new TextureRegion(Assets.mow), 0.6f + (float) (Math.random() / 2), (int) x, (int) y, 0.25f + (float) ((Math.random() + 0.5) / 2), angle - 90 + (float) (Math.random() * 15 - 7.5));
    }

    public static ObjectSprite blood(GLGraphics glGraphics, float x, float y) {
        return new ObjectSprite(glGraphics, Assets.blood[EMath.dice(0, 3)], 0.75f + (float) (Math.random() * 0.20), (int) x, (int) y, 0.5f + (float) ((Math.random() + 0.5) / 2) , (float) Math.random() * 360);
    }
}

