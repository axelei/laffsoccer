package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_BARRIER;

class PlayerStateBarrier extends PlayerState {

    private int ballMovingTime;

    PlayerStateBarrier(PlayerFsm fsm) {
        super(STATE_BARRIER, fsm);
    }

    @Override
    void entryActions() {
        super.entryActions();

        ballMovingTime = 0;
    }

    @Override
    void doActions() {
        super.doActions();

        if (ballMovingTime == 0 && ball.v > 0) {
            ballMovingTime = timer;
        }

        player.v = 0;
        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (ballMovingTime != 0 && (timer - ballMovingTime) > 0.5f * Const.SECOND) {
            return fsm.stateStandRun;
        }
        return null;
    }
}

