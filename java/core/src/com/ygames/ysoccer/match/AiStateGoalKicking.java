package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.math.Emath;

class AiStateGoalKicking extends AiState {

    AiStateGoalKicking(Ai ai) {
        super(ai);
        id = AiFsm.STATE_GOAL_KICKING;
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = 0;
        ai.y0 = 0;
        ai.fire10 = Emath.isIn(timer, 1.0f * GlGame.VIRTUAL_REFRESH_RATE, 1.05f * GlGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        if (timer > 1.5f * GlGame.VIRTUAL_REFRESH_RATE) {
            return ai.fsm.stateIdle;
        }
        return null;
    }
}
