package com.ygames.ysoccer.framework;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GLSpriteBatch extends SpriteBatch {

    public GLGraphics graphics;

    public GLSpriteBatch(GLGraphics graphics) {
        this.graphics = graphics;
    }

    public void setColor(int rgb, float alpha) {
        setColor(graphics.light * GLColor.red(rgb) / 65025f, graphics.light * GLColor.green(rgb) / 65025f, graphics.light * GLColor.blue(rgb) / 65025f, alpha);
    }
}
