package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

public class TrainingFsm extends SceneFsm {

    private static int STATE_FREE;
    static int STATE_REPLAY;

    TrainingFsm(Training training) {
        super(training);

        setHotKeys(new TrainingHotKeys(training));
        setSceneRenderer(new TrainingRenderer(training.game.glGraphics, training));

        STATE_FREE = addState(new TrainingStateFree(this));
        STATE_REPLAY = addState(new TrainingStateReplay(this));
    }

    @Override
    public void start() {
        pushAction(NEW_FOREGROUND, STATE_FREE);
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
