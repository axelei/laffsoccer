package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;

import java.util.ArrayList;

public class Cup extends Competition {

    public ArrayList<Round> rounds;

    public enum AwayGoals {
        OFF,
        AFTER_90_MINS,
        AFTER_EXTRA_TIME
    }

    public AwayGoals awayGoals;

    public Cup() {
        rounds = new ArrayList<Round>();
        awayGoals = AwayGoals.OFF;
    }

    public Type getType() {
        return Type.CUP;
    }

    public void addRound() {
        if (rounds.size() < 6) {
            rounds.add(new Round());
        }
    }

    public void removeRound() {
        if (rounds.size() > 1) {
            rounds.remove(rounds.size() - 1);
        }
    }

    public String getRoundName(int round) {
        if (round == rounds.size() - 1) {
            return "FINAL";
        } else if (round == rounds.size() - 2) {
            return "SEMI-FINAL";
        } else if (round == rounds.size() - 3) {
            return "QUARTER-FINAL";
        } else if (round == 0) {
            return "FIRST ROUND";
        } else if (round == 1) {
            return "SECOND ROUND";
        } else {
            return "THIRD ROUND";
        }
    }

    public int getRoundTeams(int round) {
        return (int) Math.pow(2, (rounds.size() - round));
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
        for (Round round : rounds) {
            if (round.legs == 2) {
                return true;
            }
        }
        return false;
    }
}
