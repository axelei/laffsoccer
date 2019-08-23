package com.ygames.ysoccer.match;

class PlayerStateOutside extends PlayerState {

    PlayerStateOutside(Player player) {
        super(PlayerFsm.STATE_OUTSIDE, player);
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
