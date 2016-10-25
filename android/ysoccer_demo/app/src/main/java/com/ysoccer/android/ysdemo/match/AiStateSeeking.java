package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.math.Emath;
import com.ysoccer.android.framework.math.Vector3;

class AiStateSeeking extends AiState {

    AiStateSeeking(Ai ai) {
        super(ai);
        id = AiFsm.STATE_SEEKING;
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

        // someone has got the ball
        if (player.match.ball.owner != null) {
            // player
            if (player.match.ball.owner == player) {
                return ai.fsm.stateAttacking;
            }
            // mate
            if (player.match.ball.owner.team == player.team) {
                return ai.fsm.statePositioning;
            }
        }

        // no longer the nearest
        if (player.team.near1 != player) {
            return ai.fsm.statePositioning;
        }

        return null;
    }
}
