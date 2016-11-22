package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

class MatchStateGoal extends MatchState {

    private Goal goal;

    MatchStateGoal(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_GOAL;
    }

    @Override
    void entryActions() {
        super.entryActions();

        matchRenderer.displayControlledPlayer = false;
        matchRenderer.displayBallOwner = false;
        matchRenderer.displayGoalScorer = true;
        matchRenderer.displayTime = true;
        matchRenderer.displayWindVane = true;
        matchRenderer.displayScore = false;
        matchRenderer.displayStatistics = false;
        matchRenderer.displayRadar = true;

        // TODO
        // match.listener.homeGoalSound(match.settings.sfxVolume);

        goal = match.goals.get(match.goals.size() - 1);

        if (match.team[Match.HOME].side == match.ball.ySide) {
            match.kickOffTeam = Match.HOME;
        } else if (match.team[Match.AWAY].side == match.ball.ySide) {
            match.kickOffTeam = Match.AWAY;
        } else {
            throw new RuntimeException("cannot decide kick_off_team!");
        }

        match.resetAutomaticInputDevices();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // set states
        if (goal.type == Goal.Type.OWN_GOAL) {
            match.setStatesForOwnGoal(goal);
        } else {
            match.setStatesForGoal(goal);
        }

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.collisionGoal();
            match.ball.collisionNet();

            match.updatePlayers(true);

            match.nextSubframe();

            match.save();

            if ((match.ball.v > 0) || (match.ball.vz != 0)) {
                // follow ball
                matchRenderer.updateCameraX(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);
                matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL, 0, false);
            } else {
                // follow scorer
                matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, goal.player.data[match.subframe].x);
                matchRenderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, goal.player.data[match.subframe].y, false);
            }
            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if ((match.ball.v == 0) && (match.ball.vz == 0)
                && (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE)) {

            match.recorder.saveHighlight(matchRenderer);

            match.ball.setPosition(0, 0, 0);
            match.ball.updatePrediction();
            matchRenderer.actionCamera.offx = 0;
            matchRenderer.actionCamera.offy = 0;

            if (match.game.settings.autoReplays) {
                match.fsm.pushAction(MatchFsm.ActionType.FADE_OUT);
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_REPLAY);
                match.fsm.pushAction(MatchFsm.ActionType.FADE_IN);
                return;
            } else {
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_STARTING_POSITIONS);
                return;
            }
        }
    }
}
