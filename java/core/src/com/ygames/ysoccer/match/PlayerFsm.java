package com.ygames.ysoccer.match;

class PlayerFsm extends Fsm {

    static final int STATE_IDLE = 1;
    static final int STATE_OUTSIDE = 2;
    static final int STATE_BENCH_SITTING = 3;

    static final int STATE_PHOTO = 6;
    static final int STATE_STAND_RUN = 7;
    static final int STATE_KICK = 8;
    static final int STATE_HEAD = 9;
    static final int STATE_TACKLE = 10;
    static final int STATE_REACH_TARGET = 11;
    static final int STATE_KICK_OFF = 12;

    PlayerStateIdle stateIdle;
    PlayerStateOutside stateOutSide;
    PlayerStateBenchSitting stateBenchSitting;

    PlayerStatePhoto statePhoto;
    PlayerStateStandRun stateStandRun;
    PlayerStateKick stateKick;
    PlayerStateHead stateHead;
    PlayerStateTackle stateTackle;
    PlayerStateReachTarget stateReachTarget;
    PlayerStateKickOff stateKickOff;

    public PlayerFsm(Player player) {
        addState(stateIdle = new PlayerStateIdle(player));
        addState(stateOutSide = new PlayerStateOutside(player));
        addState(stateBenchSitting = new PlayerStateBenchSitting(player));

        addState(statePhoto = new PlayerStatePhoto(player));
        addState(stateStandRun = new PlayerStateStandRun(player));
        addState(stateKick = new PlayerStateKick(player));
        addState(stateHead = new PlayerStateHead(player));
        addState(stateTackle = new PlayerStateTackle(player));
        addState(stateReachTarget = new PlayerStateReachTarget(player));
        addState(stateKickOff = new PlayerStateKickOff(player));
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }
}
