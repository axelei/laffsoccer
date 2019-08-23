package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_THROW_IN_ANGLE;

class MatchStateThrowIn extends MatchState {

    private Player throwInPlayer;
    private boolean isThrowingIn;

    MatchStateThrowIn(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_THROW_IN;

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayScore = true;
        displayRadar = true;
    }

    @Override
    void onResume() {
        super.onResume();

        isThrowingIn = false;

        fsm.throwInTeam.updateFrameDistance();
        fsm.throwInTeam.findNearest();
        throwInPlayer = fsm.throwInTeam.near1;

        throwInPlayer.setTarget(match.ball.x, match.ball.y);
        throwInPlayer.fsm.setState(STATE_REACH_TARGET);
    }

    @Override
    void onPause() {
        super.onPause();

        match.updateTeamTactics();

        match.ball.setPosition(match.throwInX, match.throwInY, 0);
        match.ball.updatePrediction();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        boolean move = true;
        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();

            move = match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_FAST);
            matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isThrowingIn) {

            Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

            throwInPlayer.fsm.setState(STATE_THROW_IN_ANGLE);
            if (throwInPlayer.team.usesAutomaticInputDevice()) {
                throwInPlayer.inputDevice = throwInPlayer.team.inputDevice;
            }
            isThrowingIn = true;
        }
    }

    @Override
    void checkConditions() {
        if (Math.abs(match.ball.x) < Const.TOUCH_LINE) {
            match.setPlayersState(STATE_STAND_RUN, throwInPlayer);
            fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_MAIN);
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
            fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_PAUSE);
            return;
        }

        InputDevice inputDevice;
        for (int t = HOME; t <= AWAY; t++) {
            inputDevice = match.team[t].fire2Down();
            if (inputDevice != null) {
                fsm.benchStatus.team = match.team[t];
                fsm.benchStatus.inputDevice = inputDevice;
                fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_BENCH_ENTER);
                return;
            }
        }
    }
}
