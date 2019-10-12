package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateKickOff extends MatchState {

    private Player kickOffPlayer;
    private boolean isKickingOff;

    MatchStateKickOff(MatchFsm fsm) {
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

        isKickingOff = false;

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        Team kickOffTeam = match.team[match.kickOffTeam];
        kickOffTeam.updateFrameDistance();
        kickOffTeam.findNearest();
        kickOffPlayer = kickOffTeam.near1;
        kickOffPlayer.tx = match.ball.x - 7 * kickOffPlayer.team.side + 1;
        kickOffPlayer.ty = match.ball.y + 1;

        if (kickOffTeam.usesAutomaticInputDevice()) {
            kickOffPlayer.inputDevice = kickOffTeam.inputDevice;
        }

        sceneRenderer.actionCamera
                .setMode(FOLLOW_BALL)
                .setSpeed(FAST);
    }

    @Override
    void onPause() {
        super.onPause();
        match.setStartingPositions();
        kickOffPlayer.setState(STATE_REACH_TARGET);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        boolean move = true;
        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.ball.updatePrediction();
                match.updateFrameDistance();
                match.updateAi();
            }

            match.updateBall();
            move = match.updatePlayers(true);
            match.findNearest();

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKickingOff) {
            kickOffPlayer.setState(PlayerFsm.Id.STATE_KICK_OFF);
            isKickingOff = true;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (EMath.dist(match.ball.x, match.ball.y, 0, 0) > 10) {
            for (int t = HOME; t <= AWAY; t++) {
                for (int i = 0; i < Const.TEAM_SIZE; i++) {
                    Player player = match.team[t].lineup.get(i);
                    if (player != kickOffPlayer) {
                        player.setState(STATE_STAND_RUN);
                    }
                }
            }
            return newAction(NEW_FOREGROUND, STATE_MAIN);
        }

        return checkCommonConditions();
    }
}
