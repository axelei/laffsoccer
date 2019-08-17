package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateKeeperStop extends MatchState {

    private Player keeper;
    private Team keeperTeam;
    private Team opponentTeam;

    MatchStateKeeperStop(Match match) {
        super(match);
        id = MatchFsm.STATE_KEEPER_STOP;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = true;
        match.renderer.displayBallOwner = true;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = true;
        match.renderer.displayWindVane = true;
        match.renderer.displayScore = false;
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = true;
        match.renderer.displayControls = true;

        keeper = match.ball.holder;
        keeperTeam = match.team[keeper.team.index];
        opponentTeam = match.team[1 - keeper.team.index];

        match.stats[opponentTeam.index].overallShots += 1;
        match.stats[opponentTeam.index].centeredShots += 1;

        keeperTeam.assignAutomaticInputDevices(keeper);
        opponentTeam.assignAutomaticInputDevices(null);

        keeperTeam.setPlayersState(PlayerFsm.STATE_REACH_TARGET, keeper);
        opponentTeam.setPlayersState(PlayerFsm.STATE_REACH_TARGET, null);

        keeperTeam.updateTactics(true);
        opponentTeam.updateTactics(true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;

        while (timeLeft > GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.updatePlayers(true);

            if ((match.subframe % GLGame.VIRTUAL_REFRESH_RATE) == 0) {
                match.ball.updatePrediction();
            }

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);
            match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (match.ball.holder == null) {
            keeperTeam.setPlayersState(PlayerFsm.STATE_STAND_RUN, keeper);
            opponentTeam.setPlayersState(PlayerFsm.STATE_STAND_RUN, null);
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_MAIN);
            return;
        }

//		If (KeyDown(KEY_ESCAPE))
//			Self.quit_match()
//			Return
//		EndIf
//		
//		If (KeyDown(KEY_R))
//			Self.replay()
//			Return
//		EndIf
//		
//		If (KeyDown(KEY_P))
//			Self.pause()
//			Return
//		EndIf

    }
}
