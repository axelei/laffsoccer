package com.ygames.ysoccer.competitions.tournament.groups;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ygames.ysoccer.competitions.TableRow;
import com.ygames.ysoccer.match.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Group implements Json.Serializable {

    private Groups groups;
    ArrayList<Match> calendar;
    List<TableRow> table;

    Group() {
        calendar = new ArrayList<Match>();
        table = new ArrayList<TableRow>();
    }

    public void setGroups(Groups groups) {
        this.groups = groups;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        Match[] calendarArray = json.readValue("calendar", Match[].class, jsonData);
        Collections.addAll(calendar, calendarArray);

        TableRow[] tableArray = json.readValue("table", TableRow[].class, jsonData);
        Collections.addAll(table, tableArray);
    }

    @Override
    public void write(Json json) {
        json.writeValue("calendar", calendar, Match[].class, Match.class);
        json.writeValue("table", table, TableRow[].class, TableRow.class);
    }
}
