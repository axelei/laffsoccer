package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateHalfTimeWait extends MatchState {

    MatchStateHalfTimeWait(Match match) {
        super(match);
        id = MatchFsm.STATE_HALF_TIME_WAIT;
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
        match.renderer.displayControls = true;

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

            match.renderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);
            match.renderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);

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

        // If (KeyDown(KEY_ESCAPE))
        // Self.quit_match()
        // Return
        // EndIf
        //
        // If (KeyDown(KEY_P))
        // Self.pause()
        // Return
        // EndIf
    }
}
