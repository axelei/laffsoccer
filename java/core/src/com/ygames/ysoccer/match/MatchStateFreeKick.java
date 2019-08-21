package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import static com.ygames.ysoccer.match.MatchFsm.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.STATE_MAIN;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PAUSE;
import static com.ygames.ysoccer.match.PlayerFsm.STATE_CORNER_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.STATE_STAND_RUN;

class MatchStateFreeKick extends MatchState {

    private Player freeKickPlayer;
    private boolean isKicking;

    MatchStateFreeKick(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_FREE_KICK;

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
        /* TODO
        if (match.settings.commentary) {
            int size = Assets.Commentary.foul.size();
            if (size > 0) {
                Assets.Commentary.foul.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
            }
        }
        */
    }

    @Override
    void onResume() {
        super.onResume();

        Team freeKickTeam = match.foul.opponent.team;

        matchRenderer.actionCamera.offy = -80 * freeKickTeam.side;

        isKicking = false;

        freeKickTeam.updateFrameDistance();
        freeKickTeam.findNearest();
        freeKickPlayer = freeKickTeam.near1;

        freeKickPlayer.tx = match.ball.x - 7 * freeKickPlayer.team.side + 1;
        freeKickPlayer.ty = match.ball.y + 1;
        freeKickPlayer.fsm.setState(STATE_REACH_TARGET);
    }

    @Override
    void onPause() {
        super.onPause();
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

            match.save();

            matchRenderer.updateCameraX(CF_BALL, CS_FAST);
            matchRenderer.updateCameraY(CF_BALL, CS_FAST);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }

        if (!move && !isKicking) {
            Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

            // TODO freeKickPlayer.setState(STATE_FREE_KICK);
            // TEMPORARY //
            freeKickPlayer.setState(STATE_CORNER_KICK_ANGLE);
            if (freeKickPlayer.team.usesAutomaticInputDevice()) {
                freeKickPlayer.inputDevice = freeKickPlayer.team.inputDevice;
            }
            // END OF TEMPORARY //
            isKicking = true;
        }
    }

    @Override
    void checkConditions() {
        if (match.ball.v > 0) {
            match.setPlayersState(STATE_STAND_RUN, freeKickPlayer);
            match.foul = null;
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
