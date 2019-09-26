package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.framework.GLGame.VIRTUAL_REFRESH_RATE;
import static com.ygames.ysoccer.match.AiFsm.Id.STATE_PASSING;

class AiStatePassing extends AiState {

    private int duration;

    AiStatePassing(AiFsm fsm) {
        super(STATE_PASSING, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        ai.fire10 = false;
        duration = 8;
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = ai.x1;
        ai.y0 = ai.y1;
        ai.fire10 = timer <= duration;
    }

    @Override
    State checkConditions() {
        if (timer > VIRTUAL_REFRESH_RATE / 2) {
            return fsm.stateIdle;
        }

        return null;
    }
}
