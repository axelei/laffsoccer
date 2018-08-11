package com.ygames.ysoccer.competitions.tournament;

import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class Round {

    public enum ExtraTime {OFF, ON, IF_REPLAY}

    public int numberOfTeams;

    public static String getExtraTimeLabel(ExtraTime extraTime) {
        switch (extraTime) {
            case OFF:
                return "EXTRA TIME.OFF";
            case ON:
                return "EXTRA TIME.ON";
            case IF_REPLAY:
                return "EXTRA TIME.IF REPLAY";
            default:
                throw new GdxRuntimeException("Wrong extraTime value");
        }
    }
}
