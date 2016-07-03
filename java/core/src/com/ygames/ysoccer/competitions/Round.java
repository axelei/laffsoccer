package com.ygames.ysoccer.competitions;

public class Round {

    int legs;

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
}
