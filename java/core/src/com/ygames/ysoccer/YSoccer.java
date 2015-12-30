package com.ygames.ysoccer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GlGraphics;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.framework.Keyboard;
import com.ygames.ysoccer.framework.Settings;

import java.util.ArrayList;
import java.util.List;

public class YSoccer extends Game {

    public Settings settings;
    public GlGraphics glGraphics;
    Keyboard keyboard;
    public List<InputDevice> inputDevices;


    @Override
    public void create() {
        settings = new Settings();
        glGraphics = new GlGraphics();
        Assets.load();
        this.setScreen(new MenuMain(this));

        inputDevices = new ArrayList<InputDevice>();
        keyboard = new Keyboard();
        keyboard.setKeys(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S, Input.Keys.COMMA, Input.Keys.PERIOD);
        inputDevices.add(keyboard);
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
