package com.ygames.ysoccer.match;

public class TrainingFsm extends SceneFsm {

    enum Id {
        STATE_FREE,
        STATE_REPLAY
    }

    TrainingFsm(Training training) {
        super(training);

        setHotKeys(new TrainingHotKeys(training));
        setSceneRenderer(new TrainingRenderer(training.game.glGraphics, training));

        new TrainingStateFree(this);
        new TrainingStateReplay(this);
    }

    public TrainingState getState() {
        return (TrainingState) super.getState();
    }

    public Training getTraining() {
        return (Training) getScene();
    }

    TrainingRenderer getRenderer() {
        return (TrainingRenderer) getSceneRenderer();
    }
}
