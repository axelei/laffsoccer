package com.ygames.ysoccer.screens;

import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.Const;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Training;

class TrainingScreen extends GLScreen {

    private Training training;
    private boolean started;
    private boolean paused;
    private boolean ended;

    private Console console;

    TrainingScreen(GLGame game, Training training) {
        super(game);

        this.training = training;

        started = false;
        paused = false;
        ended = false;
        game.glGraphics.light = 0;

        training.listener = new Training.TrainingListener() {
            public void quitTraining() {
                quit();
            }
        };

        if (game.settings.development) {
            console = new GUIConsole();
            console.setSizePercent(25, 100);
            console.setPositionPercent(0, 0);
            console.setHoverAlpha(0.9f);
            console.setNoHoverAlpha(0.9f);
            console.setCommandExecutor(new ConsoleCommandExecutor());
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
            training.fsm.getTrainingRenderer().render();
        }

        if (game.settings.development) {
            console.draw();
            paused = console.isVisible();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        training.fsm.getTrainingRenderer().resize(width, height, training.settings.zoom);

        if (game.settings.development) {
            console.refresh();
        }
    }

    private void quit() {
        ended = true;
        game.setMouse();

        int len = training.team.lineup.size();
        for (int i = 0; i < len; i++) {
            Player player = training.team.lineup.get(i);
            if (player.role == Player.Role.GOALKEEPER) {
                Assets.unloadKeeper(player);
            } else {
                Assets.unloadPlayer(player);
            }
            Assets.unloadHair(player);
        }
        training.team.lineup.clear();

        game.setScreen(new SetupTraining(game));
    }

    public class ConsoleCommandExecutor extends CommandExecutor {
        public void setGravity(float f) {
            Const.GRAVITY = f;
        }

        public void showGravity() {
            console.log("Gravity " + Const.GRAVITY);
        }
    }
}
