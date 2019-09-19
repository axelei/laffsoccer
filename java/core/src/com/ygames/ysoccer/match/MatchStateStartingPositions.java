package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_KICK_OFF;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_STARTING_POSITIONS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateStartingPositions extends MatchState {

    private boolean move;

    MatchStateStartingPositions(MatchFsm fsm) {
        super(STATE_STARTING_POSITIONS, fsm);

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.setStartingPositions();
        match.setPlayersState(STATE_REACH_TARGET, null);
        match.setPointOfInterest(match.ball.x, match.ball.y);
    }

    @Override
    void onResume() {
        sceneRenderer.actionCamera
                .setSpeedMode(FAST)
                .setLimited(true, true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            move = match.updatePlayers(false);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (!move) {
            fsm.pushAction(NEW_FOREGROUND, STATE_KICK_OFF);
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replay();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(HOLD_FOREGROUND, STATE_PAUSE);
            return;
        }

        InputDevice inputDevice;
        for (int t = HOME; t <= AWAY; t++) {
            inputDevice = match.team[t].fire2Down();
            if (inputDevice != null) {
                getFsm().benchStatus.team = match.team[t];
                getFsm().benchStatus.inputDevice = inputDevice;
                fsm.pushAction(HOLD_FOREGROUND, STATE_BENCH_ENTER);
                return;
            }
        }
    }
}
