package com.ygames.ysoccer.framework;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Image extends TextureRegion {

    public Image(String internalPath) {
        super(new Texture(internalPath));
        flip(false, true);
    }

    public void dispose() {
        getTexture().dispose();
    }
}
