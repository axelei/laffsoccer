package com.ygames.ysoccer.match;

class TrainingState {

    protected int timer;
    protected int id;
    protected TrainingFsm fsm;

    protected Training training;
    TrainingRenderer trainingRenderer;

    TrainingState(TrainingFsm fsm) {
        this.fsm = fsm;
        this.training = fsm.getTraining();
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

    void quitTraining() {
        training.quit();
    }
}
