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
        addState(stateIdle = new PlayerStateIdle(player));
        addState(stateOutSide = new PlayerStateOutside(player));
        addState(stateBenchSitting = new PlayerStateBenchSitting(player));
        addState(stateBenchStanding = new PlayerStateBenchStanding(player));
        addState(stateBenchOut = new PlayerStateBenchOut(player));

        addState(statePhoto = new PlayerStatePhoto(player));
        addState(stateStandRun = new PlayerStateStandRun(player));
        addState(stateKick = new PlayerStateKick(player));
        addState(stateHead = new PlayerStateHead(player));
        addState(stateTackle = new PlayerStateTackle(player));
        addState(stateDown = new PlayerStateDown(player));
        addState(stateReachTarget = new PlayerStateReachTarget(player));
        addState(stateKickOff = new PlayerStateKickOff(player));
        addState(stateGoalKick = new PlayerStateGoalKick(player));
        addState(stateThrowInAngle = new PlayerStateThrowInAngle(player));
        addState(stateThrowInSpeed = new PlayerStateThrowInSpeed(player));
        addState(stateCornerKickAngle = new PlayerStateCornerKickAngle(player));
        addState(stateCornerKickSpeed = new PlayerStateCornerKickSpeed(player));
        addState(statePenaltyKickAngle = new PlayerStatePenaltyKickAngle(player));
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

    void setState(Id id) {
        super.setState(id.ordinal());
    }
}
