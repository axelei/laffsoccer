package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_DOWN;

class PlayerStateDown extends PlayerState {

    PlayerStateDown(PlayerFsm fsm, Player player) {
        super(STATE_DOWN, fsm, player);
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.v = 0;
        player.fmy = 7;
    }

    @Override
    PlayerState checkConditions() {
        if (timer > 3f * Const.SECOND) {
            return player.fsm.stateStandRun;
        }
        return null;
    }
}
