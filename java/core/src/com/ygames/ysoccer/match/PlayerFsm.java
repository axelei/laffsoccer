package com.ygames.ysoccer.match;

class PlayerFsm extends Fsm {

    static final int STATE_IDLE = 1;
    static final int STATE_OUTSIDE = 2;
    static final int STATE_BENCH_SITTING = 3;

    static final int STATE_PHOTO = 6;
    static final int STATE_STAND_RUN = 7;
    static final int STATE_KICK = 8;
    static final int STATE_HEAD = 9;

    PlayerStateIdle stateIdle;
    PlayerStateOutside stateOutSide;
    PlayerStateBenchSitting stateBenchSitting;

    PlayerStatePhoto statePhoto;
    PlayerStateStandRun stateStandRun;
    PlayerStateKick stateKick;
    PlayerStateHead stateHead;

    public PlayerFsm(Player player) {
        addState(stateIdle = new PlayerStateIdle(player));
        addState(stateOutSide = new PlayerStateOutside(player));
        addState(stateBenchSitting = new PlayerStateBenchSitting(player));

        addState(statePhoto = new PlayerStatePhoto(player));
        addState(stateStandRun = new PlayerStateStandRun(player));
        addState(stateKick = new PlayerStateKick(player));
        addState(stateHead = new PlayerStateHead(player));
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }
}
