package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.gui.WidgetColor;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.List;

public class GlGame extends Game {
    public Settings settings;
    public GlGraphics glGraphics;
    public List<InputDevice> inputDevices;
    public MenuInput menuInput;

    public enum State {
        NONE, FRIENDLY, COMPETITION, EDIT, TRAINING
    }

    public State state;
    public Image stateBackground;
    public WidgetColor stateColor;

    public ArrayList<Team> teamList;
    public Competition competition;

    @Override
    public void create() {
        settings = new Settings();
        glGraphics = new GlGraphics();
        Assets.load(settings);

        inputDevices = new ArrayList<InputDevice>();
        menuInput = new MenuInput();

        // Keyboard 1
        Keyboard keyboard = new Keyboard();
        keyboard.setKeys(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.CONTROL_RIGHT, Input.Keys.SHIFT_RIGHT);
        inputDevices.add(keyboard);

        // Keyboard 2
        keyboard = new Keyboard();
        keyboard.setKeys(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S, Input.Keys.CONTROL_LEFT, Input.Keys.SHIFT_LEFT);
        inputDevices.add(keyboard);

        state = State.NONE;
        stateColor = new WidgetColor();

        teamList = new ArrayList<Team>();
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
