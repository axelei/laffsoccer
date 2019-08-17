package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateHalfTimeEnter extends MatchState {

    private int enteringCounter;

    MatchStateHalfTimeEnter(Match match) {
        super(match);
        id = MatchFsm.STATE_HALF_TIME_ENTER;
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
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = true;
        match.renderer.displayControls = true;

        match.setStartingPositions();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
                if ((enteringCounter % 4) == 0 && enteringCounter / 4 < Const.TEAM_SIZE) {
                    for (int t = Match.HOME; t <= Match.AWAY; t++) {
                        int i = enteringCounter / 4;
                        Player player = match.team[t].lineup.get(i);
                        player.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
                    }
                }
                enteringCounter += 1;
            }

            match.updatePlayers(false);

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_FAST);
            match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {

        if (enteringCounter / 4 == Const.TEAM_SIZE) {
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_STARTING_POSITIONS);
            return;
        }

        // If (KeyDown(KEY_ESCAPE))
        // Self.quit_match()
        // Return
        // EndIf
        //
        // If (KeyDown(KEY_R))
        // Self.replay()
        // Return
        // EndIf
        //
        // If (KeyDown(KEY_P))
        // Self.pause()
        // Return
        // EndIf
    }
}
