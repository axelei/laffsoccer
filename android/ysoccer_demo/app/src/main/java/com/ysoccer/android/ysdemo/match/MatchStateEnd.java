package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;

class MatchStateEnd extends MatchState {

    MatchStateEnd(Match match) {
        super(match);
        id = MatchFsm.STATE_END;
    }

    @Override
    void entryActions() {
        super.entryActions();

        match.renderer.displayControlledPlayer = false;
        match.renderer.displayBallOwner = false;
        match.renderer.displayGoalScorer = false;
        match.renderer.displayTime = false;
        match.renderer.displayWindVane = false;
        match.renderer.displayScore = false;
        match.renderer.displayStatistics = true;
        match.renderer.displayRadar = false;
        match.renderer.displayControls = true;

        match.period = Match.Period.UNDEFINED;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.nextSubframe();

            match.save();

            match.renderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);
            match.renderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (match.glGame.touchInput.fire1Up()
                || (timer > 20 * GLGame.VIRTUAL_REFRESH_RATE)) {
            match.quit();
        }
    }
}
