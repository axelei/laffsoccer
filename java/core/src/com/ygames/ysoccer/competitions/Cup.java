package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class Cup extends Competition {

    public int rounds;

    public enum AwayGoals {
        OFF,
        AFTER_90_MINS,
        AFTER_EXTRA_TIME
    }

    public AwayGoals awayGoals;

    public Cup() {
        setRounds(4);
        awayGoals = AwayGoals.OFF;
    }

    public Type getType() {
        return Type.CUP;
    }

    public void setRounds(int n) {
        rounds = n;
    }

    public String getAwayGoalsLabel() {
        switch (awayGoals) {
            case OFF:
                return "OFF";
            case AFTER_90_MINS:
                return "AFTER 90 MINS";
            case AFTER_EXTRA_TIME:
                return "AFTER EXTRA TIME";
            default:
                throw new GdxRuntimeException("Unknown AwayGoals value");
        }
    }

    public boolean hasTwoLegsRound() {
        return true; // TODO: implement this
    }
}
