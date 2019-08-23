package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_THROW_IN_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_THROW_IN_SPEED;

class AiStateThrowingIn extends AiState {

    AiStateThrowingIn(Ai ai) {
        super(AiFsm.Id.STATE_THROWING_IN, ai);
    }

    @Override
    void doActions() {
        super.doActions();
        if (timer > 1.5f * GLGame.VIRTUAL_REFRESH_RATE) {
            ai.x0 = player.team.side;
        }
        ai.fire10 = Emath.isIn(timer, 2.0f * GLGame.VIRTUAL_REFRESH_RATE, 2.31f * GLGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.fsm.getState();
        if (playerState.checkId(STATE_THROW_IN_ANGLE)) {
            return null;
        }
        if (playerState.checkId(STATE_THROW_IN_SPEED)) {
            return null;
        }

        return ai.fsm.stateIdle;
    }
}
