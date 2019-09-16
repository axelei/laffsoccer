package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.GLGame;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;

abstract class SceneFsm {

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

    GLGame game;

    private List<SceneState> states;
    private SceneState currentState;
    private SceneState holdState;

    private ArrayDeque<Action> actions;
    private Action currentAction;

    private SceneRenderer sceneRenderer;

    private SceneHotKeys hotKeys;

    SceneFsm(GLGame game) {
        this.game = game;
        states = new ArrayList<>();
        actions = new ArrayDeque<>();
    }

    public SceneState getState() {
        return currentState;
    }

    void addState(SceneState state) {
        states.add(state);
    }

    SceneState getHoldState() {
        return holdState;
    }

    SceneHotKeys getHotKeys() {
        return hotKeys;
    }

    void setHotKeys(SceneHotKeys hotKeys) {
        this.hotKeys = hotKeys;
    }

    SceneRenderer getSceneRenderer() {
        return sceneRenderer;
    }

    void setSceneRenderer(SceneRenderer sceneRenderer) {
        this.sceneRenderer = sceneRenderer;
    }

    void think(float deltaTime) {
        boolean doUpdate = true;

        // fade in/out
        if (currentAction != null && currentAction.type == FADE_OUT) {
            game.glGraphics.light = 8 * (currentAction.timer - 1);
            doUpdate = false;
        }
        if (currentAction != null && currentAction.type == FADE_IN) {
            game.glGraphics.light = 255 - 8 * (currentAction.timer - 1);
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
                currentState = searchState(currentAction.stateId);
                Gdx.app.debug("HOLD_FOREGROUND", currentState.getClass().getSimpleName());
                break;

            case RESTORE_FOREGROUND:
                currentState = holdState;
                holdState = null;
                break;

        }
    }

    private SceneState searchState(Enum id) {
        for (int i = 0; i < states.size(); i++) {
            SceneState s = states.get(i);
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

    public void pushAction(ActionType type, Enum stateId) {
        actions.offer(new Action(type, stateId));
    }

}
