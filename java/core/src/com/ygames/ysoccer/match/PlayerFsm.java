package com.ygames.ysoccer.match;

class PlayerFsm extends Fsm {

    public PlayerFsm(Player player) {
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }
}
