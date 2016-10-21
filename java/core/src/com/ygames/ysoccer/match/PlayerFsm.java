package com.ygames.ysoccer.match;

class PlayerFsm extends Fsm {

    static final int STATE_IDLE = 1;
    static final int STATE_OUTSIDE = 2;
    static final int STATE_BENCH_SITTING = 3;

    PlayerStateIdle stateIdle;
    PlayerStateOutside stateOutSide;
    PlayerStateBenchSitting stateBenchSitting;

    public PlayerFsm(Player player) {
        addState(stateIdle = new PlayerStateIdle(player));
        addState(stateOutSide = new PlayerStateOutside(player));
        addState(stateBenchSitting = new PlayerStateBenchSitting(player));
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }
}
