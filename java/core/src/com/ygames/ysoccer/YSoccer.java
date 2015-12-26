package com.ygames.ysoccer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class YSoccer extends ApplicationAdapter {
    private OrthographicCamera camera;
    SpriteBatch batch;
    Texture img;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 1280, 720);
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
    }

    @Override
    public void render() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        img.dispose();
        batch.dispose();
    }
}
