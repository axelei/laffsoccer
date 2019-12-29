package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.MenuMusic;
import com.ygames.ysoccer.gui.TextBox;
import com.ygames.ysoccer.gui.Widget;

import java.util.Arrays;

public class Intro extends GLScreen {

    public Intro(GLGame game) {
        super(game);

        usesMouse = false;

        game.disableMouse();
        Gdx.input.setInputProcessor(new IntroInputProcessor());

        Widget w;

        BitmapFont font = new BitmapFont(true);
        String[] lines = {
                "Charnego Soccer",
                "Charnego Translations Inc.",
                "",
                "Basado en YSoccer",
                "",
                "Este juego se distribuye SIN NINGUNA GARANTÍA, pulsa 'W' para saber más.",
                "Juego libre y gratuíto con licencia GPL2.",
                "Dale a la 'C' para ver más.",
                "",
                "",
                "Pica una tecla para viciarte"
        };
        w = new TextBox(font, Arrays.asList(lines), game.gui.WIDTH / 2, 270);
        widgets.add(w);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (widgetEvent == Widget.Event.FIRE1_UP || widgetEvent == Widget.Event.FIRE2_UP) {
            setMainMenu();
        }
    }

    private class IntroInputProcessor extends InputAdapter {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            setMainMenu();
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Input.Keys.W:
                    game.setScreen(new Warranty(game));
                    break;

                case Input.Keys.C:
                    game.setScreen(new Copying(game));
                    break;

                default:
                    setMainMenu();
                    break;
            }
            return true;
        }
    }

    private void setMainMenu() {
        Gdx.input.setInputProcessor(null);
        game.enableMouse();
        game.menuMusic.setMode(game.settings.musicMode);
        game.prematchMusic.setMode(MenuMusic.ALL);
        game.setScreen(new Main(game));
    }
}
