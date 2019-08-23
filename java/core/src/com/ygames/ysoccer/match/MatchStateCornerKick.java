package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.badlogic.gdx.Input.Keys.ESCAPE;
import static com.badlogic.gdx.Input.Keys.P;
import static com.badlogic.gdx.Input.Keys.R;
import static com.ygames.ysoccer.match.ActionCamera.CF_BALL;
import static com.ygames.ysoccer.match.ActionCamera.CS_FAST;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_CORNER_KICK;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_MAIN;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_CORNER_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class MatchStateCornerKick extends MatchState {

    private Player cornerKickPlayer;
    private boolean isKicking;

    MatchStateCornerKick(MatchFsm fsm) {
        super(STATE_CORNER_KICK, fsm);

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

        matchRenderer.actionCamera.offx = -30 * match.ball.xSide;
        matchRenderer.actionCamera.offy = -30 * match.ball.ySide;

        isKicking = false;

        fsm.cornerKickTeam.updateFrameDistance();
        fsm.cornerKickTeam.findNearest();
        cornerKickPlayer = fsm.cornerKickTeam.near1;

        cornerKickPlayer.setTarget(match.ball.x + 7 * match.ball.xSide, match.ball.y);
        cornerKickPlayer.fsm.setState(STATE_REACH_TARGET);
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

            match.save();

            matchRenderer.updateCameraX(CF_BALL, CS_FAST);
            matchRenderer.updateCameraY(CF_BALL, CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

            cornerKickPlayer.fsm.setState(STATE_CORNER_KICK_ANGLE);
            if (cornerKickPlayer.team.usesAutomaticInputDevice()) {
                cornerKickPlayer.inputDevice = cornerKickPlayer.team.inputDevice;
            }
            isKicking = true;
        }
    }

    @Override
    void checkConditions() {
        if (match.ball.v > 0) {
            match.setPlayersState(STATE_STAND_RUN, cornerKickPlayer);
            fsm.pushAction(NEW_FOREGROUND, STATE_MAIN);
            return;
        }

        if (Gdx.input.isKeyPressed(ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(R)) {
            replay();
            return;
        }

        if (Gdx.input.isKeyPressed(P)) {
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
