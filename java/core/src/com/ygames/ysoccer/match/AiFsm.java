package com.ygames.ysoccer.match;

import com.ygames.ysoccer.framework.Ai;

public class AiFsm extends Fsm {

    public static final int STATE_IDLE = 1;
    public static final int STATE_KICKING_OFF = 2;
    public static final int STATE_POSITIONING = 3;
    public static final int STATE_SEEKING = 4;
    public static final int STATE_DEFENDING = 5;
    public static final int STATE_ATTACKING = 6;
    public static final int STATE_PASSING = 7;
    public static final int STATE_KICKING = 8;
    public static final int STATE_GOAL_KICKING = 9;
    public static final int STATE_THROWING_IN = 10;
    public static final int STATE_CORNER_KICKING = 11;
    public static final int STATE_KEEPER_KICKING = 12;

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
    }
}
