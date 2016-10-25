package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.GlGame;
import com.ygames.ysoccer.math.Emath;

class AiStateKeeperKicking extends AiState {

    AiStateKeeperKicking(Ai ai) {
        super(ai);
        id = AiFsm.STATE_KEEPER_KICKING;
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = 0;
        ai.y0 = (timer > 0.5f * GlGame.VIRTUAL_REFRESH_RATE ? 1 : -1) * player.team.side;
        ai.fire10 = Emath.isIn(timer, 1.0f * GlGame.VIRTUAL_REFRESH_RATE, 1.05f * GlGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.fsm.getState();
        if (playerState.checkId(PlayerFsm.STATE_KEEPER_KICK_ANGLE)) {
            return null;
        }
        if (playerState.checkId(PlayerFsm.STATE_KICK)) {
            return null;
        }
        return ai.fsm.stateIdle;
    }
}
