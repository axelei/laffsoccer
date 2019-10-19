package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.GLGame;

abstract class Scene {

    protected GLGame game;
    protected SceneFsm fsm;
    protected int subframe;
    protected SceneSettings settings;

    Vector2 pointOfInterest;

    SceneRenderer getRenderer() {
        return fsm.getSceneRenderer();
    }

    public void start() {
        fsm.start();
    }

    public void update(float deltaTime) {
        fsm.think(deltaTime);
    }

    public void nextSubframe() {
        subframe = (subframe + 1) % Const.REPLAY_SUBFRAMES;
    }

    public void render() {
        game.glGraphics.light = fsm.getSceneRenderer().light;
        fsm.getSceneRenderer().render();
    }

    public void resize(int width, int height) {
        fsm.getSceneRenderer().resize(width, height, settings.zoom);
    }

    void setPointOfInterest(float x, float y) {
        pointOfInterest.set(x, y);
    }

    void setPointOfInterest(Vector2 v) {
        pointOfInterest.set(v);
    }

    abstract Player getNearestOfAll();

    public void setBallOwner(Player player) {
        setBallOwner(player, true);
    }

    abstract public void setBallOwner(Player player, boolean updateGoalOwner);

    abstract void quit();
}
