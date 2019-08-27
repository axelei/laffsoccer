package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.NORMAL;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_FREE_KICK;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_FREE_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_DOWN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_TACKLE;

class MatchStateFreeKickStop extends MatchState {

    private boolean allPlayersReachingTarget;
    private boolean move;

    MatchStateFreeKickStop(MatchFsm fsm) {
        super(STATE_FREE_KICK_STOP, fsm);

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        if (match.settings.commentary) {
            int size = Assets.Commentary.foul.size();
            if (size > 0) {
                Assets.Commentary.foul.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
            }
        }

        // set the player targets relative to foul zone
        // even before moving the ball itself
        match.ball.updateZone(match.foul.position.x, match.foul.position.y, 0, 0);
        match.updateTeamTactics();
        match.team[HOME].lineup.get(0).setTarget(0, match.team[HOME].side * (Const.GOAL_LINE - 8));
        match.team[AWAY].lineup.get(0).setTarget(0, match.team[AWAY].side * (Const.GOAL_LINE - 8));

        match.resetAutomaticInputDevices();
    }

    @Override
    void onResume() {
        matchRenderer.actionCamera.setSpeedMode(NORMAL);
        matchRenderer.actionCamera.setLimited(true, true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        allPlayersReachingTarget = true;
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 0; i < TEAM_SIZE; i++) {
                Player player = match.team[t].lineup.get(i);

                // wait for tackle and down states to finish
                if (player.checkState(STATE_TACKLE) || player.checkState(STATE_DOWN)) {
                    allPlayersReachingTarget = false;
                } else {
                    player.setState(STATE_REACH_TARGET);
                }
            }
        }

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.inFieldKeep();

            move = match.updatePlayers(true);
            match.updateTeamTactics();

            match.nextSubframe();

            matchRenderer.save();

            matchRenderer.updateCamera(STILL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (allPlayersReachingTarget && !move) {
            match.ball.setPosition(match.foul.position.x, match.foul.position.y, 0);
            match.ball.updatePrediction();

            fsm.pushAction(NEW_FOREGROUND, STATE_FREE_KICK);
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
