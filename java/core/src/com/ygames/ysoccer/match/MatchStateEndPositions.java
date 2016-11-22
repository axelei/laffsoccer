package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

class MatchStateEndPositions extends MatchState {

    boolean move;

    MatchStateEndPositions(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_END_POSITIONS;
    }

    @Override
    void entryActions() {
        super.entryActions();

        matchRenderer.displayControlledPlayer = false;
        matchRenderer.displayBallOwner = false;
        matchRenderer.displayGoalScorer = false;
        matchRenderer.displayTime = false;
        matchRenderer.displayWindVane = false;
        matchRenderer.displayScore = true;
        matchRenderer.displayStatistics = false;
        matchRenderer.displayRadar = false;

        match.period = MatchCore.Period.UNDEFINED;

        match.ball.setPosition(0, 0, 0);
        match.ball.updatePrediction();

        matchRenderer.actionCamera.offx = 0;
        matchRenderer.actionCamera.offy = 0;

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
            if (match.recorder.hasHighlights()) {
                match.recorder.restart();
                match.fsm.pushAction(MatchFsm.ActionType.FADE_OUT);
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_HIGHLIGHTS);
                match.fsm.pushAction(MatchFsm.ActionType.FADE_IN);
                return;
            } else {
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_END);
                return;
            }
        }
    }
}
