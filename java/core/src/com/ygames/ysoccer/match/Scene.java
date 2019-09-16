package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

abstract class Scene {

    protected GLGame game;
    protected SceneFsm fsm;
    protected int subframe;
    protected SceneSettings settings;

    SceneRenderer getRenderer() {
        return fsm.getSceneRenderer();
    }

    abstract public void start();

    public void update(float deltaTime) {
        fsm.think(deltaTime);
    }

    public void nextSubframe() {
        subframe = (subframe + 1) % Const.REPLAY_SUBFRAMES;
    }

    public void render() {
        fsm.getSceneRenderer().render();
    }

    public void resize(int width, int height) {
        fsm.getSceneRenderer().resize(width, height, settings.zoom);
    }

    abstract void quit();
}
