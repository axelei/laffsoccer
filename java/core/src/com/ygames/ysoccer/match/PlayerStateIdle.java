package com.ygames.ysoccer.match;

class PlayerStateIdle extends PlayerState {

    PlayerStateIdle(Player player) {
        super(player);
        id = PlayerFsm.STATE_IDLE;
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.v = 0;
        player.animationStandRun();
    }
}
