package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.math.Emath;

class AiStateCornerKicking extends AiState {

    AiStateCornerKicking(Ai ai) {
        super(ai);
        id = AiFsm.STATE_CORNER_KICKING;
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = Emath.isIn(timer, 0.5f * GlGame.VIRTUAL_REFRESH_RATE, 0.55f * GlGame.VIRTUAL_REFRESH_RATE) ? player.team.side : 0;
        ai.y0 = 0;
        ai.fire10 = Emath.isIn(timer, 1.0f * GlGame.VIRTUAL_REFRESH_RATE, 1.05f * GlGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.fsm.getState();
        if (playerState.checkId(PlayerFsm.STATE_CORNER_KICK_ANGLE)) {
            return null;
        }
        if (playerState.checkId(PlayerFsm.STATE_CORNER_KICK_SPEED)) {
            return null;
        }
        return ai.fsm.stateIdle;
    }
}
