package com.ygames.ysoccer;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;

public class MenuMain implements Screen {

    private YSoccer game;
    private OrthographicCamera camera;
    private Image background;
    private Button gameSettingsButton;

    public MenuMain(YSoccer game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 1280, 720);
        background = new Image("images/backgrounds/menu_main.jpg");
        gameSettingsButton = new Button();
        gameSettingsButton.setGeometry(1280 / 2 - 30 - 320, 290, 320, 36);
        gameSettingsButton.setColors(0x536B9000, 0x7090C200, 0x26314200);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0, 1280, 720);
        game.batch.end();

        gameSettingsButton.render(game);
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
