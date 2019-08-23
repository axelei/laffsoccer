package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.RESTORE_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_REPLAY;

class MatchStateReplay extends MatchState {

    private int subframe0;
    private boolean paused;
    private boolean slowMotion;
    private boolean keySlow;
    private boolean keyPause;
    private int position;
    private InputDevice inputDevice;

    MatchStateReplay(MatchFsm fsm) {
        super(STATE_REPLAY, fsm);

        displayWindVane = true;
    }

    @Override
    void entryActions() {

        subframe0 = match.subframe;

        match.subframe = (subframe0 + 1) % Const.REPLAY_SUBFRAMES;

        paused = false;
        slowMotion = false;

        // control keys
        keySlow = Gdx.input.isKeyPressed(Input.Keys.R);
        keyPause = Gdx.input.isKeyPressed(Input.Keys.P);

        // position of current frame inside the replay vector
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
            speed = 12 * inputDevice.x1 - 2 * inputDevice.y1 + 8 * Math.abs(inputDevice.x1) * inputDevice.y1;
        } else if (slowMotion) {
            speed = GLGame.SUBFRAMES / 2;
        } else {
            speed = GLGame.SUBFRAMES;
        }

        // set position
        if (!paused) {
            position += speed;

            // limits
            position = Math.max(position, 1);
            position = Math.min(position, Const.REPLAY_SUBFRAMES);

            match.subframe = (subframe0 + position) % Const.REPLAY_SUBFRAMES;
        }
    }

    @Override
    void checkConditions() {

        // quit on 'ESC'
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

        // quit on last position
        if ((position == Const.REPLAY_SUBFRAMES) && (inputDevice == null)) {
            quit();
            return;
        }
    }

    @Override
    void render() {
        super.render();

        int f = Math.round(match.subframe / GLGame.SUBFRAMES) % 32;
        matchRenderer.batch.draw(Assets.replay[f % 16][f / 16], 34, 28);
        if (inputDevice != null) {
            int frameX = 1 + inputDevice.x1;
            int frameY = 1 + inputDevice.y1;
            matchRenderer.batch.draw(Assets.replaySpeed[frameX][frameY], matchRenderer.guiWidth - 50, matchRenderer.guiHeight - 50);
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
