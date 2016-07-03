package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class Round {

    public int legs;

    public enum ExtraTime {
        OFF,
        ON,
        IF_REPLAY
    }

    public ExtraTime extraTime;

    public enum Penalties {
        OFF,
        ON,
        IF_REPLAY
    }

    Penalties penalties;

    public Round() {
        legs = 1;
        extraTime = ExtraTime.ON;
        penalties = Penalties.OFF;
    }

    public String getLegsLabel() {
        switch (legs) {
            case 1:
                return "ONE LEG";
            case 2:
                return "TWO LEGS";
            default:
                throw new GdxRuntimeException("Wrong legs value");
        }
    }

    public String getExtraTimeLabel() {
        switch (extraTime) {
            case OFF:
                return "EXTRA TIME.OFF";
            case ON:
                return "EXTRA TIME.ON";
            case IF_REPLAY:
                return "EXTRA TIME.IF REPLAY";
            default:
                throw new GdxRuntimeException("Unknown extraTime value");
        }
    }
}
