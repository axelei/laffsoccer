package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector3;
import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_SEEKING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class AiStateSeeking extends AiState {

    AiStateSeeking(AiFsm fsm) {
        super(STATE_SEEKING, fsm);
    }

    @Override
    void doActions() {
        super.doActions();

        if (player.ballDistance > Const.BALL_R) {
            int d = player.frameDistance;
            if (d < Const.BALL_PREDICTION) {
                Vector3 b = player.ball.prediction[d];
                float a = EMath.aTan2(b.y - player.y, b.x - player.x);
                ai.x0 = Math.round(EMath.cos(a));
                ai.y0 = Math.round(EMath.sin(a));
            }
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

        // someone has got the ball
        if (player.ball.owner != null) {
            // player
            if (player.ball.owner == player) {
                return fsm.stateAttacking;
            }
            // mate
            if (player.ball.owner.team == player.team) {
                return fsm.statePositioning;
            }
        }

        // no longer the nearest
        if (player.team.near1 != player) {
            return fsm.statePositioning;
        }

        return null;
    }
}
