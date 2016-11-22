package com.ygames.ysoccer.match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchStateHighlights extends MatchState {

    private int subframe0;
    private int position;
    private boolean keySlow;
    private boolean slowMotion;

    MatchStateHighlights(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_HIGHLIGHTS;
    }

    @Override
    void entryActions() {
        super.entryActions();

        matchRenderer.displayControlledPlayer = false;
        matchRenderer.displayBallOwner = false;
        matchRenderer.displayGoalScorer = false;
        matchRenderer.displayTime = false;
        matchRenderer.displayWindVane = true;
        matchRenderer.displayScore = false;
        matchRenderer.displayStatistics = false;
        matchRenderer.displayRadar = false;

        // position of current frame inside the highlights vector
        position = 0;

        // slow motion
        keySlow = false;
        slowMotion = false;

        // store initial frame
        subframe0 = match.subframe;

        match.recorder.loadHighlight(matchRenderer);
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        // toggle slow-motion
        if (Gdx.input.isKeyPressed(Input.Keys.R) && !keySlow) {
            slowMotion = !slowMotion;
        }
        keySlow = Gdx.input.isKeyPressed(Input.Keys.R);

        int speed = GLGame.SUBFRAMES;

        // slow motion
        if (slowMotion) {
            speed = GLGame.SUBFRAMES / 2;
        }

        position += speed;
        if (position > Const.REPLAY_SUBFRAMES) {
            position = Const.REPLAY_SUBFRAMES;
        }

        match.subframe = (subframe0 + position) % Const.REPLAY_SUBFRAMES;
    }

    @Override
    void checkConditions() {

        // quit on touch
        if ((match.team[HOME].fire1Up() != null)
                || (match.team[AWAY].fire1Up() != null)) {
            match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_END);
            return;
        }

        // quit on finish
        if (position == Const.REPLAY_SUBFRAMES) {
            match.recorder.nextHighlight();
            if (match.recorder.hasEnded()) {
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_END);
                return;
            } else {
                match.fsm.pushAction(MatchFsm.ActionType.FADE_OUT);
                match.fsm.pushAction(MatchFsm.ActionType.NEW_FOREGROUND, MatchFsm.STATE_HIGHLIGHTS);
                match.fsm.pushAction(MatchFsm.ActionType.FADE_IN);
                return;
            }
        }
    }
}
