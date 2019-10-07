package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.badlogic.gdx.Input.Keys.F1;
import static com.ygames.ysoccer.match.ActionCamera.Mode.FOLLOW_BALL;
import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HELP;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.STATE_REPLAY;
import static com.ygames.ysoccer.match.MatchFsm.STATE_STARTING_POSITIONS;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateGoal extends MatchState {

    private Goal goal;
    private boolean replayDone;
    private boolean recordingDone;

    MatchStateGoal(MatchFsm fsm) {
        super(fsm);

        displayGoalScorer = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;

        checkReplayKey = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        replayDone = false;
        recordingDone = false;

        Assets.Sounds.homeGoal.play(Assets.Sounds.volume / 100f);

        goal = match.goals.get(match.goals.size() - 1);

        if (match.settings.commentary) {
            if (goal.type == Goal.Type.OWN_GOAL) {
                int size = Assets.Commentary.ownGoal.size();
                if (size > 0) {
                    Assets.Commentary.ownGoal.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
                }
            } else {
                int size = Assets.Commentary.goal.size();
                if (size > 0) {
                    Assets.Commentary.goal.get(Assets.random.nextInt(size)).play(Assets.Sounds.volume / 100f);
                }
            }
        }

        if (match.team[HOME].side == match.ball.ySide) {
            match.kickOffTeam = HOME;
        } else if (match.team[AWAY].side == match.ball.ySide) {
            match.kickOffTeam = AWAY;
        } else {
            throw new RuntimeException("cannot decide kick_off_team!");
        }

        match.resetAutomaticInputDevices();

        match.setPointOfInterest(match.ball.x, match.ball.y);

        sceneRenderer.actionCamera.setLimited(true, true);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // set states
        if (goal.type == Goal.Type.OWN_GOAL) {
            match.setStatesForOwnGoal(goal);
        } else {
            match.setStatesForGoal(goal);
        }

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            match.updateBall();
            match.ball.collisionGoal();
            match.ball.collisionNet();

            match.updatePlayers(true);

            match.nextSubframe();

            sceneRenderer.save();

            if ((match.ball.v > 0) || (match.ball.vz != 0)) {
                // follow ball
                sceneRenderer.actionCamera.update(FOLLOW_BALL);
            } else {
                // follow scorer
                sceneRenderer.actionCamera
                        .setSpeedMode(FAST)
                        .setTarget(goal.player.data[match.subframe].x, goal.player.data[match.subframe].y)
                        .update(REACH_TARGET);
            }
            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if ((match.ball.v == 0) && (match.ball.vz == 0)
                && (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE)) {

            if (!recordingDone) {
                match.recorder.saveHighlight(sceneRenderer);
                recordingDone = true;
            }

            if (match.getSettings().autoReplays && !replayDone) {
                replayDone = true;
                return newFadedAction(HOLD_FOREGROUND, STATE_REPLAY);
            } else {
                match.ball.setPosition(0, 0, 0);
                match.ball.updatePrediction();
                sceneRenderer.actionCamera.setOffset(0, 0);

                return newAction(NEW_FOREGROUND, STATE_STARTING_POSITIONS);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replayDone = true;
            return newFadedAction(HOLD_FOREGROUND, STATE_REPLAY);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            return newAction(HOLD_FOREGROUND, STATE_PAUSE);
        }

        if (Gdx.input.isKeyPressed(F1)) {
            return newAction(HOLD_FOREGROUND, STATE_HELP);
        }

        return checkCommonConditions();
    }
}
