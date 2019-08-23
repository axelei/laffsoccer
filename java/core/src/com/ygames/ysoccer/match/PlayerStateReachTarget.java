package com.ygames.ysoccer.match;

class PlayerStateReachTarget extends PlayerState {

    PlayerStateReachTarget(Player player) {
        super(PlayerFsm.Id.STATE_REACH_TARGET, player);
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
