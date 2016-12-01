package com.ygames.ysoccer.framework;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class GLGraphics {

    public GLSpriteBatch batch;
    public GLShapeRenderer shapeRenderer;
    public OrthographicCamera camera;
    public int light = 255;

    public GLGraphics() {
        batch = new GLSpriteBatch(this);
        shapeRenderer = new GLShapeRenderer(this);
        camera = new OrthographicCamera();
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }
}
