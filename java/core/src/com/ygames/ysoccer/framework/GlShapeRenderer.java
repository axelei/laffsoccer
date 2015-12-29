package com.ygames.ysoccer.framework;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GlShapeRenderer extends ShapeRenderer {

    public void setColor(int rgb, float alpha) {
        setColor(GlColor.red(rgb) / 255f, GlColor.green(rgb) / 255f, GlColor.blue(rgb) / 255f, alpha);
    }
}
