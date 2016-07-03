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
            return Assets.strings.get("FINAL");
        } else if (round == rounds.size() - 2) {
            return Assets.strings.get("SEMI-FINAL");
        } else if (round == rounds.size() - 3) {
            return Assets.strings.get("QUARTER-FINAL");
        } else if (round == 0) {
            return Assets.strings.get("FIRST ROUND");
        } else if (round == 1) {
            return Assets.strings.get("SECOND ROUND");
        } else {
            return Assets.strings.get("THIRD ROUND");
        }
    }

    public int getRoundTeams(int round) {
        return (int)Math.pow(2, (rounds.size() - round));
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
