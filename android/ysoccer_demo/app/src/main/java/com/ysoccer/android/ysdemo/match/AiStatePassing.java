package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;

public class AiStatePassing extends AiState {

    int duration;

    public AiStatePassing(Ai ai) {
        super(ai);
        id = AiFsm.STATE_PASSING;
    }

    @Override
    void entryActions() {
        super.entryActions();
        float d = Emath.dist(player.x, player.y, 0, Math.signum(player.y)
                * Const.GOAL_LINE);
        duration = (int) (0.01f * (4 + d / 50) * GLGame.VIRTUAL_REFRATE);
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
        if (timer > 0.5f * GLGame.VIRTUAL_REFRATE) {
            return ai.fsm.stateIdle;
        }

        return null;
    }
}
