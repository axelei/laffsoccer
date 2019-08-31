package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;

class PlayerStateIdle extends PlayerState {

    PlayerStateIdle(PlayerFsm fsm) {
        super(STATE_IDLE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.v = 0;
        player.animationStandRun();
    }
}
