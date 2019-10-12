package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.MatchFsm.STATE_MAIN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_CORNER_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateCornerKick extends MatchState {

    private Player cornerKickPlayer;
    private boolean isKicking;

    MatchStateCornerKick(MatchFsm fsm) {
        super(fsm);

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayScore = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();
        if (match.settings.commentary) {
            int size = Assets.Commentary.cornerKick.size();
            if (size > 0) {
                Assets.Commentary.cornerKick.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
            }
        }
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

        getFsm().cornerKickTeam.updateFrameDistance();
        getFsm().cornerKickTeam.findNearest();
        cornerKickPlayer = getFsm().cornerKickTeam.near1;

        cornerKickPlayer.setTarget(match.ball.x + 7 * match.ball.xSide, match.ball.y);
        cornerKickPlayer.setState(STATE_REACH_TARGET);
    }

    @Override
    void onPause() {
        super.onPause();
        match.updateTeamTactics();
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

            cornerKickPlayer.setState(STATE_CORNER_KICK_ANGLE);
            if (cornerKickPlayer.team.usesAutomaticInputDevice()) {
                cornerKickPlayer.inputDevice = cornerKickPlayer.team.inputDevice;
            }
            isKicking = true;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (match.ball.v > 0) {
            match.setPlayersState(STATE_STAND_RUN, cornerKickPlayer);
            return newAction(NEW_FOREGROUND, STATE_MAIN);
        }

        return checkCommonConditions();
    }
}
