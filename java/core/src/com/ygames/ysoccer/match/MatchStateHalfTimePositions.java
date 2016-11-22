package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

class MatchStateHalfTimePositions extends MatchState {

    private boolean move;

    MatchStateHalfTimePositions(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_HALF_TIME_POSITIONS;
    }

    @Override
    void entryActions() {
        super.entryActions();

        matchRenderer.displayControlledPlayer = false;
        matchRenderer.displayBallOwner = false;
        matchRenderer.displayGoalScorer = false;
        matchRenderer.displayTime = true;
        matchRenderer.displayWindVane = true;
        matchRenderer.displayScore = false;
        matchRenderer.displayStatistics = true;
        matchRenderer.displayRadar = false;

        match.ball.setPosition(0, 0, 0);
        match.ball.updatePrediction();

        matchRenderer.actionCamera.offx = 0;
        matchRenderer.actionCamera.offy = 0;

        match.period = Match.Period.UNDEFINED;
        match.clock = match.length * 45 / 90;

        match.setPlayersTarget(Const.TOUCH_LINE + 80, 0);
        match.setPlayersState(PlayerFsm.STATE_OUTSIDE, null);
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

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);
            matchRenderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (!move) {
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_HALF_TIME_WAIT);
            return;
        }
    }
}
