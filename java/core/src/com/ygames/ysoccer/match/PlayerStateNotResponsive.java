package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_NOT_RESPONSIVE;

class PlayerStateNotResponsive extends PlayerState {

    PlayerStateNotResponsive(PlayerFsm fsm) {
        super(STATE_NOT_RESPONSIVE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (timer > 0.2f * Const.SECOND) {
            return fsm.stateStandRun;
        }
        return null;
    }
}
