package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.TrainingFsm.Id.STATE_REPLAY;

class TrainingState extends SceneState {

    boolean displayControlledPlayer;

    // convenience references
    protected Training training;
    protected final Team team;
    protected final Ball ball;
    TrainingRenderer trainingRenderer;

    TrainingState(TrainingFsm.Id id, TrainingFsm fsm) {
        super(id, fsm);

        this.training = fsm.getTraining();
        this.team = training.team;
        this.ball = training.ball;
        this.trainingRenderer = fsm.getTrainingRenderer();

        fsm.addState(this);
    }

    TrainingFsm getFsm() {
        return (TrainingFsm) fsm;
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
