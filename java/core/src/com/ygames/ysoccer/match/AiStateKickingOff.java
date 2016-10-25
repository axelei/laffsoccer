package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.math.Emath;

class AiStateKickingOff extends AiState {

    AiStateKickingOff(Ai ai) {
        super(ai);
        id = AiFsm.STATE_KICKING_OFF;
    }

    @Override
    void doActions() {
        super.doActions();
        ai.x0 = player.team.side;
        ai.fire10 = Emath.isIn(timer, 1.0f * GlGame.VIRTUAL_REFRESH_RATE, 1.05f * GlGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        if (timer > 1.2f * GlGame.VIRTUAL_REFRESH_RATE) {
            return ai.fsm.stateIdle;
        }
        return null;
    }
}
