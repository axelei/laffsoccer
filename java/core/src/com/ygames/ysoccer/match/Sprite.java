package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GlGraphics;
import com.ygames.ysoccer.framework.Image;

public class Sprite {

    GlGraphics glGraphics;

    Image textureRegion;
    int x;
    int y;
    int z;
    int offset;

    public Sprite(GlGraphics glGraphics) {
        this.glGraphics = glGraphics;
    }

    public void draw(int subframe) {
        glGraphics.batch.draw(textureRegion, x, y - z - offset);
    }

    public int getY(int subframe) {
        return y;
    }
}
