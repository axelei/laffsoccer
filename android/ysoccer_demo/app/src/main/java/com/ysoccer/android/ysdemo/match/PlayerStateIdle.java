package com.ysoccer.android.ysdemo.match;


public class PlayerStateIdle extends PlayerState {

    public PlayerStateIdle(Player player) {
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
