package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_ATTACKING;
import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_STAND_RUN;

class AiStateAttacking extends AiState {

    AiStateAttacking(AiFsm fsm) {
        super(STATE_ATTACKING, fsm);
    }

    @Override
    void doActions() {
        super.doActions();

        ai.x0 = -Emath.sgn(Math.round(player.match.ball.x / (Const.POST_X + Const.POST_R + Const.BALL_R)));
        ai.y0 = -player.team.side;

        player.searchFacingPlayer(false);
    }

    @Override
    State checkConditions() {
        // player has changed its state
        PlayerState playerState = player.getState();
        if (playerState != null
                && !playerState.checkId(STATE_STAND_RUN)) {
            return fsm.stateIdle;
        }

        // lost the ball
        if (player.match.ball.owner != player) {
            return fsm.stateSeeking;
        }

        // passing
        if (player.facingPlayer != null
                && (timer > 0.25f * GLGame.VIRTUAL_REFRESH_RATE)) {
            return fsm.statePassing;
        }

        // near the goal
        if (Emath.isIn(
                player.match.ball.y,
                -player.team.side * (Const.GOAL_LINE - 1.5f * Const.PENALTY_AREA_H),
                -player.team.side * Const.GOAL_LINE)
                && (player.match.ball.z < 4)) {
            return fsm.stateKicking;
        }

        return null;
    }
}
