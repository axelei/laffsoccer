package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;

public class Cup extends Competition {

    public ArrayList<Round> rounds;
    int currentLeg;
    ArrayList<Integer> qualifiedTeams;
    public ArrayList<Match> calendarCurrent;

    public enum AwayGoals {
        OFF,
        AFTER_90_MINS,
        AFTER_EXTRA_TIME
    }

    public AwayGoals awayGoals;

    public Cup() {
        rounds = new ArrayList<Round>();
        awayGoals = AwayGoals.OFF;
        currentLeg = 0;
        calendarCurrent = null;
        qualifiedTeams = new ArrayList<Integer>();
    }

    public void start(ArrayList<Team> teams) {
        super.start(teams);
        for (int i = 0; i < teams.size(); i++) {
            qualifiedTeams.add(i);
        }
        calendarGenerate();
    }

    public Match getMatch() {
        return calendarCurrent.get(currentMatch);
    }

    @Override
    public boolean isEnded() {
        return currentRound == rounds.size();
    }

    public void nextMatch() {
        currentMatch += 1;
        if (currentMatch == calendarCurrent.size()) {
            nextLeg();
        }
    }

    public void nextLeg() {
        currentLeg += 1;
        currentMatch = 0;
        calendarGenerate();
        if (calendarCurrent.size() == 0) {
            nextRound();
        }
    }

    public void nextRound() {
        currentRound += 1;
        currentLeg = 0;
        currentMatch = 0;
        calendarGenerate();
    }

    void calendarGenerate() {
        // first leg
        if (currentLeg == 0) {
            Collections.shuffle(qualifiedTeams);
            calendarCurrent = new ArrayList<Match>();
            for (int i = 0; i < qualifiedTeams.size() / 2; i++) {
                Match match = new Match();
                match.team[Match.HOME] = qualifiedTeams.get(2 * i);
                match.team[Match.AWAY] = qualifiedTeams.get(2 * i + 1);
                match.stats = null;
                match.oldStats = null;
                calendarCurrent.add(match);
            }
            qualifiedTeams.clear();
        }

        // second leg
        else if ((currentLeg == 1) && (rounds.get(currentRound).legs == 2)) {
            for (int i = 0; i < calendarCurrent.size(); i++) {
                Match match = calendarCurrent.get(i);
                // swap teams
                int tmp = match.team[Match.HOME];
                match.team[Match.HOME] = match.team[Match.AWAY];
                match.team[Match.AWAY] = tmp;

                match.oldStats = match.stats;
                match.stats = null;
                match.includesExtraTime = false;
                match.statsAfter90 = null;
                match.status = Assets.strings.get("1ST LEG") +
                        " " + match.oldStats[Match.AWAY].goals +
                        "-" + match.oldStats[Match.HOME].goals;
            }
        }

        // replays
        else {
            for (int i = 0; i < calendarCurrent.size(); i++) {
                Match match = calendarCurrent.get(i);
                if (match.qualified == -1) {
                    // swap teams
                    int tmp = match.team[Match.HOME];
                    match.team[Match.HOME] = match.team[Match.AWAY];
                    match.team[Match.AWAY] = tmp;

                    match.stats = null;
                    match.includesExtraTime = false;
                    match.statsAfter90 = null;
                    match.oldStats = null;
                    match.status = "";
                } else {
                    calendarCurrent.remove(match);
                }
            }
        }

        // update month
        int seasonLength = ((seasonEnd - seasonStart + 12) % 12);
        currentMonth = (seasonStart + seasonLength * currentRound / rounds.size()) % 12;
    }

    public Type getType() {
        return Type.CUP;
    }

    public void addRound() {
        if (rounds.size() < 6) {
            rounds.add(new Round());
        }
        numberOfTeams = getRoundTeams(0);
    }

    public void removeRound() {
        if (rounds.size() > 1) {
            rounds.remove(rounds.size() - 1);
        }
        numberOfTeams = getRoundTeams(0);
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

    public boolean bothComputers() {
        Match match = getMatch();
        return teams.get(match.team[Match.HOME]).controlMode == Team.ControlMode.COMPUTER
                && teams.get(match.team[Match.AWAY]).controlMode == Team.ControlMode.COMPUTER;
    }
}
