package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Screen;
import com.ygames.ysoccer.YSoccer;

public abstract class GlScreen implements Screen {

    protected YSoccer game;
    protected Image background;

    public GlScreen(YSoccer game) {
        this.game = game;
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
