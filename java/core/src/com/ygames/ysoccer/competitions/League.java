package com.ygames.ysoccer.competitions;

import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class League extends Competition {

    public int rounds;
    public int pointsForAWin;
    private ArrayList<Match> calendarCurrent;

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

    public Team getTeam(int t) {
        return teams.get(getMatch().teams[t]);
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

    private void nextRound() {
        currentRound += 1;
        updateMonth();
    }

    private void updateMonth() {
        int seasonLength = ((seasonEnd - seasonStart + 12) % 12);
        currentMonth = (seasonStart + seasonLength * currentRound / rounds) % 12;
    }

    private void calendarGenerate() {
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
                match.teams[HOME] = Assets.calendars[pos];
                match.teams[AWAY] = Assets.calendars[pos + 1];
            } else {
                match.teams[HOME] = Assets.calendars[pos + 1];
                match.teams[AWAY] = Assets.calendars[pos];
            }
            calendarCurrent.add(match);

            nextMatch();
        }

        currentMatch = 0;
        currentRound = 0;
        updateMonth();
    }

    public void generateResult() {
        Team homeTeam = getTeam(HOME);
        Team awayTeam = getTeam(AWAY);

        int goalA = Match.generateScore(homeTeam, awayTeam, false);
        int goalB = Match.generateScore(awayTeam, homeTeam, false);

        setResult(goalA, goalB);

        homeTeam.generateScorers(goalA);
        awayTeam.generateScorers(goalB);
    }

    private void setResult(int homeGoals, int awayGoals) {
        Team homeTeam = getTeam(HOME);
        Team awayTeam = getTeam(AWAY);

        Match match = getMatch();
        match.result = new Match.Result(homeGoals, awayGoals);
        homeTeam.updateStats(homeGoals, awayGoals, pointsForAWin);
        awayTeam.updateStats(awayGoals, homeGoals, pointsForAWin);
        match.ended = true;
    }

    public boolean bothComputers() {
        Team homeTeam = getTeam(HOME);
        Team awayTeam = getTeam(AWAY);

        return homeTeam.controlMode == Team.ControlMode.COMPUTER
                && awayTeam.controlMode == Team.ControlMode.COMPUTER;
    }
}
