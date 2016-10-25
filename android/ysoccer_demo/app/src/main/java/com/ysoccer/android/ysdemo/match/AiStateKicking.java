package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;

class AiStateKicking extends AiState {

    private int duration;

    AiStateKicking(Ai ai) {
        super(ai);
        id = AiFsm.STATE_KICKING;
    }

    @Override
    void entryActions() {
        super.entryActions();

        float d = Emath.dist(player.x, player.y, 0, Math.signum(player.y) * Const.GOAL_LINE);
        duration = (int) (0.01f * (player.match.random.nextInt(9) + d / 50) * GLGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = -Emath.sgn(Math.round(player.match.ball.x / (Const.POST_X + Const.POST_R + Const.BALL_R)));
        ai.y0 = -player.team.side;
        ai.fire10 = true;
    }

    @Override
    State checkConditions() {
        if (timer > duration) {
            return ai.fsm.stateIdle;
        }
        return null;
    }
}
