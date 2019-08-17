package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;

class MatchStateKickOff extends MatchState {

    private Player kickOffPlayer;
    private boolean isKickingOff;

    MatchStateKickOff(Match match) {
        super(match);
        id = MatchFsm.STATE_KICK_OFF;
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

        isKickingOff = false;

        match.listener.whistleSound(match.settings.sfxVolume);

        Team kickOffTeam = match.team[match.kickOffTeam];
        kickOffTeam.updateFrameDistance();
        kickOffTeam.findNearest();
        kickOffPlayer = kickOffTeam.near1;
        kickOffPlayer.tx = match.ball.x - 7 * kickOffPlayer.team.side + 1;
        kickOffPlayer.ty = match.ball.y + 1;

        if (kickOffTeam.usesAutomaticInputDevice()) {
            kickOffPlayer.inputDevice = kickOffTeam.inputDevice;
        }
    }

//	Method on_pause()
//		Self.set_starting_positions(match_status.kick_off_team)
//		Self.kick_off_player.set_state("state_reach_target")
//	End Method

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
            move = match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_FAST);
            match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKickingOff) {
            kickOffPlayer.setState(PlayerFsm.STATE_KICK_OFF);
            isKickingOff = true;
        }
    }

    @Override
    void checkConditions() {
        if (Emath.dist(match.ball.x, match.ball.y, 0, 0) > 10) {
            for (int t = Match.HOME; t <= Match.AWAY; t++) {
                for (int i = 0; i < Const.TEAM_SIZE; i++) {
                    Player player = match.team[t].lineup.get(i);
                    if (player != kickOffPlayer) {
                        player.fsm.setState(PlayerFsm.STATE_STAND_RUN);
                    }
                }
            }
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_MAIN);
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
//		
//		''BENCH
//		Self.bench(team[HOME], team[HOME].fire2_down())
//		Self.bench(team[AWAY], team[AWAY].fire2_down())
    }
}
