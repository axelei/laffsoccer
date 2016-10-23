package com.ysoccer.android.ysdemo.match;

public class PlayerStateOutside extends PlayerState {

    public PlayerStateOutside(Player player) {
        super(player);
        id = PlayerFsm.STATE_OUTSIDE;
    }

    @Override
    void doActions() {
        super.doActions();
        if (player.isVisible && player.targetDistance() > 1.5f) {
            player.v = 200.0f;
            player.a = player.targetAngle();
        } else {
            player.v = 0.0f;
            player.isVisible = false;
        }

        // animation
        player.fmx = Math.round(((player.a + 360.0f) % 360.0f) / 45.0f) % 8;
        player.animationStandRun();
    }

    @Override
    void exitActions() {
        super.exitActions();
        player.isVisible = true;
    }

}
