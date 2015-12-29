package com.ygames.ysoccer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GlScreen;
import com.ygames.ysoccer.framework.Image;
import com.ygames.ysoccer.gui.Button;

public class MenuMain extends GlScreen {

    private OrthographicCamera camera;
    private Button gameSettingsButton;

    public MenuMain(YSoccer game) {
        super(game);
        camera = new OrthographicCamera();
        camera.setToOrtho(true, 1280, 720);
        background = new Image("images/backgrounds/menu_main.jpg");
        gameSettingsButton = new GameSettingsButton();
    }

    @Override
    public void render(float delta) {
        SpriteBatch batch = game.glGraphics.batch;
        ShapeRenderer shapeRenderer = game.glGraphics.shapeRenderer;

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(background, 0, 0, 1280, 720);
        batch.end();

        gameSettingsButton.render(game.glGraphics);
    }

    class GameSettingsButton extends Button {
        public GameSettingsButton() {
            setGeometry(1280 / 2 - 30 - 320, 290, 320, 36);
            setColors(0x536B9000, 0x7090C200, 0x26314200);
            setText("SETTINGS", Font.Align.CENTER, Assets.font14);
        }
    }
}
