package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_GOAL;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_STARTING_POSITIONS;

class MatchStateGoal extends MatchState {

    private Goal goal;
    private boolean replayDone;

    MatchStateGoal(MatchFsm fsm) {
        super(STATE_GOAL, fsm);

        displayGoalScorer = true;
        displayTime = true;
        displayWindVane = true;
        displayRadar = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        replayDone = false;

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

            match.save();

            if ((match.ball.v > 0) || (match.ball.vz != 0)) {
                // follow ball
                matchRenderer.updateCameraX(ActionCamera.CF_NONE, ActionCamera.CS_NORMAL);
                matchRenderer.updateCameraY(ActionCamera.CF_BALL, ActionCamera.CS_NORMAL, 0, false);
            } else {
                // follow scorer
                matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, goal.player.data[match.subframe].x);
                matchRenderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, goal.player.data[match.subframe].y, false);
            }
            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if ((match.ball.v == 0) && (match.ball.vz == 0)
                && (timer > 3 * GLGame.VIRTUAL_REFRESH_RATE)) {

            if (match.settings.autoReplays && !replayDone) {
                replay();
                replayDone = true;
                return;
            } else {
                match.recorder.saveHighlight(matchRenderer);

                match.ball.setPosition(0, 0, 0);
                match.ball.updatePrediction();
                matchRenderer.actionCamera.offx = 0;
                matchRenderer.actionCamera.offy = 0;

                fsm.pushAction(NEW_FOREGROUND, STATE_STARTING_POSITIONS);
                return;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            quitMatch();
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            replay();
            replayDone = true;
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            fsm.pushAction(HOLD_FOREGROUND, STATE_PAUSE);
            return;
        }
    }
}
