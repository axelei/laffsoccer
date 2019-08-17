package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateGoalKick extends MatchState {

    private Team goalKickTeam;
    private Player goalKickPlayer;
    private boolean isKicking;

    MatchStateGoalKick(Match match) {
        super(match);
        id = MatchFsm.STATE_GOAL_KICK;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = true;
        match.renderer.displayBallOwner = true;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = true;
        match.renderer.displayWindVane = true;
        match.renderer.displayScore = true;
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = true;
        match.renderer.displayControls = true;

        goalKickTeam = match.team[1 - match.ball.ownerLast.team.index];

        match.renderer.actionCamera.offx = -30 * match.ball.xSide;
        match.renderer.actionCamera.offy = -30 * match.ball.ySide;

        isKicking = false;

        goalKickPlayer = goalKickTeam.lineup.get(0);
        goalKickPlayer.setTarget(match.ball.x, match.ball.y + 6 * match.ball.ySide);
        goalKickPlayer.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
    }

    // Method on_pause()
    //
    // Self.goal_kick_player.set_target(ball.x / 4, Self.goal_kick_team.side *
    // (GOAL_LINE -8))
    // team[Not Self.goal_kick_team.index].lineup_at_position(0).set_target(0,
    // team[Not Self.goal_kick_team.index].side * (GOAL_LINE -8))
    // team[HOME].update_tactics(True)
    // team[AWAY].update_tactics(True)
    //
    // End Method

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        boolean move = true;
        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();

            move = match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_FAST);
            match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            match.listener.whistleSound(match.settings.sfxVolume);

            goalKickPlayer.fsm.setState(PlayerFsm.STATE_GOAL_KICK);
            if (goalKickPlayer.team.usesAutomaticInputDevice()) {
                goalKickPlayer.inputDevice = goalKickPlayer.team.inputDevice;
            }
            isKicking = true;
        }
    }

    @Override
    void checkConditions() {
        if (match.ball.v > 0) {
            match.setPlayersState(PlayerFsm.STATE_STAND_RUN, goalKickPlayer);
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_MAIN);
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
        //
        // ''BENCH
        // Self.bench(team[HOME], team[HOME].fire2_down())
        // Self.bench(team[AWAY], team[AWAY].fire2_down())
    }
}
