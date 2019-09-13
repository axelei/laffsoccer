package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.TrainingFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.TrainingFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.TrainingFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.TrainingFsm.STATE_REPLAY;

class TrainingState {

    protected int timer;
    protected int id;
    protected TrainingFsm fsm;

    boolean displayControlledPlayer;

    protected Training training;
    protected final Team team;
    protected final Ball ball;
    TrainingRenderer trainingRenderer;

    TrainingState(TrainingFsm fsm) {
        this.fsm = fsm;
        this.training = fsm.getTraining();
        this.team = training.team;
        this.ball = training.ball;
        this.trainingRenderer = fsm.getTrainingRenderer();
    }

    void entryActions() {
        timer = 0;
    }

    void onResume() {
    }

    void onPause() {
    }

    void doActions(float deltaTime) {
        timer += 1;
        fsm.trainingKeys.update();
    }

    void checkConditions() {
    }

    boolean checkId(int id) {
        return (this.id == id);
    }

    void render() {
    }

    void replay() {
        fsm.pushAction(FADE_OUT);
        fsm.pushAction(HOLD_FOREGROUND, STATE_REPLAY);
        fsm.pushAction(FADE_IN);
    }

    void quitTraining() {
        training.quit();
    }
}
