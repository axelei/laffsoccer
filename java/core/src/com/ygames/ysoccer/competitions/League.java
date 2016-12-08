package com.ygames.ysoccer.competitions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.Month;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class League extends Competition implements Json.Serializable {

    public int rounds;
    public int pointsForAWin;
    private ArrayList<Match> calendar;
    public List<TableRow> table;
    private Comparator<League.TableRow> tableRowComparator;

    public League() {
        super(Type.LEAGUE);
        numberOfTeams = 2;
        rounds = 1;
        pointsForAWin = 3;
        calendar = new ArrayList<Match>();
        table = new ArrayList<TableRow>();
        tableRowComparator = new TableRowComparator();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        rounds = jsonData.getInt("rounds");
        pointsForAWin = jsonData.getInt("pointsForAWin");

        Match[] calendarArray = json.readValue("calendar", Match[].class, jsonData);
        if (calendarArray != null) {
            Collections.addAll(calendar, calendarArray);
        }

        TableRow[] tableArray = json.readValue("table", TableRow[].class, jsonData);
        if (tableArray != null) {
            Collections.addAll(table, tableArray);
        }
    }

    @Override
    public void write(Json json) {
        super.write(json);

        json.writeValue("rounds", rounds);
        json.writeValue("pointsForAWin", pointsForAWin);
        json.writeValue("calendar", calendar, Match[].class, Match.class);
        json.writeValue("table", table, TableRow[].class, TableRow.class);
    }

    @Override
    public void start(ArrayList<Team> teams) {
        super.start(teams);
        generateCalendar();
        populateTable();
    }

    @Override
    public void restart() {
        super.restart();
        resetCalendar();
        resetTable();
    }

    @Override
    public Match getMatch() {
        return calendar.get(currentMatch);
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
        calendar.clear();
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
            calendar.add(match);

            nextMatch();
        }

        // randomize teams
        List<Integer> randomIndexes = new ArrayList<Integer>();
        for (int i = 0; i < numberOfTeams; i++) randomIndexes.add(i);
        Collections.shuffle(randomIndexes);
        for (Match match : calendar) {
            match.teams[HOME] = randomIndexes.get(match.teams[HOME]);
            match.teams[AWAY] = randomIndexes.get(match.teams[AWAY]);
        }

        currentMatch = 0;
        currentRound = 0;
        updateMonth();
    }

    private void resetCalendar() {
        for (Match match : calendar) {
            resetMatch(match);
        }

        currentRound = 0;
        currentMatch = 0;
        updateMonth();
    }

    private void populateTable() {
        for (int i = 0; i < numberOfTeams; i++) {
            table.add(new TableRow(i));
        }
        sortTable();
    }

    private void resetTable() {
        for (TableRow row : table) {
            row.reset();
        }
        sortTable();
    }

    private void sortTable() {
        Collections.sort(table, tableRowComparator);
    }

    private void resetMatch(Match match) {
        match.result = null;
        match.resultAfter90 = null;
        match.resultAfterPenalties = null;
    }

    public void generateResult() {
        Team homeTeam = getTeam(HOME);
        Team awayTeam = getTeam(AWAY);

        int homeGoals = Match.generateGoals(homeTeam, awayTeam, false);
        int awayGoals = Match.generateGoals(awayTeam, homeTeam, false);

        setResult(homeGoals, awayGoals);

        generateScorers(homeTeam, homeGoals);
        generateScorers(awayTeam, awayGoals);
    }

    public void setResult(int homeGoals, int awayGoals) {
        Match match = getMatch();
        match.setResult(homeGoals, awayGoals);

        for (TableRow row : table) {
            if (row.team == getTeamIndex(HOME)) {
                row.update(homeGoals, awayGoals, pointsForAWin);
            }
            if (row.team == getTeamIndex(AWAY)) {
                row.update(awayGoals, homeGoals, pointsForAWin);
            }
        }
        sortTable();
    }

    public static class TableRow {

        public int team;

        public int won;
        public int drawn;
        public int lost;

        public int goalsFor;
        public int goalsAgainst;
        public int points;

        // needed by json deserializer
        TableRow() {
        }

        TableRow(int team) {
            this.team = team;
        }

        public void update(int goalsFor, int goalsAgainst, int pointsForAWin) {
            this.goalsFor += goalsFor;
            this.goalsAgainst += goalsAgainst;
            if (goalsFor > goalsAgainst) {
                won += 1;
                points += pointsForAWin;
            } else if (goalsFor == goalsAgainst) {
                drawn += 1;
                points += 1;
            } else {
                lost += 1;
            }
        }

        void reset() {
            won = 0;
            drawn = 0;
            lost = 0;

            goalsFor = 0;
            goalsAgainst = 0;
            points = 0;
        }
    }

    private class TableRowComparator implements Comparator<TableRow> {

        @Override
        public int compare(TableRow o1, TableRow o2) {
            // by points
            if (o1.points != o2.points) {
                return o2.points - o1.points;
            }

            // by goals diff
            int diff1 = o1.goalsFor - o1.goalsAgainst;
            int diff2 = o2.goalsFor - o2.goalsAgainst;
            if (diff1 != diff2) {
                return diff2 - diff1;
            }

            // by scored goals
            if (o1.goalsFor != o2.goalsFor) {
                return o2.goalsFor - o1.goalsFor;
            }

            // by names
            return teams.get(o1.team).name.compareTo(teams.get(o2.team).name);
        }
    }
}
