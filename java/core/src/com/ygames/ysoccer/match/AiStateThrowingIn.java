package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_THROWING_IN;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_THROW_IN_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_THROW_IN_SPEED;

class AiStateThrowingIn extends AiState {

    AiStateThrowingIn(AiFsm fsm) {
        super(STATE_THROWING_IN, fsm);
    }

    @Override
    void doActions() {
        super.doActions();
        if (timer > 1.5f * GLGame.VIRTUAL_REFRESH_RATE) {
            ai.x0 = player.team.side;
        }
        ai.fire10 = EMath.isIn(timer, 2.0f * GLGame.VIRTUAL_REFRESH_RATE, 2.31f * GLGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.getState();
        if (playerState.checkId(STATE_THROW_IN_ANGLE)) {
            return null;
        }
        if (playerState.checkId(STATE_THROW_IN_SPEED)) {
            return null;
        }

        return fsm.stateIdle;
    }
}
