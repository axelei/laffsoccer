package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_DOWN;

class PlayerStateDown extends PlayerState {

    PlayerStateDown(PlayerFsm fsm) {
        super(STATE_DOWN, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.v = 0;
        player.fmy = 7;
    }

    @Override
    PlayerState checkConditions() {
        if (timer > 1.5f * Const.SECOND) {
            return fsm.stateStandRun;
        }
        return null;
    }
}
