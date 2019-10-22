package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.EMath;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BARRIER;

class PlayerStateBarrier extends PlayerState {

    private int ballMovingTime;
    private float exitDelay;

    PlayerStateBarrier(PlayerFsm fsm) {
        super(STATE_BARRIER, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        ballMovingTime = 0;
        exitDelay = 0.01f * EMath.rand(45, 55) * Const.SECOND;
    }

    @Override
    void doActions() {
        super.doActions();

        if (ballMovingTime == 0) {
            if (ball.v > 0) {
                ballMovingTime = timer;
            }
        } else if (player.z == 0 && player.inputDevice.fire1Down()) {
            player.vz = 50 + 5 * player.skills.heading;
        }

        player.v = 0;
        player.fmx = Math.round(((player.a + 360) % 360) / 45) % 8;
        player.fmy = 10;
    }

    @Override
    State checkConditions() {
        if (ballMovingTime > 0
                && (timer - ballMovingTime) > exitDelay
                && player.z == 0) {
            return fsm.stateStandRun;
        }
        return null;
    }
}

