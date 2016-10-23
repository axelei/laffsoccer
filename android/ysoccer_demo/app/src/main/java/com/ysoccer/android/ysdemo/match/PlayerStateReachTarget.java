package com.ysoccer.android.ysdemo.match;

public class PlayerStateReachTarget extends PlayerState {

    public PlayerStateReachTarget(Player player) {
        super(player);
        id = PlayerFsm.STATE_REACH_TARGET;
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
