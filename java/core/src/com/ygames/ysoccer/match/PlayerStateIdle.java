package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_IDLE;

class PlayerStateIdle extends PlayerState {

    PlayerStateIdle(PlayerFsm fsm, Player player) {
        super(STATE_IDLE, fsm, player);
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.v = 0;
        player.animationStandRun();
    }
}
