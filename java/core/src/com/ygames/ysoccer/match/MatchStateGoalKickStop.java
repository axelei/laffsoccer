package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;

class MatchStateGoalKickStop extends MatchState {

    private int xSide;
    private int ySide;

    MatchStateGoalKickStop(MatchFsm fsm) {
        super(MatchFsm.STATE_GOAL_KICK_STOP, fsm);

        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
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

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL);
            matchRenderer.updateCameraY(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if ((match.ball.v < 5) && (match.ball.vz < 5)) {
            match.ball.setPosition((Const.GOAL_AREA_W / 2) * xSide, (Const.GOAL_LINE - Const.GOAL_AREA_H) * ySide, 0);
            match.ball.updatePrediction();

            fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_GOAL_KICK);
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
            fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_PAUSE);
            return;
        }

        InputDevice inputDevice;
        for (int t = HOME; t <= AWAY; t++) {
            inputDevice = match.team[t].fire2Down();
            if (inputDevice != null) {
                fsm.benchStatus.team = match.team[t];
                fsm.benchStatus.inputDevice = inputDevice;
                fsm.pushAction(MatchFsm.ActionType.HOLD_FOREGROUND, MatchFsm.STATE_BENCH_ENTER);
                return;
            }
        }
    }
}
