package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_KICKING_OFF;

class AiStateKickingOff extends AiState {

    AiStateKickingOff(AiFsm fsm) {
        super(STATE_KICKING_OFF, fsm);
    }

    @Override
    void doActions() {
        super.doActions();
        ai.x0 = player.team.side;
        ai.fire10 = EMath.isIn(timer, 50, 56);
    }

    @Override
    State checkConditions() {
        if (timer > 70) {
            return fsm.stateIdle;
        }
        return null;
    }
}
