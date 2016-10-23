package com.ysoccer.android.ysdemo.match;

public class PlayerFsm extends Fsm {

    static final int STATE_IDLE = 1;
    static final int STATE_OUTSIDE = 2;
    static final int STATE_BENCH_SITTING = 3;
    static final int STATE_BENCH_STANDING = 4;
    static final int STATE_BENCH_OUT = 5;
    static final int STATE_PHOTO = 6;
    static final int STATE_STAND_RUN = 7;
    static final int STATE_KICK = 8;
    static final int STATE_HEAD = 9;
    static final int STATE_TACKLE = 10;
    static final int STATE_REACH_TARGET = 11;
    static final int STATE_KICK_OFF = 12;
    static final int STATE_GOAL_KICK = 13;
    static final int STATE_THROW_IN_ANGLE = 14;
    static final int STATE_THROW_IN_SPEED = 15;
    static final int STATE_CORNER_KICK_ANGLE = 16;
    static final int STATE_CORNER_KICK_SPEED = 17;
    static final int STATE_GOAL_SCORER = 18;
    static final int STATE_GOAL_MATE = 19;
    static final int STATE_OWN_GOAL_SCORER = 20;

    static final int STATE_KEEPER_POSITIONING = 21;
    static final int STATE_KEEPER_DIVING_LOW_SINGLE = 22;
    static final int STATE_KEEPER_DIVING_LOW_DOUBLE = 23;
    static final int STATE_KEEPER_DIVING_MIDDLE_ONE = 24;
    static final int STATE_KEEPER_DIVING_MIDDLE_TWO = 25;
    static final int STATE_KEEPER_DIVING_HIGH_ONE = 26;
    static final int STATE_KEEPER_CATCHING_HIGH = 27;
    static final int STATE_KEEPER_CATCHING_LOW = 28;
    static final int STATE_KEEPER_KICK_ANGLE = 29;

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
    PlayerStateGoalKick stateGoalKick;
    PlayerStateThrowInAngle stateThrowInAngle;
    PlayerStateThrowInSpeed stateThrowInSpeed;
    PlayerStateCornerKickAngle stateCornerKickAngle;
    PlayerStateCornerKickSpeed stateCornerKickSpeed;
    PlayerStateGoalScorer stateGoalScorer;
    PlayerStateGoalMate stateGoalMate;
    PlayerStateOwnGoalScorer stateOwnGoalScorer;

    PlayerStateKeeperPositioning stateKeeperPositioning;
    PlayerStateKeeperDivingLowSingle stateKeeperDivingLowSingle;
    PlayerStateKeeperDivingLowDouble stateKeeperDivingLowDouble;
    PlayerStateKeeperDivingMiddleOne stateKeeperDivingMiddleOne;
    PlayerStateKeeperDivingMiddleTwo stateKeeperDivingMiddleTwo;
    PlayerStateKeeperDivingHighOne stateKeeperDivingHighOne;
    PlayerStateKeeperCatchingHigh stateKeeperCatchingHigh;
    PlayerStateKeeperCatchingLow stateKeeperCatchingLow;
    PlayerStateKeeperKickAngle stateKeeperKickAngle;

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
        addState(stateGoalKick = new PlayerStateGoalKick(player));
        addState(stateThrowInAngle = new PlayerStateThrowInAngle(player));
        addState(stateThrowInSpeed = new PlayerStateThrowInSpeed(player));
        addState(stateCornerKickAngle = new PlayerStateCornerKickAngle(player));
        addState(stateCornerKickSpeed = new PlayerStateCornerKickSpeed(player));
        addState(stateGoalScorer = new PlayerStateGoalScorer(player));
        addState(stateGoalMate = new PlayerStateGoalMate(player));
        addState(stateOwnGoalScorer = new PlayerStateOwnGoalScorer(player));

        addState(stateKeeperPositioning = new PlayerStateKeeperPositioning(player));
        addState(stateKeeperDivingLowSingle = new PlayerStateKeeperDivingLowSingle(player));
        addState(stateKeeperDivingLowDouble = new PlayerStateKeeperDivingLowDouble(player));
        addState(stateKeeperDivingMiddleOne = new PlayerStateKeeperDivingMiddleOne(player));
        addState(stateKeeperDivingMiddleTwo = new PlayerStateKeeperDivingMiddleTwo(player));
        addState(stateKeeperDivingHighOne = new PlayerStateKeeperDivingHighOne(player));
        addState(stateKeeperCatchingHigh = new PlayerStateKeeperCatchingHigh(player));
        addState(stateKeeperCatchingLow = new PlayerStateKeeperCatchingLow(player));
        addState(stateKeeperKickAngle = new PlayerStateKeeperKickAngle(player));
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }
}
