package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_GOAL_KICKING;

class AiStateGoalKicking extends AiState {

    AiStateGoalKicking(AiFsm fsm) {
        super(STATE_GOAL_KICKING, fsm);
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = 0;
        ai.y0 = 0;
        ai.fire10 = EMath.isIn(timer, 1.0f * GLGame.VIRTUAL_REFRESH_RATE, 1.05f * GLGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        if (timer > 1.5f * GLGame.VIRTUAL_REFRESH_RATE) {
            return fsm.stateIdle;
        }
        return null;
    }
}
