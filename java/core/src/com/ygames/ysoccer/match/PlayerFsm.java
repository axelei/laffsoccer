package com.ygames.ysoccer.match;

class PlayerFsm extends Fsm {

    static final int STATE_IDLE = 1;
    static final int STATE_OUTSIDE = 2;
    static final int STATE_BENCH_SITTING = 3;

    static final int STATE_PHOTO = 6;
    static final int STATE_STAND_RUN = 7;

    PlayerStateIdle stateIdle;
    PlayerStateOutside stateOutSide;
    PlayerStateBenchSitting stateBenchSitting;

    PlayerStatePhoto statePhoto;
    PlayerStateStandRun stateStandRun;

    public PlayerFsm(Player player) {
        addState(stateIdle = new PlayerStateIdle(player));
        addState(stateOutSide = new PlayerStateOutside(player));
        addState(stateBenchSitting = new PlayerStateBenchSitting(player));

        addState(statePhoto = new PlayerStatePhoto(player));
        addState(stateStandRun = new PlayerStateStandRun(player));
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }
}
