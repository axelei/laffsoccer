package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import java.util.ArrayList;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.NORMAL;
import static com.ygames.ysoccer.match.Const.PENALTY_SPOT_Y;
import static com.ygames.ysoccer.match.Const.TEAM_SIZE;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTY_KICK;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PENALTY_KICK_STOP;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_DOWN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_TACKLE;

class MatchStatePenaltyKickStop extends MatchState {

    private boolean allPlayersReachingTarget;
    private ArrayList<Player> playersReachingTarget;
    private boolean move;
    private Vector2 penaltyKickPosition;

    MatchStatePenaltyKickStop(MatchFsm fsm) {
        super(STATE_PENALTY_KICK_STOP, fsm);

        displayControlledPlayer = true;
        displayBallOwner = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;

        playersReachingTarget = new ArrayList<>();
        penaltyKickPosition = new Vector2();
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        if (match.settings.commentary) {
            int size = Assets.Commentary.penalty.size();
            if (size > 0) {
                Assets.Commentary.penalty.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
            }
        }

        Player penaltyKicker = match.foul.opponent.team.lastOfLineup();
        match.foul = null;
        match.createPenalty(penaltyKicker, match.ball.ySide);


        // set the player targets relative to penalty
        // even before moving the ball itself
        match.ball.updateZone(0, match.penalty.side * PENALTY_SPOT_Y, 0, 0);
        match.updateTeamTactics();
        match.team[HOME].lineup.get(0).setTarget(0, match.team[HOME].side * (Const.GOAL_LINE - 8));
        match.team[AWAY].lineup.get(0).setTarget(0, match.team[AWAY].side * (Const.GOAL_LINE - 8));
        for (int t = HOME; t <= AWAY; t++) {
            for (int i = 1; i < TEAM_SIZE; i++) {
                Player player = match.team[t].lineup.get(i);
                player.tx = player.tx >= 0 ? Math.max(player.tx, 100) : -Math.max(-player.tx, 100);
                player.ty = Math.signum(player.ty) * Math.min(Math.abs(player.ty), Const.GOAL_LINE - Const.PENALTY_AREA_H - 4);
            }
        }

        penaltyKicker.setTarget(-40 * match.ball.ySide, match.penalty.side * (PENALTY_SPOT_Y - 45));
        penaltyKickPosition.set(0, match.penalty.side * PENALTY_SPOT_Y);

        match.resetAutomaticInputDevices();

        allPlayersReachingTarget = false;
        playersReachingTarget.clear();
    }

    @Override
    void onResume() {
        matchRenderer.actionCamera.setTarget(penaltyKickPosition.x, penaltyKickPosition.y);
        matchRenderer.actionCamera.setSpeedMode(NORMAL);
        matchRenderer.actionCamera.setLimited(true, true);

        match.setPointOfInterest(penaltyKickPosition);
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
                } else if (!playersReachingTarget.contains(player)) {
                    player.setState(STATE_REACH_TARGET);
                    playersReachingTarget.add(player);
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
            match.ball.collisionGoal();
            match.ball.collisionJumpers();
            match.ball.collisionNetOut();
            match.ball.collisionNet();

            move = match.updatePlayers(true);

            match.nextSubframe();

            matchRenderer.save();

            matchRenderer.updateCamera(REACH_TARGET);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (allPlayersReachingTarget && !move) {
            match.ball.setPosition(penaltyKickPosition.x, penaltyKickPosition.y, 0);
            match.ball.updatePrediction();

            fsm.pushAction(NEW_FOREGROUND, STATE_PENALTY_KICK);
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
