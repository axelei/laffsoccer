package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.GLGraphics;

import java.util.Comparator;

class Sprite {

    GLGraphics glGraphics;

    TextureRegion textureRegion;
    int x;
    int y;
    int z;
    int offset;

    Sprite(GLGraphics glGraphics) {
        this.glGraphics = glGraphics;
    }

    public void draw(int subframe) {
        glGraphics.batch.draw(textureRegion, x, y - z - offset);
    }

    public int getY(int subframe) {
        return y;
    }

    static class SpriteComparator implements Comparator<Sprite> {

        int subframe;

        @Override
        public int compare(Sprite sprite1, Sprite sprite2) {
            return sprite1.getY(subframe) - sprite2.getY(subframe);
        }
    }
}
