package com.ygames.ysoccer.screens;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Training;

class TrainingScreen extends GLScreen {

    private Training training;
    private boolean started;
    private boolean paused;
    private boolean ended;

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
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        training.fsm.getTrainingRenderer().resize(width, height, game.settings);
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
}
