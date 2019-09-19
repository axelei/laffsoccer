package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.NORMAL;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_GOAL_KICK;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_GOAL_KICK_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateGoalKickStop extends MatchState {

    private int xSide;
    private int ySide;
    private Vector2 goalKickPosition;

    MatchStateGoalKickStop(MatchFsm fsm) {
        super(STATE_GOAL_KICK_STOP, fsm);

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;

        goalKickPosition = new Vector2();
    }

    @Override
    void entryActions() {
        super.entryActions();

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        xSide = match.ball.xSide;
        ySide = match.ball.ySide;

        match.resetAutomaticInputDevices();
        match.setPlayersState(STATE_REACH_TARGET, null);

        Team goalKickTeam = match.team[1 - match.ball.ownerLast.team.index];
        Player goalKickKeeper = goalKickTeam.lineup.get(0);
        goalKickKeeper.tx = match.ball.x / 4;
        goalKickKeeper.ty = goalKickTeam.side * (Const.GOAL_LINE - 8);

        Team opponentTeam = match.team[1 - goalKickTeam.index];
        Player opponentKeeper = opponentTeam.lineup.get(0);
        opponentKeeper.tx = 0;
        opponentKeeper.ty = opponentTeam.side * (Const.GOAL_LINE - 8);

        goalKickTeam.updateTactics(true);
        opponentTeam.updateTactics(true);

        goalKickPosition.set(
                (Const.GOAL_AREA_W / 2f) * xSide,
                (Const.GOAL_LINE - Const.GOAL_AREA_H) * ySide
        );
    }

    @Override
    void onResume() {
        sceneRenderer.actionCamera
                .setSpeedMode(NORMAL)
                .setLimited(true, true);

        match.setPointOfInterest(goalKickPosition);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

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

            match.updatePlayers(true);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update(FOLLOW_BALL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if ((match.ball.v < 5) && (match.ball.vz < 5)) {
            match.ball.setPosition(goalKickPosition.x, goalKickPosition.y, 0);
            match.ball.updatePrediction();

            fsm.pushAction(NEW_FOREGROUND, STATE_GOAL_KICK);
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
                getFsm().benchStatus.team = match.team[t];
                getFsm().benchStatus.inputDevice = inputDevice;
                fsm.pushAction(HOLD_FOREGROUND, STATE_BENCH_ENTER);
                return;
            }
        }
    }
}
