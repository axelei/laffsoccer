package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateCornerStop extends MatchState {

    private int cornerX;
    private int cornerY;

    MatchStateCornerStop(Match match) {
        super(match);
        id = MatchFsm.STATE_CORNER_STOP;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = true;
        match.renderer.displayBallOwner = false;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = true;
        match.renderer.displayWindVane = true;
        match.renderer.displayScore = false;
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = true;
        match.renderer.displayControls = true;

        if (match.team[Match.HOME].side == -match.ball.ySide) {
            match.stats[Match.HOME].cornersWon += 1;
        } else {
            match.stats[Match.AWAY].cornersWon += 1;
        }

        match.listener.whistleSound(match.settings.sfxVolume);

        cornerX = (Const.TOUCH_LINE - 12) * match.ball.xSide;
        cornerY = (Const.GOAL_LINE - 12) * match.ball.ySide;

        // set the player targets relative to corner zone
        // even before moving the ball itself
        match.ball.updateZone(cornerX, cornerY, 0, 0);
        match.updateTeamTactics();
        match.team[Match.HOME].lineup.get(0).setTarget(0, match.team[Match.HOME].side * (Const.GOAL_LINE - 8));
        match.team[Match.AWAY].lineup.get(0).setTarget(0, match.team[Match.AWAY].side * (Const.GOAL_LINE - 8));

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
            match.ball.setPosition(cornerX, cornerY, 0);
            match.ball.updatePrediction();

            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_CORNER_KICK);
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
