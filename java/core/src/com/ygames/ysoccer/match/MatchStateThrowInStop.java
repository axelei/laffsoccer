package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.NORMAL;
import static com.ygames.ysoccer.match.MatchFsm.STATE_THROW_IN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateThrowInStop extends MatchState {

    private boolean move;

    MatchStateThrowInStop(MatchFsm fsm) {
        super(fsm);

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        getFsm().throwInPosition.set(match.ball.xSide * Const.TOUCH_LINE, match.ball.y);

        match.resetAutomaticInputDevices();
        match.setPlayersState(STATE_REACH_TARGET, null);
    }

    @Override
    void onResume() {
        match.setPointOfInterest(getFsm().throwInPosition);

        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(NORMAL)
                .setLimited(true, true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();

            move = match.updatePlayers(true);
            match.updateTeamTactics();

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (!move) {
            match.ball.setPosition(getFsm().throwInPosition);
            match.ball.updatePrediction();

            return newAction(NEW_FOREGROUND, STATE_THROW_IN);
        }

        return checkCommonConditions();
    }
}
