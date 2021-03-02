package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.STILL;
import static com.ygames.ysoccer.match.Const.SECOND;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;
import static com.ygames.ysoccer.match.Match.PenaltyState.MISSED;
import static com.ygames.ysoccer.match.Match.PenaltyState.SCORED;
import static com.ygames.ysoccer.match.MatchFsm.STATE_END_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.STATE_FINAL_CELEBRATION;
import static com.ygames.ysoccer.match.MatchFsm.STATE_PENALTIES;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_POSITIONING;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStatePenaltiesEnd extends MatchState {

    private boolean goalLineCrossed;
    private boolean isGoal;
    private Player keeper;

    MatchStatePenaltiesEnd(MatchFsm fsm) {
        super(fsm);

        displayWindVane = true;
        displayPenaltiesScore = true;

        checkBenchCall = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        goalLineCrossed = false;
        isGoal = false;
        keeper = match.team[1 - match.penaltyKickingTeam].lineupAtPosition(0);

        match.resetAutomaticInputDevices();
    }

    @Override
    void onResume() {
        super.onResume();

        sceneRenderer.actionCamera.setMode(STILL);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
                match.updateFrameDistance();
            }

            match.updateBall();
            if (!goalLineCrossed && !isGoal
                    && match.ball.y * match.ball.ySide >= (Const.GOAL_LINE + Const.BALL_R)
                    && EMath.isIn(match.ball.x, -Const.POST_X, Const.POST_X)
                    && (match.ball.z <= Const.CROSSBAR_H)) {
                isGoal = true;
                Assets.Sounds.homeGoal.play(Assets.Sounds.volume / 100f);
            }

            if (match.ball.y * match.ball.ySide >= (Const.GOAL_LINE + Const.BALL_R)) {
                goalLineCrossed = true;
            }

            // if ball crosses the goal line or comes back, keeper has nothing more to do
            if (goalLineCrossed || EMath.sin(match.ball.a) > 0) {
                if (keeper.checkState(STATE_KEEPER_POSITIONING)) {
                    keeper.setState(STATE_IDLE);
                }
            }

            // if keeper catches the ball, has nothing more to do
            if (match.ball.holder == keeper) {
                if (keeper.checkState(STATE_KEEPER_KICK_ANGLE)) {
                    keeper.setState(STATE_IDLE);
                }
            }

            match.ball.collisionGoal();
            match.ball.collisionNet();

            match.updatePlayers(true);

            if ((match.subframe % GLGame.VIRTUAL_REFRESH_RATE) == 0) {
                match.ball.updatePrediction();
            }

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {

        if ((match.ball.v == 0) && (match.ball.vz == 0)) {
            if (isGoal) {
                match.penalty.setState(SCORED);
            } else {
                match.penalty.setState(MISSED);
            }

            if (timer > 3 * SECOND) {

                match.ball.setPosition(0, -Const.PENALTY_SPOT_Y, 0);
                match.ball.updatePrediction();

                if (haveWinner()) {
                    match.setResult(match.penaltiesScore(HOME), match.penaltiesScore(AWAY), Match.ResultType.AFTER_PENALTIES);
                    getFsm().matchCompleted = true;

                    if (match.competition.getFinalWinner() != null) {
                        return newAction(NEW_FOREGROUND, STATE_FINAL_CELEBRATION);
                    } else {
                        return newAction(NEW_FOREGROUND, STATE_END_POSITIONS);
                    }
                } else {
                    return newAction(NEW_FOREGROUND, STATE_PENALTIES);
                }
            }
        }

        return checkCommonConditions();
    }

    private boolean haveWinner() {
        // 1) home team cannot be reached
        if (match.penaltiesScore(HOME) > match.penaltiesPotentialScore(AWAY)) return true;

        // 2) away team cannot be reached
        if (match.penaltiesScore(AWAY) > match.penaltiesPotentialScore(HOME)) return true;

        // 3) all penalties have been kicked and score is not the same
        return match.penaltiesLeft(HOME) == 0
                && match.penaltiesLeft(AWAY) == 0
                && match.penaltiesScore(HOME) != match.penaltiesScore(AWAY);
    }
}
