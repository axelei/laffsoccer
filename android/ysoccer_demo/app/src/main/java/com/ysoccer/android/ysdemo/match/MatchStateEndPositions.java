package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateEndPositions extends MatchState {

    boolean move;

    MatchStateEndPositions(Match match) {
        super(match);
        id = MatchFsm.STATE_END_POSITIONS;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = false;
        match.renderer.displayBallOwner = false;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = false;
        match.renderer.displayWindVane = false;
        match.renderer.displayScore = true;
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = false;
        match.renderer.displayControls = true;

        match.period = Match.Period.UNDEFINED;

        match.ball.setPosition(0, 0, 0);
        match.ball.updatePrediction();

        match.renderer.actionCamera.offx = 0;
        match.renderer.actionCamera.offy = 0;

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

            match.renderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);
            match.renderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);

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
