package com.ysoccer.android.ysdemo.match;

public class AiFsm extends Fsm {

    static final int STATE_IDLE = 1;
    static final int STATE_KICKING_OFF = 2;
    static final int STATE_POSITIONING = 3;
    static final int STATE_SEEKING = 4;
    static final int STATE_DEFENDING = 5;
    static final int STATE_ATTACKING = 6;
    static final int STATE_PASSING = 7;
    static final int STATE_KICKING = 8;
    static final int STATE_GOAL_KICKING = 9;
    static final int STATE_THROWING_IN = 10;
    static final int STATE_CORNER_KICKING = 11;
    static final int STATE_KEEPER_KICKING = 12;

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
    }

}
