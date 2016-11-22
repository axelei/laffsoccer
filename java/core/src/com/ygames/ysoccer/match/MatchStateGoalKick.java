package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

class MatchStateGoalKick extends MatchState {

    private Team goalKickTeam;
    private Player goalKickPlayer;
    private boolean isKicking;

    MatchStateGoalKick(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_GOAL_KICK;
    }

    @Override
    void entryActions() {
        super.entryActions();

        matchRenderer.displayControlledPlayer = true;
        matchRenderer.displayBallOwner = true;
        matchRenderer.displayGoalScorer = false;
        matchRenderer.displayTime = true;
        matchRenderer.displayWindVane = true;
        matchRenderer.displayScore = true;
        matchRenderer.displayStatistics = false;
        matchRenderer.displayRadar = true;

        goalKickTeam = match.team[1 - match.ball.ownerLast.team.index];

        matchRenderer.actionCamera.offx = -30 * match.ball.xSide;
        matchRenderer.actionCamera.offy = -30 * match.ball.ySide;

        isKicking = false;

        goalKickPlayer = goalKickTeam.lineup.get(0);
        goalKickPlayer.setTarget(match.ball.x, match.ball.y + 6 * match.ball.ySide);
        goalKickPlayer.fsm.setState(PlayerFsm.STATE_REACH_TARGET);
    }

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

            matchRenderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_FAST);
            matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            // TODO
            // match.listener.whistleSound(match.settings.sfxVolume);

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
    }
}
