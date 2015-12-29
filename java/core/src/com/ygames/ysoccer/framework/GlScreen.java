package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.YSoccer;
import com.ygames.ysoccer.gui.Widget;

import java.util.ArrayList;
import java.util.List;

public abstract class GlScreen implements Screen {

    protected YSoccer game;
    protected Image background;
    protected List<Widget> widgets;

    public GlScreen(YSoccer game) {
        this.game = game;
        widgets = new ArrayList<Widget>();
    }

    @Override
    public void render(float delta) {
        OrthographicCamera camera = game.glGraphics.camera;
        SpriteBatch batch = game.glGraphics.batch;
        ShapeRenderer shapeRenderer = game.glGraphics.shapeRenderer;

        camera.update();

        // background
        batch.setProjectionMatrix(camera.combined);
        if (background != null) {
            batch.begin();
            batch.draw(background, 0, 0, 1280, 720);
            batch.end();
        }

        // widgets
        shapeRenderer.setProjectionMatrix(camera.combined);
        int len = widgets.size();
        for (int i = 0; i < len; i++) {
            widgets.get(i).render(game.glGraphics);
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (background != null) {
            background.dispose();
        }
    }
}
