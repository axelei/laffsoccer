package com.ygames.ysoccer.screens;

import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Training;
import com.ygames.ysoccer.match.TrainingConsoleCommandExecutor;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class TrainingScreen extends GLScreen {

    private final Training training;
    private boolean started;
    private boolean paused;
    private boolean ended;

    private Console console;

    TrainingScreen(GLGame game, Training training) {
        super(game);

        this.training = training;
        usesMouse = false;

        started = false;
        paused = false;
        ended = false;
        game.glGraphics.light = 0;

        training.listener = new Training.TrainingListener() {
            public void quitTraining() {
                quit();
            }
        };

        if (Settings.development) {
            console = new GUIConsole();
            console.setSizePercent(25, 100);
            console.setPositionPercent(0, 0);
            console.setHoverAlpha(0.9f);
            console.setNoHoverAlpha(0.9f);
            console.setCommandExecutor(new TrainingConsoleCommandExecutor(training));
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        if (!started) {
            training.start();
            started = true;
        }

        if (!paused) {
            training.update(deltaTime);
        }

        if (!ended) {
            training.render();
        }

        if (Settings.development) {
            console.draw();
            paused = console.isVisible();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        training.resize(width, height);

        if (Settings.development) {
            console.refresh();
        }
    }

    private void quit() {
        ended = true;
        game.enableMouse();

        for (int t = HOME; t <= AWAY; t++) {
            int len = training.team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = training.team[t].lineup.get(i);
                if (player.role == Player.Role.GOALKEEPER) {
                    Assets.unloadKeeper(player);
                } else {
                    Assets.unloadPlayer(player);
                }
                Assets.unloadHair(player);
            }
            training.team[t].lineup.clear();
        }

        game.setScreen(new SetupTraining(game));
    }
}
