package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Font;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.framework.Assets.gettext;
import static com.ygames.ysoccer.match.Renderer.guiAlpha;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.RESTORE_FOREGROUND;
import static com.ygames.ysoccer.match.TrainingFsm.Id.STATE_REPLAY;

class TrainingStateReplay extends TrainingState {

    private int subframe0;
    private boolean paused;
    private boolean slowMotion;
    private boolean keySlow;
    private boolean keyPause;
    private int position;
    private InputDevice inputDevice;

    TrainingStateReplay(TrainingFsm fsm) {
        super(STATE_REPLAY, fsm);

        displayControlledPlayer = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        subframe0 = training.subframe;

        paused = false;
        slowMotion = false;

        // control keys
        keySlow = Gdx.input.isKeyPressed(Input.Keys.R);
        keyPause = Gdx.input.isKeyPressed(Input.Keys.P);

        // position of current frame in the replay vector
        position = 0;

        inputDevice = null;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // toggle pause
        if (Gdx.input.isKeyPressed(Input.Keys.P) && !keyPause) {
            paused = !paused;
        }
        keyPause = Gdx.input.isKeyPressed(Input.Keys.P);

        // toggle slow-motion
        if (Gdx.input.isKeyPressed(Input.Keys.R) && !keySlow) {
            slowMotion = !slowMotion;
        }
        keySlow = Gdx.input.isKeyPressed(Input.Keys.R);

        // set/unset controlling device
        if (inputDevice == null) {
            for (InputDevice d : training.game.inputDevices) {
                if (d.fire2Down()) {
                    inputDevice = d;
                }
            }
        } else {
            if (inputDevice.fire2Down()) {
                inputDevice = null;
            }
        }

        // set speed
        int speed;
        if (inputDevice != null) {
            speed = 12 * inputDevice.x1 - 2 * inputDevice.y1 + 8 * Math.abs(inputDevice.x1) * inputDevice.y1;
        } else if (slowMotion) {
            speed = GLGame.SUBFRAMES / 2;
        } else {
            speed = GLGame.SUBFRAMES;
        }

        // set position
        if (!paused) {
            position = Emath.slide(position, 1, Const.REPLAY_SUBFRAMES, speed);

            training.subframe = (subframe0 + position) % Const.REPLAY_SUBFRAMES;
        }
    }

    @Override
    void checkConditions() {

        // quit on ESC
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quit();
            return;
        }

        // quit on fire button
        for (InputDevice d : training.game.inputDevices) {
            if (d.fire1Down()) {
                quit();
                return;
            }
        }

        // quit on last position
        if ((position == Const.REPLAY_SUBFRAMES) && (inputDevice == null)) {
            quit();
            return;
        }
    }

    @Override
    void render() {
        super.render();

        int f = Math.round(1f * training.subframe / GLGame.SUBFRAMES) % 32;
        if (f < 16) {
            Assets.font10.draw(sceneRenderer.batch, gettext("REPLAY"), 30, 22, Font.Align.LEFT);
        }

        sceneRenderer.batch.end();
        float a = position * 360f / Const.REPLAY_SUBFRAMES;
        GLShapeRenderer shapeRenderer = sceneRenderer.shapeRenderer;
        shapeRenderer.setProjectionMatrix(sceneRenderer.camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0x242424, guiAlpha);
        shapeRenderer.arc(20, 32, 6, 270 + a, 360 - a);
        shapeRenderer.setColor(0xFF0000, guiAlpha);
        shapeRenderer.arc(18, 30, 6, 270 + a, 360 - a);
        shapeRenderer.end();
        sceneRenderer.batch.begin();

        if (inputDevice != null) {
            int frameX = 1 + inputDevice.x1;
            int frameY = 1 + inputDevice.y1;
            sceneRenderer.batch.draw(Assets.replaySpeed[frameX][frameY], sceneRenderer.guiWidth - 50, sceneRenderer.guiHeight - 50);
        }

        if (paused) {
            Assets.font10.draw(sceneRenderer.batch, gettext("PAUSE"), sceneRenderer.guiWidth / 2, 22, Font.Align.CENTER);
        }
    }

    private void quit() {
        // if final frame is different from starting frame then fade out
        if (position != Const.REPLAY_SUBFRAMES) {
            fsm.pushAction(FADE_OUT);
        }

        fsm.pushAction(RESTORE_FOREGROUND);

        // if final frame is different from starting frame then fade in
        if (position != Const.REPLAY_SUBFRAMES) {
            fsm.pushAction(FADE_IN);
        }
    }
}
