package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.gl.Texture;
import com.ysoccer.android.framework.impl.GLGraphics;

class Sprite {

    GLGraphics glGraphics;

    Texture texture;
    int x;
    int y;
    int z;
    int offset;

    Sprite(GLGraphics glGraphics) {
        this.glGraphics = glGraphics;
    }

    public void draw(int subframe) {
        glGraphics.drawTexture(texture, x, y - z - offset);
    }

    public int getY(int subframe) {
        return y;
    }
}
