package com.ygames.ysoccer.competitions.tournament.groups;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.TableRow;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class Group implements Json.Serializable {

    private Groups groups;
    private ArrayList<Match> calendar;
    private List<TableRow> table;
    private int currentMatch;
    private int currentRound;

    Group() {
        calendar = new ArrayList<Match>();
        table = new ArrayList<TableRow>();
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        currentRound = jsonData.getInt("currentRound", 0);
        currentMatch = jsonData.getInt("currentMatch", 0);

        Match[] calendarArray = json.readValue("calendar", Match[].class, jsonData);
        Collections.addAll(calendar, calendarArray);

        TableRow[] tableArray = json.readValue("table", TableRow[].class, jsonData);
        Collections.addAll(table, tableArray);
    }

    @Override
    public void write(Json json) {
        json.writeValue("currentRound", currentRound);
        json.writeValue("currentMatch", currentMatch);
        json.writeValue("calendar", calendar, Match[].class, Match.class);
        json.writeValue("table", table, TableRow[].class, TableRow.class);
    }

    public void start(ArrayList<Integer> teams) {
        // if the calendar is not preset, generate it
        if (calendar.size() == 0) {
            generateCalendar(teams);
        }
    }

    public Match getMatch() {
        return calendar.get(currentMatch);
    }

    public void nextMatch() {
        currentMatch += 1;
        if (2 * currentMatch == (currentRound + 1) * groups.groupNumberOfTeams() * (groups.groupNumberOfTeams() - 1)) {
            nextRound();
        }
    }

    private void nextRound() {
        currentRound += 1;
    }

    private void generateCalendar(ArrayList<Integer> teams) {
        calendar.clear();
        while (currentRound < groups.rounds) {

            // search position of current match in league calendars
            int pos = 0;
            for (int i = 2; i < groups.groupNumberOfTeams(); i++) {
                pos = pos + i * (i - 1);
            }
            pos = pos + 2 * currentMatch - currentRound * groups.groupNumberOfTeams() * (groups.groupNumberOfTeams() - 1);

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

        // randomize
        Collections.shuffle(teams);
        for (Match match : calendar) {
            match.teams[HOME] = teams.get(match.teams[HOME]);
            match.teams[AWAY] = teams.get(match.teams[AWAY]);
        }

        currentMatch = 0;
        currentRound = 0;
    }
}
