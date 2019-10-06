package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.InputDevice;

import static com.badlogic.gdx.Input.Keys.F1;
import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.NORMAL;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_BENCH_ENTER;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_CORNER_KICK;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_CORNER_STOP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HELP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_REPLAY;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateCornerStop extends MatchState {

    private final Vector2 cornerPosition;

    MatchStateCornerStop(MatchFsm fsm) {
        super(STATE_CORNER_STOP, fsm);

        displayControlledPlayer = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;

        cornerPosition = new Vector2();
    }

    @Override
    void entryActions() {
        super.entryActions();

        if (match.team[HOME].side == -match.ball.ySide) {
            match.stats[HOME].cornersWon += 1;
        } else {
            match.stats[AWAY].cornersWon += 1;
        }

        Assets.Sounds.whistle.play(Assets.Sounds.volume / 100f);

        cornerPosition.set((Const.TOUCH_LINE - 12) * match.ball.xSide, (Const.GOAL_LINE - 12) * match.ball.ySide);

        // set the player targets relative to corner zone
        // even before moving the ball itself
        match.ball.updateZone(cornerPosition.x, cornerPosition.y, 0, 0);
        match.updateTeamTactics();
        match.team[HOME].lineup.get(0).setTarget(0, match.team[HOME].side * (Const.GOAL_LINE - 8));
        match.team[AWAY].lineup.get(0).setTarget(0, match.team[AWAY].side * (Const.GOAL_LINE - 8));

        match.resetAutomaticInputDevices();

        match.setPlayersState(STATE_REACH_TARGET, null);
    }

    @Override
    void onResume() {
        match.setPointOfInterest(cornerPosition);

        sceneRenderer.actionCamera
                .setSpeedMode(NORMAL)
                .setLimited(true, true);
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
    SceneFsm.Action[] checkConditions() {
        if ((match.ball.v < 5) && (match.ball.vz < 5)) {
            match.ball.setPosition(cornerPosition);
            match.ball.updatePrediction();

            return newAction(NEW_FOREGROUND, STATE_CORNER_KICK);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            return newFadedAction(HOLD_FOREGROUND, STATE_REPLAY);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
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
