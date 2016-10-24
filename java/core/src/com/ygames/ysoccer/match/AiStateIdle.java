package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;

class AiStateIdle extends AiState {

    AiStateIdle(Ai ai) {
        super(ai);
        id = AiFsm.STATE_IDLE;
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
            switch (playerState.id) {
                case PlayerFsm.STATE_KICK_OFF:
                    // TODO
                    // return ai.fsm.stateKickingOff;

                case PlayerFsm.STATE_STAND_RUN:
                    // TODO
                    // return ai.fsm.statePositioning;

                case PlayerFsm.STATE_GOAL_KICK:
                    // TODO
                    // return ai.fsm.stateGoalKicking;

                case PlayerFsm.STATE_THROW_IN_ANGLE:
                    // TODO
                    // return ai.fsm.stateThrowingIn;

                case PlayerFsm.STATE_CORNER_KICK_ANGLE:
                    // TODO
                    // return ai.fsm.stateCornerKicking;

                case PlayerFsm.STATE_KEEPER_KICK_ANGLE:
                    // TODO
                    // return ai.fsm.stateKeeperKicking;
            }
        }
        return null;
    }
}
