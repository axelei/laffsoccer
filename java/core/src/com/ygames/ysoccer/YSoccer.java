package com.ygames.ysoccer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GlGraphics;
import com.ygames.ysoccer.framework.Keyboard;
import com.ygames.ysoccer.framework.Settings;

public class YSoccer extends Game {

    public Settings settings;
    public GlGraphics glGraphics;
    Keyboard keyboard;

    @Override
    public void create() {
        settings = new Settings();
        glGraphics = new GlGraphics();
        Assets.load();
        this.setScreen(new MenuMain(this));
        keyboard = new Keyboard();
        keyboard.setKeys(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S, Input.Keys.COMMA, Input.Keys.PERIOD);
    }

    @Override
    public void render() {
        keyboard.read();
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        glGraphics.dispose();
    }
}
