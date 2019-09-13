package com.ygames.ysoccer.screens;

import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.annotation.HiddenCommand;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.framework.Settings;
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

        if (Settings.development) {
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

        if (Settings.development) {
            console.draw();
            paused = console.isVisible();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        training.fsm.getTrainingRenderer().resize(width, height, training.settings.zoom);

        if (Settings.development) {
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

    class ConsoleCommandExecutor extends CommandExecutor {

        @HiddenCommand
        public void gravity() {
            console.log("gravity " + Const.GRAVITY);
        }
        public void gravity(float f) {
            Const.GRAVITY = f;
        }

        @HiddenCommand
        public void airFriction() {
            console.log("airFriction " + Const.AIR_FRICTION);
        }
        public void airFriction(float f) {
            Const.AIR_FRICTION = f;
        }

        @HiddenCommand
        public void spinFactor() {
            console.log("spinFactor " + Const.SPIN_FACTOR);
        }
        public void spinFactor(float f) {
            Const.SPIN_FACTOR = f;
        }

        @HiddenCommand
        public void spinDampening() {
            console.log("spinDampening " + Const.SPIN_DAMPENING);
        }
        public void spinDampening(float f) {
            Const.SPIN_DAMPENING = f;
        }

        @HiddenCommand
        public void bounce() {
            console.log("bounce " + Const.BOUNCE);
        }
        public void bounce(float f) {
            Const.BOUNCE = f;
        }
    }
}
