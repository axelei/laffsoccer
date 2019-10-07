package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.framework.EMath;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.F1;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.R;
import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.Const.GOAL_LINE;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_FREE_KICK;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HELP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_MAIN;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_REPLAY;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BARRIER;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_FREE_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateFreeKick extends MatchState {

    private Player freeKickPlayer;
    private Team freeKickTeam;
    private Team defendingTeam;
    private boolean isKicking;

    MatchStateFreeKick(MatchFsm fsm) {
        super(STATE_FREE_KICK, fsm);

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
                .setOffset(-30 * match.ball.xSide, -80 * freeKickTeam.side)
                .setSpeedMode(FAST)
                .setLimited(true, true);

        isKicking = false;

        freeKickTeam.updateFrameDistance();
        freeKickTeam.findNearest();
        freeKickPlayer = freeKickTeam.near1;

        float ballToGoal = EMath.roundBy(EMath.angle(match.ball.x, match.ball.y, 0, defendingTeam.side * GOAL_LINE), 45f);
        freeKickPlayer.tx = match.ball.x - 7 * EMath.cos(ballToGoal);
        freeKickPlayer.ty = match.ball.y - 7 * EMath.sin(ballToGoal);
        freeKickPlayer.setState(STATE_REACH_TARGET);

        for (Player ply : defendingTeam.lineup) {
            if (defendingTeam.barrier.contains(ply)) {
                ply.setState(STATE_BARRIER);
            }
        }
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

            sceneRenderer.actionCamera.update(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
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

        if (Gdx.input.isKeyPressed(P)) {
            return newAction(HOLD_FOREGROUND, STATE_PAUSE);
        }

        if (Gdx.input.isKeyPressed(F1)) {
            return newAction(HOLD_FOREGROUND, STATE_HELP);
        }

        InputDevice inputDevice;
        for (int t = HOME; t <= AWAY; t++) {
            inputDevice = match.team[t].fire2Down();
            if (inputDevice != null) {
                getFsm().benchStatus.team = match.team[t];
                getFsm().benchStatus.inputDevice = inputDevice;
                return newAction(HOLD_FOREGROUND, STATE_BENCH_ENTER);
            }
        }

        return checkCommonConditions();
    }
}
