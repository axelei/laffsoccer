package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateGoal extends MatchState {

    private Goal goal;

    MatchStateGoal(Match match) {
        super(match);
        id = MatchFsm.STATE_GOAL;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = false;
        match.renderer.displayBallOwner = false;
        match.renderer.displayGoalScorer = true;
        match.renderer.displayTime = true;
        match.renderer.displayWindVane = true;
        match.renderer.displayScore = false;
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = true;
        match.renderer.displayControls = true;

        match.listener.homeGoalSound(match.settings.sfxVolume);

        // If (match_settings.commentary = True)
        // SetChannelVolume(chn_comm, 0.1 *
        // location_settings.sound_vol)
        // If (ball.goal_owner.team = team[attacking_team])
        // PlaySound(comm_goal[Rand(0, comm_goal.length -1)],
        // chn_comm)
        // Else
        // PlaySound(comm_owngoal[Rand(0, comm_owngoal.length -1)],
        // chn_comm)
        // EndIf
        // EndIf

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
                match.renderer.updateCameraX(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);
                match.renderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL, 0, false);
            } else {
                // follow scorer
                match.renderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, goal.player.data[match.subframe].x);
                match.renderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, goal.player.data[match.subframe].y, false);
            }
            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if ((match.ball.v == 0) && (match.ball.vz == 0)
                && (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE)) {

            match.recorder.saveHighlight();

            match.ball.setPosition(0, 0, 0);
            match.ball.updatePrediction();
            match.renderer.actionCamera.offx = 0;
            match.renderer.actionCamera.offy = 0;

            if (match.glGame.settings.autoReplay) {
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
