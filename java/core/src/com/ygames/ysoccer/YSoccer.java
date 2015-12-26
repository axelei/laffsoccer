package com.ygames.ysoccer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class YSoccer extends ApplicationAdapter {
    private OrthographicCamera camera;
    SpriteBatch batch;
    TextureRegion region;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 1280, 720);
        batch = new SpriteBatch();
        region = new TextureRegion(new Texture("badlogic.jpg"));
        region.flip(false, true);
    }

    @Override
    public void render() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(region, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        region.getTexture().dispose();
        batch.dispose();
    }
}
