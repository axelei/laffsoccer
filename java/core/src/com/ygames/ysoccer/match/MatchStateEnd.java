package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

class MatchStateEnd extends MatchState {

    MatchStateEnd(MatchFsm fsm) {
        super(fsm);
        id = MatchFsm.STATE_END;
    }

    @Override
    void entryActions() {
        super.entryActions();

        matchRenderer.displayControlledPlayer = false;
        matchRenderer.displayBallOwner = false;
        matchRenderer.displayGoalScorer = false;
        matchRenderer.displayTime = false;
        matchRenderer.displayWindVane = false;
        matchRenderer.displayScore = false;
        matchRenderer.displayStatistics = true;
        matchRenderer.displayRadar = false;

        match.period = Match.Period.UNDEFINED;
    }

    @Override
    void doActions(float deltaTime) {
        super.doActions(deltaTime);

        float timeLeft = deltaTime;
        while (timeLeft >= GLGame.SUBFRAME_DURATION) {

            match.nextSubframe();

            match.save();

            matchRenderer.updateCameraX(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);
            matchRenderer.updateCameraY(ActionCamera.CF_TARGET, ActionCamera.CS_NORMAL, 0);

            timeLeft -= GLGame.SUBFRAME_DURATION;
        }
    }

    @Override
    void checkConditions() {
        if (match.team[HOME].fire1Up() != null
                || match.team[AWAY].fire1Up() != null
                || timer > 20 * GLGame.VIRTUAL_REFRESH_RATE) {
            match.quit();
        }
    }
}
