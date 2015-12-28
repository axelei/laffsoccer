package com.ygames.ysoccer.framework;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GlGraphics {

    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;

    public GlGraphics() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }

}
