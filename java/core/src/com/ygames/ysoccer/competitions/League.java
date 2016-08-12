package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;
import com.ygames.ysoccer.math.Emath;

import java.util.ArrayList;

public class League extends Competition {

    public int rounds;
    public int pointsForAWin;
    public ArrayList<Match> calendarCurrent;

    public League() {
        numberOfTeams = 2;
        rounds = 1;
        pointsForAWin = 3;
        calendarCurrent = new ArrayList<Match>();
    }

    public Type getType() {
        return Type.LEAGUE;
    }

    public void init() {
        numberOfTeams = teams.size();
        absolutePath = Assets.teamsFolder.child(path).path();
    }

    public void start(ArrayList<Team> teams) {
        super.start(teams);
        calendarGenerate();
    }

    @Override
    public void restart() {
        currentRound = 0;
        currentMatch = 0;
        calendarGenerate();
        start(loadTeams());
    }

    public Match getMatch() {
        return calendarCurrent.get(currentMatch);
    }

    @Override
    public boolean isEnded() {
        return currentRound == rounds;
    }

    public void nextMatch() {
        currentMatch += 1;
        if (2 * currentMatch == (currentRound + 1) * numberOfTeams * (numberOfTeams - 1)) {
            nextRound();
        }
    }

    public void nextRound() {
        currentRound += 1;
        updateMonth();
    }

    public void updateMonth() {
        int seasonLength = ((seasonEnd - seasonStart + 12) % 12);
        currentMonth = (seasonStart + seasonLength * currentRound / rounds) % 12;
    }

    public void calendarGenerate() {
        calendarCurrent.clear();
        while (currentRound < rounds) {

            // search position of current match in league calendars
            int pos = 0;
            for (int i = 2; i < numberOfTeams; i++) {
                pos = pos + i * (i - 1);
            }
            pos = pos + 2 * currentMatch - currentRound * numberOfTeams * (numberOfTeams - 1);

            // create match
            Match match = new Match();
            if ((currentRound % 2) == 0) {
                match.team[Match.HOME] = Assets.calendars[pos];
                match.team[Match.AWAY] = Assets.calendars[pos + 1];
            } else {
                match.team[Match.HOME] = Assets.calendars[pos + 1];
                match.team[Match.AWAY] = Assets.calendars[pos];
            }
            calendarCurrent.add(match);

            nextMatch();
        }

        currentMatch = 0;
        currentRound = 0;
        updateMonth();
    }

    public void generateResult() {
        // TODO: generate result
        int goalA = 6 - Emath.floor(Math.log10(1000000 * Math.random()));
        int goalB = 6 - Emath.floor(Math.log10(1000000 * Math.random()));

        setResult(goalA, goalB);

        teams.get(getMatch().team[Match.HOME]).generateScorers(goalA);
        teams.get(getMatch().team[Match.AWAY]).generateScorers(goalB);
    }

    public void setResult(int homeGoals, int awayGoals) {
        Match match = getMatch();
        match.result = new Match.Result(homeGoals, awayGoals);
        teams.get(match.team[Match.HOME]).updateStats(homeGoals, awayGoals, pointsForAWin);
        teams.get(match.team[Match.AWAY]).updateStats(awayGoals, homeGoals, pointsForAWin);
        match.ended = true;
    }

    public boolean bothComputers() {
        Match match = getMatch();
        return teams.get(match.team[Match.HOME]).controlMode == Team.ControlMode.COMPUTER
                && teams.get(match.team[Match.AWAY]).controlMode == Team.ControlMode.COMPUTER;
    }
}
