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
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_END;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HIGHLIGHTS;
import static com.ygames.ysoccer.match.Renderer.guiAlpha;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHighlights extends MatchState {

    private int subframe0;
    private boolean paused;
    private boolean slowMotion;
    private boolean keySlow;
    private boolean keyPause;
    private int position;
    private InputDevice inputDevice;
    int speed;

    MatchStateHighlights(MatchFsm fsm) {
        super(STATE_HIGHLIGHTS, fsm);

        displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        // store initial frame
        subframe0 = match.subframe;

        paused = false;
        slowMotion = false;

        // control keys
        keySlow = Gdx.input.isKeyPressed(Input.Keys.R);
        keyPause = Gdx.input.isKeyPressed(Input.Keys.P);

        // position of current frame in the highlights vector
        position = 0;

        inputDevice = null;

        match.recorder.loadHighlight(matchRenderer);
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
            for (InputDevice d : match.game.inputDevices) {
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
            speed = 12 * inputDevice.x1 - 4 * inputDevice.y1 + 8 * Math.abs(inputDevice.x1) * inputDevice.y1;
        } else if (slowMotion) {
            speed = GLGame.SUBFRAMES / 2;
        } else {
            speed = GLGame.SUBFRAMES;
        }

        // set position
        if (!paused) {
            position = Emath.slide(position, GLGame.SUBFRAMES / 2, Const.REPLAY_SUBFRAMES, speed);

            match.subframe = (subframe0 + position) % Const.REPLAY_SUBFRAMES;
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
        for (InputDevice d : match.game.inputDevices) {
            if (d.fire1Down()) {
                quit();
                return;
            }
        }

        // quit on finish
        if (position == Const.REPLAY_SUBFRAMES) {
            match.recorder.nextHighlight();
            if (match.recorder.hasEnded()) {
                quit();
                return;
            } else {
                fsm.pushAction(FADE_OUT);
                fsm.pushAction(NEW_FOREGROUND, STATE_HIGHLIGHTS);
                fsm.pushAction(FADE_IN);
                return;
            }
        }
    }

    @Override
    void render() {
        super.render();

        int f = Math.round(1f * match.subframe / GLGame.SUBFRAMES) % 32;
        if (f < 16) {
            Assets.font10.draw(matchRenderer.batch, (match.recorder.getCurrent() + 1) + "/" + match.recorder.getRecorded(), 30, 22, Font.Align.LEFT);
        }

        matchRenderer.batch.end();
        float a = position * 360f / Const.REPLAY_SUBFRAMES;
        GLShapeRenderer shapeRenderer = matchRenderer.shapeRenderer;
        shapeRenderer.setProjectionMatrix(matchRenderer.camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0x242424, guiAlpha);
        shapeRenderer.arc(20, 32, 6, 270 + a, 360 - a);
        shapeRenderer.setColor(0xFF0000, guiAlpha);
        shapeRenderer.arc(18, 30, 6, 270 + a, 360 - a);
        shapeRenderer.end();
        matchRenderer.batch.begin();

        if (inputDevice != null) {
            int frameX = 1 + inputDevice.x1;
            int frameY = 1 + inputDevice.y1;
            matchRenderer.batch.draw(Assets.replaySpeed[frameX][frameY], matchRenderer.guiWidth - 50, matchRenderer.guiHeight - 50);
        }

        if (paused) {
            Assets.font10.draw(matchRenderer.batch, gettext("PAUSE"), matchRenderer.guiWidth / 2, 22, Font.Align.CENTER);
        }
    }

    void quit() {
        fsm.pushAction(FADE_OUT);
        fsm.pushAction(NEW_FOREGROUND, STATE_END);
        fsm.pushAction(FADE_IN);
    }
}
