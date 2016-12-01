package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;

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
    public void render(GLSpriteBatch batch, GLShapeRenderer shapeRenderer) {
        if (!visible) {
            return;
        }

        if (textureRegion != null) {
            batch.begin();
            batch.draw(textureRegion, x, y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
            batch.end();
        }
    }
}
