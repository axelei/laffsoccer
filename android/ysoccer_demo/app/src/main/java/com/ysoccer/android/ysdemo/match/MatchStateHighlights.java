package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateHighlights extends MatchState {

    private int subframe0;
    private int position;

    MatchStateHighlights(Match match) {
        super(match);
        id = MatchFsm.STATE_HIGHLIGHTS;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = false;
        match.renderer.displayBallOwner = false;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = false;
        match.renderer.displayWindVane = true;
        match.renderer.displayScore = false;
        match.renderer.displayStatistics = false;
        match.renderer.displayRadar = false;
        match.renderer.displayControls = false;

        // position of current frame inside the highlights vector
        position = 0;

        // store initial frame
        subframe0 = match.subframe;

        match.recorder.loadHighlight();
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        int speed = GLGame.SUBFRAMES;

        // slow motion
        if (match.glGame.touchInput.fire21) {
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
        if ((match.team[Match.HOME].fire1Up() != null)
                || (match.team[Match.AWAY].fire1Up() != null)) {
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
