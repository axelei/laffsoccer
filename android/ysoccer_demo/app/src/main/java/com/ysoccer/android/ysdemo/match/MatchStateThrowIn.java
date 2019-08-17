package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateThrowIn extends MatchState {

    private Team throwInTeam;
    private Player throwInPlayer;
    private boolean isThrowingIn;

    MatchStateThrowIn(Match match) {
        super(match);
        id = MatchFsm.STATE_THROW_IN;
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

        throwInTeam = match.team[1 - match.ball.ownerLast.team.index];
        isThrowingIn = false;

        throwInTeam.updateFrameDistance();
        throwInTeam.findNearest();
        throwInPlayer = throwInTeam.near1;

        throwInPlayer.setTarget(match.ball.x, match.ball.y);
        throwInPlayer.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
    }

    // TODO
    // Method on_pause()
    //
    // team[HOME].update_tactics()
    // team[AWAY].update_tactics()
    //
    // ball.set_position(match_status.throw_in_x, match_status.throw_in_y)
    // ball.update_prediction()
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

        if (!move && !isThrowingIn) {

            match.listener.whistleSound(match.settings.sfxVolume);

            throwInPlayer.fsm.setState(PlayerFsm.STATE_THROW_IN_ANGLE);
            if (throwInPlayer.team.usesAutomaticInputDevice()) {
                throwInPlayer.inputDevice = throwInPlayer.team.inputDevice;
            }
            isThrowingIn = true;
        }
    }

    @Override
    void checkConditions() {

        if (Math.abs(match.ball.x) < Const.TOUCH_LINE) {
            match.setPlayersState(PlayerFsm.STATE_STAND_RUN, throwInPlayer);
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_MAIN);
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
