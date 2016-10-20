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

    private enum ActionType {
        NONE, NEW_FOREGROUND, FADE_IN, FADE_OUT, HOLD_FOREGROUND, RESTORE_FOREGROUND
    }

    private MatchCore match;

    private List<MatchState> states;

    private ArrayDeque<Action> actions;
    private Action currentAction;

    static final int STATE_INTRO = 1;

    private MatchStateIntro stateIntro;

    MatchFsm(MatchCore match) {
        this.match = match;
        states = new ArrayList<MatchState>();
        actions = new ArrayDeque<Action>();

        states.add(stateIntro = new MatchStateIntro(match));
    }
}
