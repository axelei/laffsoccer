package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;
import com.ygames.ysoccer.gui.Widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                "under certain conditions; press 'C' for details."
        };
        w = new TextBox(font, Arrays.asList(lines), 312);
        widgets.add(w);

        w = new TextBox(font, Collections.singletonList("Press any key to continue"), 482);
        widgets.add(w);
    }

    private class TextBox extends Widget {

        BitmapFont font;
        private List<String> lines;
        int top;

        TextBox(BitmapFont font, List<String> lines, int top) {
            this.font = font;
            this.lines = lines;
            this.top = top;
        }

        @Override
        public void render(GLSpriteBatch batch, GLShapeRenderer shapeRenderer) {
            batch.begin();
            int y = top;
            for (String line : lines) {
                font.draw(batch, line, game.gui.WIDTH / 2, y, 0, Align.center, true);
                y += 18;
            }
            batch.end();
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
