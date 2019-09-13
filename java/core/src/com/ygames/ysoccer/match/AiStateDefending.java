package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;
import com.ygames.ysoccer.math.Vector3;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_DEFENDING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class AiStateDefending extends AiState {

    AiStateDefending(AiFsm fsm) {
        super(STATE_DEFENDING, fsm);
    }

    @Override
    void doActions() {
        super.doActions();
        int d = player.frameDistance;
        if (d < Const.BALL_PREDICTION) {
            Vector3 b = player.ball.prediction[d];
            float a = Emath.aTan2(b.y - player.y, b.x - player.x);
            ai.x0 = Math.round(Emath.cos(a));
            ai.y0 = Math.round(Emath.sin(a));
        }
    }

    @Override
    State checkConditions() {
        // player has changed its state
        PlayerState playerState = player.getState();
        if ((playerState != null)
                && !playerState.checkId(STATE_STAND_RUN)) {
            return fsm.stateIdle;
        }

        // got the ball
        if (player.ball.owner != null) {
            // self
            if (player.ball.owner == player) {
                return fsm.stateAttacking;
            }
            // mate
            if (player.ball.owner.team == player.team) {
                return fsm.statePositioning;
            }
        }

        // no longer the best defender
        if (player.team.bestDefender != player) {
            return fsm.statePositioning;
        }

        return null;
    }
}
