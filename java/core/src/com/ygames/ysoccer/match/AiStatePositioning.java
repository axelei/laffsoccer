package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class AiStatePositioning extends AiState {

    AiStatePositioning(Ai ai) {
        super(AiFsm.Id.STATE_POSITIONING, ai);
    }

    @Override
    void doActions() {
        super.doActions();
        if (player.targetDistance() > 20) {
            float a = player.targetAngle();
            ai.x0 = Math.round(Emath.cos(a));
            ai.y0 = Math.round(Emath.sin(a));
        } else {
            ai.x0 = 0;
            ai.y0 = 0;
        }
    }

    @Override
    State checkConditions() {
        PlayerState playerState = player.fsm.getState();
        if ((playerState != null)
                && !playerState.checkId(STATE_STAND_RUN)) {
            return ai.fsm.stateIdle;
        }

        if (player == player.team.near1) {
            if (player.match.ball.owner == null) {
                return ai.fsm.stateSeeking;
            } else if (player.match.ball.owner == player) {
                return ai.fsm.stateAttacking;
            }
        }

        if (player.team.bestDefender == player) {
            return ai.fsm.stateDefending;
        }

        return null;
    }
}
