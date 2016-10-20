package com.ygames.ysoccer.match;

class PlayerFsm extends Fsm {

    static final int STATE_IDLE = 1;
    static final int STATE_OUTSIDE = 2;

    PlayerStateIdle stateIdle;
    PlayerStateOutside stateOutSide;

    public PlayerFsm(Player player) {
        addState(stateIdle = new PlayerStateIdle(player));
        addState(stateOutSide = new PlayerStateOutside(player));
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }
}
