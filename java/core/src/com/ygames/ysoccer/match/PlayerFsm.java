package com.ygames.ysoccer.match;

class PlayerFsm extends Fsm {

    enum Id {
        STATE_CELEBRATION,
        STATE_IDLE,
        STATE_OUTSIDE,
        STATE_BENCH_SITTING,
        STATE_BENCH_STANDING,
        STATE_BENCH_OUT,

        STATE_PHOTO,
        STATE_STAND_RUN,
        STATE_SUBSTITUTED,
        STATE_KICK,
        STATE_HEAD,
        STATE_TACKLE,
        STATE_DOWN,
        STATE_FINAL_CELEBRATION,
        STATE_REACH_TARGET,
        STATE_KICK_OFF,
        STATE_NOT_RESPONSIVE,
        STATE_GOAL_KICK,
        STATE_THROW_IN_ANGLE,
        STATE_THROW_IN_SPEED,
        STATE_CORNER_KICK_ANGLE,
        STATE_CORNER_KICK_SPEED,
        STATE_FREE_KICK_ANGLE,
        STATE_FREE_KICK_SPEED,
        STATE_BARRIER,
        STATE_PENALTY_KICK_ANGLE,
        STATE_PENALTY_KICK_SPEED,
        STATE_GOAL_SCORER,
        STATE_GOAL_MATE,
        STATE_OWN_GOAL_SCORER,
        STATE_YELLOW_CARD,
        STATE_RED_CARD,
        STATE_SENT_OFF,

        STATE_KEEPER_POSITIONING,
        STATE_KEEPER_PENALTY_POSITIONING,
        STATE_KEEPER_DIVING_LOW_SINGLE,
        STATE_KEEPER_DIVING_LOW_DOUBLE,
        STATE_KEEPER_DIVING_MIDDLE_ONE,
        STATE_KEEPER_DIVING_MIDDLE_TWO,
        STATE_KEEPER_DIVING_HIGH_ONE,
        STATE_KEEPER_CATCHING_HIGH,
        STATE_KEEPER_CATCHING_LOW,
        STATE_KEEPER_KICK_ANGLE,
    }

    protected Player player;

    PlayerState stateCelebration;
    PlayerState stateIdle;
    PlayerState stateOutSide;
    PlayerState stateBenchSitting;
    PlayerState stateBenchStanding;
    PlayerState stateBenchOut;

    PlayerState statePhoto;
    PlayerState stateStandRun;
    PlayerState stateSubstituted;
    PlayerState stateKick;
    PlayerState stateHead;
    PlayerState stateTackle;
    PlayerState stateDown;
    PlayerState stateFinalCelebration;
    PlayerState stateReachTarget;
    PlayerState stateKickOff;
    PlayerState stateNotResponsive;
    PlayerState stateGoalKick;
    PlayerState stateThrowInAngle;
    PlayerState stateThrowInSpeed;
    PlayerState stateCornerKickAngle;
    PlayerState stateCornerKickSpeed;
    PlayerState stateFreeKickAngle;
    PlayerState stateFreeKickSpeed;
    PlayerState stateBarrier;
    PlayerState statePenaltyKickAngle;
    PlayerState statePenaltyKickSpeed;
    PlayerState stateGoalScorer;
    PlayerState stateGoalMate;
    PlayerState stateOwnGoalScorer;
    PlayerState stateYellowCard;
    PlayerState stateRedCard;
    PlayerState stateSentOff;

    PlayerState stateKeeperPositioning;
    PlayerState stateKeeperPenaltyPositioning;
    PlayerState stateKeeperDivingLowSingle;
    PlayerState stateKeeperDivingLowDouble;
    PlayerState stateKeeperDivingMiddleOne;
    PlayerState stateKeeperDivingMiddleTwo;
    PlayerState stateKeeperDivingHighOne;
    PlayerState stateKeeperCatchingHigh;
    PlayerState stateKeeperCatchingLow;
    PlayerState stateKeeperKickAngle;

    public PlayerFsm(Player player) {
        this.player = player;
        stateCelebration = new PlayerStateCelebration(this);
        stateIdle = new PlayerStateIdle(this);
        stateOutSide = new PlayerStateOutside(this);
        stateBenchSitting = new PlayerStateBenchSitting(this);
        stateBenchStanding = new PlayerStateBenchStanding(this);
        stateBenchOut = new PlayerStateBenchOut(this);

        statePhoto = new PlayerStatePhoto(this);
        stateStandRun = new PlayerStateStandRun(this);
        stateSubstituted = new PlayerStateSubstituted(this);
        stateKick = new PlayerStateKick(this);
        stateHead = new PlayerStateHead(this);
        stateTackle = new PlayerStateTackle(this);
        stateDown = new PlayerStateDown(this);
        stateFinalCelebration = new PlayerStateFinalCelebration(this);
        stateReachTarget = new PlayerStateReachTarget(this);
        stateKickOff = new PlayerStateKickOff(this);
        stateNotResponsive = new PlayerStateNotResponsive(this);
        stateGoalKick = new PlayerStateGoalKick(this);
        stateThrowInAngle = new PlayerStateThrowInAngle(this);
        stateThrowInSpeed = new PlayerStateThrowInSpeed(this);
        stateCornerKickAngle = new PlayerStateCornerKickAngle(this);
        stateCornerKickSpeed = new PlayerStateCornerKickSpeed(this);
        stateFreeKickAngle = new PlayerStateFreeKickAngle(this);
        stateFreeKickSpeed = new PlayerStateFreeKickSpeed(this);
        stateBarrier = new PlayerStateBarrier(this);
        statePenaltyKickAngle = new PlayerStatePenaltyKickAngle(this);
        statePenaltyKickSpeed = new PlayerStatePenaltyKickSpeed(this);
        stateGoalScorer = new PlayerStateGoalScorer(this);
        stateGoalMate = new PlayerStateGoalMate(this);
        stateOwnGoalScorer = new PlayerStateOwnGoalScorer(this);
        stateYellowCard = new PlayerStateYellowCard(this);
        stateRedCard = new PlayerStateRedCard(this);
        stateSentOff = new PlayerStateSentOff(this);

        stateKeeperPositioning = new PlayerStateKeeperPositioning(this);
        stateKeeperPenaltyPositioning = new PlayerStateKeeperPenaltyPositioning(this);
        stateKeeperDivingLowSingle = new PlayerStateKeeperDivingLowSingle(this);
        stateKeeperDivingLowDouble = new PlayerStateKeeperDivingLowDouble(this);
        stateKeeperDivingMiddleOne = new PlayerStateKeeperDivingMiddleOne(this);
        stateKeeperDivingMiddleTwo = new PlayerStateKeeperDivingMiddleTwo(this);
        stateKeeperDivingHighOne = new PlayerStateKeeperDivingHighOne(this);
        stateKeeperCatchingHigh = new PlayerStateKeeperCatchingHigh(this);
        stateKeeperCatchingLow = new PlayerStateKeeperCatchingLow(this);
        stateKeeperKickAngle = new PlayerStateKeeperKickAngle(this);
    }

    @Override
    public PlayerState getState() {
        return (PlayerState) state;
    }

    void setState(Id id) {
        if (player.isActive) {
            super.setState(id.ordinal());
        }
    }
}
