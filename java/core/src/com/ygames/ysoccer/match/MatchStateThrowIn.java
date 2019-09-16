package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_MAIN;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_THROW_IN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_THROW_IN_ANGLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateThrowIn extends MatchState {

    private Player throwInPlayer;
    private boolean isThrowingIn;

    MatchStateThrowIn(MatchFsm fsm) {
        super(STATE_THROW_IN, fsm);

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
        throwInPlayer.setState(STATE_REACH_TARGET);

        matchRenderer.actionCamera.setSpeedMode(FAST);
        matchRenderer.actionCamera.setLimited(true, true);
    }

    @Override
    void onPause() {
        super.onPause();

        match.updateTeamTactics();

        match.ball.setPosition(fsm.throwInPosition);
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

            matchRenderer.save();

            matchRenderer.updateCamera(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isThrowingIn) {

            Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

            throwInPlayer.setState(STATE_THROW_IN_ANGLE);
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
            fsm.pushAction(NEW_FOREGROUND, STATE_MAIN);
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
                fsm.benchStatus.team = match.team[t];
                fsm.benchStatus.inputDevice = inputDevice;
                fsm.pushAction(HOLD_FOREGROUND, STATE_BENCH_ENTER);
                return;
            }
        }
    }
}
