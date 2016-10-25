package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.framework.math.Vector3;

class AiStateDefending extends AiState {

    AiStateDefending(Ai ai) {
        super(ai);
        id = AiFsm.STATE_DEFENDING;
    }

    @Override
    void doActions() {
        super.doActions();
        int d = player.frameDistance;
        if (d < Const.BALL_PREDICTION) {
            Vector3 b = player.match.ball.prediction[d];
            float a = Emath.aTan2(b.y - player.y, b.x - player.x);
            ai.x0 = Math.round(Emath.cos(a));
            ai.y0 = Math.round(Emath.sin(a));
        }
    }

    @Override
    State checkConditions() {
        // player has changed its state
        State playerState = player.fsm.getState();
        if ((playerState != null)
                && !playerState.checkId(PlayerFsm.STATE_STAND_RUN)) {
            return ai.fsm.stateIdle;
        }

        // got the ball
        if (player.match.ball.owner != null) {
            // self
            if (player.match.ball.owner == player) {
                return ai.fsm.stateAttacking;
            }
            // mate
            if (player.match.ball.owner.team == player.team) {
                return ai.fsm.statePositioning;
            }
        }

        // no longer the best defender
        if (player.team.bestDefender != player) {
            return ai.fsm.statePositioning;
        }

        return null;
    }
}
