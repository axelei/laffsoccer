package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

abstract class Scene {

    protected GLGame game;

    protected SceneFsm fsm;

    protected int subframe;

    public void update(float deltaTime) {
        fsm.think(deltaTime);
    }

    public void nextSubframe() {
        subframe = (subframe + 1) % Const.REPLAY_SUBFRAMES;
    }

    abstract public void start();

    abstract void quit();
}
