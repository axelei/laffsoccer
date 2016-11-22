package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

class MatchStateHalfTimeWait extends MatchState {

    MatchStateHalfTimeWait(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_HALF_TIME_WAIT;
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

        match.swapTeamSides();

        match.kickOffTeam = 1 - match.coinToss;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);
            matchRenderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (match.team[Match.HOME].fire1Down() != null
                || match.team[Match.AWAY].fire1Down() != null
                || (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE)) {
            match.period = Match.Period.SECOND_HALF;
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_HALF_TIME_ENTER);
            return;
        }
    }
}
