package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateGoalKickStop extends MatchState {

    int xSide;
    int ySide;

    MatchStateGoalKickStop(Match match) {
        super(match);
        id = MatchFsm.STATE_GOAL_KICK_STOP;
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

        match.listener.whistleSound(match.settings.sfxVolume);

        xSide = match.ball.xSide;
        ySide = match.ball.ySide;

        match.resetAutomaticInputDevices();
        match.setPlayersState(PlayerFsm.STATE_REACH_TARGET, null);

        Team goalKickTeam = match.team[1 - match.ball.ownerLast.team.index];
        Player goalKickKeeper = goalKickTeam.lineup.get(0);
        goalKickKeeper.tx = match.ball.x / 4;
        goalKickKeeper.ty = goalKickTeam.side * (Const.GOAL_LINE - 8);

        Team opponentTeam = match.team[1 - goalKickTeam.index];
        Player opponentKeeper = opponentTeam.lineup.get(0);
        opponentKeeper.tx = 0;
        opponentKeeper.ty = opponentTeam.side * (Const.GOAL_LINE - 8);

        goalKickTeam.updateTactics(true);
        opponentTeam.updateTactics(true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();
            match.ball.collisionGoal();
            match.ball.collisionJumpers();
            match.ball.collisionNetOut();

            match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);
            match.renderer.updateCameraY(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if ((match.ball.v < 5) && (match.ball.vz < 5)) {
            match.ball.setPosition((Const.GOAL_AREA_W / 2) * xSide, (Const.GOAL_LINE - Const.GOAL_AREA_H) * ySide, 0);
            match.ball.updatePrediction();

            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_GOAL_KICK);
            return;
        }

        // TODO
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
        //
        // ''BENCH
        // Self.bench(team[HOME], team[HOME].fire2_down())
        // Self.bench(team[AWAY], team[AWAY].fire2_down())

    }
}
