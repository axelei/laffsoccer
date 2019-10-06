package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.badlogic.gdx.Input.Keys.F1;
import static com.ygames.ysoccer.match.ActionCamera.Mode.REACH_TARGET;
import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.Match.Period.PENALTIES;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_END;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_END_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HELP;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HIGHLIGHTS;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_REPLAY;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.SceneFsm.ActionType.NEW_FOREGROUND;

class MatchStateEndPositions extends MatchState {

    private boolean move;

    MatchStateEndPositions(MatchFsm fsm) {
        super(STATE_END_POSITIONS, fsm);
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
                .setTarget(0, 0)
                .setOffset(0, 0)
                .setSpeedMode(FAST);

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

            sceneRenderer.actionCamera.update(REACH_TARGET);

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

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
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
