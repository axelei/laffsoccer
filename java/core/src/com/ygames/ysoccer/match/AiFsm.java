package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;

public class AiFsm extends Fsm {

    enum Id {
        AI_STATE_BARRIER,
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

    AiState stateBarrier;
    AiState stateIdle;
    AiState stateKickingOff;
    AiState statePositioning;
    AiState stateSeeking;
    AiState stateDefending;
    AiState stateAttacking;
    AiState statePassing;
    AiState stateKicking;
    AiState stateGoalKicking;
    AiState stateThrowingIn;
    AiState stateCornerKicking;
    AiState stateFreeKicking;
    AiState statePenaltyKicking;
    AiState stateKeeperKicking;

    public AiFsm(Ai ai) {
        this.ai = ai;

        stateBarrier = new AiStateBarrier(this);
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
