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
        addState(stateIdle = new PlayerStateIdle(this, player));
        addState(stateOutSide = new PlayerStateOutside(this, player));
        addState(stateBenchSitting = new PlayerStateBenchSitting(this, player));
        addState(stateBenchStanding = new PlayerStateBenchStanding(this, player));
        addState(stateBenchOut = new PlayerStateBenchOut(this, player));

        addState(statePhoto = new PlayerStatePhoto(this, player));
        addState(stateStandRun = new PlayerStateStandRun(this, player));
        addState(stateKick = new PlayerStateKick(this, player));
        addState(stateHead = new PlayerStateHead(this, player));
        addState(stateTackle = new PlayerStateTackle(this, player));
        addState(stateDown = new PlayerStateDown(this, player));
        addState(stateReachTarget = new PlayerStateReachTarget(this, player));
        addState(stateKickOff = new PlayerStateKickOff(this, player));
        addState(stateGoalKick = new PlayerStateGoalKick(this, player));
        addState(stateThrowInAngle = new PlayerStateThrowInAngle(this, player));
        addState(stateThrowInSpeed = new PlayerStateThrowInSpeed(this, player));
        addState(stateCornerKickAngle = new PlayerStateCornerKickAngle(this, player));
        addState(stateCornerKickSpeed = new PlayerStateCornerKickSpeed(this, player));
        addState(statePenaltyKickAngle = new PlayerStatePenaltyKickAngle(this, player));
        addState(stateGoalScorer = new PlayerStateGoalScorer(this, player));
        addState(stateGoalMate = new PlayerStateGoalMate(this, player));
        addState(stateOwnGoalScorer = new PlayerStateOwnGoalScorer(this, player));

        addState(stateKeeperPositioning = new PlayerStateKeeperPositioning(this, player));
        addState(stateKeeperDivingLowSingle = new PlayerStateKeeperDivingLowSingle(this, player));
        addState(stateKeeperDivingLowDouble = new PlayerStateKeeperDivingLowDouble(this, player));
        addState(stateKeeperDivingMiddleOne = new PlayerStateKeeperDivingMiddleOne(this, player));
        addState(stateKeeperDivingMiddleTwo = new PlayerStateKeeperDivingMiddleTwo(this, player));
        addState(stateKeeperDivingHighOne = new PlayerStateKeeperDivingHighOne(this, player));
        addState(stateKeeperCatchingHigh = new PlayerStateKeeperCatchingHigh(this, player));
        addState(stateKeeperCatchingLow = new PlayerStateKeeperCatchingLow(this, player));
        addState(stateKeeperKickAngle = new PlayerStateKeeperKickAngle(this, player));
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }

    void setState(Id id) {
        super.setState(id.ordinal());
    }
}
