package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.GLGraphics;

public class Picture extends Widget {

    public Picture(TextureRegion textureRegion) {
        setTextureRegion(textureRegion);
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        if (textureRegion != null) {
            this.textureRegion = textureRegion;
            w = textureRegion.getRegionWidth();
            h = textureRegion.getRegionHeight();
        }
    }

    @Override
    public void render(GLGraphics glGraphics) {
        if (!visible) {
            return;
        }

        if (textureRegion != null) {
            SpriteBatch batch = glGraphics.batch;
            batch.begin();
            batch.draw(textureRegion, x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
            batch.end();
        }
    }
}
