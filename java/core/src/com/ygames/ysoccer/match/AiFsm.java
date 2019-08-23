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
        addState(stateIdle = new AiStateIdle(ai));
        addState(stateKickingOff = new AiStateKickingOff(ai));
        addState(statePositioning = new AiStatePositioning(ai));
        addState(stateSeeking = new AiStateSeeking(ai));
        addState(stateDefending = new AiStateDefending(ai));
        addState(stateAttacking = new AiStateAttacking(ai));
        addState(statePassing = new AiStatePassing(ai));
        addState(stateKicking = new AiStateKicking(ai));
        addState(stateGoalKicking = new AiStateGoalKicking(ai));
        addState(stateThrowingIn = new AiStateThrowingIn(ai));
        addState(stateCornerKicking = new AiStateCornerKicking(ai));
        addState(stateKeeperKicking = new AiStateKeeperKicking(ai));

        setState(Id.STATE_IDLE);
    }

    void setState(Id id) {
        super.setState(id.ordinal());
    }
}
