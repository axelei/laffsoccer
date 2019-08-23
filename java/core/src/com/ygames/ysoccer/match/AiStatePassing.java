package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

class AiStatePassing extends AiState {

    private int duration;

    AiStatePassing(Ai ai) {
        super(AiFsm.Id.STATE_PASSING, ai);
    }

    @Override
    void entryActions() {
        super.entryActions();
        float d = Emath.dist(player.x, player.y, 0, Math.signum(player.y) * Const.GOAL_LINE);
        duration = (int) (0.01f * (4 + d / 50) * GLGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = ai.x1;
        ai.y0 = ai.y1;
        ai.fire10 = Emath.isIn(timer, 2, duration);
    }

    @Override
    State checkConditions() {
        if (timer > 0.5f * GLGame.VIRTUAL_REFRESH_RATE) {
            return ai.fsm.stateIdle;
        }

        return null;
    }
}
