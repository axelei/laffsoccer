package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateHalfTimePositions extends MatchState {

    boolean move;

    MatchStateHalfTimePositions(Match match) {
        super(match);
        id = MatchFsm.STATE_HALF_TIME_POSITIONS;
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

        match.ball.setPosition(0, 0, 0);
        match.ball.updatePrediction();

        match.renderer.actionCamera.offx = 0;
        match.renderer.actionCamera.offy = 0;

        match.period = Match.Period.UNDEFINED;
        match.clock = match.length * 45 / 90;

        match.setPlayersTarget(Const.TOUCH_LINE + 80, 0);
        match.setPlayersState(PlayerFsm.STATE_OUTSIDE, null);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            move = match.updatePlayers(false);

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);
            match.renderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (!move) {
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_HALF_TIME_WAIT);
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
