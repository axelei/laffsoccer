package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_KICK_OFF;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_MAIN;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class MatchStateKickOff extends MatchState {

    private Player kickOffPlayer;
    private boolean isKickingOff;

    MatchStateKickOff(MatchFsm fsm) {
        super(STATE_KICK_OFF, fsm);

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

        matchRenderer.actionCamera.setSpeedMode(FAST);
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
                match.updateAi();
            }

            match.updateBall();
            move = match.updatePlayers(true);

            match.nextSubframe();

            matchRenderer.save();

            matchRenderer.updateCamera(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKickingOff) {
            kickOffPlayer.setState(PlayerFsm.Id.STATE_KICK_OFF);
            isKickingOff = true;
        }
    }

    @Override
    void checkConditions() {
        if (Emath.dist(match.ball.x, match.ball.y, 0, 0) > 10) {
            for (int t = HOME; t <= AWAY; t++) {
                for (int i = 0; i < Const.TEAM_SIZE; i++) {
                    Player player = match.team[t].lineup.get(i);
                    if (player != kickOffPlayer) {
                        player.setState(STATE_STAND_RUN);
                    }
                }
            }
            fsm.pushAction(NEW_FOREGROUND, STATE_MAIN);
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replay();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(HOLD_FOREGROUND, STATE_PAUSE);
            return;
        }

        InputDevice inputDevice;
        for (int t = HOME; t <= AWAY; t++) {
            inputDevice = match.team[t].fire2Down();
            if (inputDevice != null) {
                fsm.benchStatus.team = match.team[t];
                fsm.benchStatus.inputDevice = inputDevice;
                fsm.pushAction(HOLD_FOREGROUND, STATE_BENCH_ENTER);
                return;
            }
        }
    }
}
