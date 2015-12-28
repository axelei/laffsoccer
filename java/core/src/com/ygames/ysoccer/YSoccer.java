package com.ygames.ysoccer;

import com.badlogic.gdx.Game;
import com.ygames.ysoccer.framework.GlGraphics;

public class YSoccer extends Game {

    public GlGraphics glGraphics;

    @Override
    public void create() {
        glGraphics = new GlGraphics();
        Assets.load();
        this.setScreen(new MenuMain(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        glGraphics.dispose();
    }
}
