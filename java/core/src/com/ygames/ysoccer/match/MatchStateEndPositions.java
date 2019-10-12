package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.Speed.FAST;
import static com.ygames.ysoccer.match.Match.Period.PENALTIES;
import static com.ygames.ysoccer.match.MatchFsm.STATE_END;
import static com.ygames.ysoccer.match.MatchFsm.STATE_HIGHLIGHTS;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateEndPositions extends MatchState {

    private boolean move;

    MatchStateEndPositions(MatchFsm fsm) {
        super(fsm);

        checkReplayKey = false;
        checkPauseKey = false;
        checkHelpKey = false;
        checkBenchCall = false;
    }

    @Override
    void entryActions() {
        super.entryActions();

        displayScore = (match.period != PENALTIES);
        displayPenaltiesScore = (match.period == PENALTIES);

        match.period = Match.Period.UNDEFINED;

        match.ball.setPosition(0, 0, 0);
        match.ball.updatePrediction();

        sceneRenderer.actionCamera
                .setMode(REACH_TARGET)
                .setTarget(0, 0)
                .setOffset(0, 0)
                .setSpeed(FAST);

        match.setLineupTarget(Const.TOUCH_LINE + 80, 0);
        match.setLineupState(STATE_OUTSIDE);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            if (match.subframe % GLGame.SUBFRAMES == 0) {
                match.updateAi();
            }

            move = match.updatePlayers(false);

            match.nextSubframe();

            sceneRenderer.save();

            sceneRenderer.actionCamera.update();

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    SceneFsm.Action[] checkConditions() {
        if (!move) {
            if (match.recorder.hasHighlights()) {
                match.recorder.restart();
                return newFadedAction(NEW_FOREGROUND, STATE_HIGHLIGHTS);
            } else {
                return newAction(NEW_FOREGROUND, STATE_END);
            }
        }

        return checkCommonConditions();
    }
}
