package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateGoalKick extends MatchState {

    private Player goalKickPlayer;
    private boolean isKicking;

    MatchStateGoalKick(MatchFsm fsm) {
        super(fsm);

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayScore = true;
        displayRadar = true;
    }

    @Override
    void onResume() {
        super.onResume();

        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setOffset(-30 * match.ball.xSide, -30 * match.ball.ySide)
                .setSpeed(FAST)
                .setLimited(true, true);

        isKicking = false;

        goalKickPlayer = getFsm().goalKickTeam.lineupAtPosition(0);
        goalKickPlayer.setTarget(match.ball.x, match.ball.y + 6 * match.ball.ySide);
        goalKickPlayer.setState(STATE_REACH_TARGET);
    }

    @Override
    void onPause() {
        super.onPause();

        goalKickPlayer.setTarget(match.ball.x / 4, getFsm().goalKickTeam.side * (GOAL_LINE - 8));
        match.team[HOME].updateTactics(true);
        match.team[AWAY].updateTactics(true);
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

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

            goalKickPlayer.setState(PlayerFsm.Id.STATE_GOAL_KICK);
            if (goalKickPlayer.team.usesAutomaticInputDevice()) {
                goalKickPlayer.inputDevice = goalKickPlayer.team.inputDevice;
            }
            isKicking = true;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (match.ball.v > 0) {
            match.setPlayersState(STATE_STAND_RUN, goalKickPlayer);
            return newAction(NEW_FOREGROUND, STATE_MAIN);
        }

        return checkCommonConditions();
    }
}
