package com.ygames.ysoccer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class YSoccer extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        this.setScreen(new MenuMain(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }
}
