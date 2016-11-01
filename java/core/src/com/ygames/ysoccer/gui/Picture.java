package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Image;

public class Picture extends Widget {

    public Picture(Image image) {
        setImage(image);
    }

    public void setImage(Image image) {
        if (image != null) {
            this.image = image;
            w = image.getRegionWidth();
            h = image.getRegionHeight();
        }
    }

    @Override
    public void render(GLGraphics glGraphics) {
        if (!isVisible) {
            return;
        }

        if (image != null) {
            SpriteBatch batch = glGraphics.batch;
            batch.begin();
            batch.draw(image, x, y, image.getRegionWidth(), image.getRegionHeight());
            batch.end();
        }
    }
}
