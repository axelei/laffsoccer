package com.ysoccer.android.ysdemo.match;

import com.ysoccer.android.framework.impl.GLGame;
import com.ysoccer.android.framework.math.Emath;

class AiStateAttacking extends AiState {

    AiStateAttacking(Ai ai) {
        super(ai);
        id = AiFsm.STATE_ATTACKING;
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
        State playerState = player.fsm.getState();
        if (playerState != null
                && !playerState.checkId(PlayerFsm.STATE_STAND_RUN)) {
            return ai.fsm.stateIdle;
        }

        // lost the ball
        if (player.match.ball.owner != player) {
            return ai.fsm.stateSeeking;
        }

        // passing
        if (player.facingPlayer != null
                && (timer > 0.25f * GLGame.VIRTUAL_REFRESH_RATE)) {
            return ai.fsm.statePassing;
        }

        // near the goal
        if (Emath.isIn(
                player.match.ball.y,
                -player.team.side * (Const.GOAL_LINE - 1.5f * Const.PENALTY_AREA_H),
                -player.team.side * Const.GOAL_LINE)
                && (player.match.ball.z < 4)) {
            return ai.fsm.stateKicking;
        }

        return null;
    }
}
