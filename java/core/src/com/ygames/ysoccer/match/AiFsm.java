package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;

public class AiFsm extends Fsm {

    enum Id {
        STATE_IDLE,
        STATE_KICKING_OFF,
        STATE_POSITIONING,
        STATE_SEEKING,
        STATE_DEFENDING,
        STATE_ATTACKING,
        STATE_PASSING,
        STATE_KICKING,
        STATE_GOAL_KICKING,
        STATE_THROWING_IN,
        STATE_CORNER_KICKING,
        STATE_FREE_KICKING,
        STATE_PENALTY_KICKING,
        STATE_KEEPER_KICKING
    }

    Ai ai;

    AiStateIdle stateIdle;
    AiStateKickingOff stateKickingOff;
    AiStatePositioning statePositioning;
    AiStateSeeking stateSeeking;
    AiStateDefending stateDefending;
    AiStateAttacking stateAttacking;
    AiStatePassing statePassing;
    AiStateKicking stateKicking;
    AiStateGoalKicking stateGoalKicking;
    AiStateThrowingIn stateThrowingIn;
    AiStateCornerKicking stateCornerKicking;
    AiStateFreeKicking stateFreeKicking;
    AiStatePenaltyKicking statePenaltyKicking;
    AiStateKeeperKicking stateKeeperKicking;

    public AiFsm(Ai ai) {
        this.ai = ai;

        stateIdle = new AiStateIdle(this);
        stateKickingOff = new AiStateKickingOff(this);
        statePositioning = new AiStatePositioning(this);
        stateSeeking = new AiStateSeeking(this);
        stateDefending = new AiStateDefending(this);
        stateAttacking = new AiStateAttacking(this);
        statePassing = new AiStatePassing(this);
        stateKicking = new AiStateKicking(this);
        stateGoalKicking = new AiStateGoalKicking(this);
        stateThrowingIn = new AiStateThrowingIn(this);
        stateCornerKicking = new AiStateCornerKicking(this);
        stateFreeKicking = new AiStateFreeKicking(this);
        statePenaltyKicking = new AiStatePenaltyKicking(this);
        stateKeeperKicking = new AiStateKeeperKicking(this);

        setState(Id.STATE_IDLE);
    }

    void setState(Id id) {
        super.setState(id.ordinal());
    }
}
