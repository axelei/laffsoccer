package com.ysoccer.android.ysdemo.match;

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
                    return ai.fsm.stateKickingOff;

                case PlayerFsm.STATE_STAND_RUN:
                    return ai.fsm.statePositioning;

                case PlayerFsm.STATE_GOAL_KICK:
                    return ai.fsm.stateGoalKicking;

                case PlayerFsm.STATE_THROW_IN_ANGLE:
                    return ai.fsm.stateThrowingIn;

                case PlayerFsm.STATE_CORNER_KICK_ANGLE:
                    return ai.fsm.stateCornerKicking;

                case PlayerFsm.STATE_KEEPER_KICK_ANGLE:
                    return ai.fsm.stateKeeperKicking;
            }
        }
        return null;
    }
}
