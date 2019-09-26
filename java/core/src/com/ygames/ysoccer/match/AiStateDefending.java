package com.ygames.ysoccer.match;

import com.badlogic.gdx.math.Vector3;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_DEFENDING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class AiStateDefending extends AiState {

    private static int MIN_UPDATE_INTERVAL = 12;
    private static int MAX_UPDATE_INTERVAL = 24;

    private int nextUpdate;

    AiStateDefending(AiFsm fsm) {
        super(STATE_DEFENDING, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        nextUpdate = Emath.rand(MIN_UPDATE_INTERVAL, MAX_UPDATE_INTERVAL);
    }

    @Override
    void doActions() {
        super.doActions();

        if (timer > nextUpdate) {
            int d = player.frameDistance;
            if (d < Const.BALL_PREDICTION) {
                Vector3 b = player.ball.prediction[d];
                float a = Emath.aTan2(b.y - player.y, b.x - player.x);
                ai.x0 = Math.round(Emath.cos(a));
                ai.y0 = Math.round(Emath.sin(a));
            }

            nextUpdate += Emath.rand(MIN_UPDATE_INTERVAL, MAX_UPDATE_INTERVAL);
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
