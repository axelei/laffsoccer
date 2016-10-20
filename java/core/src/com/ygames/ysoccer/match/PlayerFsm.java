package com.ygames.ysoccer.match;

class PlayerFsm extends Fsm {

    static final int STATE_IDLE = 1;

    PlayerStateIdle stateIdle;

    public PlayerFsm(Player player) {
        addState(stateIdle = new PlayerStateIdle(player));
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }
}
