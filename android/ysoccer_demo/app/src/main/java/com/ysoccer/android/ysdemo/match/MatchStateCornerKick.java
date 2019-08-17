package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateCornerKick extends MatchState {

    private Team cornerKickTeam;
    private Player cornerKickPlayer;
    private boolean isKicking;

    MatchStateCornerKick(Match match) {
        super(match);
        id = MatchFsm.STATE_CORNER_KICK;
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

        cornerKickTeam = match.team[1 - match.ball.ownerLast.team.index];

        match.renderer.actionCamera.offx = -30 * match.ball.xSide;
        match.renderer.actionCamera.offy = -30 * match.ball.ySide;

        // If (game_settings.sound_enabled And match_settings.commentary)
        // SetChannelVolume(chn_comm, 0.1 * location_settings.sound_vol)
        // PlaySound(comm_corner[Rand(0, comm_corner.length -1)], chn_comm)
        // EndIf

        isKicking = false;

        cornerKickTeam.updateFrameDistance();
        cornerKickTeam.findNearest();
        cornerKickPlayer = cornerKickTeam.near1;

        cornerKickPlayer.setTarget(match.ball.x + 7 * match.ball.xSide, match.ball.y);
        cornerKickPlayer.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
    }

    // Method on_pause()
    //
    // team[HOME].update_tactics()
    // team[AWAY].update_tactics()
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

            cornerKickPlayer.fsm.setState(PlayerFsm.STATE_CORNER_KICK_ANGLE);
            if (cornerKickPlayer.team.usesAutomaticInputDevice()) {
                cornerKickPlayer.inputDevice = cornerKickPlayer.team.inputDevice;
            }
            isKicking = true;
        }
    }

    @Override
    void checkConditions() {
        if (match.ball.v > 0) {
            match.setPlayersState(PlayerFsm.STATE_STAND_RUN, cornerKickPlayer);
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
