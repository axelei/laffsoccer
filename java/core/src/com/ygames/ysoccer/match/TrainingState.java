package com.ygames.ysoccer.match;

abstract class TrainingState extends SceneState {

    boolean displayControlledPlayer;

    // convenience references
    final Training training;
    final Team[] team;
    final Ball ball;

    TrainingState(TrainingFsm trainingFsm) {
        super(trainingFsm);

        this.training = trainingFsm.getTraining();
        this.team = training.team;
        this.ball = training.ball;
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type, int stateId) {
        return fsm.newFadedAction(type, stateId);
    }

    void quitTraining() {
        training.quit();
    }
}
