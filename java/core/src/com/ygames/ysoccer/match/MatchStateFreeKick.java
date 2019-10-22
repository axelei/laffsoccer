package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BARRIER;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FREE_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateFreeKick extends MatchState {

    private Player freeKickPlayer;
    private Team freeKickTeam;
    private Team defendingTeam;
    private boolean isKicking;

    MatchStateFreeKick(MatchFsm fsm) {
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

        freeKickTeam = match.foul.opponent.team;
        defendingTeam = match.foul.player.team;

        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(FAST)
                .setOffset(-30 * match.ball.xSide, -80 * freeKickTeam.side)
                .setLimited(true, true);

        isKicking = false;

        freeKickTeam.updateFrameDistance();
        freeKickTeam.findNearest();
        freeKickPlayer = freeKickTeam.near1;

        float ballToGoal = EMath.roundBy(EMath.angle(match.ball.x, match.ball.y, 0, defendingTeam.side * GOAL_LINE), 45f);
        freeKickPlayer.tx = match.ball.x - 7 * EMath.cos(ballToGoal);
        freeKickPlayer.ty = match.ball.y - 7 * EMath.sin(ballToGoal);
        freeKickPlayer.setState(STATE_REACH_TARGET);
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
            move = match.updatePlayers(true);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            for (Player ply : defendingTeam.lineup) {
                if (defendingTeam.barrier.contains(ply)) {
                    ply.setState(STATE_BARRIER);
                }
            }

            Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

            freeKickPlayer.setState(STATE_FREE_KICK_ANGLE);
            if (freeKickPlayer.team.usesAutomaticInputDevice()) {
                freeKickPlayer.inputDevice = freeKickPlayer.team.inputDevice;
            }
            isKicking = true;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (match.ball.v > 0) {
            freeKickTeam.setPlayersState(STATE_STAND_RUN, freeKickPlayer);
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = defendingTeam.lineup.get(i);
                if (!defendingTeam.barrier.contains(player)) {
                    player.setState(STATE_STAND_RUN);
                }
            }

            match.foul = null;
            return newAction(NEW_FOREGROUND, STATE_MAIN);
        }

        return checkCommonConditions();
    }
}
