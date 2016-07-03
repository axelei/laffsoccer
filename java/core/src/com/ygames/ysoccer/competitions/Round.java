package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;

public class Round {

    public int legs;

    public enum ExtraTime {
        OFF,
        ON,
        IF_REPLAY
    }

    ExtraTime extraTime;

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
}
