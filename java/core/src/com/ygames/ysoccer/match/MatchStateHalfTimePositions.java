package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HALF_TIME_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HALF_TIME_WAIT;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;

class MatchStateHalfTimePositions extends MatchState {

    private boolean move;

    MatchStateHalfTimePositions(MatchFsm fsm) {
        super(STATE_HALF_TIME_POSITIONS, fsm);

        displayTime = true;
        displayWindVane = true;
        displayStatistics = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.ball.setPosition(0, 0, 0);
        match.ball.updatePrediction();

        matchRenderer.actionCamera.setOffset(0, 0);

        match.period = Match.Period.UNDEFINED;
        match.clock = match.length * 45 / 90;

        match.setPlayersTarget(Const.TOUCH_LINE + 80, 0);
        match.setPlayersState(STATE_OUTSIDE, null);
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

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);
            matchRenderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_FAST, 0);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (!move) {
            fsm.pushAction(NEW_FOREGROUND, STATE_HALF_TIME_WAIT);
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
    }
}
