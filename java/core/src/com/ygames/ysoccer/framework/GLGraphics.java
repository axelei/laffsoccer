package com.ygames.ysoccer.framework;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GLGraphics {

    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public OrthographicCamera camera;

    public GLGraphics() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }

    public static void setSpriteBatchColor(SpriteBatch spriteBatch, int rgb, float alpha) {
        spriteBatch.setColor(GlColor.red(rgb) / 255f, GlColor.green(rgb) / 255f, GlColor.blue(rgb) / 255f, alpha);
    }

    public static void setShapeRendererColor(ShapeRenderer shapeRenderer, int rgb, float alpha) {
        shapeRenderer.setColor(GlColor.red(rgb) / 255f, GlColor.green(rgb) / 255f, GlColor.blue(rgb) / 255f, alpha);
    }
}
