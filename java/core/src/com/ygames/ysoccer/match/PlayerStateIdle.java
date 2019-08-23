package com.ygames.ysoccer.match;

class PlayerStateIdle extends PlayerState {

    PlayerStateIdle(Player player) {
        super(PlayerFsm.STATE_IDLE, player);
    }

    @Override
    void entryActions() {
        super.entryActions();
        player.v = 0;
        player.animationStandRun();
    }
}
