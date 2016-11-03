package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

class MatchStateHalfTimePositions extends MatchState {

    private boolean move;

    MatchStateHalfTimePositions(MatchCore match) {
        super(match);
        id = MatchFsm.STATE_HALF_TIME_POSITIONS;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = false;
        match.renderer.displayBallOwner = false;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = true;
        match.renderer.displayWindVane = true;
        match.renderer.displayScore = false;
        match.renderer.displayStatistics = true;
        match.renderer.displayRadar = false;

        match.ball.setPosition(0, 0, 0);
        match.ball.updatePrediction();

        match.renderer.actionCamera.offx = 0;
        match.renderer.actionCamera.offy = 0;

        match.period = MatchCore.Period.UNDEFINED;
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

            match.renderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);
            match.renderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }
}
