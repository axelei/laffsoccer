package com.ygames.ysoccer.framework;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GLGraphics {

    public SpriteBatch batch;
    public GlShapeRenderer shapeRenderer;
    public OrthographicCamera camera;

    public GLGraphics() {
        batch = new SpriteBatch();
        shapeRenderer = new GlShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 1280, 720);
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }
}
