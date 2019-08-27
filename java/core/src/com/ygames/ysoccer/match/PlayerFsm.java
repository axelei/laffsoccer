package com.ygames.ysoccer.match;

class PlayerFsm extends Fsm {

    enum Id {
        STATE_IDLE,
        STATE_OUTSIDE,
        STATE_BENCH_SITTING,
        STATE_BENCH_STANDING,
        STATE_BENCH_OUT,

        STATE_PHOTO,
        STATE_STAND_RUN,
        STATE_KICK,
        STATE_HEAD,
        STATE_TACKLE,
        STATE_DOWN,
        STATE_REACH_TARGET,
        STATE_KICK_OFF,
        STATE_GOAL_KICK,
        STATE_THROW_IN_ANGLE,
        STATE_THROW_IN_SPEED,
        STATE_CORNER_KICK_ANGLE,
        STATE_CORNER_KICK_SPEED,
        STATE_FREE_KICK_ANGLE,
        STATE_PENALTY_KICK_ANGLE,
        STATE_GOAL_SCORER,
        STATE_GOAL_MATE,
        STATE_OWN_GOAL_SCORER,

        STATE_KEEPER_POSITIONING,
        STATE_KEEPER_DIVING_LOW_SINGLE,
        STATE_KEEPER_DIVING_LOW_DOUBLE,
        STATE_KEEPER_DIVING_MIDDLE_ONE,
        STATE_KEEPER_DIVING_MIDDLE_TWO,
        STATE_KEEPER_DIVING_HIGH_ONE,
        STATE_KEEPER_CATCHING_HIGH,
        STATE_KEEPER_CATCHING_LOW,
        STATE_KEEPER_KICK_ANGLE,
    }

    PlayerStateIdle stateIdle;
    PlayerStateOutside stateOutSide;
    PlayerStateBenchSitting stateBenchSitting;
    PlayerStateBenchStanding stateBenchStanding;
    PlayerStateBenchOut stateBenchOut;

    PlayerStatePhoto statePhoto;
    PlayerStateStandRun stateStandRun;
    PlayerStateKick stateKick;
    PlayerStateHead stateHead;
    PlayerStateTackle stateTackle;
    PlayerStateDown stateDown;
    PlayerStateReachTarget stateReachTarget;
    PlayerStateKickOff stateKickOff;
    PlayerStateGoalKick stateGoalKick;
    PlayerStateThrowInAngle stateThrowInAngle;
    PlayerStateThrowInSpeed stateThrowInSpeed;
    PlayerStateCornerKickAngle stateCornerKickAngle;
    PlayerStateCornerKickSpeed stateCornerKickSpeed;
    PlayerStateFreeKickAngle stateFreeKickAngle;
    PlayerStatePenaltyKickAngle statePenaltyKickAngle;
    PlayerStateGoalScorer stateGoalScorer;
    PlayerStateGoalMate stateGoalMate;
    PlayerStateOwnGoalScorer stateOwnGoalScorer;
    PlayerStateKeeperKickAngle stateKeeperKickAngle;

    PlayerStateKeeperPositioning stateKeeperPositioning;
    PlayerStateKeeperDivingLowSingle stateKeeperDivingLowSingle;
    PlayerStateKeeperDivingLowDouble stateKeeperDivingLowDouble;
    PlayerStateKeeperDivingMiddleOne stateKeeperDivingMiddleOne;
    PlayerStateKeeperDivingMiddleTwo stateKeeperDivingMiddleTwo;
    PlayerStateKeeperDivingHighOne stateKeeperDivingHighOne;
    PlayerStateKeeperCatchingHigh stateKeeperCatchingHigh;
    PlayerStateKeeperCatchingLow stateKeeperCatchingLow;

    public PlayerFsm(Player player) {
        stateIdle = new PlayerStateIdle(this, player);
        stateOutSide = new PlayerStateOutside(this, player);
        stateBenchSitting = new PlayerStateBenchSitting(this, player);
        stateBenchStanding = new PlayerStateBenchStanding(this, player);
        stateBenchOut = new PlayerStateBenchOut(this, player);

        statePhoto = new PlayerStatePhoto(this, player);
        stateStandRun = new PlayerStateStandRun(this, player);
        stateKick = new PlayerStateKick(this, player);
        stateHead = new PlayerStateHead(this, player);
        stateTackle = new PlayerStateTackle(this, player);
        stateDown = new PlayerStateDown(this, player);
        stateReachTarget = new PlayerStateReachTarget(this, player);
        stateKickOff = new PlayerStateKickOff(this, player);
        stateGoalKick = new PlayerStateGoalKick(this, player);
        stateThrowInAngle = new PlayerStateThrowInAngle(this, player);
        stateThrowInSpeed = new PlayerStateThrowInSpeed(this, player);
        stateCornerKickAngle = new PlayerStateCornerKickAngle(this, player);
        stateCornerKickSpeed = new PlayerStateCornerKickSpeed(this, player);
        stateFreeKickAngle = new PlayerStateFreeKickAngle(this, player);
        statePenaltyKickAngle = new PlayerStatePenaltyKickAngle(this, player);
        stateGoalScorer = new PlayerStateGoalScorer(this, player);
        stateGoalMate = new PlayerStateGoalMate(this, player);
        stateOwnGoalScorer = new PlayerStateOwnGoalScorer(this, player);

        stateKeeperPositioning = new PlayerStateKeeperPositioning(this, player);
        stateKeeperDivingLowSingle = new PlayerStateKeeperDivingLowSingle(this, player);
        stateKeeperDivingLowDouble = new PlayerStateKeeperDivingLowDouble(this, player);
        stateKeeperDivingMiddleOne = new PlayerStateKeeperDivingMiddleOne(this, player);
        stateKeeperDivingMiddleTwo = new PlayerStateKeeperDivingMiddleTwo(this, player);
        stateKeeperDivingHighOne = new PlayerStateKeeperDivingHighOne(this, player);
        stateKeeperCatchingHigh = new PlayerStateKeeperCatchingHigh(this, player);
        stateKeeperCatchingLow = new PlayerStateKeeperCatchingLow(this, player);
        stateKeeperKickAngle = new PlayerStateKeeperKickAngle(this, player);
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }

    void setState(Id id) {
        super.setState(id.ordinal());
    }
}
