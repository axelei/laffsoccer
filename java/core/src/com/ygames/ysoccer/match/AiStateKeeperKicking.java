package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KEEPER_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_KICK;

class AiStateKeeperKicking extends AiState {

    AiStateKeeperKicking(Ai ai) {
        super(AiFsm.Id.STATE_KEEPER_KICKING, ai);
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = 0;
        ai.y0 = (timer > 0.5f * GLGame.VIRTUAL_REFRESH_RATE ? 1 : -1) * player.team.side;
        ai.fire10 = Emath.isIn(timer, 1.0f * GLGame.VIRTUAL_REFRESH_RATE, 1.05f * GLGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.fsm.getState();
        if (playerState.checkId(STATE_KEEPER_KICK_ANGLE)) {
            return null;
        }
        if (playerState.checkId(STATE_KICK)) {
            return null;
        }
        return ai.fsm.stateIdle;
    }
}
