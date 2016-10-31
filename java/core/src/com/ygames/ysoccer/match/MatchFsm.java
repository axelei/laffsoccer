package com.ygames.ysoccer.match;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

class MatchFsm {

    private class Action {
        ActionType type;
        int stateId;
        int timer;

        Action(ActionType type, int state) {
            this.type = type;
            this.stateId = state;
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

    private MatchCore match;
    private int savedSubframe;

    private List<MatchState> states;
    private MatchState currentState;
    private MatchState holdState;

    private ArrayDeque<Action> actions;
    private Action currentAction;

    static final int STATE_INTRO = 1;
    static final int STATE_STARTING_POSITIONS = 2;
    static final int STATE_KICK_OFF = 3;

    MatchFsm(MatchCore match) {
        this.match = match;
        states = new ArrayList<MatchState>();
        actions = new ArrayDeque<Action>();

        states.add(new MatchStateIntro(match));
        states.add(new MatchStateStartingPositions(match));
        states.add(new MatchStateKickOff(match));
    }

    void think(float deltaTime) {

        boolean doUpdate = true;

        // fade in/out
        if (currentAction != null && currentAction.type == ActionType.FADE_OUT) {
            // TODO
            // match.renderer.glGraphics.light = 8 * (currentAction.timer - 1);
            doUpdate = false;
        }
        if (currentAction != null && currentAction.type == ActionType.FADE_IN) {
            // TODO
            // match.renderer.glGraphics.light = 255 - 8 * (currentAction.timer - 1);
            doUpdate = false;
        }

        // update current state
        if (currentState != null && doUpdate) {
            if (currentAction != null
                    && (currentAction.type == ActionType.NEW_FOREGROUND || currentAction.type == ActionType.RESTORE_FOREGROUND)) {
                currentState.onResume();
            }
            if (currentAction != null
                    && (currentAction.type == ActionType.HOLD_FOREGROUND)) {
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
                break;

            case HOLD_FOREGROUND:
                holdState = currentState;
                savedSubframe = match.subframe;
                currentState = searchState(currentAction.stateId);
                break;

            case RESTORE_FOREGROUND:
                match.subframe = savedSubframe;
                currentState = holdState;
                holdState = null;
                break;

        }
    }

    private MatchState searchState(int id) {
        for (int i = 0; i < states.size(); i++) {
            MatchState s = states.get(i);
            if (s.checkId(id)) {
                s.entryActions();
                return s;
            }
        }
        return null;
    }

    public void pushAction(ActionType type) {
        pushAction(type, 0);
    }

    public void pushAction(ActionType type, int state) {
        actions.offer(new Action(type, state));
    }
}
