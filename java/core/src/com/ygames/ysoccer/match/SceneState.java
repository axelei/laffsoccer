package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

abstract class SceneState {

    private int id;
    final SceneFsm fsm;
    SceneRenderer sceneRenderer;
    int timer;

    SceneState(SceneFsm fsm) {
        this.fsm = fsm;
        this.sceneRenderer = fsm.getSceneRenderer();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    void entryActions() {
        timer = 0;
    }

    void exitActions() {
    }

    void doActions(float deltaTime) {
        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {
            timer += 1;

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
        fsm.getHotKeys().update();
    }

    SceneFsm.Action[] newAction(SceneFsm.ActionType type, int stateId) {
        return fsm.newAction(type, stateId);
    }

    SceneFsm.Action[] newAction(SceneFsm.ActionType type) {
        return newAction(type, -1);
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type, int stateId) {
        return fsm.newFadedAction(type, stateId);
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type) {
        return newFadedAction(type, -1);
    }

    void onResume() {
    }

    void onPause() {
    }

    abstract SceneFsm.Action[] checkConditions();

    boolean checkId(int id) {
        return (this.id == id);
    }

    void render() {
    }
}
