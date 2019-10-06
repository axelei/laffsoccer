package com.ygames.ysoccer.match;

abstract class SceneState {

    private final Enum id;
    final SceneFsm fsm;
    protected SceneRenderer sceneRenderer;
    protected int timer;

    SceneState(Enum id, SceneFsm fsm) {
        this.id = id;
        this.fsm = fsm;
        this.sceneRenderer = fsm.getSceneRenderer();
    }

    void entryActions() {
        timer = 0;
    }

    void exitActions() {
    }

    void doActions(float deltaTime) {
        timer += 1;
        fsm.getHotKeys().update();
    }

    SceneFsm.Action[] newAction(SceneFsm.ActionType type, Enum stateId) {
        return fsm.newAction(type, stateId);
    }

    SceneFsm.Action[] newAction(SceneFsm.ActionType type) {
        return newAction(type, null);
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type, Enum stateId) {
        return fsm.newFadedAction(type, stateId);
    }

    SceneFsm.Action[] newFadedAction(SceneFsm.ActionType type) {
        return newFadedAction(type, null);
    }

    void onResume() {
    }

    void onPause() {
    }

    abstract SceneFsm.Action[] checkConditions();

    boolean checkId(Enum id) {
        return (this.id == id);
    }

    void render() {
    }
}
