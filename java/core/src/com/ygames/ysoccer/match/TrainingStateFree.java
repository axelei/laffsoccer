package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.GLGame;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.ygames.ysoccer.match.ActionCamera.CF_BALL;
import static com.ygames.ysoccer.match.ActionCamera.CS_NORMAL;

class TrainingStateFree extends TrainingState {

    TrainingStateFree(TrainingFsm fsm) {
        super(fsm);
        id = TrainingFsm.STATE_FREE;
    }

    @Override
    void entryActions() {
        super.entryActions();

        training.setIntroPositions();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            training.updatePlayers();
            training.updateBall();

            for (Player ply : training.team.lineup) {
                if (ply.inputDevice == ply.ai && ply != training.team.lineup.get(0)) {
                    ply.watchBall();
                }
            }

            training.nextSubframe();

            training.save();

            trainingRenderer.updateCameraX(CF_BALL, CS_NORMAL);
            trainingRenderer.updateCameraY(CF_BALL, CS_NORMAL, 0, false);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (Gdx.input.isKeyPressed(ESCAPE)) {
            quitTraining();
            return;
        }
    }
}
