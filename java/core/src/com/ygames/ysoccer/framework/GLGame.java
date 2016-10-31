package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.gui.WidgetColor;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.List;

public class GLGame extends Game {

    public static final int SUBFRAMES = 8;
    public static final int VIRTUAL_REFRESH_RATE = 64;
    public static final int SUBFRAMES_PER_SECOND = VIRTUAL_REFRESH_RATE * SUBFRAMES;
    public static final float SUBFRAME_DURATION = 1.0f / SUBFRAMES_PER_SECOND;
    public static final int[] gameLengths = {3, 5, 7, 10};

    public Settings settings;
    public GlGraphics glGraphics;
    private float deltaTime;
    public InputDeviceList inputDevices;
    public Mouse mouse;
    public MenuInput menuInput;
    public Player tmpPlayer;

    public enum State {
        NONE, FRIENDLY, COMPETITION, EDIT, TRAINING
    }

    private State state;
    public Image stateBackground;
    public WidgetColor stateColor;

    public ArrayList<Team> teamList;
    public Competition competition;

    @Override
    public void create() {
        settings = new Settings();
        glGraphics = new GlGraphics();
        Assets.load(settings);

        inputDevices = new InputDeviceList();
        menuInput = new MenuInput();

        // Keyboard 1
        Keyboard keyboard = new Keyboard(0);
        keyboard.setKeys(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.CONTROL_RIGHT, Input.Keys.SHIFT_RIGHT);
        inputDevices.add(keyboard);

        // Keyboard 2
        keyboard = new Keyboard(1);
        keyboard.setKeys(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S, Input.Keys.CONTROL_LEFT, Input.Keys.SHIFT_LEFT);
        inputDevices.add(keyboard);

        Gdx.graphics.setCursor(Assets.customCursor);
        mouse = new Mouse();

        state = State.NONE;
        stateColor = new WidgetColor();

        teamList = new ArrayList<Team>();
    }

    @Override
    public void render() {

        deltaTime += Gdx.graphics.getDeltaTime();

        int subFrames = (int) (deltaTime / SUBFRAME_DURATION);

        if (screen != null) {
            screen.render(subFrames * SUBFRAME_DURATION);
        }

        deltaTime -= subFrames * SUBFRAME_DURATION;
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

    public void setState(State state, Competition.Category category) {
        this.state = state;
        switch (state) {
            case COMPETITION:
                stateBackground = new Image("images/backgrounds/menu_competition.jpg");
                switch (category) {
                    case DIY_COMPETITION:
                        stateColor.set(0x376E2F, 0x4E983F, 0x214014);
                        break;
                    case PRESET_COMPETITION:
                        stateColor.set(0x415600, 0x5E7D00, 0x243000);
                        break;
                }
                break;
            case FRIENDLY:
                stateBackground = new Image("images/backgrounds/menu_friendly.jpg");
                stateColor.set(0x2D855D, 0x3DB37D, 0x1E5027);
                break;
            case EDIT:
                stateBackground = new Image("images/backgrounds/menu_edit.jpg");
                stateColor.set(0x89421B, 0xBB5A25, 0x3D1E0D);
                break;
        }
    }

    public State getState() {
        return state;
    }

    public boolean hasCompetition() {
        return competition != null;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public void clearCompetition() {
        this.competition = null;
    }
}
