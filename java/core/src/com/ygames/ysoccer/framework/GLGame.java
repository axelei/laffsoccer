package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.ygames.ysoccer.competitions.Competition;
import com.ygames.ysoccer.gui.Gui;
import com.ygames.ysoccer.gui.WidgetColor;
import com.ygames.ysoccer.match.Tactics;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Stack;

public class GLGame extends Game {

    public static final int SUBFRAMES = 8;
    public static final int VIRTUAL_REFRESH_RATE = 64;
    public static final int SUBFRAMES_PER_SECOND = VIRTUAL_REFRESH_RATE * SUBFRAMES;
    public static final float SUBFRAME_DURATION = 1.0f / SUBFRAMES_PER_SECOND;

    public Settings settings;
    public GLGraphics glGraphics;
    public Gui gui;
    private float deltaTime;
    public InputDeviceList inputDevices;
    Mouse mouse;
    MenuInput menuInput;

    public int tacticsToEdit;
    public Tactics editedTactics;
    public Stack<Tactics> tacticsUndo;
    public boolean tacticsFlip = true;
    public Team tacticsTeam;

    public enum State {
        NONE, FRIENDLY, COMPETITION, EDIT, TRAINING
    }

    private State state;
    public Texture stateBackground;
    public WidgetColor stateColor;

    public TeamList teamList;
    public Competition competition;

    public MenuMusic menuMusic;

    @Override
    public void create() {
        settings = new Settings();
        Gdx.app.setLogLevel(Settings.logLevel);
        glGraphics = new GLGraphics();
        gui = new Gui();
        setScreenMode(settings.fullScreen);
        Assets.load(settings);

        inputDevices = new InputDeviceList();
        reloadInputDevices();

        menuInput = new MenuInput();

        mouse = new Mouse();
        setMouse();

        state = State.NONE;
        stateColor = new WidgetColor();

        teamList = new TeamList();

        menuMusic = new MenuMusic("music");

        restoreSaveGame();
    }

    private void createSaveGame() {
        if (hasCompetition()) {
            competition.save(Assets.saveGame);
        } else if (Assets.saveGame.exists()) {
            Assets.saveGame.delete();
        }
    }

    private void restoreSaveGame() {
        if (Assets.saveGame.exists()) {
            Competition competition = Competition.load(Assets.saveGame);
            setCompetition(competition);
        }
    }

    public void setScreenMode(boolean fullScreen) {
        if (fullScreen) {
            if (!Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        } else if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(gui.WIDTH, gui.HEIGHT);
        }
    }

    public void setMouse() {
        if (settings.mouseEnabled) {
            Gdx.graphics.setCursor(Assets.customCursor);
        } else {
            Gdx.graphics.setCursor(Assets.hiddenCursor);
        }
    }

    public void unsetMouse() {
        if (settings.mouseEnabled) {
            Gdx.graphics.setCursor(Assets.hiddenCursor);
        }
    }

    @Override
    public void render() {
        try {
            deltaTime += Gdx.graphics.getDeltaTime();

            int subFrames = (int) (deltaTime / SUBFRAME_DURATION);

            if (screen != null) {
                screen.render(subFrames * SUBFRAME_DURATION);
            }

            deltaTime -= subFrames * SUBFRAME_DURATION;

        } catch (Exception e) {
            e.printStackTrace();

            FileHandle file = Gdx.files.local("error.log");
            file.writeString("Exception in thread \"" + Thread.currentThread().getName() + "\" " + e.toString() + "\n", false);
            for (StackTraceElement s : e.getStackTrace()) {
                file.writeString("\tat " + s.toString() + "\n", true);
            }
            file.writeString("\n\n", true);

            Gdx.app.exit();
        }
    }

    @Override
    public void pause() {
        super.pause();
        createSaveGame();
    }

    @Override
    public void dispose() {
        super.dispose();
        glGraphics.dispose();
    }

    class MenuInput {
        // values
        protected int x;
        protected int y;
        boolean fire1;
        boolean fire2;

        // old values
        int xOld;
        int yOld;
        boolean fire1Old;
        boolean fire2Old;

        // timers
        int xTimer;
        int yTimer;
        int fire1Timer;
        int fire2Timer;
    }

    public void setState(State state, Competition.Category category) {

        this.state = state;

        switch (state) {
            case COMPETITION:
                stateBackground = new Texture("images/backgrounds/menu_competition.jpg");
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
                stateBackground = new Texture("images/backgrounds/menu_friendly.jpg");
                stateColor.set(0x2D855D, 0x3DB37D, 0x1E5027);
                break;

            case EDIT:
                stateBackground = new Texture("images/backgrounds/menu_edit.jpg");
                stateColor.set(0x89421B, 0xBB5A25, 0x3D1E0D);
                break;

            case TRAINING:
                stateBackground = new Texture("images/backgrounds/menu_training.jpg");
                stateColor.set(0x1B8A7F, 0x25BDAE, 0x115750);
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
        setState(GLGame.State.COMPETITION, competition.category);
    }

    public void clearCompetition() {
        this.competition = null;
    }

    public void reloadInputDevices() {
        inputDevices.clear();

        // keyboard
        ArrayList<KeyboardConfig> keyboardConfigs = settings.getKeyboardConfigs();
        inputDevices.add(new Keyboard(0, keyboardConfigs.get(0)));
        inputDevices.add(new Keyboard(1, keyboardConfigs.get(1)));

        // joysticks
        int port = 0;
        for (Controller controller : Controllers.getControllers()) {
            JoystickConfig joystickConfig = settings.getJoystickConfigByName(controller.getName());
            if (joystickConfig != null) {
                inputDevices.add(new Joystick(controller, joystickConfig, port));
                port++;
            }
        }
    }

    public enum LogType {
        PASSING,
        PLAYER_SELECTION,
        AI_ATTACKING,
        AI_KICKING
    }

    public static void debug(LogType type, Object Object, String message) {
        debug(type, Object.getClass().getSimpleName(), message);
    }

    public static void debug(LogType type, String tag, String message) {
        if (Settings.development && isSetLogFilter(type)) {
            Gdx.app.debug(tag, message);
        }
    }

    public static boolean isSetLogFilter(LogType type) {
        return (Settings.logFilter & (1 << type.ordinal())) != 0;
    }

    public static void toggleLogFilter(LogType type) {
        Settings.logFilter = Settings.logFilter ^ (1 << type.ordinal());
    }
}
