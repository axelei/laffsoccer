package com.ygames.ysoccer.match;

import static com.ygames.ysoccer.match.PlayerFsm.Id.STATE_REACH_TARGET;

class PlayerStateReachTarget extends PlayerState {

    PlayerStateReachTarget(PlayerFsm fsm, Player player) {
        super(STATE_REACH_TARGET, fsm, player);
    }

    @Override
    void doActions() {
        super.doActions();

        player.v = 200;
        player.a = player.targetAngle();

        player.animationStandRun();
    }

    @Override
    State checkConditions() {
        if (player.targetDistance() < 1) {
            return player.fsm.stateIdle;
        }
        return null;
    }

    @Override
    void exitActions() {
        player.v = 0;
        player.watchBall();
    }
}
