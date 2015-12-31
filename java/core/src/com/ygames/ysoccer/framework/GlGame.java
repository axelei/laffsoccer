package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;

import java.util.ArrayList;
import java.util.List;

public class GlGame extends Game {
    public Settings settings;
    public GlGraphics glGraphics;
    Keyboard keyboard;
    public List<InputDevice> inputDevices;
    public MenuInput menuInput;

    @Override
    public void create() {
        settings = new Settings();
        glGraphics = new GlGraphics();
        Assets.load(settings);

        inputDevices = new ArrayList<InputDevice>();
        menuInput = new MenuInput();
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

    public class MenuInput {
        // values
        protected int x;
        protected int y;
        protected boolean fire1;
        protected boolean fire2;

        // old values
        protected int xOld;
        protected int yOld;
        protected boolean fire1Old;
        protected boolean fire2Old;

        // timers
        protected int xTimer;
        protected int yTimer;
        protected int fire1Timer;
        protected int fire2Timer;
    }
}
