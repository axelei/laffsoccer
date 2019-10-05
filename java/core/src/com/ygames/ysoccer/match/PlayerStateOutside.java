package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_OUTSIDE;

class PlayerStateOutside extends PlayerState {

    private float v;

    PlayerStateOutside(PlayerFsm fsm) {
        super(STATE_OUTSIDE, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        v = 180 + EMath.rand(0, 30);
    }

    @Override
    void doActions() {
        super.doActions();
        if (player.isVisible && player.targetDistance() > 1.5f) {
            player.v = v;
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
