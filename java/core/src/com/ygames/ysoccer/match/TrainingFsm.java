package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

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

    @Override
    public void start() {
        pushAction(NEW_FOREGROUND, Id.STATE_FREE);
        pushAction(FADE_IN);
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
