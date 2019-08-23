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
        STATE_KEEPER_KICKING
    }

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
    AiStateKeeperKicking stateKeeperKicking;

    public AiFsm(Ai ai) {
        stateIdle = new AiStateIdle(this, ai);
        stateKickingOff = new AiStateKickingOff(this, ai);
        statePositioning = new AiStatePositioning(this, ai);
        stateSeeking = new AiStateSeeking(this, ai);
        stateDefending = new AiStateDefending(this, ai);
        stateAttacking = new AiStateAttacking(this, ai);
        statePassing = new AiStatePassing(this, ai);
        stateKicking = new AiStateKicking(this, ai);
        stateGoalKicking = new AiStateGoalKicking(this, ai);
        stateThrowingIn = new AiStateThrowingIn(this, ai);
        stateCornerKicking = new AiStateCornerKicking(this, ai);
        stateKeeperKicking = new AiStateKeeperKicking(this, ai);

        setState(Id.STATE_IDLE);
    }

    void setState(Id id) {
        super.setState(id.ordinal());
    }
}
