package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.R;
import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Match.Period.PENALTIES;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_MAIN;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTY_KICK;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTY_KICK_END;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class MatchStatePenaltyKick extends MatchState {

    private boolean isKicking;

    MatchStatePenaltyKick(MatchFsm fsm) {
        super(STATE_PENALTY_KICK, fsm);

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        displayTime = (match.period != PENALTIES);
        displayScore = (match.period != PENALTIES);
        displayPenaltiesScore = (match.period == PENALTIES);
        displayRadar = (match.period != PENALTIES);
    }

    @Override
    void onResume() {
        super.onResume();

        isKicking = false;

        match.penalty.kicker.setTarget(match.ball.x, match.ball.y - 7 * match.ball.ySide);
        match.penalty.kicker.setState(STATE_REACH_TARGET);

        matchRenderer.actionCamera.setSpeedMode(FAST);
        matchRenderer.actionCamera.setLimited(true, true);
    }

    @Override
    void onPause() {
        super.onPause();

        match.penalty.kicker.setTarget(-40 * match.ball.ySide, match.penalty.side * (Const.PENALTY_SPOT_Y - 45));
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

            if (match.period == PENALTIES) {
                matchRenderer.updateCamera(STILL);
            } else {
                matchRenderer.updateCamera(FOLLOW_BALL);
            }

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

            match.penalty.kicker.setState(STATE_PENALTY_KICK_ANGLE);
            if (match.penalty.kicker.team.usesAutomaticInputDevice()) {
                match.penalty.kicker.inputDevice = match.penalty.kicker.team.inputDevice;
            }

            isKicking = true;
        }
    }

    @Override
    void checkConditions() {
        if (match.ball.v > 0) {
            if (match.period == PENALTIES) {
                match.penalty.kicker.setState(STATE_IDLE);
                fsm.pushAction(NEW_FOREGROUND, STATE_PENALTY_KICK_END);
                return;
            } else {
                match.setPlayersState(STATE_STAND_RUN, match.penalty.kicker);
                match.penalty = null;
                fsm.pushAction(NEW_FOREGROUND, STATE_MAIN);
                return;
            }
        }

        if (Gdx.input.isKeyPressed(ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(R)) {
            replay();
            return;
        }

        if (Gdx.input.isKeyPressed(P)) {
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
