package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchStateCornerStop extends MatchState {

    private int cornerX;
    private int cornerY;

    MatchStateCornerStop(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_CORNER_STOP;

        displayControlledPlayer = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        if (match.team[HOME].side == -match.ball.ySide) {
            match.stats[HOME].cornersWon += 1;
        } else {
            match.stats[AWAY].cornersWon += 1;
        }

        Assets.Sounds.whistle.play(match.settings.soundVolume / 100f);

        cornerX = (Const.TOUCH_LINE - 12) * match.ball.xSide;
        cornerY = (Const.GOAL_LINE - 12) * match.ball.ySide;

        // set the player targets relative to corner zone
        // even before moving the ball itself
        match.ball.updateZone(cornerX, cornerY, 0, 0);
        match.updateTeamTactics();
        match.team[HOME].lineup.get(0).setTarget(0, match.team[HOME].side * (Const.GOAL_LINE - 8));
        match.team[AWAY].lineup.get(0).setTarget(0, match.team[AWAY].side * (Const.GOAL_LINE - 8));

        match.resetAutomaticInputDevices();

        match.setPlayersState(PlayerFsm.STATE_REACH_TARGET, null);
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
            match.ball.setPosition(cornerX, cornerY, 0);
            match.ball.updatePrediction();

            fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_CORNER_KICK);
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
