package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HALF_TIME_WAIT;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateHalfTimePositions extends MatchState {

    private boolean move;

    MatchStateHalfTimePositions(MatchFsm fsm) {
        super(fsm);

        displayTime = true;
        displayWindVane = true;
        displayStatistics = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.ball.setPosition(0, 0, 0);
        match.ball.updatePrediction();

        sceneRenderer.actionCamera
                .setMode(REACH_TARGET)
                .setTarget(0, 0)
                .setOffset(0, 0)
                .setSpeed(FAST);

        match.period = Match.Period.UNDEFINED;
        match.clock = match.length * 45f / 90f;

        match.setPlayersTarget(Const.TOUCH_LINE + 80, 0);
        match.setPlayersState(STATE_OUTSIDE, null);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            move = match.updatePlayers(false);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (!move) {
            return newAction(NEW_FOREGROUND, STATE_HALF_TIME_WAIT);
        }

        return checkCommonConditions();
    }
}
