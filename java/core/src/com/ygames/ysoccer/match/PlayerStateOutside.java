package com.ygames.ysoccer.match;

import com.ygames.ysoccer.math.Emath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;

class PlayerStateOutside extends PlayerState {

    PlayerStateOutside(PlayerFsm fsm) {
        super(STATE_OUTSIDE, fsm);
    }

    @Override
    void doActions() {
        super.doActions();
        if (player.isVisible && player.targetDistance() > 1.5f) {
            player.v = 160 + Emath.rand(0, 20) + 3 * player.skills.speed;
            player.a = player.targetAngle();
        } else {
            player.v = 0;
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
