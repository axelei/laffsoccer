package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.MatchFsm.STATE_MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_PENALTY_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenaltyKick extends MatchState {

    private boolean isKicking;

    MatchStatePenaltyKick(MatchFsm fsm) {
        super(fsm);

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayWindVane = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        displayTime = true;
        displayScore = true;
    }

    @Override
    void onResume() {
        super.onResume();

        isKicking = false;

        match.penalty.kicker.setTarget(0, match.penalty.side * (Const.PENALTY_SPOT_Y - 7));
        match.penalty.kicker.setState(STATE_REACH_TARGET);

        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(FAST)
                .setLimited(true, true);
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

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

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
    SceneFsm.Action[] checkConditions() {
        if (match.ball.v > 0) {
            match.setPlayersState(STATE_STAND_RUN, match.penalty.kicker);
            match.penaltyScorer = match.penalty.kicker;
            match.penalty = null;
            return newAction(NEW_FOREGROUND, STATE_MAIN);
        }

        return checkCommonConditions();
    }
}
