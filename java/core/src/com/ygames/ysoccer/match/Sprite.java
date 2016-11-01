package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGraphics;
import com.ygames.ysoccer.framework.Image;

import java.util.Comparator;

public class Sprite {

    GLGraphics glGraphics;

    Image textureRegion;
    int x;
    int y;
    int z;
    int offset;

    public Sprite(GLGraphics glGraphics) {
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
