package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.ActionCamera.SpeedMode.FAST;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_IN;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.FADE_OUT;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.HOLD_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.ActionType.NEW_FOREGROUND;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_END;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_END_POSITIONS;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_HIGHLIGHTS;
import static com.ygames.ysoccer.match.MatchFsm.Id.STATE_PAUSE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;

class MatchStateEndPositions extends MatchState {

    boolean move;

    MatchStateEndPositions(MatchFsm fsm) {
        super(STATE_END_POSITIONS, fsm);

        displayScore = true;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.period = Match.Period.UNDEFINED;

        match.ball.setPosition(0, 0, 0);
        match.ball.updatePrediction();

        matchRenderer.actionCamera.setOffset(0, 0);
        matchRenderer.actionCamera.setSpeedMode(FAST);

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

            matchRenderer.updateCameraX(ActionCamera.CF_TARGET, 0);
            matchRenderer.updateCameraY(ActionCamera.CF_TARGET, 0);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (!move) {
            if (match.recorder.hasHighlights()) {
                match.recorder.restart();
                fsm.pushAction(FADE_OUT);
                fsm.pushAction(NEW_FOREGROUND, STATE_HIGHLIGHTS);
                fsm.pushAction(FADE_IN);
                return;
            } else {
                fsm.pushAction(NEW_FOREGROUND, STATE_END);
                return;
            }
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
