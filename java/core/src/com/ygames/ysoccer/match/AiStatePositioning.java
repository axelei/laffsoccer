package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_POSITIONING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class AiStatePositioning extends AiState {

    AiStatePositioning(AiFsm fsm) {
        super(STATE_POSITIONING, fsm);
    }

    @Override
    void doActions() {
        super.doActions();
        if (player.targetDistance() > 20) {
            float a = player.targetAngle();
            ai.x0 = Math.round(EMath.cos(a));
            ai.y0 = Math.round(EMath.sin(a));
        } else {
            ai.x0 = 0;
            ai.y0 = 0;
        }
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.getState();
        if ((playerState != null)
                && !playerState.checkId(STATE_STAND_RUN)) {
            return fsm.stateIdle;
        }

        if (player == player.team.near1) {
            if (player.ball.owner == null) {
                return fsm.stateSeeking;
            } else if (player.ball.owner == player) {
                return fsm.stateAttacking;
            }
        }

        if (player.team.bestDefender == player) {
            return fsm.stateDefending;
        }

        return null;
    }
}
