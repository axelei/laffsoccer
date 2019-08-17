package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateHalfTimeStop extends MatchState {

    MatchStateHalfTimeStop(Match match) {
        super(match);
        id = MatchFsm.STATE_HALF_TIME_STOP;
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

        match.listener.endGameSound(match.settings.sfxVolume);

        match.resetAutomaticInputDevices();
        match.setPlayersState(PlayerFsm.STATE_IDLE, null);
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

            match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);
            match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {

        if (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE) {
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_HALF_TIME_POSITIONS);
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
