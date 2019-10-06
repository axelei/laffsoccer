package com.ygames.ysoccer.match;

abstract class TrainingState extends SceneState {

    boolean displayControlledPlayer;

    // convenience references
    protected final Training training;
    protected final Team[] team;
    protected final Ball ball;

    TrainingState(TrainingFsm.Id id, TrainingFsm trainingFsm) {
        super(id, trainingFsm);

        this.training = trainingFsm.getTraining();
        this.team = training.team;
        this.ball = training.ball;
        this.sceneRenderer = trainingFsm.getRenderer();

        fsm.addState(this);
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type, Enum stateId) {
        return fsm.newFadedAction(type, stateId);
    }

    void quitTraining() {
        training.quit();
    }
}
