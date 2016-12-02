package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class League extends Competition implements Json.Serializable {

    public int rounds;
    public int pointsForAWin;
    private ArrayList<Match> calendarCurrent;

    public League() {
        super(Type.LEAGUE);
        numberOfTeams = 2;
        rounds = 1;
        pointsForAWin = 3;
        calendarCurrent = new ArrayList<Match>();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.readFields(this, jsonData);
    }

    @Override
    public void write(Json json) {
        // config
        json.writeValue("name", name);
        if (filename.length() > 0) {
            json.writeValue("filename", filename);
        }
        json.writeValue("category", category);
        json.writeValue("numberOfTeams", numberOfTeams);
        json.writeValue("rounds", rounds);
        json.writeValue("pointsForAWin", pointsForAWin);
        json.writeValue("substitutions", substitutions);
        json.writeValue("benchSize", benchSize);
        json.writeValue("time", time);
        json.writeValue("weather", weather);
        if (weather == Weather.BY_SEASON) {
            json.writeValue("seasonStart", seasonStart);
            json.writeValue("seasonEnd", seasonEnd);
            json.writeValue("currentMonth", currentMonth);
        } else {
            json.writeValue("pitchType", pitchType);
        }

        // state
        json.writeValue("userPrefersResult", userPrefersResult);
        json.writeValue("currentRound", currentRound);
        json.writeValue("currentMatch", currentMatch);
        json.writeValue("teams", teams, Team[].class, Team.class);
        json.writeValue("calendarCurrent", calendarCurrent, Match[].class, Match.class);
    }

    @Override
    public void start(ArrayList<Team> teams) {
        super.start(teams);
        generateCalendar();
    }

    @Override
    public void restart() {
        super.restart();
        resetCalendar();
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
        if (weather == Weather.BY_SEASON) {
            int seasonLength = ((seasonEnd.ordinal() - seasonStart.ordinal() + 12) % 12);
            currentMonth = Month.values()[(seasonStart.ordinal() + seasonLength * currentRound / rounds) % 12];
        }
    }

    private void generateCalendar() {
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

    private void resetCalendar() {
        for (Match match : calendarCurrent) {
            resetMatch(match);
        }

        currentRound = 0;
        currentMatch = 0;
        updateMonth();
    }

    private void resetMatch(Match match) {
        match.result = null;
        match.ended = false;
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
        match.setResult(homeGoals, awayGoals);
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
