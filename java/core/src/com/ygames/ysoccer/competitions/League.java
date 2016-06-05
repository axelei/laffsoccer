package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;

public class League extends Competition {

    public int rounds;
    public int pointsForAWin;
    public ArrayList<Match> calendarCurrent;

    public League() {
        type = Type.LEAGUE;
        numberOfTeams = 2;
        rounds = 1;
        pointsForAWin = 3;
        calendarCurrent = new ArrayList<Match>();
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

            // get teams
            Team teamA;
            Team teamB;
            if ((currentRound % 2) == 0) {
                teamA = teams.get(Assets.calendars[pos]);
                teamB = teams.get(Assets.calendars[pos + 1]);
            } else {
                teamA = teams.get(Assets.calendars[pos + 1]);
                teamB = teams.get(Assets.calendars[pos]);
            }

            Team team[] = {teamA, teamB};
            Match match = new Match(team);

            calendarCurrent.add(match);
            nextMatch();
        }

        currentMatch = 0;
        currentRound = 0;
        updateMonth();
    }

    public void setResult(int homeGoals, int awayGoals) {
        Match match = getMatch();
        match.stats[Match.HOME].goals = homeGoals;
        match.stats[Match.AWAY].goals = awayGoals;
        match.team[Match.HOME].updateStats(homeGoals, awayGoals, pointsForAWin);
        match.team[Match.AWAY].updateStats(awayGoals, homeGoals, pointsForAWin);
        match.ended = true;
    }
}
