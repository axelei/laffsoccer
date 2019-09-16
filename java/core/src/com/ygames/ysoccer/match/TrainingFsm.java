package com.ygames.ysoccer.match;

public class TrainingFsm extends SceneFsm {

    private Training training;

    private TrainingRenderer trainingRenderer;

    enum Id {
        STATE_FREE,
        STATE_REPLAY
    }

    TrainingFsm(Training training) {
        super(training.game);
        this.training = training;
        this.hotKeys = new TrainingHotKeys(training);
        trainingRenderer = new TrainingRenderer(training.game.glGraphics, training);

        new TrainingStateFree(this);
        new TrainingStateReplay(this);
    }

    public TrainingState getState() {
        return (TrainingState) super.getState();
    }

    public Training getTraining() {
        return training;
    }

    public TrainingRenderer getTrainingRenderer() {
        return trainingRenderer;
    }
}
