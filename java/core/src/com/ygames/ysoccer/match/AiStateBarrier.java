package com.ygames.ysoccer.match;


import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.AiFsm.Id.AI_STATE_BARRIER;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BARRIER;

class AiStateBarrier extends AiState {

    private boolean jumped;

    AiStateBarrier(AiFsm fsm) {
        super(AI_STATE_BARRIER, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        jumped = false;
    }

    @Override
    void doActions() {
        super.doActions();

        ai.fire10 = false;

        if (!jumped && jumpingIsUseful()) {
            ai.fire10 = true;
            jumped = true;
        }
    }

    @Override
    State checkConditions() {
        if (!player.getState().checkId(STATE_BARRIER)) {
            return fsm.stateIdle;
        }

        return null;
    }

    private boolean jumpingIsUseful() {
        return player.ball.v > 0
                && player.ball.z > 5
                && EMath.angleDiff(player.a, player.ballAngle) < 45;
    }
}
