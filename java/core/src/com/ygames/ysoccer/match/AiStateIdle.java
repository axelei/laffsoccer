package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;

class AiStateIdle extends AiState {

    AiStateIdle(Ai ai) {
        super(AiFsm.Id.STATE_IDLE, ai);
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
        State playerState = player.fsm.getState();
        if (playerState != null) {
            switch (PlayerFsm.Id.values()[playerState.id]) {
                case STATE_KICK_OFF:
                     return ai.fsm.stateKickingOff;

                case STATE_STAND_RUN:
                     return ai.fsm.statePositioning;

                case STATE_GOAL_KICK:
                     return ai.fsm.stateGoalKicking;

                case STATE_THROW_IN_ANGLE:
                     return ai.fsm.stateThrowingIn;

                case STATE_CORNER_KICK_ANGLE:
                     return ai.fsm.stateCornerKicking;

                case STATE_KEEPER_KICK_ANGLE:
                     return ai.fsm.stateKeeperKicking;
            }
        }
        return null;
    }
}
