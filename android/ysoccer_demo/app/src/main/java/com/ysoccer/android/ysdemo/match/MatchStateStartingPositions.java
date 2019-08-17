package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateStartingPositions extends MatchState {

    boolean move;

    MatchStateStartingPositions(Match match) {
        super(match);
        id = MatchFsm.STATE_STARTING_POSITIONS;
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
        match.setPlayersState(PlayerFsm.STATE_REACH_TARGET, null);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            move = match.updatePlayers(false);

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_FAST);
            match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (!move) {
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_KICK_OFF);
        }

        //TODO
        /*
        If (KeyDown(KEY_ESCAPE))
		Self.quit_match()
			Return
		EndIf
		
		If (KeyDown(KEY_R))
			Self.replay()
			Return
		EndIf
		
		If (KeyDown(KEY_P))
			Self.pause()
			Return
		EndIf
		
		''BENCH
		Self.bench(team[HOME], team[HOME].fire2_down())
		Self.bench(team[AWAY], team[AWAY].fire2_down())
		*/
    }
}
