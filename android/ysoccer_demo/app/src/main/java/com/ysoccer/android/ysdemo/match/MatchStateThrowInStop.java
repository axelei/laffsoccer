package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateThrowInStop extends MatchState {

    boolean move;

    MatchStateThrowInStop(Match match) {
        super(match);
        id = MatchFsm.STATE_THROW_IN_STOP;
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

        match.listener.whistleSound(match.settings.sfxVolume);

        match.throwInX = match.ball.xSide * Const.TOUCH_LINE;
        match.throwInY = match.ball.y;

        match.resetAutomaticInputDevices();
        match.setPlayersState(PlayerFsm.STATE_REACH_TARGET, null);
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

            move = match.updatePlayers(true);
            match.updateTeamTactics();

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);
            match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (!move) {
            match.ball.setPosition(match.throwInX, match.throwInY, 0);
            match.ball.updatePrediction();

            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_THROW_IN);
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
