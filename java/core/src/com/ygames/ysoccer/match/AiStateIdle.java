package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;

import static com.ygames.ysoccer.match.AiFsm.Id.STATE_IDLE;

class AiStateIdle extends AiState {

    AiStateIdle(AiFsm fsm, Ai ai) {
        super(STATE_IDLE, fsm, ai);
    }

    @Override
    void doActions() {
        super.doActions();
        ai.x0 = 0;
        ai.y0 = 0;
        ai.fire10 = false;
        ai.fire20 = false;
    }

    @Override
    State checkConditions() {
        State playerState = player.getState();
        if (playerState != null) {
            switch (PlayerFsm.Id.values()[playerState.id]) {
                case STATE_KICK_OFF:
                     return fsm.stateKickingOff;

                case STATE_STAND_RUN:
                     return fsm.statePositioning;

                case STATE_GOAL_KICK:
                     return fsm.stateGoalKicking;

                case STATE_THROW_IN_ANGLE:
                     return fsm.stateThrowingIn;

                case STATE_CORNER_KICK_ANGLE:
                     return fsm.stateCornerKicking;

                case STATE_KEEPER_KICK_ANGLE:
                     return fsm.stateKeeperKicking;
            }
        }
        return null;
    }
}
