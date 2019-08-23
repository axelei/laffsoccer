package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_KICKING_OFF;

class AiStateKickingOff extends AiState {

    AiStateKickingOff(AiFsm fsm, Ai ai) {
        super(STATE_KICKING_OFF, fsm, ai);
    }

    @Override
    void doActions() {
        super.doActions();
        ai.x0 = player.team.side;
        ai.fire10 = Emath.isIn(timer, 1.0f * GLGame.VIRTUAL_REFRESH_RATE, 1.05f * GLGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        if (timer > 1.2f * GLGame.VIRTUAL_REFRESH_RATE) {
            return fsm.stateIdle;
        }
        return null;
    }
}
