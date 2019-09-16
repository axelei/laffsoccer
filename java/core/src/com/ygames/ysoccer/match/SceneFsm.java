package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import java.util.ArrayDeque;

class SceneFsm {

    GLGame game;

    ArrayDeque<Action> actions;
    Action currentAction;

    SceneFsm(GLGame game) {
        this.game = game;
        actions = new ArrayDeque<>();
    }

    enum ActionType {
        NONE,
        NEW_FOREGROUND,
        FADE_IN,
        FADE_OUT,
        HOLD_FOREGROUND,
        RESTORE_FOREGROUND
    }

    class Action {
        ActionType type;
        Enum stateId;
        int timer;

        Action(ActionType type, Enum stateId) {
            this.type = type;
            this.stateId = stateId;
        }

        void update() {
            if (timer > 0) {
                timer -= 1;
            }
        }
    }

    public void pushAction(ActionType type) {
        pushAction(type, null);
    }

    public void pushAction(ActionType type, Enum stateId) {
        actions.offer(new Action(type, stateId));
    }

}
