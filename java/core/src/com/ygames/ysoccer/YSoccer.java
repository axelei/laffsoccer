package com.ygames.ysoccer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ygames.ysoccer.framework.Image;

public class YSoccer extends ApplicationAdapter {
    private OrthographicCamera camera;
    SpriteBatch batch;
    Image background;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 1280, 720);
        batch = new SpriteBatch();
        background = new Image("images/backgrounds/menu_main.jpg");
    }

    @Override
    public void render() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, 1280, 720);
        batch.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        batch.dispose();
    }
}
