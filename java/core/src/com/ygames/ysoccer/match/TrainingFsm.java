package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static com.ygames.ysoccer.match.TrainingFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.TrainingFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.TrainingFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.TrainingFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.TrainingFsm.ActionType.RESTORE_FOREGROUND;

public class TrainingFsm {

    private class Action {
        ActionType type;
        Id stateId;
        int timer;

        Action(ActionType type, Id stateId) {
            this.type = type;
            this.stateId = stateId;
        }

        void update() {
            if (timer > 0) {
                timer -= 1;
            }
        }
    }

    enum ActionType {
        NONE,
        NEW_FOREGROUND,
        FADE_IN,
        FADE_OUT,
        HOLD_FOREGROUND,
        RESTORE_FOREGROUND
    }

    private Training training;

    private TrainingRenderer trainingRenderer;
    private int savedSubframe;

    private List<TrainingState> states;
    private TrainingState currentState;
    private TrainingState holdState;

    private ArrayDeque<Action> actions;
    private Action currentAction;

    TrainingKeys trainingKeys;

    enum Id {
        STATE_FREE,
        STATE_REPLAY
    }

    TrainingFsm(Training training) {
        this.training = training;
        this.trainingKeys = new TrainingKeys(training);
        trainingRenderer = new TrainingRenderer(training.game.glGraphics, training);
        states = new ArrayList<TrainingState>();

        actions = new ArrayDeque<Action>();

        states.add(new TrainingStateFree(this));
        states.add(new TrainingStateReplay(this));
    }

    public TrainingState getState() {
        return currentState;
    }

    public Training getTraining() {
        return training;
    }

    public TrainingRenderer getTrainingRenderer() {
        return trainingRenderer;
    }

    void think(float deltaTime) {
        boolean doUpdate = true;

        // fade in/out
        if (currentAction != null && currentAction.type == FADE_OUT) {
            training.game.glGraphics.light = 8 * (currentAction.timer - 1);
            doUpdate = false;
        }
        if (currentAction != null && currentAction.type == FADE_IN) {
            training.game.glGraphics.light = 255 - 8 * (currentAction.timer - 1);
            doUpdate = false;
        }

        // update current state
        if (currentState != null && doUpdate) {
            if (currentAction != null
                    && (currentAction.type == NEW_FOREGROUND || currentAction.type == RESTORE_FOREGROUND)) {
                currentState.onResume();
            }
            if (currentAction != null
                    && (currentAction.type == HOLD_FOREGROUND)) {
                holdState.onPause();
            }
            currentState.doActions(deltaTime);
            currentState.checkConditions();
        }

        // update current action
        if (currentAction != null) {
            currentAction.update();
            if (currentAction.timer == 0) {
                currentAction = null;
            }
        }

        // get new action
        if (currentAction == null) {
            if (actions.size() > 0) {
                pollAction();
            }
        }
    }

    private void pollAction() {

        currentAction = actions.pop();

        switch (currentAction.type) {

            case NONE:
                break;

            case FADE_IN:
                currentAction.timer = 32;
                break;

            case FADE_OUT:
                currentAction.timer = 32;
                break;

            case NEW_FOREGROUND:
                currentState = searchState(currentAction.stateId);
                Gdx.app.debug("NEW_FOREGROUND", currentState.getClass().getSimpleName());
                break;

            case HOLD_FOREGROUND:
                holdState = currentState;
                savedSubframe = training.subframe;
                currentState = searchState(currentAction.stateId);
                Gdx.app.debug("HOLD_FOREGROUND", currentState.getClass().getSimpleName());
                break;

            case RESTORE_FOREGROUND:
                training.subframe = savedSubframe;
                currentState = holdState;
                holdState = null;
                break;

        }
    }

    private TrainingState searchState(Id id) {
        for (int i = 0; i < states.size(); i++) {
            TrainingState s = states.get(i);
            if (s.checkId(id)) {
                s.entryActions();
                return s;
            }
        }
        return null;
    }

    public void pushAction(ActionType type) {
        pushAction(type, null);
    }

    public void pushAction(ActionType type, Id stateId) {
        actions.offer(new Action(type, stateId));
    }
}
