package com.ygames.ysoccer;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ygames.ysoccer.framework.Image;

public class MenuMain implements Screen {

    private YSoccer game;
    private OrthographicCamera camera;
    private Image background;

    public MenuMain(YSoccer game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 1280, 720);
        background = new Image("images/backgrounds/menu_main.jpg");
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, 1280, 720);
        game.batch.end();
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
        background.dispose();
    }
}
