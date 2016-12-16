package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.TextBox;
import com.ygames.ysoccer.gui.Widget;

import java.util.Arrays;

public class Intro extends GLScreen {

    public Intro(GLGame game) {
        super(game);

        game.unsetMouse();
        Gdx.input.setInputProcessor(new IntroInputProcessor());

        Widget w;

        BitmapFont font = new BitmapFont(true);
        String[] lines = {
                "YSoccer 16, Copyright (C) 2016",
                "by Massimo Modica, Daniele Giannarini, Marco Modica",
                "",
                "YSoccer comes with ABSOLUTELY NO WARRANTY; for details press 'W'.",
                "This is free software, and you are welcome to redistribute it",
                "under certain conditions; press 'C' for details.",
                "",
                "",
                "Press any key to continue"
        };
        w = new TextBox(font, Arrays.asList(lines), game.gui.WIDTH / 2, 270);
        widgets.add(w);
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
                    // TODO: show warranty
                    break;
                case Input.Keys.C:
                    // TODO: show details
                    break;
                default:
                    setMainMenu();
                    break;
            }
            return true;
        }

        private void setMainMenu() {
            Gdx.input.setInputProcessor(null);
            game.setMouse();
            game.menuMusic.setMode(game.settings.musicMode);
            game.setScreen(new Main(game));
        }
    }
}
