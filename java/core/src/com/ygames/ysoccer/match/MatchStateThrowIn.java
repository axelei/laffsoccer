package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.MatchFsm.STATE_MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_THROW_IN_ANGLE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateThrowIn extends MatchState {

    private Player throwInPlayer;
    private boolean isThrowingIn;

    MatchStateThrowIn(MatchFsm fsm) {
        super(fsm);

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

        getFsm().throwInTeam.updateFrameDistance();
        getFsm().throwInTeam.findNearest();
        throwInPlayer = getFsm().throwInTeam.near1;

        throwInPlayer.setTarget(match.ball.x, match.ball.y);
        throwInPlayer.setState(STATE_REACH_TARGET);

        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(FAST)
                .setLimited(true, true);
    }

    @Override
    void onPause() {
        super.onPause();

        match.updateTeamTactics();

        match.ball.setPosition(getFsm().throwInPosition);
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

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

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
    SceneFsm.Action[] checkConditions() {
        if (Math.abs(match.ball.x) < Const.TOUCH_LINE) {
            match.setPlayersState(STATE_STAND_RUN, throwInPlayer);
            return newAction(NEW_FOREGROUND, STATE_MAIN);
        }

        return checkCommonConditions();
    }
}
