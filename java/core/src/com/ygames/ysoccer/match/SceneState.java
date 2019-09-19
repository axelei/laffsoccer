package com.ygames.ysoccer.match;

abstract class SceneState {

    protected int timer;
    protected final Enum id;
    protected SceneFsm fsm;
    protected SceneRenderer sceneRenderer;

    SceneState(Enum id, SceneFsm fsm) {
        this.id = id;
        this.fsm = fsm;
        this.sceneRenderer = fsm.getSceneRenderer();
    }

    void entryActions() {
        timer = 0;
    }

    void doActions(float deltaTime) {
        timer += 1;
        fsm.getHotKeys().update();
    }

    void onResume() {
    }

    void onPause() {
    }

    void checkConditions() {
    }

    boolean checkId(Enum id) {
        return (this.id == id);
    }

    void render() {
    }
}
