package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_CORNER_KICKING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_CORNER_KICK_ANGLE;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_CORNER_KICK_SPEED;

class AiStateCornerKicking extends AiState {

    AiStateCornerKicking(AiFsm fsm) {
        super(STATE_CORNER_KICKING, fsm);
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = EMath.isIn(timer, 0.5f * GLGame.VIRTUAL_REFRESH_RATE, 0.55f * GLGame.VIRTUAL_REFRESH_RATE) ? player.team.side : 0;
        ai.y0 = 0;
        ai.fire10 = EMath.isIn(timer, 1.0f * GLGame.VIRTUAL_REFRESH_RATE, 1.05f * GLGame.VIRTUAL_REFRESH_RATE);
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.getState();
        if (playerState.checkId(STATE_CORNER_KICK_ANGLE)) {
            return null;
        }
        if (playerState.checkId(STATE_CORNER_KICK_SPEED)) {
            return null;
        }
        return fsm.stateIdle;
    }
}
